/*
 * License.java - a class for storing the server license.
 * Copyright (C) 2001 John Neffenger
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
import  COM.volano.chat.license.Constants;
import  java.io.*;
import  java.math.BigInteger;
import  java.net.*;
import  java.security.*;
import  java.security.spec.*;
import  java.text.*;
import  java.util.*;
import org.apache.catalina.util.Base64;

/**
 * This class verifies and stores the server license.
 *
 * @author  John Neffenger
 * @version 18 Jul 2001
 */

class License {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String VOLANO_CHAT     = "VolanoChat";
    private static final String VOLANO_CHAT_PRO = "VolanoChatPro";
    private static final String VOLANO_CHAT_SP  = "VolanoChatSP";
    private static final String UNLIMITED       = "unlimited connections";
    private static final String LIMIT           = " connection limit";
    private static final String DASH            = " - ";

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String HOST_PORT_SEPARATOR = ":";
    private static final String DELIMETER           = ",";
    private static final String URL_PREFIX          = "http://";
    private static final String ENCODING            = "UTF-8";
    private static final String KEY_ALGORITHM       = "DSA";
    private static final String SIG_ALGORITHM       = "DSA";
    // private static final String TRIAL_ADDRESS    = "0.0.0.0:0";
    private static final String NULL_ADDRESS        = "0.0.0.0";
    private static final String NULL_DATE           = "null";

    // From license for this host in license key file.
    private String  address = null;
    private boolean isVC    = false;
    private boolean isPro   = false;
    private boolean isSP    = false;
    private int     limit   = 0;
    private Date    date    = null;

    // Values for server socket.
    private InetAddress inetAddress = null;
    private int         port        = -1;

    // Convert the host name to its numeric IP address.
    //   www.volano.com, 8000 -> 209.61.182.173:8000
    private static String getNumericAddress(String host, int port) throws MalformedURLException, UnknownHostException {
        URL url = new URL(URL_PREFIX + host + HOST_PORT_SEPARATOR + port);
        InetAddress address = InetAddress.getByName(url.getHost());
        return address.getHostAddress() + HOST_PORT_SEPARATOR + url.getPort();
    }

    // Get all but the signature in the line:
    //   host:port=product,level,expiration,signature
    private static String getData(String string) {
        return string.substring(0, string.lastIndexOf(DELIMETER));
    }

    // Get the signature in the line:
    //   host:port=product,level,expiration,signature
    private static String getSignature(String string) {
        return string.substring(string.lastIndexOf(DELIMETER) + 1);
    }

