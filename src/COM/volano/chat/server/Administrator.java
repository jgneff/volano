/*
 * Administrator.java - an interface for Web-based administration.
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
import  java.io.File;
import  java.io.IOException;
import  java.text.MessageFormat;
import  java.util.Date;
import  java.util.Hashtable;
import  java.util.Properties;

/**
 * This class provides an interface for configuring and monitoring the server
 * through a Web interface.  This class is written with particular attention to
 * making it easy to use with the Velocity template engine.  This class acts as
 * a model for views created using the Velocity template language.  Java
 * servlets can use this model to configure the server's properties or modify
 * its runtime parameters.
 *
 * @author  John Neffenger
 * @version 18 Jul 2001
 */

public class Administrator {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private Value   value;
    private License license;

    Administrator(Value value) {
        this.value   = value;
        this.license = value.getLicense();
    }

    // Secondary configuration files.

    public String[] getPermanentRoomList() {
        return value.getRoomList();
    }

    public String[] getMemberMonitorList() {
        return (String[]) value.getMemberMonitorTable().keySet().toArray(new String[0]);
    }

    // Server license information.

    public boolean getIsVolanoChatPro() {
        return license.isVolanoChatPro();
    }

    public int getLicenseLimit() {
        return license.getLimit();
    }

    public String getLicenseExpiration() {
        Date date = license.getDate();
        return date == null ? "" : date.toString();
    }

    // Access control configuration and banned host list.

    public Properties getHostsAllowedList() {
        return AccessControl.getHostsAllowed();
    }

    public Properties getHostsDeniedList() {
        return AccessControl.getHostsDenied();
    }

    public Properties getReferrersAllowedList() {
        return AccessControl.getReferrersAllowed();
    }

    public Properties getReferrersDeniedList() {
        return AccessControl.getReferrersDenied();
    }

    public Hashtable getHostsBannedList() {
        return AccessControl.getHostsBanned();
    }

    // Server startup environment.

