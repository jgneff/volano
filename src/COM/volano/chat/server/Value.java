/*
 * Value.java - a class for getting and storing server property values.
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
import  COM.volano.io.UnicodeReader;
import  COM.volano.net.Connection;
import  COM.volano.net.DNSBlacklist;
import  COM.volano.util.Message;
import  COM.volano.chat.packet.*;
import  java.io.*;
import  java.net.*;
import  java.text.*;
import  java.util.*;

/**
 * This class gets and stores server properties.  It also stores some global
 * variables relating to the server's environment.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Value {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    // Comment character for parsing secondary files.
    private static final String COMMENT = "#";

    // Server properties file.
    private File       propertiesFile;    // Path to properties file
    private Properties properties;        // Server/System properties

    // Server runtime directory information.
    private String     installRoot;       // InstallShield root installation directory
    private String     baseDirectory;     // Base directory of server properties file

    // Objects for secondary configuration files.
    License       license;                // VolanoChat server license
    String[]      roomList;               // List of permanent rooms
    Hashtable     memberMonitorTable;     // Hash table of member monitors

    // Java system properties.
    String javaVendor;
    String javaVendorUrl;
    String javaVersion;
    String javaClassVersion;
    String javaCompiler;
    String osName;
    String osVersion;
    String osArch;
    String userLanguage;
    String userRegion;
    String fileEncoding;
    String fileEncodingPkg;
    String userDir;

    // From admin.client.host.
    InetAddress adminClientAddr;

    // VolanoChat server properties expected in System properties.
    // COM.volano.ConfigServlet
    //   install.root
    //   applet.codebase
    //   admin.password
    // COM.volano.chat.admin.Shutdown
    //   install.root
    //   server.host
    //   admin.port
    //   admin.password
    // COM.volano.chat.admin.Status
    //   install.root
    //   server.host
    //   admin.port
    //   admin.password
    //   status.interval
    //   format.status
    //   format.status.memory
    //   format.date

    // Applet codebase directory.
    File             appletCodebase;

    // General properties.
    String           serverHost;
    int              serverPort;
    String           serverPassword;
    boolean          clientAuthentication;
    boolean          duplicateAddresses;
    String           adminClientHost;
    int              adminPort;
    String           adminPassword;
    int              statusInterval;
    int              statusHistory;
    int              serverBacklog;
    int              serverLimit;
    int              serverTimeout;
    boolean          serverVerbose;
    boolean          serverTrace;
    boolean          serverNothreadgroups;
    boolean          addressBroadcast;
    int              scriptTimeout;
    boolean          scriptTrace;
    int              roomLimit;
    boolean          roomNodynamic;
    int              roomSweepInterval;
    boolean          usernameMatchcase;
    int              lengthChattext;
    int              lengthProfile;
    int              lengthRoomname;
    int              lengthUsername;

    // Servlet runner.
    int              servletPort;
    int              servletMinprocessors;
    int              servletMaxprocessors;

    // Flood control.
    long             delayAccess;
    long             delayAuthenticate;
    long             delayBeep;
    long             delayChat;
    long             delayEnterPrivate;
    long             delayEnterRoom;
    long             delayExitPrivate;
    long             delayExitRoom;
    long             delayKick;
    long             delayPing;
    long             delayRoomList;
    long             delayUserList;

    // Banning control.
    String           dnslistDenied;
    String           dnslistDynamic;
    int              banStaticDuration;
    int              banDynamicDuration;
    int              banNetblockDuration;
    String           banNetblockIpv4mask;

    // Configuration files.
    File             serverKey;
    File             serverRooms;
    String           accessHostsAllow;
    String           accessHostsDeny;
    String           accessReferrersAllow;
    String           accessReferrersDeny;

    // Log files.
    String           logDirectory;
    String           logAccessPrefix;
    String           logAccessSuffix;
    String           logErrorPrefix;
    String           logErrorSuffix;
    String           logPublicPrefix;
    String           logPublicSuffix;
    String           logPrivatePrefix;
    String           logPrivateSuffix;
    String           logBannedPrefix;
    String           logBannedSuffix;
    String           logServletPrefix;
    String           logServletSuffix;
    String           logHttpPrefix;
    String           logHttpSuffix;
    String           logVelocityPrefix;
    String           logVelocitySuffix;

    File             logSupport;
    File             logChatPublicDir;
    String           logChatPublicSuffix;
    File             logChatPrivate;

    // Room transcription.
    boolean          transcribeRoomPermanent;
    boolean          transcribeRoomDynamic;
    boolean          transcribeRoomPersonal;      // Always false for now
    boolean          transcribeRoomEvent;
    boolean          transcribeRoomPrivate;

    // Log file formats.
    SimpleDateFormat formatDate;
    MessageFormat    formatAccess;
    MessageFormat    formatAccessAgent;
    MessageFormat    formatAccessExtra;
    MessageFormat    formatPublic;
    MessageFormat    formatPrivate;
    MessageFormat    formatBanned;
    MessageFormat    formatStatus;
    MessageFormat    formatStatusMemory;
    MessageFormat    formatStatusResources;
    MessageFormat    formatChatPublic;
    MessageFormat    formatChatPrivate;

    // Member access.
    String           memberVersion;
    boolean          memberOnly;
    MessageFormat    memberAccess;
    MessageFormat    memberName;

    boolean          memberMonitorMatchcase;
    boolean          memberMonitorMultiuse;
    File             memberMonitors;

    // Auditorium access and creation.
    boolean          auditoriumsPermanent;
    String           entranceStage;

    // Access to server events.
    String           eventCallbackPrefix;

    /**
     * Gets the properties defined in the specified file, with the System properties
     * as the default set of properties.
     *
     * @param file  the Java properties file, or <code>null</code> for all default
     *              property values.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private static Properties getProperties(File file) throws IOException {
        Properties properties = new Properties(System.getProperties());
        if (file != null) {
            try {
                FileInputStream input = new FileInputStream(file);
                properties.load(input);
                input.close();

                // Put our server properties into the Java system properties so they're
                // accessible from any embedded servlets like ConfigServlet.
                System.setProperties(properties);
            } catch (IOException e) {
                System.err.println(Message.format(Msg.BAD_PROPERTIES, e.toString()));
                throw e;
            }
        }
        return properties;
    }

    /**
     * Prints an error message indicating an error in a property value.
     *
     * @param key    the property name.
     * @param value  the property value in error.
     * @param t      the exception or error describing the problem.
     */

    private static void printError(String key, String value, Throwable t) {
        Vector vector = new Vector(3);
        vector.addElement(key);
        vector.addElement(value);
        vector.addElement(t.toString());
        System.err.println(Message.format(Msg.PROPERTY_ERROR, vector));
    }

    /**
     * Reads a text file into an array with one string per line.
     *
     * @param input  the input stream for reading the file.
     * @return  the array of strings, with one string for each line in the file,
     *          ignoring blank lines and comments.
     * @exception java.io.IOException  if an I/O error occurs reading the file.
     */

    private static String[] getList(BufferedReader input) throws IOException {
        Vector vector = new Vector();
        String line   = input.readLine();
        while (line != null) {
            line = line.trim();
            if (line.length() > 0) {
                if (! line.startsWith(COMMENT)) {
                    vector.addElement(line);
                }
            }
            line = input.readLine();
        }
        return (String[]) vector.toArray(new String[0]);
    }

    /**
     * Creates a file from the specified installation root directory, base
     * directory, and path specification, with the following algorithm:
     * <pre>
     * If path is an empty string:
     *   Return null.
     * Otherwise if path is an absolute specification:
     *   Return file of absolute path.
     * Otherwise, path is a relative specification:
     *   If root directory is defined (not null):
     *     Return file of path relative to root directory.
     *   Otherwise if base directory is defined (not null):
     *     Return file of path relative to base directory.
     *   Otherwise:
     *     Return file of path relative to current working directory.
     * </pre>
     * <p>The root directory is defined by the <code>install.root</code> system
     * property.  The base directory is the directory containing the
     * <code>properites.txt</code> file.  The current working directory is where
     * the server was started.
     *
     * @param root  the installation root directory, or <code>null</code> if
     *              undefined.
     * @param base  the directory containing the properties file, or
     *              <code>null</code> if undefined.
     * @param path  the absolute or relative path name of the file.
     * @return  a file for the specified path, or <code>null</code> if no file can
     *          be created from the path.
     */

    private static File getFile(String root, String base, String path) {
        File file = null;
        if (path.length() > 0) {
            file = new File(path);
            if (! file.isAbsolute()) {
                file = root != null ? new File(root, path) : new File(base, path);
            }
        }
        return file;
    }

    // Path to properties file

    File getPropertiesFile() {
        return propertiesFile;
    }

    // Server runtime directory information.

    File getInstallRoot() {
        return installRoot == null ? null : new File(installRoot);
    }

    File getBaseDirectory() {
        return baseDirectory == null ? null : new File(baseDirectory);
    }

    // Secondary configuration objects.

    License getLicense() {
        return license;
    }

    String[] getRoomList() {
        return roomList;
    }

    Hashtable getMemberMonitorTable() {
        return memberMonitorTable;
    }

    // Java system properties.

    String getJavaVendor() {
        return javaVendor;
    }

    String getJavaVendorUrl() {
        return javaVendorUrl;
    }

    String getJavaVersion() {
        return javaVersion;
    }

    String getJavaClassVersion() {
        return javaClassVersion;
    }

    String getJavaCompiler() {
        return javaCompiler;
    }

    String getOsName() {
        return osName;
    }

    String getOsVersion() {
        return osVersion;
    }

    String getOsArch() {
        return osArch;
    }

    String getUserLanguage() {
        return userLanguage;
    }

    String getUserRegion() {
        return userRegion;
    }

    String getFileEncoding() {
        return fileEncoding;
    }

    String getFileEncodingPkg() {
        return fileEncodingPkg;
    }

    String getUserDir() {
        return userDir;
    }

    // Server properties.

    // server.host=

    String getServerHost() {
        return serverHost;
    }

    void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    // server.port=8000

    int getServerPort() {
        return serverPort;
    }

    void setServerPort(String serverPort) throws NumberFormatException {
        this.serverPort = Integer.parseInt(serverPort);
    }

    // servlet.port=8080

    int getServletPort() {
        return servletPort;
    }

    void setServletPort(String servletPort) throws NumberFormatException {
        this.servletPort = Integer.parseInt(servletPort);
    }

    // server.password=monitor

    String getServerPassword() {
        return serverPassword;
    }

    void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    // client.authentication=true

    boolean getClientAuthentication() {
        return clientAuthentication;
    }

    void setClientAuthentication(boolean clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    // admin.password=admin

    String getAdminPassword() {
        return adminPassword;
    }

    void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    // admin.client.host=127.0.0.1

    String getAdminClientHost() {
        return adminClientHost;
    }

    void setAdminClientHost(String adminClientHost) {
        this.adminClientHost = adminClientHost;
    }

    // admin.port=8001

    int getAdminPort() {
        return adminPort;
    }

    void setAdminPort(String adminPort) throws NumberFormatException {
        this.adminPort = Integer.parseInt(adminPort);
    }

    // status.interval=60

    int getStatusInterval() {
        return statusInterval;
    }

    void setStatusInterval(String statusInterval) throws NumberFormatException {
        this.statusInterval = Integer.parseInt(statusInterval);
    }

    // status.history=60

    int getStatusHistory() {
        return statusHistory;
    }

    void setStatusHistory(String statusHistory) throws NumberFormatException {
        this.statusHistory = Integer.parseInt(statusHistory);
    }

    // server.backlog=50

    int getServerBacklog() {
        return serverBacklog;
    }

    void setServerBacklog(String serverBacklog) throws NumberFormatException {
        this.serverBacklog = Integer.parseInt(serverBacklog);
    }

    // server.limit=0

    int getServerLimit() {
        return serverLimit;
    }

    void setServerLimit(String serverLimit) throws NumberFormatException {
        this.serverLimit = Integer.parseInt(serverLimit);
    }

    // server.timeout=10

    int getServerTimeout() {
        return serverTimeout;
    }

    void setServerTimeout(String serverTimeout) throws NumberFormatException {
        this.serverTimeout = Integer.parseInt(serverTimeout);
    }

    // server.verbose=false

    boolean getServerVerbose() {
        return serverVerbose;
    }

    void setServerVerbose(String serverVerbose) {
        this.serverVerbose = Boolean.valueOf(serverVerbose).booleanValue();
    }

    // server.trace=false

    boolean getServerTrace() {
        return serverTrace;
    }

    void setServerTrace(String serverTrace) {
        this.serverTrace = Boolean.valueOf(serverTrace).booleanValue();
    }

    // address.broadcast=false

    boolean getAddressBroadcast() {
        return addressBroadcast;
    }

    void setAddressBroadcast(String addressBroadcast) {
        this.addressBroadcast = Boolean.valueOf(addressBroadcast).booleanValue();
    }

    // script.timeout=10

    int getScriptTimeout() {
        return scriptTimeout;
    }

    void setScriptTimeout(String scriptTimeout) throws NumberFormatException {
        this.scriptTimeout = Integer.parseInt(scriptTimeout);
    }

    // script.trace=false

    boolean getScriptTrace() {
        return scriptTrace;
    }

    void setScriptTrace(String scriptTrace) {
        this.scriptTrace = Boolean.valueOf(scriptTrace).booleanValue();
    }

    // room.limit=25

    int getRoomLimit() {
        return roomLimit;
    }

    void setRoomLimit(String roomLimit) throws NumberFormatException {
        this.roomLimit = Integer.parseInt(roomLimit);
    }

    // room.nodynamic=false

    boolean getRoomNodynamic() {
        return roomNodynamic;
    }

    void setRoomNodynamic(String roomNodynamic) {
        this.roomNodynamic = Boolean.valueOf(roomNodynamic).booleanValue();
    }

    // room.sweep.interval=15

    int getRoomSweepInterval() {
        return roomSweepInterval;
    }

    void setRoomSweepInterval(String roomSweepInterval) throws NumberFormatException {
        this.roomSweepInterval = Integer.parseInt(roomSweepInterval);
    }

    // length.chattext=1000

    int getLengthChattext() {
        return lengthChattext;
    }

    void setLengthChattext(String lengthChattext) throws NumberFormatException {
        this.lengthChattext = Integer.parseInt(lengthChattext);
    }

    // length.profile=1000

    int getLengthProfile() {
        return lengthProfile;
    }

    void setLengthProfile(String lengthProfile) throws NumberFormatException {
        this.lengthProfile = Integer.parseInt(lengthProfile);
    }

    // length.roomname=100

    int getLengthRoomname() {
        return lengthRoomname;
    }

    void setLengthRoomname(String lengthRoomname) throws NumberFormatException {
        this.lengthRoomname = Integer.parseInt(lengthRoomname);
    }

    // length.username=20

    int getLengthUsername() {
        return lengthUsername;
    }

    void setLengthUsername(String lengthUsername) throws NumberFormatException {
        this.lengthUsername = Integer.parseInt(lengthUsername);
    }

    // server.key=conf/key.txt

    File getServerKey() {
        return serverKey;
    }

    void setServerKey(String serverKey) {
        this.serverKey = getFile(installRoot, baseDirectory, serverKey);
    }

    // server.rooms=conf/rooms.txt

    File getServerRooms() {
        return serverRooms;
    }

    void setServerRooms(String serverRooms) {
        this.serverRooms = getFile(installRoot, baseDirectory, serverRooms);
    }

    // member.monitors=

    File getMemberMonitors() {
        return memberMonitors;
    }

    void setMemberMonitors(String memberMonitors) {
        this.memberMonitors = getFile(installRoot, baseDirectory, memberMonitors);
    }

    // log.directory=logs

    String getLogDirectory() {
        return logDirectory;
    }

    void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    // log.access.prefix=access-

    String getLogAccessPrefix() {
        return logAccessPrefix;
    }

    void setLogAccessPrefix(String logAccessPrefix) {
        this.logAccessPrefix = logAccessPrefix;
    }

    // log.access.suffix=.log

    String getLogAccessSuffix() {
        return logAccessSuffix;
    }

    void setLogAccessSuffix(String logAccessSuffix) {
        this.logAccessSuffix = logAccessSuffix;
    }

    // log.error.prefix=error-

    String getLogErrorPrefix() {
        return logErrorPrefix;
    }

    void setLogErrorPrefix(String logErrorPrefix) {
        this.logErrorPrefix = logErrorPrefix;
    }

    // log.error.suffix=.log

    String getLogErrorSuffix() {
        return logErrorSuffix;
    }

    void setLogErrorSuffix(String logErrorSuffix) {
        this.logErrorSuffix = logErrorSuffix;
    }

    // log.public.prefix=public-

    String getLogPublicPrefix() {
        return logPublicPrefix;
    }

    void setLogPublicPrefix(String logPublicPrefix) {
        this.logPublicPrefix = logPublicPrefix;
    }

    // log.public.suffix=.log

    String getLogPublicSuffix() {
        return logPublicSuffix;
    }

    void setLogPublicSuffix(String logPublicSuffix) {
        this.logPublicSuffix = logPublicSuffix;
    }

    // log.private.prefix=private-

    String getLogPrivatePrefix() {
        return logPrivatePrefix;
    }

    void setLogPrivatePrefix(String logPrivatePrefix) {
        this.logPrivatePrefix = logPrivatePrefix;
    }

    // log.private.suffix=.log

    String getLogPrivateSuffix() {
        return logPrivateSuffix;
    }

    void setLogPrivateSuffix(String logPrivateSuffix) {
        this.logPrivateSuffix = logPrivateSuffix;
    }

    // log.servlet.prefix=servlet-

    String getLogServletPrefix() {
        return logServletPrefix;
    }

    void setLogServletPrefix(String logServletPrefix) {
        this.logServletPrefix = logServletPrefix;
    }

    // log.servlet.suffix=.log

    String getLogServletSuffix() {
        return logServletSuffix;
    }

    void setLogServletSuffix(String logServletSuffix) {
        this.logServletSuffix = logServletSuffix;
    }

    // log.velocity.prefix=velocity-

    String getLogVelocityPrefix() {
        return logVelocityPrefix;
    }

    void setLogVelocityPrefix(String logVelocityPrefix) {
        this.logVelocityPrefix = logVelocityPrefix;
    }

    // log.velocity.suffix=.log

    String getLogVelocitySuffix() {
        return logVelocitySuffix;
    }

    void setLogVelocitySuffix(String logVelocitySuffix) {
        this.logVelocitySuffix = logVelocitySuffix;
    }

    // log.support=logs/support.log

    File getLogSupport() {
        return logSupport;
    }

    void setLogSupport(String logSupport) {
        this.logSupport = getFile(installRoot, baseDirectory, logSupport);
    }

    // log.chat.public.dir=webapps/ROOT

    File getLogChatPublicDir() {
        return logChatPublicDir;
    }

    void setLogChatPublicDir(String logChatPublicDir) {
        this.logChatPublicDir = getFile(installRoot, baseDirectory, logChatPublicDir);
    }

    // log.chat.public.suffix=.html

    String getLogChatPublicSuffix() {
        return logChatPublicSuffix;
    }

    void setLogChatPublicSuffix(String logChatPublicSuffix) {
        this.logChatPublicSuffix = logChatPublicSuffix;
    }

    // log.chat.private=

    File getLogChatPrivate() {
        return logChatPrivate;
    }

    void setLogChatPrivate(String logChatPrivate) {
        this.logChatPrivate = getFile(installRoot, baseDirectory, logChatPrivate);
    }

    // applet.codebase=webapps/ROOT

    File getAppletCodebase() {
        return appletCodebase;
    }

    void setAppletCodebase(String appletCodebase) {
        this.appletCodebase = getFile(installRoot, baseDirectory, appletCodebase);
    }

    // transcribe.room.permanent=false

    boolean getTranscribeRoomPermanent() {
        return transcribeRoomPermanent;
    }

    void setTranscribeRoomPermanent(String transcribeRoomPermanent) {
        this.transcribeRoomPermanent = Boolean.valueOf(transcribeRoomPermanent).booleanValue();
    }

    // transcribe.room.dynamic=false

    boolean getTranscribeRoomDynamic() {
        return transcribeRoomDynamic;
    }

    void setTranscribeRoomDynamic(String transcribeRoomDynamic) {
        this.transcribeRoomDynamic = Boolean.valueOf(transcribeRoomDynamic).booleanValue();
    }

    // transcribe.room.event=false

    boolean getTranscribeRoomEvent() {
        return transcribeRoomEvent;
    }

    void setTranscribeRoomEvent(String transcribeRoomEvent) {
        this.transcribeRoomEvent = Boolean.valueOf(transcribeRoomEvent).booleanValue();
    }

    // transcribe.room.private=false

    boolean getTranscribeRoomPrivate() {
        return transcribeRoomPrivate;
    }

    void setTranscribeRoomPrivate(String transcribeRoomPrivate) {
        this.transcribeRoomPrivate = Boolean.valueOf(transcribeRoomPrivate).booleanValue();
    }

    // format.date=[dd/MMM/yyyy:HH:mm:ss z]

    SimpleDateFormat getFormatDate() {
        return formatDate;
    }

    void setFormatDate(String formatDate) {
        this.formatDate = new SimpleDateFormat(formatDate);
        this.formatDate.setTimeZone(TimeZone.getDefault());
    }

    // format.access={0} - - {1} "GET {2} HTTP/{3}" {4,number,0} {5,number,0} "{6}" "{7}" {8}

    MessageFormat getFormatAccess() {
        return formatAccess;
    }

    void setFormatAccess(String formatAccess) {
        this.formatAccess = new MessageFormat(formatAccess);
    }

    // format.access.agent={0}/{1} API/{2} ({3}/{4} {5}) {6}

    MessageFormat getFormatAccessAgent() {
        return formatAccessAgent;
    }

    void setFormatAccessAgent(String formatAccessAgent) {
        this.formatAccessAgent = new MessageFormat(formatAccessAgent);
    }

    // format.access.extra={0,number,0} {6}

    MessageFormat getFormatAccessExtra() {
        return formatAccessExtra;
    }

    void setFormatAccessExtra(String formatAccessExtra) {
        this.formatAccessExtra = new MessageFormat(formatAccessExtra);
    }

    // format.public={0} {1,number,0} "{2}" "{3}" {4}

    MessageFormat getFormatPublic() {
        return formatPublic;
    }

    void setFormatPublic(String formatPublic) {
        this.formatPublic = new MessageFormat(formatPublic);
    }

    // format.private={0} {1,number,0} "{2}" "{3}" {4} "{5}" {6}

    MessageFormat getFormatPrivate() {
        return formatPrivate;
    }

    void setFormatPrivate(String formatPrivate) {
        this.formatPrivate = new MessageFormat(formatPrivate);
    }

    // format.status={0} {1} {2} {3,number,0} {4,number,0} {5,number,0} {6,number,0}+{7,number,0}={8,number,0}

    MessageFormat getFormatStatus() {
        return formatStatus;
    }

    void setFormatStatus(String formatStatus) {
        this.formatStatus = new MessageFormat(formatStatus);
    }

    // format.status.memory={0,number,0}KB/{1,number,0}KB={2,number,0%}

    MessageFormat getFormatStatusMemory() {
        return formatStatusMemory;
    }

    void setFormatStatusMemory(String formatStatusMemory) {
        this.formatStatusMemory = new MessageFormat(formatStatusMemory);
    }

    // format.status.resources={0,number,0} {1,number,0} ({2,number,0})

    MessageFormat getFormatStatusResources() {
        return formatStatusResources;
    }

    void setFormatStatusResources(String formatStatusResources) {
        this.formatStatusResources = new MessageFormat(formatStatusResources);
    }

    // format.chat.public={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0}&gt;</b> {2}<br>

    MessageFormat getFormatChatPublic() {
        return formatChatPublic;
    }

    void setFormatChatPublic(String formatChatPublic) {
        this.formatChatPublic = new MessageFormat(formatChatPublic);
    }

    // format.chat.private={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0} -&gt; {1}&gt;</b> {2}<br>

    MessageFormat getFormatChatPrivate() {
        return formatChatPrivate;
    }

    void setFormatChatPrivate(String formatChatPrivate) {
        this.formatChatPrivate = new MessageFormat(formatChatPrivate);
    }

    // member.version=

    String getMemberVersion() {
        return memberVersion;
    }

    void setMemberVersion(String memberVersion) {
        this.memberVersion = memberVersion;
    }

    // member.only=false

    boolean getMemberOnly() {
        return memberOnly;
    }

    void setMemberOnly(String memberOnly) {
        this.memberOnly = Boolean.valueOf(memberOnly).booleanValue();
    }

    // member.access=

    MessageFormat getMemberAccess() {
        return memberAccess;
    }

    void setMemberAccess(String memberAccess) {
        this.memberAccess = new MessageFormat(memberAccess);
    }

    // member.name=

    MessageFormat getMemberName() {
        return memberName;
    }

    void setMemberName(String memberName) {
        this.memberName = new MessageFormat(memberName);
    }

    // member.monitor.matchcase=false

    boolean getMemberMonitorMatchcase() {
        return memberMonitorMatchcase;
    }

    void setMemberMonitorMatchcase(String memberMonitorMatchcase) {
        this.memberMonitorMatchcase = Boolean.valueOf(memberMonitorMatchcase).booleanValue();
    }

    // member.monitor.multiuse=false

    boolean getMemberMonitorMultiuse() {
        return memberMonitorMultiuse;
    }

    void setMemberMonitorMultiuse(String memberMonitorMultiuse) {
        this.memberMonitorMultiuse = Boolean.valueOf(memberMonitorMultiuse).booleanValue();
    }

    // auditoriums.permanent=false

    boolean getAuditoriumsPermanent() {
        return auditoriumsPermanent;
    }

    void setAuditoriumsPermanent(String auditoriumsPermanent) {
        this.auditoriumsPermanent = Boolean.valueOf(auditoriumsPermanent).booleanValue();
    }

    // entrance.stage=

    String getEntranceStage() {
        return entranceStage;
    }

    void setEntranceStage(String entranceStage) {
        this.entranceStage = entranceStage;
    }

    // event.callback.prefix=

    String getEventCallbackPrefix() {
        return eventCallbackPrefix;
    }

    void setEventCallbackPrefix(String eventCallbackPrefix) {
        this.eventCallbackPrefix = eventCallbackPrefix;
    }

    /**
     * Creates a new value object, reading in all property values and initializing
     * all global variables.
     */

    Value(File file) throws Exception {
        propertiesFile = file;
        if (file != null) {
            baseDirectory = file.getParent();
        }

        properties  = getProperties(file);
        installRoot = properties.getProperty(Key.INSTALL_ROOT);

        // Get the system properties.
        javaVendor       = properties.getProperty(Key.JAVA_VENDOR,        Default.JAVA_VENDOR);
        javaVendorUrl    = properties.getProperty(Key.JAVA_VENDOR_URL,    Default.JAVA_VENDOR_URL);
        javaVersion      = properties.getProperty(Key.JAVA_VERSION,       Default.JAVA_VERSION);
        javaClassVersion = properties.getProperty(Key.JAVA_CLASS_VERSION, Default.JAVA_CLASS_VERSION);
        javaCompiler     = properties.getProperty(Key.JAVA_COMPILER,      Default.JAVA_COMPILER);
        osName           = properties.getProperty(Key.OS_NAME,            Default.OS_NAME);
        osVersion        = properties.getProperty(Key.OS_VERSION,         Default.OS_VERSION);
        osArch           = properties.getProperty(Key.OS_ARCH,            Default.OS_ARCH);
        userLanguage     = properties.getProperty(Key.USER_LANGUAGE,      Default.USER_LANGUAGE);
        userRegion       = properties.getProperty(Key.USER_REGION,        Default.USER_REGION);
        fileEncoding     = properties.getProperty(Key.FILE_ENCODING,      Default.FILE_ENCODING);
        fileEncodingPkg  = properties.getProperty(Key.FILE_ENCODING_PKG,  Default.FILE_ENCODING_PKG);
        userDir          = properties.getProperty(Key.USER_DIR,           Default.USER_DIR);

        String valueAppletCodebase = properties.getProperty(Key.APPLET_CODEBASE, Default.APPLET_CODEBASE);

        serverHost           = properties.getProperty(Key.SERVER_HOST,                           Default.SERVER_HOST);
        serverPort           = Integer.parseInt(properties.getProperty(Key.SERVER_PORT,          Default.SERVER_PORT));
        serverPassword       = properties.getProperty(Key.SERVER_PASSWORD,                       Default.SERVER_PASSWORD);
        clientAuthentication = Boolean.valueOf(properties.getProperty(Key.CLIENT_AUTHENTICATION, Default.CLIENT_AUTHENTICATION)).booleanValue();
        duplicateAddresses   = Boolean.valueOf(properties.getProperty(Key.DUPLICATE_ADDRESSES,   Default.DUPLICATE_ADDRESSES)).booleanValue();
        adminClientHost      = properties.getProperty(Key.ADMIN_CLIENT_HOST,                     Default.ADMIN_CLIENT_HOST);
        adminPort            = Integer.parseInt(properties.getProperty(Key.ADMIN_PORT,           Default.ADMIN_PORT));
        adminPassword        = properties.getProperty(Key.ADMIN_PASSWORD,                        Default.ADMIN_PASSWORD);
        statusInterval       = Integer.parseInt(properties.getProperty(Key.STATUS_INTERVAL,      Default.STATUS_INTERVAL));
        statusHistory        = Integer.parseInt(properties.getProperty(Key.STATUS_HISTORY,       Default.STATUS_HISTORY));
        serverBacklog        = Integer.parseInt(properties.getProperty(Key.SERVER_BACKLOG,       Default.SERVER_BACKLOG));
        serverLimit          = Integer.parseInt(properties.getProperty(Key.SERVER_LIMIT,         Default.SERVER_LIMIT));
        serverTimeout        = Integer.parseInt(properties.getProperty(Key.SERVER_TIMEOUT,       Default.SERVER_TIMEOUT));
        serverVerbose        = Boolean.valueOf(properties.getProperty(Key.SERVER_VERBOSE,        Default.SERVER_VERBOSE)).booleanValue();
        serverTrace          = Boolean.valueOf(properties.getProperty(Key.SERVER_TRACE,          Default.SERVER_TRACE)).booleanValue();
        serverNothreadgroups = Boolean.valueOf(properties.getProperty(Key.SERVER_NOTHREADGROUPS, Default.SERVER_NOTHREADGROUPS)).booleanValue();
        addressBroadcast     = Boolean.valueOf(properties.getProperty(Key.ADDRESS_BROADCAST,     Default.ADDRESS_BROADCAST)).booleanValue();
        scriptTimeout        = Integer.parseInt(properties.getProperty(Key.SCRIPT_TIMEOUT,       Default.SCRIPT_TIMEOUT));
        scriptTrace          = Boolean.valueOf(properties.getProperty(Key.SCRIPT_TRACE,          Default.SCRIPT_TRACE)).booleanValue();
        roomLimit            = Integer.parseInt(properties.getProperty(Key.ROOM_LIMIT,           Default.ROOM_LIMIT));
        roomNodynamic        = Boolean.valueOf(properties.getProperty(Key.ROOM_NODYNAMIC,        Default.ROOM_NODYNAMIC)).booleanValue();
        roomSweepInterval    = Integer.parseInt(properties.getProperty(Key.ROOM_SWEEP_INTERVAL,  Default.ROOM_SWEEP_INTERVAL));
        usernameMatchcase    = Boolean.valueOf(properties.getProperty(Key.USERNAME_MATCHCASE,    Default.USERNAME_MATCHCASE)).booleanValue();
        lengthChattext       = Integer.parseInt(properties.getProperty(Key.LENGTH_CHATTEXT,      Default.LENGTH_CHATTEXT));
        lengthProfile        = Integer.parseInt(properties.getProperty(Key.LENGTH_PROFILE,       Default.LENGTH_PROFILE));
        lengthRoomname       = Integer.parseInt(properties.getProperty(Key.LENGTH_ROOMNAME,      Default.LENGTH_ROOMNAME));
        lengthUsername       = Integer.parseInt(properties.getProperty(Key.LENGTH_USERNAME,      Default.LENGTH_USERNAME));

        servletPort          = Integer.parseInt(properties.getProperty(Key.SERVLET_PORT,          Default.SERVLET_PORT));
        servletMinprocessors = Integer.parseInt(properties.getProperty(Key.SERVLET_MINPROCESSORS, Default.SERVLET_MINPROCESSORS));
        servletMaxprocessors = Integer.parseInt(properties.getProperty(Key.SERVLET_MAXPROCESSORS, Default.SERVLET_MAXPROCESSORS));

        delayAccess       = Long.parseLong(properties.getProperty(Key.DELAY_ACCESS,        Default.DELAY_ACCESS));
        delayAuthenticate = Long.parseLong(properties.getProperty(Key.DELAY_AUTHENTICATE,  Default.DELAY_AUTHENTICATE));
        delayBeep         = Long.parseLong(properties.getProperty(Key.DELAY_BEEP,          Default.DELAY_BEEP));
        delayChat         = Long.parseLong(properties.getProperty(Key.DELAY_CHAT,          Default.DELAY_CHAT));
        delayEnterPrivate = Long.parseLong(properties.getProperty(Key.DELAY_ENTER_PRIVATE, Default.DELAY_ENTER_PRIVATE));
        delayEnterRoom    = Long.parseLong(properties.getProperty(Key.DELAY_ENTER_ROOM,    Default.DELAY_ENTER_ROOM));
        delayExitPrivate  = Long.parseLong(properties.getProperty(Key.DELAY_EXIT_PRIVATE,  Default.DELAY_EXIT_PRIVATE));
        delayExitRoom     = Long.parseLong(properties.getProperty(Key.DELAY_EXIT_ROOM,     Default.DELAY_EXIT_ROOM));
        delayKick         = Long.parseLong(properties.getProperty(Key.DELAY_KICK,          Default.DELAY_KICK));
        delayPing         = Long.parseLong(properties.getProperty(Key.DELAY_PING,          Default.DELAY_PING));
        delayRoomList     = Long.parseLong(properties.getProperty(Key.DELAY_ROOM_LIST,     Default.DELAY_ROOM_LIST));
        delayUserList     = Long.parseLong(properties.getProperty(Key.DELAY_USER_LIST,     Default.DELAY_USER_LIST));

        dnslistDenied       = properties.getProperty(Key.DNSLIST_DENIED,  Default.DNSLIST_DENIED);
        dnslistDynamic      = properties.getProperty(Key.DNSLIST_DYNAMIC, Default.DNSLIST_DYNAMIC);
        banStaticDuration   = Integer.parseInt(properties.getProperty(Key.BAN_STATIC_DURATION,   Default.BAN_STATIC_DURATION));
        banDynamicDuration  = Integer.parseInt(properties.getProperty(Key.BAN_DYNAMIC_DURATION,  Default.BAN_DYNAMIC_DURATION));
        banNetblockDuration = Integer.parseInt(properties.getProperty(Key.BAN_NETBLOCK_DURATION, Default.BAN_NETBLOCK_DURATION));
        banNetblockIpv4mask = properties.getProperty(Key.BAN_NETBLOCK_IPV4MASK, Default.BAN_NETBLOCK_IPV4MASK);

        String valueServerKey   = properties.getProperty(Key.SERVER_KEY,             Default.SERVER_KEY);
        String valueServerRooms = properties.getProperty(Key.SERVER_ROOMS,           Default.SERVER_ROOMS);
        accessHostsAllow     = properties.getProperty(Key.ACCESS_HOSTS_ALLOW,     Default.ACCESS_HOSTS_ALLOW);
        accessHostsDeny      = properties.getProperty(Key.ACCESS_HOSTS_DENY,      Default.ACCESS_HOSTS_DENY);
        accessReferrersAllow = properties.getProperty(Key.ACCESS_REFERRERS_ALLOW, Default.ACCESS_REFERRERS_ALLOW);
        accessReferrersDeny  = properties.getProperty(Key.ACCESS_REFERRERS_DENY,  Default.ACCESS_REFERRERS_DENY);

        logDirectory      = properties.getProperty(Key.LOG_DIRECTORY,       Default.LOG_DIRECTORY);
        logAccessPrefix   = properties.getProperty(Key.LOG_ACCESS_PREFIX,   Default.LOG_ACCESS_PREFIX);
        logAccessSuffix   = properties.getProperty(Key.LOG_ACCESS_SUFFIX,   Default.LOG_ACCESS_SUFFIX);
        logErrorPrefix    = properties.getProperty(Key.LOG_ERROR_PREFIX,    Default.LOG_ERROR_PREFIX);
        logErrorSuffix    = properties.getProperty(Key.LOG_ERROR_SUFFIX,    Default.LOG_ERROR_SUFFIX);
        logPublicPrefix   = properties.getProperty(Key.LOG_PUBLIC_PREFIX,   Default.LOG_PUBLIC_PREFIX);
        logPublicSuffix   = properties.getProperty(Key.LOG_PUBLIC_SUFFIX,   Default.LOG_PUBLIC_SUFFIX);
        logPrivatePrefix  = properties.getProperty(Key.LOG_PRIVATE_PREFIX,  Default.LOG_PRIVATE_PREFIX);
        logPrivateSuffix  = properties.getProperty(Key.LOG_PRIVATE_SUFFIX,  Default.LOG_PRIVATE_SUFFIX);
        logBannedPrefix   = properties.getProperty(Key.LOG_BANNED_PREFIX,   Default.LOG_BANNED_PREFIX);
        logBannedSuffix   = properties.getProperty(Key.LOG_BANNED_SUFFIX,   Default.LOG_BANNED_SUFFIX);
        logServletPrefix  = properties.getProperty(Key.LOG_SERVLET_PREFIX,  Default.LOG_SERVLET_PREFIX);
        logServletSuffix  = properties.getProperty(Key.LOG_SERVLET_SUFFIX,  Default.LOG_SERVLET_SUFFIX);
        logHttpPrefix     = properties.getProperty(Key.LOG_HTTP_PREFIX,     Default.LOG_HTTP_PREFIX);
        logHttpSuffix     = properties.getProperty(Key.LOG_HTTP_SUFFIX,     Default.LOG_HTTP_SUFFIX);
        logVelocityPrefix = properties.getProperty(Key.LOG_VELOCITY_PREFIX, Default.LOG_VELOCITY_PREFIX);
        logVelocitySuffix = properties.getProperty(Key.LOG_VELOCITY_SUFFIX, Default.LOG_VELOCITY_SUFFIX);

        String valueLogSupport       = properties.getProperty(Key.LOG_SUPPORT,            Default.LOG_SUPPORT);
        String valueLogChatPublicDir = properties.getProperty(Key.LOG_CHAT_PUBLIC_DIR,    Default.LOG_CHAT_PUBLIC_DIR);
        logChatPublicSuffix          = properties.getProperty(Key.LOG_CHAT_PUBLIC_SUFFIX, Default.LOG_CHAT_PUBLIC_SUFFIX);
        String valueLogChatPrivate   = properties.getProperty(Key.LOG_CHAT_PRIVATE,       Default.LOG_CHAT_PRIVATE);

        transcribeRoomPermanent = Boolean.valueOf(properties.getProperty(Key.TRANSCRIBE_ROOM_PERMANENT, Default.TRANSCRIBE_ROOM_PERMANENT)).booleanValue();
        transcribeRoomDynamic   = Boolean.valueOf(properties.getProperty(Key.TRANSCRIBE_ROOM_DYNAMIC,   Default.TRANSCRIBE_ROOM_DYNAMIC)).booleanValue();
        // Not until I figure out how to do the file names, since they currently use the document base URL as the room name.
        transcribeRoomPersonal  = false;
        transcribeRoomEvent     = Boolean.valueOf(properties.getProperty(Key.TRANSCRIBE_ROOM_EVENT,     Default.TRANSCRIBE_ROOM_EVENT)).booleanValue();
        transcribeRoomPrivate   = Boolean.valueOf(properties.getProperty(Key.TRANSCRIBE_ROOM_PRIVATE,   Default.TRANSCRIBE_ROOM_PRIVATE)).booleanValue();

        String valueFormatDate            = properties.getProperty(Key.FORMAT_DATE,             Default.FORMAT_DATE);
        String valueFormatAccess          = properties.getProperty(Key.FORMAT_ACCESS,           Default.FORMAT_ACCESS);
        String valueFormatAccessAgent     = properties.getProperty(Key.FORMAT_ACCESS_AGENT,     Default.FORMAT_ACCESS_AGENT);
        String valueFormatAccessExtra     = properties.getProperty(Key.FORMAT_ACCESS_EXTRA,     Default.FORMAT_ACCESS_EXTRA);
        String valueFormatPublic          = properties.getProperty(Key.FORMAT_PUBLIC,           Default.FORMAT_PUBLIC);
        String valueFormatPrivate         = properties.getProperty(Key.FORMAT_PRIVATE,          Default.FORMAT_PRIVATE);
        String valueFormatBanned          = properties.getProperty(Key.FORMAT_BANNED,           Default.FORMAT_BANNED);
        String valueFormatStatus          = properties.getProperty(Key.FORMAT_STATUS,           Default.FORMAT_STATUS);
        String valueFormatStatusMemory    = properties.getProperty(Key.FORMAT_STATUS_MEMORY,    Default.FORMAT_STATUS_MEMORY);
        String valueFormatStatusResources = properties.getProperty(Key.FORMAT_STATUS_RESOURCES, Default.FORMAT_STATUS_RESOURCES);
        String valueFormatChatPublic      = properties.getProperty(Key.FORMAT_CHAT_PUBLIC,      Default.FORMAT_CHAT_PUBLIC);
        String valueFormatChatPrivate     = properties.getProperty(Key.FORMAT_CHAT_PRIVATE,     Default.FORMAT_CHAT_PRIVATE);

        memberVersion = properties.getProperty(Key.MEMBER_VERSION,              Default.MEMBER_VERSION);
        memberOnly    = Boolean.valueOf(properties.getProperty(Key.MEMBER_ONLY, Default.MEMBER_ONLY)).booleanValue();
        String valueMemberAccess = properties.getProperty(Key.MEMBER_ACCESS,    Default.MEMBER_ACCESS);
        String valueMemberName   = properties.getProperty(Key.MEMBER_NAME,      Default.MEMBER_NAME);

        memberMonitorMatchcase = Boolean.valueOf(properties.getProperty(Key.MEMBER_MONITOR_MATCHCASE, Default.MEMBER_MONITOR_MATCHCASE)).booleanValue();
        memberMonitorMultiuse  = Boolean.valueOf(properties.getProperty(Key.MEMBER_MONITOR_MULTIUSE,  Default.MEMBER_MONITOR_MULTIUSE)).booleanValue();
        String valueMemberMonitors = properties.getProperty(Key.MEMBER_MONITORS,                      Default.MEMBER_MONITORS);

        auditoriumsPermanent = Boolean.valueOf(properties.getProperty(Key.AUDITORIUMS_PERMANENT, Default.AUDITORIUMS_PERMANENT)).booleanValue();
        entranceStage        = properties.getProperty(Key.ENTRANCE_STAGE, Default.ENTRANCE_STAGE);

        eventCallbackPrefix = properties.getProperty(Key.EVENT_CALLBACK_PREFIX, Default.EVENT_CALLBACK_PREFIX);

        formatDate = new SimpleDateFormat(valueFormatDate);
        formatDate.setTimeZone(TimeZone.getDefault());
        formatAccess          = new MessageFormat(valueFormatAccess);
        formatAccessAgent     = new MessageFormat(valueFormatAccessAgent);
        formatAccessExtra     = new MessageFormat(valueFormatAccessExtra);
        formatPublic          = new MessageFormat(valueFormatPublic);
        formatPrivate         = new MessageFormat(valueFormatPrivate);
        formatBanned          = new MessageFormat(valueFormatBanned);
        formatStatus          = new MessageFormat(valueFormatStatus);
        formatStatusMemory    = new MessageFormat(valueFormatStatusMemory);
        formatStatusResources = new MessageFormat(valueFormatStatusResources);
        formatChatPublic      = new MessageFormat(valueFormatChatPublic);
        formatChatPrivate     = new MessageFormat(valueFormatChatPrivate);
        memberAccess          = new MessageFormat(valueMemberAccess);
        memberName            = new MessageFormat(valueMemberName);

        appletCodebase   = getFile(installRoot, baseDirectory, valueAppletCodebase);
        serverKey        = getFile(installRoot, baseDirectory, valueServerKey);
        serverRooms      = getFile(installRoot, baseDirectory, valueServerRooms);
        logSupport       = getFile(installRoot, baseDirectory, valueLogSupport);
        logChatPublicDir = getFile(installRoot, baseDirectory, valueLogChatPublicDir);
        logChatPrivate   = getFile(installRoot, baseDirectory, valueLogChatPrivate);
        memberMonitors   = getFile(installRoot, baseDirectory, valueMemberMonitors);

        // Ensure that log files and format strings are defined for transcriptions.
        if (logChatPublicDir == null || formatChatPublic.toPattern().length() == 0) {
            transcribeRoomPermanent = false;
            transcribeRoomDynamic   = false;
            transcribeRoomPersonal  = false;
            transcribeRoomEvent     = false;
        }
        if (logChatPrivate == null || formatChatPrivate.toPattern().length() == 0) {
            transcribeRoomPrivate = false;
        }

        // Load permanent room list.
        try {
            if (serverRooms == null) {
                roomList = new String[0];
            } else {
                BufferedReader input = new BufferedReader(new UnicodeReader(new FileReader(serverRooms)));
                roomList = getList(input);
                input.close();
            }
        } catch (IOException e) {
            printError(Key.SERVER_ROOMS, serverRooms.toString(), e);
            throw e;
        }

        // Load member monitor list.
        memberMonitorTable = new Hashtable();
        try {
            String[] list;
            if (memberMonitors == null) {
                list = new String[0];
            } else {
                BufferedReader input = new BufferedReader(new UnicodeReader(new FileReader(memberMonitors)));
                list = getList(input);
                input.close();
            }
            for (int i = 0; i < list.length; i++) {
                String name = memberMonitorMatchcase ? list[i] : list[i].toLowerCase();
                memberMonitorTable.put(name, new Object());
            }
        } catch (IOException e) {
            printError(Key.MEMBER_MONITORS, memberMonitors.toString(), e);
            throw e;
        }

        // Allow administrative connections to be restricted by IP address.
        adminClientAddr = null;
        if (adminClientHost.length() > 0) {
            try {
                adminClientAddr = InetAddress.getByName(adminClientHost);
            } catch (UnknownHostException e) {}
        }

        // Load license key.
        try {
            // license = new License(serverKey, serverHost, serverPort);
            // Ignore the license key in version 2.13 for Java 8.
            // The applet code base and (optionally) the document base are
            // locked to the domain name now.
            license = new License(serverHost, serverPort);
        }
        // catch (NoServerHostException e) {
        //   System.err.println(Msg.NO_SERVER_HOST);
        //   throw e;
        // }
        catch (Exception e) {
            System.err.println(Msg.NO_LICENSE);
            throw e;
        }

        // Enforce license limit on the number of concurrent connections.
        int limit = license.getLimit();
        if (limit > 0) {
            serverLimit = serverLimit == 0 ? limit : Math.min(serverLimit, limit);
        }

        // Enforce the non-VolanoChatPro feature restrictions.
        if (! license.isVolanoChatPro()) {  // If this is not a VolanoChatPro version
            memberAccess.applyPattern("");    // Prevent member access
            memberName.applyPattern("");      // Prevent reservation of member names
            entranceStage       = "";         // Prevent moderated events
            eventCallbackPrefix = "";         // Prevent server side event notifications

            // Prevent chat message transcriptions.
            transcribeRoomPermanent = false;
            transcribeRoomDynamic   = false;
            transcribeRoomPersonal  = false;
            transcribeRoomEvent     = false;
            transcribeRoomPrivate   = false;
        }

        // Set work around for Apple MRJ bug 2232076.
        if (serverNothreadgroups) {
            System.out.println(Msg.NO_THREAD_GROUPS);
            Connection.useThreadGroups = false;
        }

        if (Build.IS_BENCHMARK) {       // If this is the VolanoMark benchmark
            clientAuthentication = false; // No client authentication
            serverTimeout = 0;            // No dead session detection and timeouts
        }

        if (! Build.IS_BENCHMARK) { // No delays on socket read for VolanoMark
            setPauses();
        }

        // Add DNS blacklists.
        if (! Build.IS_BENCHMARK) { // No DNS lookups for VolanoMark
            DNSBlacklist.parseList(DNSBlacklist.getDenied(),  dnslistDenied);
            DNSBlacklist.parseList(DNSBlacklist.getDynamic(), dnslistDynamic);
        }

        // Set up banned host table.
        Bantable.setStaticDuration(banStaticDuration);
        Bantable.setDynamicDuration(banDynamicDuration);
        Bantable.setNetblockDuration(banNetblockDuration);
        Bantable.setNetblockIpv4mask(banNetblockIpv4mask);
        Bantable table = new Bantable(formatDate, formatBanned);
        AccessControl.setHostsBanned(table);

        // Load access control files.
        if (accessHostsAllow.length() > 0) {
            AccessControl.loadHostsAllowed(new FileInputStream(accessHostsAllow));
        }
        if (accessHostsDeny.length() > 0) {
            AccessControl.loadHostsDenied(new FileInputStream(accessHostsDeny));
        }
        if (accessReferrersAllow.length() > 0) {
            AccessControl.loadReferrersAllowed(new FileInputStream(accessReferrersAllow));
        }
        if (accessReferrersDeny.length() > 0) {
            AccessControl.loadReferrersDenied(new FileInputStream(accessReferrersDeny));
        }
    }

    /**
     * Sets the delay pauses configured for each request type.
     */

    private void setPauses() {
        Access.setReadPause(delayAccess);
        Authenticate.setReadPause(delayAuthenticate);
        Beep.setReadPause(delayBeep);
        Chat.setReadPause(delayChat);
        EnterPrivate.setReadPause(delayEnterPrivate);
        EnterRoom.setReadPause(delayEnterRoom);
        ExitPrivate.setReadPause(delayExitPrivate);
        ExitRoom.setReadPause(delayExitRoom);
        Kick.setReadPause(delayKick);
        Ping.setReadPause(delayPing);
        RoomList.setReadPause(delayRoomList);
        UserList.setReadPause(delayUserList);
    }

    String format(Date date) {
        synchronized (formatDate) {
            return formatDate.format(date);
        }
    }
}