    // Get the public key.
    public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec    spec    = new DSAPublicKeySpec(new BigInteger(Constants.Y), new BigInteger(Constants.P), new BigInteger(Constants.Q), new BigInteger(Constants.G));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePublic(spec);
    }

    // Verify the data and signature given the public key.
    private static boolean verify(PublicKey key, byte[] data, byte[] signature) throws
        NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance(SIG_ALGORITHM);
        dsa.initVerify(key);
        dsa.update(data);
        return dsa.verify(signature);
    }

    License(File file, String serverHost, int serverPort) throws Exception {
        if (Build.IS_BENCHMARK) {
            isPro = true;
            limit = 0;
            date  = null;
            inetAddress = null;
            port        = serverPort;
        } else {
            readLicense(file, serverHost, serverPort);
        }
    }

    License(String serverHost, int serverPort) throws Exception {
        isPro = true;
        limit = 0;
        date = null;
        if (serverHost == null || serverHost.length() == 0 || serverHost.equals(NULL_ADDRESS)) {
            inetAddress = null;
        } else {
            inetAddress = InetAddress.getByName(serverHost);
        }
        port = serverPort;
    }

    String getAddress() {
        return address;
    }

    boolean isVolanoChat() {
        return isVC;
    }

    boolean isVolanoChatPro() {
        return isPro;
    }

    boolean isVolanoChatSP() {
        return isSP;
    }

    int getLimit() {
        return limit;
    }

    Date getDate() {
        return date;
    }

    InetAddress getInetAddress() {
        return inetAddress;
    }

    int getPort() {
        return port;
    }

    String getHostName() {
        if (inetAddress != null) {
            return inetAddress.getHostName();
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return NULL_ADDRESS;
        }
    }

    String getHostAddress() {
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return NULL_ADDRESS;
        }
    }

    private void readLicense(File file, String serverHost, int serverPort) throws
        SignatureException, NoSuchAlgorithmException, InvalidKeyException,
        //  InvalidKeySpecException, ParseException, IOException, NoServerHostException {
        InvalidKeySpecException, ParseException, IOException {
        // Load the properties file.
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        Properties properties = new Properties();
        properties.load(input);
        input.close();

        // If the host name is null or empty:
        //   Use the primary address of this host.
        // Otherwise:
        //   Use the specified host and port number.
        if (serverHost == null || serverHost.length() == 0 || serverHost.equals(NULL_ADDRESS))
            // if (properties.size() == 0)
            //   throw new IOException("no license in " + file);
            // if (properties.size() != 1)
            //   throw new NoServerHostException();
            // Enumeration enumeration = properties.propertyNames();
            // address = (String) enumeration.nextElement();
        {
            address = InetAddress.getLocalHost().getHostAddress() + HOST_PORT_SEPARATOR + serverPort;
        } else {
            address = getNumericAddress(serverHost, serverPort);
        }

        // Look up the value for the address we are using.
        String value = properties.getProperty(address);
        if (value == null) {
            throw new IOException("no license for " + address);
        }

        // Get our public key.
        PublicKey publicKey = getPublicKey();

        // Separate the data and signature and verify them using the public key.
        String line = address + KEY_VALUE_SEPARATOR + value;
        byte[] data = getData(line).getBytes(ENCODING);
        byte[] signature = Base64.decode(getSignature(line).getBytes(ENCODING));
        if (! verify(publicKey, data, signature)) {
            throw new SignatureException("invalid license");
        }

        // Obtain the license from the verified data.
        parse(value);

        // Save the Internet address and port number for the server socket.
        // if (address.equals(TRIAL_ADDRESS)) {

        // If server host is null or empty:
        //   Open server socket on all interfaces.
        // Otherwise:
        //   Use specified IP address.
        if (serverHost == null || serverHost.length() == 0 || serverHost.equals(NULL_ADDRESS)) {
            inetAddress = null;
            port        = serverPort;
        } else {
            URL url = new URL(URL_PREFIX + address);
            inetAddress = InetAddress.getByName(url.getHost());
            port        = url.getPort();
        }
    }

    // Parse the first 3 items in the string:
    //   product,level,expiration,signature
    // We don't have to be too careful about checking errors here since the data
    // and signature are verified before this method is called.
    private void parse(String string) throws ParseException {
        String product = null;
        String level = null;
        String expiration = null;
        StringTokenizer tokenizer = new StringTokenizer(string, DELIMETER);
        product = tokenizer.nextToken();
        level = tokenizer.nextToken();
        expiration = tokenizer.nextToken();

        isVC  = product.equals(VOLANO_CHAT);
        isPro = product.equals(VOLANO_CHAT_PRO);
        isSP  = product.equals(VOLANO_CHAT_SP);
        limit = Integer.parseInt(level);
        if (expiration.equals(NULL_DATE)) {
            date = null;
        } else {
            DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            date = format.parse(expiration);
        }
    }

    public String toString() {
        String name = null;
        if (isVC) {
            name = VOLANO_CHAT;
        }
        if (isPro) {
            name = VOLANO_CHAT_PRO;
        }
        if (isSP) {
            name = VOLANO_CHAT_SP;
        }
        String type = limit == 0 ? DASH + UNLIMITED : DASH + Integer.toString(limit) + LIMIT;
        return name + type;
    }
}
