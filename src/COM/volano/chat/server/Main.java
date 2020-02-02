/*
 * Main.java - a multithreaded chat server.
 * Copyright (C) 1996-1998 John Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package COM.volano.chat.server;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.*;
import  COM.volano.chat.security.AppletKeyPairGenerator;
import  COM.volano.io.TraceReader;
import  COM.volano.io.TraceWriter;
import  COM.volano.net.*;
import  COM.volano.util.Message;
import  java.io.*;
import  java.net.*;
import  java.security.*;
import  java.text.*;
import  java.util.*;
import  org.apache.catalina.logger.FileLogger;
import  org.apache.catalina.LifecycleException;

/**
 * This class represents the main chat server and its main thread.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Main implements Runnable, Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String PROPERTIES    = "conf/properties.txt";
    private static final String LISTENER_NAME = "MainServer";
    private static final String PUBLIC_LIST   = "PublicRooms";
    private static final String PERSONAL_LIST = "PersonalRooms";
    private static final String PRIVATE_LIST  = "PrivateRooms";
    private static final String NOTE          = "*** ";

    private static final int MILLIS_PER_MINUTE = 1000 * 60;       // Milliseconds per minute
    private static final int MILLIS_PER_SECOND = 1000;            // Milliseconds per second

    // It takes about 1.062 seconds for the interrupt to take effect.  In my
    // tests, 2 seconds was not always enough time, but 3 seconds always worked,
    // so let's be safe and go with 5 seconds.
    private static final int INTERRUPT_DELAY = 5 * 1000;  // Delay between interrupt and close
    private static final int TIMEOUT_LIMIT   = 2;         // Close after 2 consecutive timeouts
    private static final int SHUTDOWN_DELAY  = 5 * 1000;  // 5 second NT Service shutdown delay

    // RANDOM_BYTES_SIZE must be less than 255 since it's written and read as an
    // unsigned byte in the data stream.
    private static final int    RANDOM_BYTES_SIZE =     8;  // 8 bytes gives us 64 random bits
    private static final String SIG_ALGORITHM     = "DSA";

    // Expected packet lists.
    private static final int[]  FIRST_PACKET  =
    {ChatPacketId.PACKET_ACCESS_OLD, ChatPacketId.PACKET_ACCESS, ChatPacketId.PACKET_PASSWORD_ACCESS};
    private static final int[]  SECOND_PACKET = {ChatPacketId.PACKET_AUTHENTICATE};
    private static final int[]  ANY_PACKET    = new int[0];

    private static final String PREFIX           = "<result value=\"true|false|error\">";
    private static final String TRUE_PREFIX      = "<result value=\"true\">";
    private static final String FALSE_PREFIX     = "<result value=\"false\">";
    private static final String ERROR_PREFIX     = "<result value=\"error\">";
    private static final String SUFFIX           = "</result>";
    private static final int    TRUE_PREFIX_LEN  = TRUE_PREFIX.length();
    private static final int    FALSE_PREFIX_LEN = FALSE_PREFIX.length();
    private static final int    ERROR_PREFIX_LEN = ERROR_PREFIX.length();
    private static final int    SUFFIX_LEN       = SUFFIX.length();

    // There is only one instance of the Main object in each Java virtual machine.
    private static Main server = null;

    private ServerSocket   serverSocket;  // Main server socket for accepting connections
    private ServletRunner  runner;        // Embedded servlet runner
    private PublicList     publicList;    // List of public chat rooms
    private PublicList     personalList;  // List of personal chat rooms
    private Grouptable     privateList;   // List of private chat rooms
    private Hashtable      memberMonitorList;     // 2.1.8 - List of member monitors
    private Thread         listener;      // Listens for incoming socket connections

    private Value          value;         // Server properties
    private License        license;       // Server license
    private Administrator  administrator; // For Web interface access to server
    private StatusReporter reporter;      // Resource status reporter
    private StatusRecorder recorder;      // Resource status history recorder
    private EventLogger    privateChatLogger;     // For PublicChat

    private Random    random;    // Pseudo random number generator
    private PublicKey publicKey; // Public key for authenticating clients

    // Public static access methods for the Velocity context objects.

    public static Administrator getServer() {
        return server.administrator;
    }

    public static StatusSnapshot getStatus() {
        return server.recorder.getSnapshot();
    }

    public static AppletConfig getApplet(String path) throws IOException {
        return new AppletConfig(server.value.appletCodebase, path);
    }

    public static BannerConfig getBanner(String path) throws IOException {
        return new BannerConfig(server.value.appletCodebase, path);
    }

    // Public static methods for use in servlets.

    public static void refreshFileLists() throws IOException {
        AppletConfig.refreshFileList(server.value.appletCodebase);
        BannerConfig.refreshFileList(server.value.appletCodebase);
    }

    // Package static access methods for Administrator Web interface.

    static Hashtable getActiveMemberMonitors() {
        return server.memberMonitorList;
    }

    static String[] getPublicRooms() {
        return server.publicList.getNames();
    }

    static String[] getPersonalRooms() {
        return server.personalList.getNames();
    }

    static String[] getPrivateRooms() {
        Object[] list  = server.privateList.snapshot();
        String[] names = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            names[i] = list[i].toString();
        }
        return names;
    }

    // Other package static access methods.

    static EventLogger getPrivateChatLogger() {   // For PublicChat
        return server.privateChatLogger;
    }

    /**
     * Returns the Unicode string encoded as escape sequences in ASCII for
     * debugging purposes.
     *
     * @param unicode  the Unicode string to encode.
     * @return  the string encoded as Unicode escape sequences in ASCII.
     */

    public static String unicode2ascii(String unicode) {
        StringBuffer buffer = new StringBuffer(unicode.length() * 6);
        char[] c = unicode.toCharArray();
        for (int i = 0; i < c.length; i++) {
            buffer.append("\\u");
            buffer.append(Integer.toHexString((int) c[i]));
        }
        return buffer.toString();
    }

    /**
     * Traces an HTTP response for debugging purposes.
     *
     * @param connection the URL connection to the server.
     */

    public static void traceResponse(URLConnection connection) {
        String key = null;
        synchronized (System.out) {
            System.out.println("--> " + connection.getHeaderField(0));
            for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                System.out.println("--> " + key + ": " + connection.getHeaderField(i));
            }
            System.out.println("-->");
        }
    }

    /**
     * Adds an observer of a public chat room.  Each event in the specified room
     * will be sent to the callback URL with an HTTP POST containing the event in
     * XML notation.  If the room does not yet exist, the event notifications
     * will start once the room is created.
     *
     * @param name the name of the room to observe.
     * @param url  the callback URL to receive each event as an HTTP POST.
     * @exception java.io.Exception  if the server is not the "pro" edition.
     */

    public static void addRoomObserver(String name, URL url) throws Exception {
        if (server.value.eventCallbackPrefix.length() == 0 ||
                ! url.toString().startsWith(server.value.eventCallbackPrefix)) {
            throw new Exception("unauthorized URL");
        }
        server.publicList.addRoomObserver(name, url);
    }

    /**
     * Traces a line from the member script connection.
     *
     * @param prefix  the prefix to use for the traced line.
     * @param line    the line of text to trace.
     */

    private static void trace(String prefix, String line) {
        if (server.value.scriptTrace) {
            System.out.println(prefix + line);
        }
    }

    /**
     * Invokes a URL which returns up to 3 lines of text.  The first line consists
     * of the string "true", "false", or "error".  The second line contains
     * optional descriptive information, such as a member profile if found or an
     * error message if the URL request failed.  The third line contains "true" or
     * "false" to indicate whether to display the member link in the profile text.
     *
     * @param href    the hyperlink to invoke.
     * @param buffer  the string buffer for appending the message string.
     * @return  <code>true</code> if the URL request was successful; otherwise
     *          <code>false</code>.
     */

    private static boolean getURL21(String href, StringBuffer message, StringBuffer document) {
        String result = "";         // "true", "false", or "error"
        String text   = "";         // Profile text or error message
        String link   = "true";     // "true" or "false"

        Socket socket = null;
        try {
            URL url = new URL(href);

            // Use a direct socket connection instead of a URLConnection so that we
            // timeout if the HTTP response is never sent.  Don't worry about not
            // using a keep-alive connection, since they're used only when invoked
            // from the same thread anyway.
            //
            // URLConnection  connection = url.openConnection();
            // BufferedReader reader     = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // traceResponse(connection);

            // -> GET http://red.volano.com:8080/servlet/name?name=member0 HTTP/1.0
            // -> User-Agent: Java1.2.1
            // -> Host: red.volano.com:8888
            // -> Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
            // ->
            //
            // <- HTTP/1.0 200 OK
            // <- Server: servletrunner/2.0
            // <- Content-Type: text/plain
            // <- Content-Length: 7
            // <- Connection: Keep-Alive
            // <- Date: Fri, 27 Aug 1999 23:24:03 GMT
            // <-

            String host   = url.getHost();
            int    port   = url.getPort();
            if (port == -1) {                 // Set default HTTP port
                port = 80;
            }
            socket = new Socket(host, port);
            socket.setSoTimeout(server.value.scriptTimeout * MILLIS_PER_SECOND);

            PrintWriter    writer = null;
            BufferedReader reader = null;
            if (server.value.scriptTrace) {
                System.out.println(NOTE + url.toString());
                writer = new PrintWriter(new TraceWriter(new OutputStreamWriter(socket.getOutputStream())));
                reader = new BufferedReader(new TraceReader(new InputStreamReader(socket.getInputStream())));
            } else {
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            writer.println("GET " + url.getFile() + " HTTP/1.0");
            writer.println("User-Agent: Java" + server.value.javaVersion);
            writer.println("Host: " + host + ":" + port);
            writer.println("Content-Type: text/plain; charset=utf-8");
            writer.println("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            writer.println();
            writer.flush();
            String line = reader.readLine();
            while (line != null && line.trim().length() > 0) {
                line = reader.readLine();    // Discard HTTP headers in response
            }

            if (line != null) {
                line = reader.readLine();
                if (line != null) {             // First line after headers
                    result = line.trim();         // Trim result
                    line = reader.readLine();
                    if (line != null) {           // Second line
                        text = line.trim();         // Trim profile text
                        line = reader.readLine();
                        if (line != null) {         // Third line
                            link = line.trim();       // Trim member profile link
                            while (line != null) {
                                line = reader.readLine();    // Discard remaining lines
                            }
                        }
                    }
                }
            }
            if (result.equals("error")) {
                throw new IOException(text);
            }
        } catch (IOException e) {
            Log.printError(Message.format(Msg.BAD_URL, href), e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {}
        }
        if (message != null) {
            message.append(text);
        }
        if (document != null) {
            document.append(link);
        }
        return Boolean.valueOf(result).booleanValue();
    }

    /**
     * Invokes a URL which returns an XML document of the format:
     * <pre>
     *   &lt;result value="[true|false|error]"&gt;
     *   Profile or error string.
     *   &lt;/result&gt;
     * </pre>
     *
     * @param href    the hyperlink to invoke.
     * @param buffer  the string buffer for appending the message string.
     * @return  <code>true</code> if the URL request was successful; otherwise
     *          <code>false</code>.
     */

    private static boolean getURL(String href, StringBuffer buffer) {
        String result = null;               // "true", "false", or "error"
        String text   = null;               // Profile text or error message
        Socket socket = null;
        try {
            URL url = new URL(href);
            // URLConnection  connection = url.openConnection();
            // BufferedReader reader     = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // traceResponse(connection);

            // -> GET http://red.volano.com:8080/servlet/name?name=member0 HTTP/1.0
            // -> User-Agent: Java1.2.1
            // -> Host: red.volano.com:8888
            // -> Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
            // ->
            //
            // <- HTTP/1.0 200 OK
            // <- Server: servletrunner/2.0
            // <- Content-Type: text/plain
            // <- Content-Length: 7
            // <- Connection: Keep-Alive
            // <- Date: Fri, 27 Aug 1999 23:24:03 GMT
            // <-

            String host   = url.getHost();
            int    port   = url.getPort();
            if (port == -1) {                 // Set default HTTP port
                port = 80;
            }
            socket = new Socket(host, port);
            socket.setSoTimeout(server.value.scriptTimeout * MILLIS_PER_SECOND);

            PrintWriter    writer = null;
            BufferedReader reader = null;
            if (server.value.scriptTrace) {
                System.out.println(NOTE + url.toString());
                writer = new PrintWriter(new TraceWriter(new OutputStreamWriter(socket.getOutputStream())));
                reader = new BufferedReader(new TraceReader(new InputStreamReader(socket.getInputStream())));
            } else {
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            writer.println("GET " + url.getFile() + " HTTP/1.0");
            writer.println("User-Agent: Java" + server.value.javaVersion);
            writer.println("Host: " + host + ":" + port);
            writer.println("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            writer.println();
            writer.flush();
            String line = reader.readLine();
            while (line.trim().length() > 0) {
                line = reader.readLine();    // Discard HTTP headers in response
            }

            StringBuffer input = new StringBuffer();
            line  = reader.readLine();
            while (line != null) {
                input.append(line + " ");
                line = reader.readLine();
            }

            String response = input.toString().trim();
            int start = 0;
            if (response.startsWith(TRUE_PREFIX)) {
                result = "true";
                start  = TRUE_PREFIX_LEN;
            } else if (response.startsWith(FALSE_PREFIX)) {
                result = "false";
                start  = FALSE_PREFIX_LEN;
            } else if (response.startsWith(ERROR_PREFIX)) {
                result = "error";
                start  = ERROR_PREFIX_LEN;
            } else {
                throw new IOException("Missing " + PREFIX);
            }

            if (! response.endsWith(SUFFIX)) {
                result = "false";
                throw new IOException("Missing " + SUFFIX);
            }
            int end = response.length() - SUFFIX_LEN;
            text = response.substring(start, end).trim();
            if (result.equals("error")) {
                throw new IOException(text);
            }
        } catch (IOException e) {
            Log.printError(Message.format(Msg.BAD_URL, href), e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {}
        }
        if (buffer != null && text != null) {
            buffer.append(text);
        }
        return Boolean.valueOf(result).booleanValue();
    }

    /**
     * Checks whether or not the specified name belongs to a member by invoking a
     * URL and checking the result.
     *
     * @param formatter  the message formatter for encoding the URL.
     * @param name       the name to check.
     * @return  <code>true</code> if the name belongs to a member; otherwise
     *          <code>false</code>.
     */

    private static boolean isMember(String version, MessageFormat formatter, String name, String roomName) {
        Object[] arguments = {URLEncoder.encode(name), "", URLEncoder.encode(roomName)};
        if (version.equals(Default.MEMBER_VERSION_2_1)) {
            return getURL21(formatter.format(arguments), null, null);
        } else {
            return getURL(formatter.format(arguments), null);
        }
    }

    /**
     * Checks whether or not the specified name and password are valid for a
     * member by invoking a URL and checking the result.  If successful, the
     * member profile string (if any) is appended to the string buffer.
     *
     * @param formatter  the message formatter for encoding the URL.
     * @param name       the member name.
     * @param password   the member password.
     * @param profile    the string buffer for appending the member profile.
     * @return  the member profile string, or <code>null</code> if the member is
     *          not found or the password is invalid.
     */

    private static boolean isMember(String version, MessageFormat formatter, String name, String password, String roomName,
                                    StringBuffer profile, StringBuffer link) {
        Object[] arguments = {URLEncoder.encode(name), URLEncoder.encode(password), URLEncoder.encode(roomName)};
        if (version.equals(Default.MEMBER_VERSION_2_1)) {
            return getURL21(formatter.format(arguments), profile, link);
        } else {
            link.append("true");              // Show member link by default
            return getURL(formatter.format(arguments), profile);
        }
    }

    /**
     * Writes the information about the Java environment required for support.
     *
     * @param value  the server property values.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private static void writeSupport(File file, Value value) throws IOException {
        if (value.logSupport != null) {
            try {
                String properties = null;
                try {
                    properties = file.getCanonicalPath();
                } catch (IOException e) {
                    properties = file.getAbsolutePath();
                }
                // Open log file in overwrite mode (not append mode) and with autoflush.
                String      path    = value.logSupport.getPath();
                PrintWriter writer  = new PrintWriter(new FileWriter(path, false), true);
                License     license = value.getLicense();
                writer.println("server.version     = " + "Version " + Build.VERSION);
                writer.println("server.properties  = " + properties);
                writer.println("server.license     = " + license);
                writer.println("server.expiration  = " + license.getDate());
                writer.println("server.host        = " + license.getHostName() + " (" + license.getHostAddress() + ")");
                writer.println("server.port        = " + license.getPort());
                writer.println("java.vendor        = " + value.javaVendor);
                writer.println("java.vendor.url    = " + value.javaVendorUrl);
                writer.println("java.version       = " + value.javaVersion);
                writer.println("java.class.version = " + value.javaClassVersion);
                writer.println("java.compiler      = " + value.javaCompiler);
                writer.println("os.name            = " + value.osName);
                writer.println("os.version         = " + value.osVersion);
                writer.println("os.arch            = " + value.osArch);
                writer.println("user.language      = " + value.userLanguage);
                writer.println("user.region        = " + value.userRegion);
                writer.println("file.encoding      = " + value.fileEncoding);
                writer.println("file.encoding.pkg  = " + value.fileEncodingPkg);
                writer.close();
            } catch (IOException e) {
                System.err.println(Message.format(Msg.BAD_LOG_FILE, e.toString()));
                throw e;
            }
        }
    }

    /**
     * Opens the specified log file in append mode with autoflush.
     *
     * @param file  the log file to open.
     * @return  the print writer used for writing to the log file, or
     *          <code>null</code> if an error occurred opening the log file.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private static PrintWriter openWriter(File file) throws IOException {
        PrintWriter writer = null;
        if (file != null) {
            try {
                // Open log file in append mode with autoflush.
                // Microsoft SDK 2.01 append mode does not work with a PrintWriter nor a
                // PrintStream, nor does using a FileOutputStream underneath help.
                writer = new PrintWriter(new FileWriter(file.getPath(), true), true);
            } catch (IOException e) {
                System.err.println(Message.format(Msg.BAD_LOG_FILE, e.toString()));
                throw e;
            }
        }
        return writer;
    }

    /**
     * Opens the output file.
     *
     * @param file  the output file to open.
     * @return  the print stream used for writing to the output file, or
     *          <code>null</code> if an error occurred opening the file.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private static PrintStream openStream(File file) throws IOException {
        PrintStream output = null;
        if (file != null) {
            try {
                // Open log file in append mode with autoflush.
                output = new PrintStream(new FileOutputStream(file.getPath(), true), true);
            } catch (IOException e) {
                System.err.println(Message.format(Msg.BAD_LOG_FILE, e.toString()));
                throw e;
            }
        }
        return output;
    }

    /**
     * Verifies a digital signature from a client.
     *
     * @param key        the client's public key.
     * @param data       the data that was signed.
     * @param signature  the digital signature.
     * @return  <code>true</code> if the signature is valid; otherwise
     *          <code>false</code>.
     * @exception java.security.NoSuchAlgorithmException
     *                if the signature algorithm is not found.
     * @exception java.security.InvalidKeyException
     *                if the public key is not in the correct format.
     * @exception java.security.SignatureException
     *                if the signature is not in the correct format.
     */

    private static boolean verify(PublicKey key, byte[] data, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance(SIG_ALGORITHM);
        dsa.initVerify(key);
        dsa.update(data);
        return dsa.verify(signature);
    }

    /**
     * Creates a new chat server.
     *
     * @param file  the server property file, or <code>null</code> to use default
     *              property values.
     * @exception java.io.IOException
     *           if an I/O error occurs starting the chat server.
     */

    public Main(File file) throws Exception {
        // System.exit fails when we run finalizers on exit using Solaris
        // JDK 1.1.6_03, and shutdown won't work unless we call System.exit.
        // Also, the Kaffe Virtual Machine cannot handle finalizers on exit.
        // System.runFinalizersOnExit(true);
        System.out.println(Build.CHAT_SERVER_TITLE);
        System.out.println(Build.COPYRIGHT);
        String path = null;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            path = file.getAbsolutePath();
        }
        System.out.println(Message.format(Msg.LOADING_PROPERTIES, path));

        this.value         = new Value(file);           // Read in property values
        this.license       = value.getLicense();        // Save license information
        this.administrator = new Administrator(value);  // Create public Web interface

        // Print license information.
        System.out.println(license.getHostName() + ":" + license.getPort() +
                           " (" + license.getHostAddress() + ":" + license.getPort() + ")" + " " + license + ".");
        if (license.getDate() != null) {
            // Calendar doesn't work on Microsoft SDK for Java 2.01.
            Date expire = license.getDate();
            Date today  = new Date();
            if (today.before(expire)) {
                System.out.println(Message.format(Msg.EXPIRES, expire));
            } else {
                System.err.println(Message.format(Msg.EXPIRED, expire));
                System.err.println(Msg.NEW_LICENSE);
                return;
            }
        }

        Connection.verbose = value.serverVerbose;
        Log.verbose        = value.serverVerbose;
        Packet.trace       = value.serverTrace;

        writeSupport(file, value);          // Write support information
        /*
            Log.error  = openStream(value.logError);    // Open error log
            Log.access = openWriter(value.logAccess);   // Open access log
            Log.pub    = openWriter(value.logPublic);   // Open public room log
            Log.pvt    = openWriter(value.logPrivate);  // Open private room log
        */

        FileLogger logger = null;
        if (value.logDirectory.length() > 0 && value.logErrorPrefix.length() > 0) {
            logger = new FileLogger();
            logger.setDirectory(value.logDirectory);
            logger.setPrefix(value.logErrorPrefix);
            logger.setSuffix(value.logErrorSuffix);
            logger.start();
            Log.error = logger;
        }
        if (value.logDirectory.length() > 0 && value.logAccessPrefix.length() > 0) {
            logger = new FileLogger();
            logger.setDirectory(value.logDirectory);
            logger.setPrefix(value.logAccessPrefix);
            logger.setSuffix(value.logAccessSuffix);
            logger.start();
            Log.access = logger;
        }
        if (value.logDirectory.length() > 0 && value.logPublicPrefix.length() > 0) {
            logger = new FileLogger();
            logger.setDirectory(value.logDirectory);
            logger.setPrefix(value.logPublicPrefix);
            logger.setSuffix(value.logPublicSuffix);
            logger.start();
            Log.pub = logger;
        }
        if (value.logDirectory.length() > 0 && value.logPrivatePrefix.length() > 0) {
            logger = new FileLogger();
            logger.setDirectory(value.logDirectory);
            logger.setPrefix(value.logPrivatePrefix);
            logger.setSuffix(value.logPrivateSuffix);
            logger.start();
            Log.pvt = logger;
        }
        if (value.logDirectory.length() > 0 && value.logBannedPrefix.length() > 0) {
            logger = new FileLogger();
            logger.setDirectory(value.logDirectory);
            logger.setPrefix(value.logBannedPrefix);
            logger.setSuffix(value.logBannedSuffix);
            logger.start();
            Log.ban = logger;
        }

        serverSocket = new ServerSocket(license.getPort(), value.serverBacklog, license.getInetAddress());
        publicList   = new PublicList(PUBLIC_LIST,   Thread.MIN_PRIORITY, value.roomSweepInterval * MILLIS_PER_MINUTE);
        personalList = new PublicList(PERSONAL_LIST, Thread.MIN_PRIORITY, value.roomSweepInterval * MILLIS_PER_MINUTE);
        privateList  = new Grouptable(PRIVATE_LIST,  Thread.MIN_PRIORITY, value.roomSweepInterval * MILLIS_PER_MINUTE);
        memberMonitorList = new Hashtable();        // 2.1.8

        // Get pseudo random number generator for creating random bytes for clients
        // to sign.  Get the public key for verifying client signatures.
        random = new Random();
        AppletKeyPairGenerator generator = new AppletKeyPairGenerator();
        publicKey = generator.getPublicKey();

        // Create all permanent rooms.
        for (int i = 0; i < value.roomList.length; i++) {
            String roomName = value.roomList[i];
            if (roomName.length() > value.lengthRoomname) {
                roomName = roomName.substring(0, value.lengthRoomname).trim();
            }
            PublicChat room = new PublicChat(value, privateList, roomName, "");
            publicList.add(roomName, room);
            room.incrementGuest();            // So room will never be removed
        }

        // If transcribing permanent rooms, attach any event logging observers.
        if (value.transcribeRoomPermanent) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat room    = (PublicChat) enumeration.nextElement();
                File       logFile = new File(value.logChatPublicDir, room.name() + value.logChatPublicSuffix);
                room.addObserver(new EventLogger(logFile, value.formatChatPublic));
            }
        }

        // If transcribing private chat sessions, create the global private event
        // logging observer.
        if (value.transcribeRoomPrivate) {
            privateChatLogger = new EventLogger(value.logChatPrivate, value.formatChatPrivate);
        }

        // Start up the status reporter thread.  Set its priority to the maximum so
        // that it is sure to report at regular intervals.
        // Record a history of status reports for the public Web interface.
        reporter = new StatusReporter(value.statusInterval, publicList, personalList, privateList);
        recorder = new StatusRecorder(value.statusHistory);
        reporter.addObserver(recorder);
        reporter.start(Thread.currentThread().getThreadGroup(), Thread.MAX_PRIORITY);

        // Redirect error messages to the error log file if defined.
        if (Log.error != null)
            // System.setErr(Log.error);
            // Create print stream with autoflush.
        {
            System.setErr(new PrintStream(new Log((FileLogger) Log.error), true));
        }

        // Start up administrative server if specified.
        if (value.adminPort > 0 && value.adminPassword.length() > 0) {
            new AdminServer(this, value, reporter);
        }

        // Start up main server connection accepting thread.  Leave the connection
        // accepting thread as the only non-daemon thread (except perhaps for the
        // servlet runner).
        listener = new Thread(this, LISTENER_NAME);
        if (! Build.IS_BENCHMARK) {
            listener.setPriority(Thread.MIN_PRIORITY);
        }
        listener.start();

        // Start up the embedded servlet runner if specified.  Do this last since
        // some servlets may rely on the "server" static variable being set when
        // this constructor returns.
        if (! Build.IS_BENCHMARK && value.servletPort > 0) {
            runner = new ServletRunner(value);
            runner.start();
        }
    }

    /**
     * Shuts down the server.
     */

    public void shutdown() {
        // Make sure we do this only once, since this method is called when the
        // server is told to shut down and again when this object is finalized.
        if (listener != null) {
            Log.printError(Msg.SHUTTING_DOWN);

            // Shut down embedded servlet runner.
            if (runner != null) {
                runner.stop();
            }

            // Interrupting might be ignored, but should get:
            //   (Windows) java.io.InterruptedIOException: Thread interrupted
            //   (Unix)    java.io.InterruptedIOException: operation interrupted
            // It has no effect on the thread with JDK 1.1.5 on Windows NT and OS/2,
            // and it takes a second to have an effect with SDK 2.01 on Windows NT.
            Thread thread = listener;
            listener = null;
            thread.interrupt();               // First try to interrupt thread

            // If we close the server socket using SDK 2.01 before the thread has been
            // interrupted in its accept method, the JVM destroys all threads and
            // exits without any cleanup.  We need this delay with SDK 2.01 in order
            // to give time for the thread to be interrupted before trying to close
            // the server socket.
            try {
                Thread.sleep(INTERRUPT_DELAY);  // Delay for SDK 2.01
            } catch (InterruptedException e) {}

            // Since the thread interrupt has no effect with JDK 1.1.5 on Windows NT
            // and OS/2, we need to go ahead and close the server socket in order to
            // break the thread off its accept method.  We'll then get:
            //   (Windows) java.net.SocketException: socket was closed
            //   (Unix)    java.net.SocketException: Socket closed
            if (serverSocket != null) {
                try {
                    serverSocket.close();         // Close socket in case interrupt ignored
                } catch (IOException e) {}
                serverSocket = null;
            }
        }
    }

    /**
     * Ensures that all clients are running on the local host.
     *
     * @param socket  the socket connection to the client.
     * @exception java.io.IOException
     *           if the client is not running on the same host as this server.
     */

    private void ensureLocal(Socket socket) throws IOException {
        InetAddress address = socket.getInetAddress();
        if (! address.equals(InetAddress.getLocalHost())) {
            throw new IOException(Msg.LOCAL_ONLY);
        }
    }

    /**
     * The body of the main chat server thread.  This method accepts and starts up
     * new client connections.
     */

    public void run() {
        try {
            while (listener != null) {
                try {
                    Connection.inTurnstile(value.serverLimit);    // Enter through turnstile
                    Socket socket = serverSocket.accept();
                    if (Build.IS_BENCHMARK) {
                        socket.setTcpNoDelay(true);    // Disable Nagle algorithm for benchmark
                    }
                    if (Build.MARK_LIMITED) {
                        ensureLocal(socket);
                    }
                    if (value.serverTimeout > 0) {
                        socket.setSoTimeout(value.serverTimeout * MILLIS_PER_MINUTE);
                    }
                    Connection connection = new Connection(socket, true);
                    connection.addObserver(this);
                    connection.setAttribute(Attr.EXPECTED, FIRST_PACKET);
                    connection.startSending(Thread.NORM_PRIORITY);
                    connection.startReceiving(Thread.NORM_PRIORITY);
                    Thread.yield();       // Yield to other threads with the same priority (for Solaris)
                }
                // Common errors are:
                //   java.lang.OutOfMemoryError
                // Common exceptions (when running with server limit) are:
                //   java.net.SocketException: Protocol error
                // Exceptions when shutting down are:
                //   (SDK 2.01)  java.io.InterruptedIOException: Thread interrupted
                //   (JIT 1.1.3) java.net.SocketException: Interrupted system call
                //   (JDK 1.1.5) java.net.SocketException: socket was closed
                catch (IOException e) {
                    if (listener == null) {       // If shutting down
                        throw e;    // Rethrow so we can quit
                    }
                    // Otherwise, exit turnstile since the connection is not started.
                    Connection.outTurnstile();
                    Log.printError(Msg.BAD_CONNECTION, e);
                }
            }
        } catch (ThreadDeath e) {
            Log.printError(Message.format(Msg.UNEXPECTED, LISTENER_NAME), e);
            throw e;                          // Rethrow for cleanup
        } catch (Throwable t) {
            if (listener != null) {           // If not shutting down
                Log.printError(Message.format(Msg.UNEXPECTED, LISTENER_NAME), t);
            }
        } finally {
            Log.printError(Message.format(Msg.STOPPING, LISTENER_NAME));
            listener = null;
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
                serverSocket = null;
            }

            // This takes way too long -- just exit!
            //
            // Connection.shutdown();         // Stops all send and receive threads
            // Thread.currentThread().getThreadGroup().list();

            // Stop the file loggers.
            // Error file logger is stopped when System.err is closed.
            try {
                FileLogger logger = (FileLogger) Log.access;
                if (logger != null) {
                    logger.stop();
                }
                logger = (FileLogger) Log.pub;
                if (logger != null) {
                    logger.stop();
                }
                logger = (FileLogger) Log.pvt;
                if (logger != null) {
                    logger.stop();
                }
            } catch (LifecycleException e) {}

            // System.exit causes these errors from "net stop" on Windows NT:
            //   System error 109 has occurred.
            //   The pipe has been ended.
            System.exit(0);
        }
    }

    /**
     * Checks whether we received the packet we were expecting.
     *
     * @param connection  the connection to check.
     * @param packet      the packet we received.
     * @returns <code>true</code> if we got an expected packet; otherwise
     *          <code>false</code>.
     */

    private boolean isExpected(Connection connection, Packet packet) {
        boolean expected = false;
        int[]   list     = (int[]) connection.getAttribute(Attr.EXPECTED);
        if (list.length == 0) {
            expected = true;
        } else {
            for (int i = 0; i < list.length && ! expected; i++) {
                expected = list[i] == packet.getId();
            }
        }
        return expected;
    }

    /**
     * Called when a packet is received from the client, or when the connection to
     * the client times out or is closed.
     *
     * @param observable  the connection to the client.
     * @param object      the packet received from the client, or the timeout
     *                    exception if the client connection times out, or
     *                    <code>null</code> if the connection is closed.
     */

    public void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main update ...");
        }

        if (object instanceof Packet) {
            Packet packet = (Packet) object;
            if (! packet.isHandled()) {
                Connection connection = (Connection) observable;
                if (! isExpected(connection, packet)) {
                    packet.setHandled();
                    connection.close(HttpURLConnection.HTTP_BAD_REQUEST);
                } else if (packet instanceof UserList) {
                    userListRequest(connection, (UserList) packet);
                } else if (packet instanceof RoomList) {
                    roomListRequest(connection, (RoomList) packet);
                } else if (packet instanceof EnterRoom) {
                    enterRoomRequest(connection, (EnterRoom) packet);
                } else if (packet instanceof PasswordAccess) {  // Is an access request
                    passwordAccessRequest(connection, (PasswordAccess) packet);
                } else if (packet instanceof Access) {
                    accessRequest(connection, (Access) packet);
                } else if (packet instanceof Authenticate) {    // 2.2
                    authenticateRequest(connection, (Authenticate) packet);
                } else if (packet instanceof CreateRooms) {     // 2.1.8
                    createRoomsRequest(connection, (CreateRooms) packet);
                } else if (packet instanceof Kick) {            // 2.2.0
                    kickRequest(connection, (Kick) packet);
                } else if (packet instanceof PrivatePacket) {
                    PrivatePacket privatePacket = (PrivatePacket) packet;
                    if (privatePacket.getRoomId() == 0) {
                        chatRequest(connection, (Chat) packet);
                    } else {
                        privatePacket(connection, (PrivatePacket) packet);
                    }
                }
            }
        } else if (object instanceof InterruptedIOException) {
            timeout((Connection) observable);
        } else if (object == null) {
            nullObject((Connection) observable);
        }
    }

    /**
     * Accesses the room by creating the room if it doesn't exist and by adding
     * this connection as a guest of the room.
     *
     * @param connection    the connection to the client.
     * @param list          the list of public or personal rooms.
     * @param roomName      the name of the room in the list.
     * @param documentBase  the Web page containing the client applet.
     * @param setRoomName   set the room name attribute.
     */

    private void accessRoom(Connection connection, PublicList list, String roomName, String documentBase, boolean isEvent, boolean setRoomName) {
        if (setRoomName) {
            connection.setAttribute(Attr.ROOM_NAME, roomName);
        }

        // Synchronize on the room list so that:
        // (1) the room doesn't get created twice by two different guests, and
        // (2) the room doesn't get deleted between the time we get it and the
        //     time we add this new guest.
        synchronized (list) {
            PublicChat room = (PublicChat) list.get(roomName);
            if (room == null) {
                if (isEvent) {
                    room = new Auditorium(value, privateList, roomName, documentBase);
                    if (value.auditoriumsPermanent) {
                        room.incrementGuest();    // So room will never be removed
                    }
                    list.addFirst(roomName, room);
                } else {
                    room = new PublicChat(value, privateList, roomName, documentBase);
                    list.add(roomName, room);
                }

                // If transcribing events, dynamic rooms, or personal rooms, attach any
                // event logging observers.
                boolean transcribe = false;
                if (list == publicList && value.transcribeRoomDynamic) {
                    transcribe = true;
                }
                if (list == personalList && value.transcribeRoomPersonal) {
                    transcribe = true;
                }
                if (isEvent && value.transcribeRoomEvent) {
                    transcribe = true;
                }
                if (transcribe) {
                    File logFile = new File(value.logChatPublicDir, room.name() + value.logChatPublicSuffix);
                    try {
                        room.addObserver(new EventLogger(logFile, value.formatChatPublic));
                    } catch (IOException e) {
                        Log.printError(Message.format(Msg.WRITE_ERROR, logFile.getPath()), e);
                    }
                }
            }
            room.incrementGuest();    // So we don't delete room while client is connected
        }
    }

    /**
     * Get some random bytes for the client to sign.
     *
     * @return  random bytes to sign.
     */

    private byte[] getBytes(Connection connection) {
        byte[] bytes = new byte[RANDOM_BYTES_SIZE];
        random.nextBytes(bytes);
        connection.setAttribute(Attr.RANDOM_BYTES, bytes);  // Save for verifying signature
        return bytes;
    }

    /**
     * Handles a password access request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the password access request.
     */

    private void passwordAccessRequest(Connection connection, PasswordAccess request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main passwordAccessRequest ...");
        }

        // 2.1.10 - Allow only one access request per connection.
        if (connection.getAttribute(Attr.ACCESS_REQ) != null) {
            request.setHandled();
            connection.close(HttpURLConnection.HTTP_BAD_REQUEST); // Not our VolanoChat client
            return;
        }

        connection.setAttribute(Attr.ACCESS_REQ, request);  // Save information for disconnect log entry
        String  documentBase   = request.getDocumentBase();
        String  memberName     = request.getMemberName();
        String  memberPassword = request.getMemberPassword();
        String  password       = request.getPassword();
        String  memberProfile  = "";
        boolean isEvent        = false;

        int result = Access.OKAY;                   // Assume access is allowed
        if (! value.duplicateAddresses && Connection.isDuplicate(connection)) {
            result = Access.HOST_DUPLICATE;
        } else if (! AccessControl.isHostAllowed(connection.getHostAddress()) ||
                   DNSBlacklist.contains(DNSBlacklist.getDenied(), connection.getHostAddress())) {
            result = Access.HOST_DENIED;    // Host is denied
        } else if (! AccessControl.isReferrerAllowed(AccessControl.cleanReferrer(documentBase))) {
            result = Access.DOCUMENT_DENIED;    // Referrer is denied
        }

        // Allow public room access only to VolanoChat and VolanoChatPro.
        else if (request.getAppletVersion().equals(Access.PUBLIC_VERSION) && ! license.isVolanoChatSP()) {
            connection.setAttribute(Attr.CLIENT_VER, Access.PUBLIC_VERSION);
            if (request.isMember()) {
                StringBuffer profile = new StringBuffer();
                StringBuffer link    = new StringBuffer();
                // If the member access script is defined, and the user has given a
                // valid member name and password:
                //   Mark this connection with its member name.
                String defaultRoom = request.getDefaultRoom();
                if (value.memberAccess.toPattern().length() > 0 &&
                        isMember(value.memberVersion, value.memberAccess, memberName, memberPassword, defaultRoom, profile, link)) {
                    connection.setAttribute(Attr.MEMBER_NAME, memberName);
                    connection.setAttribute(Attr.MEMBER_LINK, link.toString());
                    memberProfile = profile.toString();
                    // 2.1.8 - Check whether this member is also a monitor.
                    String lookupName = value.memberMonitorMatchcase ? memberName : memberName.toLowerCase();
                    if (value.memberMonitorTable.containsKey(lookupName)) {
                        connection.setBoolean(Attr.IS_MONITOR);
                        if (! value.memberMonitorMultiuse) {
                            Connection oldConnection = (Connection) memberMonitorList.put(lookupName, connection);
                            if (oldConnection != null) {
                                String kickerID = connection.getHostAddress() + "/" + lookupName;
                                oldConnection.setAttribute(Attr.IS_MONITOR, new Boolean(false));
                                oldConnection.close(HttpURLConnection.HTTP_CONFLICT, kickerID);
                            }
                        }
                    }
                } else {
                    result = Access.BAD_PASSWORD;
                }
            }

            // Check for VolanoChatPro and authorized access for event moderation.
            if (result == Access.OKAY && request.isStage()) {
                if (value.entranceStage.length() > 0 && documentBase.startsWith(value.entranceStage)) {
                    isEvent = true;
                    connection.setBoolean(Attr.IS_STAGE); // Client is on stage
                    documentBase = request.getTopic();    // Replace link with event topic
                } else {
                    result = Access.DOCUMENT_DENIED;    // Not Pro or not stage entrance
                }
            }

            // Check authorized access for monitors and administrators.
            if (result == Access.OKAY) {
                if (request.isAdmin()) {
                    // 2.1.10 - Check that password is even defined.
                    if (value.adminPassword.length() > 0 && value.adminPassword.equals(password)) {
                        connection.setBoolean(Attr.IS_ADMIN);
                        connection.setBoolean(Attr.IS_MONITOR);
                    } else {
                        result = Access.BAD_PASSWORD;
                    }
                } else if (request.isMonitor()) {
                    // 2.1.10 - Check that password is even defined.
                    if (value.serverPassword.length() > 0 && value.serverPassword.equals(password)) {
                        connection.setBoolean(Attr.IS_MONITOR);
                    } else {
                        result = Access.BAD_PASSWORD;
                    }
                }
            }

            if (result == Access.OKAY) {
                String roomName = request.getDefaultRoom();
                if (roomName.length() > 0 && ! value.roomNodynamic) {
                    accessRoom(connection, publicList, roomName, documentBase, isEvent, true);
                }
                if (value.clientAuthentication) {
                    request.confirm(result, getBytes(connection), memberProfile);
                    connection.setAttribute(Attr.EXPECTED, SECOND_PACKET);
                } else {
                    request.confirm(result, publicList.getNames(), memberProfile);
                    connection.setAttribute(Attr.EXPECTED, ANY_PACKET);
                }
            }
        } else {
            result = Access.VERSION_DENIED;    // Wrong applet version
        }

        request.setHandled();
        connection.setStatus(getHttpStatus(result));
        if (result != Access.OKAY) {
            request.confirm(result);
        }
        send(connection, request);

        // Don't rely on the client closing the connection.
        if (result != Access.OKAY) {
            Thread.yield();
            connection.close();
        }
    }

    /**
     * Handles an access request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the access request.
     */

    private void accessRequest(Connection connection, Access request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main accessRequest ...");
        }

        // 2.1.10 - Allow only one access request per connection.
        if (connection.getAttribute(Attr.ACCESS_REQ) != null) {
            request.setHandled();
            connection.close(HttpURLConnection.HTTP_BAD_REQUEST); // Not our VolanoChat client
            return;
        }

        connection.setAttribute(Attr.ACCESS_REQ, request);  // Save information for disconnect log entry
        String documentBase = request.getDocumentBase();

        int result = Access.OKAY;                           // Assume access is allowed
        if (! value.duplicateAddresses && Connection.isDuplicate(connection)) {
            result = Access.HOST_DUPLICATE;
        } else if (! AccessControl.isHostAllowed(connection.getHostAddress()) ||
                   DNSBlacklist.contains(DNSBlacklist.getDenied(), connection.getHostAddress())) {
            result = Access.HOST_DENIED;    // Host is denied
        } else if (! AccessControl.isReferrerAllowed(AccessControl.cleanReferrer(documentBase))) {
            result = Access.DOCUMENT_DENIED;          // Referrer is denied
        } else if (value.memberOnly) {
            result = Access.VERSION_DENIED;    // Only password access allowed
        }

        // Allow public room access only to VolanoChat and VolanoChatPro.
        else if (request.getAppletVersion().equals(Access.PUBLIC_VERSION) && ! license.isVolanoChatSP()) {
            connection.setAttribute(Attr.CLIENT_VER, Access.PUBLIC_VERSION);
            String roomName = request.getDefaultRoom();
            if (roomName.length() > 0 && ! value.roomNodynamic) {
                accessRoom(connection, publicList, roomName, documentBase, false, true);
            }
            if (value.clientAuthentication) {    // VolanoChat, WebVolanoChat
                request.confirm(result, getBytes(connection));
                connection.setAttribute(Attr.EXPECTED, SECOND_PACKET);
            } else {
                request.confirm(result, publicList.getNames());
                connection.setAttribute(Attr.EXPECTED, ANY_PACKET);
            }
        }

        // Allow personal room access only to VolanoChatPro and VolanoChatSP.
        else if (request.getAppletVersion().equals(Access.PERSONAL_VERSION) && ! license.isVolanoChat()) {
            connection.setAttribute(Attr.CLIENT_VER, Access.PERSONAL_VERSION);
            if (documentBase.length() > 0) {
                accessRoom(connection, personalList, documentBase, documentBase, false, true);
            }
            if (value.clientAuthentication) {    // MyVolanoChat
                request.confirm(result, getBytes(connection));
                connection.setAttribute(Attr.EXPECTED, SECOND_PACKET);
            } else {
                request.confirm(result, new String[0]);
                connection.setAttribute(Attr.EXPECTED, ANY_PACKET);
            }
        } else {
            result = Access.VERSION_DENIED;    // Wrong applet version
        }

        request.setHandled();
        connection.setStatus(getHttpStatus(result));
        if (result != Access.OKAY) {
            request.confirm(result);
        }
        send(connection, request);

        // Don't rely on the client closing the connection here, too! (version 2.2)
        if (result != Access.OKAY) {
            Thread.yield();
            connection.close();
        }
    }

    /**
     * Handles an authenticate request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the authenticate request.
     */

    private void authenticateRequest(Connection connection, Authenticate request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main authenticateRequest ...");
        }

        boolean valid     = false;
        byte[]  data      = (byte[]) connection.getAttribute(Attr.RANDOM_BYTES);
        byte[]  signature = request.getSignature();

        request.setHandled();
        if (signature.length == 0) {       // Sent by clients without java.security
            connection.setStatus(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
        // Signature length is read as an unsigned byte, so its value will always be
        // between 0 and 255.  A null value for "data" should never happen.
        // else if (data == null || signature.length < 0 || signature.length > SIG_MAX_LENGTH)
        else if (data == null) {
            connection.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        } else {
            try {
                valid = verify(publicKey, data, signature);
            } catch (Exception e) {}
            if (! valid) {
                connection.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            } else {
                request.confirm(Authenticate.OKAY, publicList.getNames());
                connection.setStatus(HttpURLConnection.HTTP_OK);
                connection.setAttribute(Attr.EXPECTED, ANY_PACKET);
                send(connection, request);
            }
        }

        // Don't rely on the client closing the connection. (version 2.2)
        if (! valid) {
            Thread.yield();
            connection.close();
        }
    }

    /**
     * Handles an enter room request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the enter room request.
     */

    private void enterRoomRequest(Connection connection, EnterRoom request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main enterRoomRequest ...");
        }

        String roomName = request.getRoomName();
        String userName = request.getUserName();
        String profile  = request.getProfile();
        // Don't forget these room names can be document base URLs, so make sure the
        // maximum length is not too small for any reasonable URL specification.
        if (roomName.length() > value.lengthRoomname) {
            request.setHandled();
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);        // Not our VolanoChat Client
        }
        // If the member name script is defined, the user is not entering with his
        // or her member name, and the name is taken by another member:
        //   Reject the connection.
        else if (value.memberName.toPattern().length() > 0 &&
                 ! userName.equalsIgnoreCase((String) connection.getAttribute(Attr.MEMBER_NAME)) &&
                 isMember(value.memberVersion, value.memberName, userName, roomName)) {
            request.setHandled();
            request.confirm(EnterRoom.MEMBER_TAKEN);
            send(connection, request);
        } else {
            String client = (String) connection.getAttribute(Attr.CLIENT_VER);
            if (client != null) {
                if (client.equals(Access.PUBLIC_VERSION)) {
                    // Synchronize on the room list so that the room doesn't get deleted
                    // between the time we get it and the time we add this new user.
                    synchronized (publicList) {
                        PublicChat room = (PublicChat) publicList.get(roomName);
                        if (room == null) {
                            request.setHandled();
                            request.confirm(EnterRoom.NO_SUCH_ROOM);
                            send(connection, request);
                        } else {
                            room.update(connection, request);    // Pass on to public room
                        }
                    }
                } else if (client.equals(Access.PERSONAL_VERSION)) {
                    synchronized (personalList) {
                        PublicChat room = (PublicChat) personalList.get(roomName);
                        if (room == null) {
                            request.setHandled();
                            request.confirm(EnterRoom.NO_SUCH_ROOM);
                            send(connection, request);
                        } else {
                            room.update(connection, request);    // Pass on to personal room
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles a room list request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the room list request.
     */

    private void roomListRequest(Connection connection, RoomList request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main roomListRequest ...");
        }

        request.setHandled();
        request.confirm(publicList.getNames(request.getFilter()));
        send(connection, request);
    }

    /**
     * Handles a user list request from the client.
     *
     * @param connection  the connection to the client.
     * @param request     the user list request.
     */

    private void userListRequest(Connection connection, UserList request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main userListRequest ...");
        }

        String roomName = request.getRoomName();
        if (roomName.length() > value.lengthRoomname) {
            request.setHandled();
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);        // Not our VolanoChat Client
        } else {
            PublicChat room = (PublicChat) publicList.get(roomName);
            if (room == null) {
                request.setHandled();
                request.confirm(UserList.NO_SUCH_ROOM);
                send(connection, request);
            } else {
                room.update(connection, request);    // Pass on to room
            }
        }
    }

    /**
     * 2.1.9 - Handles a create rooms request.
     *
     * @param connection  the connection to the client.
     * @param request     the create rooms request.
     */

    private void createRoomsRequest(Connection connection, CreateRooms request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main createRooms ...");
        }

        request.setHandled();
        if (connection.getBoolean(Attr.IS_ADMIN)) {
            String[] roomNames = request.getRoomNames();
            for (int i = 0; i < roomNames.length; i++) {
                if (roomNames[i].length() > value.lengthRoomname) {
                    roomNames[i] = roomNames[i].substring(0, value.lengthRoomname).trim();
                }
                accessRoom(connection, publicList, roomNames[i], "", false, false);
            }

            Vector list = (Vector) connection.getAttribute(Attr.ROOM_LIST);
            if (list == null) {
                list = new Vector();
            }
            list.addElement(roomNames);
            connection.setAttribute(Attr.ROOM_LIST, list);
        } else {
            connection.close();
        }
    }

    /**
     * 2.2.0 - Handles a kick request.
     *
     * @param connection  the connection to the client.
     * @param request     the kick request.
     */

    private void kickRequest(Connection connection, Kick request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main kickRequest ...");
        }

        request.setHandled();
        String address = request.getUserAddress();
        int    method  = request.getMethod();
        if (connection.getBoolean(Attr.IS_MONITOR) && address.length() > 0
                && (method == Kick.KICK || method == Kick.BAN)) {
            String memberName = (String) connection.getAttribute(Attr.MEMBER_NAME);
            String monitorId = memberName != null ? memberName :
                               connection.getHostAddress() + "/" + request.getKickerName();

            boolean addressInUseByMonitor = false;
            Connection[] list = Connection.get(address);
            for (int i = 0; i < list.length; i++) {
                if (list[i].getBoolean(Attr.IS_MONITOR)) {
                    addressInUseByMonitor = true;
                } else {
                    list[i].close(HttpURLConnection.HTTP_FORBIDDEN, monitorId);
                }
            }
            if (method == Kick.BAN && ! addressInUseByMonitor) {
                AccessControl.banHost(address, request.getRoomName(), request.getUserName(), monitorId);
            }
        }
    }

    /**
     * Handles a broadcast chat request from the administrative client.
     *
     * @param connection  the connection to the client.
     * @param request     the broadcast chat request.
     */

    private void chatRequest(Connection connection, Chat request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main chatRequest ...");
        }

        // 2.1.8 - Make sure the chat room name of a broadcast message is empty.
        // Otherwise, any chat message received on an administrator connection for a
        // room the administrator has not entered will end up a broadcast message.
        // if (connection.getBoolean(Attr.IS_ADMIN)) {
        if (connection.getBoolean(Attr.IS_ADMIN) && request.getRoomName().length() == 0) {
            broadcast(publicList.snapshot(), connection, request);
            broadcast(personalList.snapshot(), connection, request);
            request.setHandled();
        }
    }

    /**
     * Handles a private chat room packet.
     *
     * @param connection  the connection to the client.
     * @param request     the private chat room packet.
     */

    private void privatePacket(Connection connection, PrivatePacket packet) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main privatePacket ...");
        }

        // This method catches any private chat packets on connections that are not
        // yet being observed by their private chat rooms.  The private chat room
        // starts observing the connection once this packet has been redirected to
        // its correct destination.
        //
        // This allows the private chat room itself to avoid adding both connections
        // as an observer at the same time, which causes a deadlock under JDK 1.0.2
        // since the receive thread's notifyObservers method synchronizes on the
        // connection object.  Such synchronization has been removed in JDK 1.1.5,
        // but we'll leave this logic here for now, just in case.

        PrivateChat room = (PrivateChat) privateList.get(new Integer(packet.getRoomId()));
        if (room != null) {
            connection.addObserver(room);
            room.update(connection, packet);
        }
    }

    /**
     * Handles a timeout on the connection.
     *
     * @param connection  the connection to the client.
     */

    private void timeout(Connection connection) {
        if (connection.getTimeouts() < TIMEOUT_LIMIT) {
            send(connection, new Ping());
        } else {
            connection.close(HttpURLConnection.HTTP_CLIENT_TIMEOUT);
        }
    }

    /**
     * Handles the <code>null</code> object indicating that the connection is
     * closed.
     *
     * @param connection  the connection to the client.
     */

    private void nullObject(Connection connection) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Main nullObject ...");
        }

        connection.deleteObserver(this);

        String roomName = (String) connection.getAttribute(Attr.ROOM_NAME);
        if (roomName != null) {
            PublicChat room = (PublicChat) publicList.get(roomName);
            if (room != null) {               // Guest in public room disconnected
                room.decrementGuest();
            } else {
                room = (PublicChat) personalList.get(roomName);
                if (room != null) {             // Guest in personal room disconnected
                    room.decrementGuest();
                }
            }
        }

        // 2.1.9 - Check for lists of rooms created with one or more calls to
        // Server.createRooms.
        Vector list = (Vector) connection.getAttribute(Attr.ROOM_LIST);
        if (list != null) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                String[] names = (String[]) enumeration.nextElement();
                for (int i = 0; i < names.length; i++) {
                    PublicChat room = (PublicChat) publicList.get(names[i]);
                    if (room != null) {
                        room.decrementGuest();
                    }
                }
            }
        }

        // 2.1.8 - Check for member monitors signing off.
        if (connection.getBoolean(Attr.IS_MONITOR) && ! value.memberMonitorMultiuse) {
            String name = (String) connection.getAttribute(Attr.MEMBER_NAME);
            if (name != null) {
                if (! value.memberMonitorMatchcase) {
                    name = name.toLowerCase();
                }
                memberMonitorList.remove(name);
            }
        }

        if (Log.access != null) {
            Access request = (Access) connection.getAttribute(Attr.ACCESS_REQ);
            if (request != null) {
                writeLog(connection, request);
            }
        }
    }

    /**
     * Gets the HTTP status code corresponding to the access result code.
     *
     * @param code  the access confirmation result code.
     * @return  the corresponding HTTP status code.
     */

    private int getHttpStatus(int code) {
        // The following status codes are reported in the access log file each time
        // a connection is closed.
        //
        //   HttpURLConnection.HTTP_OK = 200
        //     The connection was closed normally by the client.
        //
        //   HttpURLConnection.HTTP_BAD_REQUEST = 400
        //     The client is not a Volano applet and sent a request which does not
        //     conform to the VolanoChat protocol, such as:
        //       - The first request was not an Access request.
        //       - The second request was not an Authenticate request (when
        //         authetication is enabled).
        //       - More than one Access request or PasswordAccess request was sent
        //         by the client on the connection.
        //       - The digital signature on an Authenticate request was not valid,
        //         had a negative length, or was more than 100 bytes in length.
        //       - The user name had length zero in an enter room request.
        //       - The client tried to enter the same room twice.
        //       - The user name or chat text had length zero in a chat message.
        //
        //   HttpURLConnection.HTTP_UNAUTHORIZED = 401
        //     The administrator, monitor or member password sent to the VolanoChat
        //     server is not valid.
        //
        //   HttpURLConnection.HTTP_FORBIDDEN = 403
        //     The host name (or IP address) or referring Web page address are
        //     denied access to the server, or the connection was closed with a kick
        //     or ban from a monitor or administrator.  If closed from a kick or
        //     ban, the kicker id is recorded in the access log record.
        //     Also used when the client requested to enter on a stage entrance with
        //     an address not starting with that defined by "entrance.stage", or
        //     sent such a request to a non-Pro VolanoChat server.
        //
        //   HttpURLConnection.HTTP_NOT_ACCEPTABLE = 406
        //      The client sent a zero-length signature in the authentication
        //      request.  A zero-length signature is sent by Volano clients when
        //      the java.security package is not available in the client's Java VM.
        //
        //   HttpURLConnection.HTTP_CLIENT_TIMEOUT = 408
        //     The client timed out (a dead session timeout) by not responding to
        //     internal ping requests.
        //
        //   HttpURLConnection.HTTP_CONFLICT = 409
        //     A member-monitor signed into the server a second time, so the first
        //     connection was closed (when member.monitor.multiuse=false).  The
        //     member name and host name are recorded as the kicker id in the
        //     access log record.
        //     Or a host tried to connect when another connection already exists
        //     with the same address and "allow.duplicate.addresses=false".
        //
        //   HttpURLConnection.HTTP_ENTITY_TOO_LARGE = 413
        //     The client sent a request with a field too large:
        //       - room name with length greater than length.roomname,
        //       - user name with length greater than length.username,
        //       - profile with length greater than length.profile,
        //       - text message with length greater than length.chattext, or
        //       - the "from" or "to" name with length greater than length.username.
        //
        //   HttpURLConnection.HTTP_INTERNAL_ERROR = 500
        //     The receive or send thread caught an unexpected java.lang.Throwable,
        //     in which case the details can be found in the error log file.
        //     This status is also reported if this method is called with an
        //     unrecognized access return code.
        //
        //   HttpURLConnection.HTTP_UNAVAILABLE = 503
        //     The connection was closed because the VolanoChat server is shutting
        //     down.
        //
        //   HttpURLConnection.HTTP_VERSION = 505
        //     The applet version sent on the Access or PasswordAccess request is
        //     not compatible with this VolanoChat server.  Specifically, the server
        //     received:
        //       - a PasswordAccess request with version not "2.5.0",
        //       - an Access request with "member.only=true",
        //       - an Access request with version not "2.5.0" nor "2.5.0p".

        int status = HttpURLConnection.HTTP_INTERNAL_ERROR; // 500
        switch (code) {
        case Access.OKAY:
            // Access is granted.
            status = HttpURLConnection.HTTP_OK;             // 200
            break;
        case Access.HOST_DENIED:
            // The access control denies the host name or IP address.
            status = HttpURLConnection.HTTP_FORBIDDEN;      // 403
            break;
        case Access.DOCUMENT_DENIED:
            // The access control denies the referring Web page address.
            // Requested to enter on a stage entrance with an address not starting
            // with that defined by "entrance.stage", or sent such a request to a
            // non-Pro VolanoChat server.
            status = HttpURLConnection.HTTP_FORBIDDEN;      // 403
            break;
        case Access.VERSION_DENIED:
            // A PasswordAccess request with version not "2.5.0".
            // An Access request with "member.only=true".
            // An Access request with version not "2.5.0" nor "2.5.0p".
            status = HttpURLConnection.HTTP_VERSION;        // 505
            break;
        case Access.BAD_PASSWORD:
            // The member, monitor or administrator password is invalid.
            status = HttpURLConnection.HTTP_UNAUTHORIZED;   // 401
            break;
        case Access.BAD_JAVA_VERSION:
            // Authentication is required but not available on client.
            status = HttpURLConnection.HTTP_NOT_ACCEPTABLE; // 406
            break;
        case Access.HOST_DUPLICATE:
            // Host address already connected.
            status = HttpURLConnection.HTTP_CONFLICT;       // 409
            break;
        default:
            status = HttpURLConnection.HTTP_INTERNAL_ERROR; // 500
            break;
        }
        return status;
    }

    /**
     * Sends a packet on a connection.
     *
     * @param connection  the connection on which to send the packet.
     * @param packet      the packet to send.
     */

    private void send(Connection connection, Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {}  // Error means connection is closed -- ignore
    }

    /**
     * Broadcasts the chat request to the list of public or personal chat rooms.
     *
     * @param list        the list of public or personal chat rooms.
     * @param connection  the connection on which this broadcast chat request was
     *                    received.
     * @param request     the chat request to broadcast.
     */

    private void broadcast(Observer[] list, Connection connection, Chat request) {
        for (int i = 0; i < list.length; i++) {
            PublicChat room = (PublicChat) list[i];
            Chat       copy = (Chat) request.clone();
            copy.setRoomName(room.name());
            room.update(connection, copy);
        }
    }

    /**
     * Writes an entry to the access log file.
     *
     * @param connection  the connection to be logged.
     * @param request     the access request associated with this connection.
     */

    private void writeLog(Connection connection, Access request) {
        try {
            if (value.formatAccess.toPattern().length() > 0) {
                String agentInfo = "";
                if (value.formatAccessAgent.toPattern().length() > 0) {
                    Object[] agent = new Object[Default.AGENT_SIZE];
                    agent[Default.AGENT_JAVA_VENDOR]        = request.getJavaVendor();
                    agent[Default.AGENT_JAVA_VERSION]       = request.getJavaVersion();
                    agent[Default.AGENT_JAVA_CLASS_VERSION] = request.getJavaClassVersion();
                    agent[Default.AGENT_OS_NAME]            = request.getOsName();
                    agent[Default.AGENT_OS_VERSION]         = request.getOsVersion();
                    agent[Default.AGENT_OS_ARCH]            = request.getOsArch();
                    agent[Default.AGENT_JAVA_VENDOR_URL]    = request.getJavaVendorUrl();
                    agentInfo = value.formatAccessAgent.format(agent);
                }

                String extraInfo = "";
                if (value.formatAccessExtra.toPattern().length() > 0) {
                    int    duration = Math.round((connection.getEndTime() - connection.getStartTime()) / 1000.0f);
                    String kicker   = connection.getKicker();

                    Object[] extra = new Object[Default.EXTRA_SIZE];
                    extra[Default.DURATION]      = new Integer(duration);
                    extra[Default.DOCUMENT_HOST] = getURLHost(request.getDocumentBase());
                    extra[Default.CODE_HOST]     = getURLHost(request.getCodeBase());
                    extra[Default.CONNECTIONS]   = new Integer(Connection.getCount());
                    extra[Default.PUBLIC]        = new Integer(publicList.size());
                    extra[Default.PRIVATE]       = new Integer(privateList.size());
                    extra[Default.MONITOR]       = kicker.length() == 0 ? "-" : kicker;
                    extraInfo = value.formatAccessExtra.format(extra);
                }

                String memberName = (String) connection.getAttribute(Attr.MEMBER_NAME);
                Object[] log = new Object[Default.FORMAT_SIZE];
                log[Default.REMOTE_HOST] = connection.getHostAddress();
                log[Default.DATE]        = value.format(new Date(connection.getEndTime()));
                log[Default.CODE_BASE]   = request.getCodeBase();
                log[Default.VERSION]     = request.getAppletVersion();
                log[Default.STATUS]      = new Integer(connection.getStatus());
                log[Default.BYTES]       = new Long(connection.getBytesSent());
                log[Default.REFERRER]    = request.getDocumentBase();
                log[Default.USER_AGENT]  = agentInfo;
                log[Default.EXTRA]       = extraInfo;
                log[Default.REMOTE_USER] = memberName == null ? "-" : memberName;
                // Log.access.println(value.formatAccess.format(log));
                Log.access.log(value.formatAccess.format(log));
            }
        } catch (IllegalArgumentException e) {
            Log.printError(Msg.BAD_ACCESS_FORMAT, e);
        }
    }

    /**
     * Gets the host name in a Web address.
     *
     * @param href  the URL from which to obtain the host name.
     * @return  the host name found in the URL, or "-" if the host name is an
     *          empty string or the URL is malformed.
     */

    private String getURLHost(String href) {
        String hostname = "";
        try {
            hostname = new URL(href).getHost();
        } catch (MalformedURLException e) {}
        return hostname.length() == 0 ? "-" : hostname;
    }

    /**
     * Finalizes this object by stopping its threads and closing its sockets.
     *
     * @exception java.lang.Throwable  if an error occurs finalizing this object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        shutdown();
    }

    /**
     * Starts up the chat server.
     *
     * @param args  the optional name of the chat server properties file.
     */

    public static void main(String args[]) {
        if (args.length > 1) {
            System.err.println(Msg.SERVER_USAGE);
            return;
        }

        Connection.setPacketFactory(new ChatPacketFactory());
        try {
            String path = args.length == 1 ? args[0] : PROPERTIES;
            File   file = new File(path);
            if (! file.isAbsolute()) {
                file = new File(System.getProperty(Key.INSTALL_ROOT), path);
            }

            if (Build.MARK_LIMITED) {
                server = new Main(null);
            } else {
                server = new Main(file);
            }
        } catch (Exception e) {
            System.err.println(Message.format(Msg.CANNOT_START, e.toString()));
            // e.printStackTrace(System.err);
        }
    }
}