    public String getPropertiesFile() {
        File   file = value.getPropertiesFile();
        String path = "";
        if (file != null) {
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                path = file.getAbsolutePath();
            }
        }
        return path;
    }

    public String getInstallRoot() {
        File file = value.getInstallRoot();
        String path = "";
        if (file != null) {
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                path = file.getAbsolutePath();
            }
        }
        return path;
    }

    public String getBaseDirectory() {
        File file = value.getBaseDirectory();
        String path = "";
        if (file != null) {
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                path = file.getAbsolutePath();
            }
        }
        return path;
    }

    // Server runtime lists.

    public String[] getActiveMemberMonitorList() {
        return (String[]) Main.getActiveMemberMonitors().keySet().toArray(new String[0]);
    }

    public String[] getPublicRoomList() {
        return Main.getPublicRooms();
    }

    public String[] getPersonalRoomList() {
        return Main.getPersonalRooms();
    }

    public String[] getPrivateRoomList() {
        return Main.getPrivateRooms();
    }

    // Java system properties.

    public String getJavaVendor() {
        return value.getJavaVendor();
    }

    public String getJavaVendorUrl() {
        return value.getJavaVendorUrl();
    }

    public String getJavaVersion() {
        return value.getJavaVersion();
    }

    public String getJavaClassVersion() {
        return value.getJavaClassVersion();
    }

    public String getJavaCompiler() {
        return value.getJavaCompiler();
    }

    public String getOsName() {
        return value.getOsName();
    }

    public String getOsVersion() {
        return value.getOsVersion();
    }

    public String getOsArch() {
        return value.getOsArch();
    }

    public String getUserLanguage() {
        return value.getUserLanguage();
    }

    public String getUserRegion() {
        return value.getUserRegion();
    }

    public String getFileEncoding() {
        return value.getFileEncoding();
    }

    public String getFileEncodingPkg() {
        return value.getFileEncodingPkg();
    }

    public String getUserDir() {
        return value.getUserDir();
    }

    // Server properties.

    // server.host=

    public String getServerHost() {
        return value.getServerHost();
    }

    public void setServerHost(String serverHost) {
        value.setServerHost(serverHost);
    }

    // server.port=8000

    public int getServerPort() {
        return value.getServerPort();
    }

    public void setServerPort(String serverPort) throws NumberFormatException {
        value.setServerPort(serverPort);
    }

    // servlet.port=8080

    public int getServletPort() {
        return value.getServletPort();
    }

    public void setServletPort(String servletPort) throws NumberFormatException {
        value.setServletPort(servletPort);
    }

    // server.password=monitor

    public String getServerPassword() {
        return value.getServerPassword();
    }

    public void setServerPassword(String serverPassword) {
        value.setServerPassword(serverPassword);
    }

    // admin.password=admin

    public String getAdminPassword() {
        return value.getAdminPassword();
    }

    public void setAdminPassword(String adminPassword) {
        value.setAdminPassword(adminPassword);
    }

    // admin.client.host=127.0.0.1

    public String getAdminClientHost() {
        return value.getAdminClientHost();
    }

    public void setAdminClientHost(String adminClientHost) {
        value.setAdminClientHost(adminClientHost);
    }

    // admin.port=8001

    public int getAdminPort() {
        return value.getAdminPort();
    }

    public void setAdminPort(String adminPort) {
        value.setAdminPort(adminPort);
    }

    // status.interval=60

    public int getStatusInterval() {
        return value.getStatusInterval();
    }

    public void setStatusInterval(String statusInterval) throws NumberFormatException {
        value.setStatusInterval(statusInterval);
    }

    // status.history=60

    public int getStatusHistory() {
        return value.getStatusHistory();
    }

    public void setStatusHistory(String statusHistory) throws NumberFormatException {
        value.setStatusHistory(statusHistory);
    }

    // server.backlog=50

    public int getServerBacklog() {
        return value.getServerBacklog();
    }

    public void setServerBacklog(String serverBacklog) {
        value.setServerBacklog(serverBacklog);
    }

    // server.limit=0

    public int getServerLimit() {
        return value.getServerLimit();
    }

    public void setServerLimit(String serverLimit) throws NumberFormatException {
        value.setServerLimit(serverLimit);
    }

    // server.timeout=10

    public int getServerTimeout() {
        return value.getServerTimeout();
    }

    public void setServerTimeout(String serverTimeout) throws NumberFormatException {
        value.setServerTimeout(serverTimeout);
    }

    // server.verbose=false

    public boolean getServerVerbose() {
        return value.getServerVerbose();
    }

    public void setServerVerbose(String serverVerbose) {
        value.setServerVerbose(serverVerbose);
    }

    // server.trace=false

    public boolean getServerTrace() {
        return value.getServerTrace();
    }

    public void setServerTrace(String serverTrace) {
        value.setServerTrace(serverTrace);
    }

    // address.broadcast=false

    public boolean getAddressBroadcast() {
        return value.getAddressBroadcast();
    }

    public void setAddressBroadcast(String addressBroadcast) {
        value.setAddressBroadcast(addressBroadcast);
    }

    // script.timeout=10

    public int getScriptTimeout() {
        return value.getScriptTimeout();
    }

    public void setScriptTimeout(String scriptTimeout) throws NumberFormatException {
        value.setScriptTimeout(scriptTimeout);
    }

    // script.trace=false

    public boolean getScriptTrace() {
        return value.getScriptTrace();
    }

    public void setScriptTrace(String scriptTrace) {
        value.setScriptTrace(scriptTrace);
    }

    // room.limit=25

    public int getRoomLimit() {
        return value.getRoomLimit();
    }

    public void setRoomLimit(String roomLimit) throws NumberFormatException {
        value.setRoomLimit(roomLimit);
    }

    // room.nodynamic=false

    public boolean getRoomNodynamic() {
        return value.getRoomNodynamic();
    }

    public void setRoomNodynamic(String roomNodynamic) {
        value.setRoomNodynamic(roomNodynamic);
    }

    // room.sweep.interval=15

    public int getRoomSweepInterval() {
        return value.getRoomSweepInterval();
    }

    public void setRoomSweepInterval(String roomSweepInterval) throws NumberFormatException {
        value.setRoomSweepInterval(roomSweepInterval);
    }

    // length.chattext=1000

    public int getLengthChattext() {
        return value.getLengthChattext();
    }

    public void setLengthChattext(String lengthChattext) throws NumberFormatException {
        value.setLengthChattext(lengthChattext);
    }

    // length.profile=1000

    public int getLengthProfile() {
        return value.getLengthProfile();
    }

    public void setLengthProfile(String lengthProfile) throws NumberFormatException {
        value.setLengthProfile(lengthProfile);
    }

    // length.roomname=100

    public int getLengthRoomname() {
        return value.getLengthRoomname();
    }

    public void setLengthRoomname(String lengthRoomname) throws NumberFormatException {
        value.setLengthRoomname(lengthRoomname);
    }

    // length.username=20

    public int getLengthUsername() {
        return value.getLengthUsername();
    }

    public void setLengthUsername(String lengthUsername) throws NumberFormatException {
        value.setLengthUsername(lengthUsername);
    }

    // server.key=conf/key.txt

    public String getServerKey() {
        File file = value.getServerKey();
        return file == null ? "" : file.toString();
    }

    public void setServerKey(String serverKey) {
        value.setServerKey(serverKey);
    }

    // server.rooms=conf/rooms.txt

    public String getServerRooms() {
        File file = value.getServerRooms();
        return file == null ? "" : file.toString();
    }

    public void setServerRooms(String serverRooms) {
        value.setServerRooms(serverRooms);
    }

    // member.monitors=

    public String getMemberMonitors() {
        File file = value.getMemberMonitors();
        return file == null ? "" : file.toString();
    }

    public void setMemberMonitors(String memberMonitors) {
        value.setMemberMonitors(memberMonitors);
    }

    // applet.codebase=webapps/ROOT

    public String getAppletCodebase() {
        File file = value.getAppletCodebase();
        return file == null ? "" : file.toString();
    }

    public void setAppletCodebase(String appletCodebase) {
        value.setAppletCodebase(appletCodebase);
    }

    // log.directory=logs

    public String getLogDirectory() {
        return value.getLogDirectory();
    }

    public void setLogDirectory(String logDirectory) {
        value.setLogDirectory(logDirectory);
    }

    // log.access.prefix=access-

    public String getLogAccessPrefix() {
        return value.getLogAccessPrefix();
    }

    public void setLogAccessPrefix(String logAccessPrefix) {
        value.setLogAccessPrefix(logAccessPrefix);
    }

    // log.access.suffix=.log

    public String getLogAccessSuffix() {
        return value.getLogAccessSuffix();
    }

    public void setLogAccessSuffix(String logAccessSuffix) {
        value.setLogAccessSuffix(logAccessSuffix);
    }

    // log.error.prefix=error-

    public String getLogErrorPrefix() {
        return value.getLogErrorPrefix();
    }

    public void setLogErrorPrefix(String logErrorPrefix) {
        value.setLogErrorPrefix(logErrorPrefix);
    }

    // log.error.suffix=.log

    public String getLogErrorSuffix() {
        return value.getLogErrorSuffix();
    }

    public void setLogErrorSuffix(String logErrorSuffix) {
        value.setLogErrorSuffix(logErrorSuffix);
    }

    // log.public.prefix=public-

    public String getLogPublicPrefix() {
        return value.getLogPublicPrefix();
    }

    public void setLogPublicPrefix(String logPublicPrefix) {
        value.setLogPublicPrefix(logPublicPrefix);
    }

    // log.public.suffix=.log

    public String getLogPublicSuffix() {
        return value.getLogPublicSuffix();
    }

    public void setLogPublicSuffix(String logPublicSuffix) {
        value.setLogPublicSuffix(logPublicSuffix);
    }

    // log.private.prefix=private-

    public String getLogPrivatePrefix() {
        return value.getLogPrivatePrefix();
    }

    public void setLogPrivatePrefix(String logPrivatePrefix) {
        value.setLogPrivatePrefix(logPrivatePrefix);
    }

    // log.private.suffix=.log

    public String getLogPrivateSuffix() {
        return value.getLogPrivateSuffix();
    }

    public void setLogPrivateSuffix(String logPrivateSuffix) {
        value.setLogPrivateSuffix(logPrivateSuffix);
    }

    // log.servlet.prefix=servlet-

    public String getLogServletPrefix() {
        return value.getLogServletPrefix();
    }

    public void setLogServletPrefix(String logServletPrefix) {
        value.setLogServletPrefix(logServletPrefix);
    }

    // log.servlet.suffix=.log

    public String getLogServletSuffix() {
        return value.getLogServletSuffix();
    }

    public void setLogServletSuffix(String logServletSuffix) {
        value.setLogServletSuffix(logServletSuffix);
    }

    // log.velocity.prefix=velocity-

    public String getLogVelocityPrefix() {
        return value.getLogVelocityPrefix();
    }

    public void setLogVelocityPrefix(String logVelocityPrefix) {
        value.setLogVelocityPrefix(logVelocityPrefix);
    }

    // log.velocity.suffix=.log

    public String getLogVelocitySuffix() {
        return value.getLogVelocitySuffix();
    }

    public void setLogVelocitySuffix(String logVelocitySuffix) {
        value.setLogVelocitySuffix(logVelocitySuffix);
    }

    // log.access=logs/access.log
    /*
      public String getLogAccess() {
        File file = value.getLogAccess();
        return file == null ? "" : file.toString();
      }

      public void setLogAccess(String logAccess) {
        value.setLogAccess(logAccess);
      }
    */

    // log.error=logs/error.log
    /*
      public String getLogError() {
        File file = value.getLogError();
        return file == null ? "" : file.toString();
      }

      public void setLogError(String logError) {
        value.setLogError(logError);
      }
    */

    // log.support=logs/support.log

    public String getLogSupport() {
        File file = value.getLogSupport();
        return file == null ? "" : file.toString();
    }

    public void setLogSupport(String logSupport) {
        value.setLogSupport(logSupport);
    }

    // log.public=
    /*
      public String getLogPublic() {
        File file = value.getLogPublic();
        return file == null ? "" : file.toString();
      }

      public void setLogPublic(String logPublic) {
        value.setLogPublic(logPublic);
      }
    */

    // log.private=
    /*
      public String getLogPrivate() {
        File file = value.getLogPrivate();
        return file == null ? "" : file.toString();
      }

      public void setLogPrivate(String logPrivate) {
        value.setLogPrivate(logPrivate);
      }
    */

    // log.chat.public.dir=webapps/ROOT

    public String getLogChatPublicDir() {
        File file = value.getLogChatPublicDir();
        return file == null ? "" : file.toString();
    }

    public void setLogChatPublicDir(String logChatPublicDir) {
        value.setLogChatPublicDir(logChatPublicDir);
    }

    // log.chat.public.suffix=.html

    public String getLogChatPublicSuffix() {
        return value.getLogChatPublicSuffix();
    }

    public void setLogChatPublicSuffix(String logChatPublicSuffix) {
        value.setLogChatPublicSuffix(logChatPublicSuffix);
    }

    // log.chat.private=

    public String getLogChatPrivate() {
        File file = value.getLogChatPrivate();
        return file == null ? "" : file.toString();
    }

    public void setLogChatPrivate(String logChatPrivate) {
        value.setLogChatPrivate(logChatPrivate);
    }

    // transcribe.room.permanent=false

    public boolean getTranscribeRoomPermanent() {
        return value.getTranscribeRoomPermanent();
    }

    public void setTranscribeRoomPermanent(String transcribeRoomPermanent) {
        value.setTranscribeRoomPermanent(transcribeRoomPermanent);
    }

    // transcribe.room.dynamic=false

    public boolean getTranscribeRoomDynamic() {
        return value.getTranscribeRoomDynamic();
    }

    public void setTranscribeRoomDynamic(String transcribeRoomDynamic) {
        value.setTranscribeRoomDynamic(transcribeRoomDynamic);
    }

    // transcribe.room.event=false

    public boolean getTranscribeRoomEvent() {
        return value.getTranscribeRoomEvent();
    }

    public void setTranscribeRoomEvent(String transcribeRoomEvent) {
        value.setTranscribeRoomEvent(transcribeRoomEvent);
    }

    // transcribe.room.private=false

    public boolean getTranscribeRoomPrivate() {
        return value.getTranscribeRoomPrivate();
    }

    public void setTranscribeRoomPrivate(String transcribeRoomPrivate) {
        value.setTranscribeRoomPrivate(transcribeRoomPrivate);
    }

    // format.date=[dd/MMM/yyyy:HH:mm:ss z]

    public String getFormatDate() {
        return value.getFormatDate().toPattern();
    }

    public void setFormatDate(String formatDate) {
        value.setFormatDate(formatDate);
    }

    // format.access={0} - - {1} "GET {2} HTTP/{3}" {4,number,0} {5,number,0} "{6}" "{7}" {8}

    public String getFormatAccess() {
        return value.getFormatAccess().toPattern();
    }

    public void setFormatAccess(String formatAccess) {
        value.setFormatAccess(formatAccess);
    }

    // format.access.agent={0}/{1} API/{2} ({3}/{4} {5}) {6}

    public String getFormatAccessAgent() {
        return value.getFormatAccessAgent().toPattern();
    }

    public void setFormatAccessAgent(String formatAccessAgent) {
        value.setFormatAccessAgent(formatAccessAgent);
    }

    // format.access.extra={0,number,0} {6}

    public String getFormatAccessExtra() {
        return value.getFormatAccessExtra().toPattern();
    }

    public void setFormatAccessExtra(String formatAccessExtra) {
        value.setFormatAccessExtra(formatAccessExtra);
    }

    // format.public={0} {1,number,0} "{2}" "{3}" {4}

    public String getFormatPublic() {
        return value.getFormatPublic().toPattern();
    }

    public void setFormatPublic(String formatPublic) {
        value.setFormatPublic(formatPublic);
    }

    // format.private={0} {1,number,0} "{2}" "{3}" {4} "{5}" {6}

    public String getFormatPrivate() {
        return value.getFormatPrivate().toPattern();
    }

    public void setFormatPrivate(String formatPrivate) {
        value.setFormatPrivate(formatPrivate);
    }

    // format.status={0} {1} {2} {3,number,0} {4,number,0} {5,number,0} {6,number,0}+{7,number,0}={8,number,0}

    public String getFormatStatus() {
        return value.getFormatStatus().toPattern();
    }

    public void setFormatStatus(String formatStatus) {
        value.setFormatStatus(formatStatus);
    }

    // format.status.memory={0,number,0}KB/{1,number,0}KB={2,number,0%}

    public String getFormatStatusMemory() {
        return value.getFormatStatusMemory().toPattern();
    }

    public void setFormatStatusMemory(String formatStatusMemory) {
        value.setFormatStatusMemory(formatStatusMemory);
    }

    // format.status.resources={0,number,0} {1,number,0} ({2,number,0})

    public String getFormatStatusResources() {
        return value.getFormatStatusResources().toPattern();
    }

    public void setFormatStatusResources(String formatStatusResources) {
        value.setFormatStatusResources(formatStatusResources);
    }

    // format.chat.public={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0}&gt;</b> {2}<br>

    public String getFormatChatPublic() {
        return value.getFormatChatPublic().toPattern();
    }

    public void setFormatChatPublic(String formatChatPublic) {
        value.setFormatChatPublic(formatChatPublic);
    }

    // format.chat.private={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0} -&gt; {1}&gt;</b> {2}<br>

    public String getFormatChatPrivate() {
        return value.getFormatChatPrivate().toPattern();
    }

    public void setFormatChatPrivate(String formatChatPrivate) {
        value.setFormatChatPrivate(formatChatPrivate);
    }

    // member.version=

    public String getMemberVersion() {
        return value.getMemberVersion();
    }

    public void setMemberVersion(String memberVersion) {
        value.setMemberVersion(memberVersion);
    }

    // member.only=false

    public boolean getMemberOnly() {
        return value.getMemberOnly();
    }

    public void setMemberOnly(String memberOnly) {
        value.setMemberOnly(memberOnly);
    }

    // member.access=

    public String getMemberAccess() {
        return value.getMemberAccess().toPattern();
    }

    public void setMemberAccess(String memberAccess) {
        value.setMemberAccess(memberAccess);
    }

    // member.name=

    public String getMemberName() {
        return value.getMemberName().toPattern();
    }

    public void setMemberName(String memberName) {
        value.setMemberName(memberName);
    }

    // member.monitor.matchcase=false

    public boolean getMemberMonitorMatchcase() {
        return value.getMemberMonitorMatchcase();
    }

    public void setMemberMonitorMatchcase(String memberMonitorMatchcase) {
        value.setMemberMonitorMatchcase(memberMonitorMatchcase);
    }

    // member.monitor.multiuse=false

    public boolean getMemberMonitorMultiuse() {
        return value.getMemberMonitorMultiuse();
    }

    public void setMemberMonitorMultiuse(String memberMonitorMultiuse) {
        value.setMemberMonitorMultiuse(memberMonitorMultiuse);
    }

    // auditoriums.permanent=false

    public boolean getAuditoriumsPermanent() {
        return value.getAuditoriumsPermanent();
    }

    public void setAuditoriumsPermanent(String auditoriumsPermanent) {
        value.setAuditoriumsPermanent(auditoriumsPermanent);
    }

    // entrance.stage=

    public String getEntranceStage() {
        return value.getEntranceStage();
    }

    public void setEntranceStage(String entranceStage) {
        value.setEntranceStage(entranceStage);
    }

    // event.callback.prefix=

    public String getEventCallbackPrefix() {
        return value.getEventCallbackPrefix();
    }

    public void setEventCallbackPrefix(String eventCallbackPrefix) {
        value.setEventCallbackPrefix(eventCallbackPrefix);
    }
}
