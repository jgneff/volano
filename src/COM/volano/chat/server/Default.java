/*
 * Default.java - an interface for defining server property defaults.
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

/**
 * This interface defines the server property default values and other property
 * related constants.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Default {
    // Extended Common Log Format fields.
    int REMOTE_HOST        = 0;
    int DATE               = 1;
    int CODE_BASE          = 2;
    int VERSION            = 3;
    int STATUS             = 4;
    int BYTES              = 5;
    int REFERRER           = 6;
    int USER_AGENT         = 7;
    int EXTRA              = 8;
    int REMOTE_USER        = 9;
    int FORMAT_SIZE        = 10;

    // User agent fields.
    int AGENT_JAVA_VENDOR        = 0;
    int AGENT_JAVA_VERSION       = 1;
    int AGENT_JAVA_CLASS_VERSION = 2;
    int AGENT_OS_NAME            = 3;
    int AGENT_OS_VERSION         = 4;
    int AGENT_OS_ARCH            = 5;
    int AGENT_JAVA_VENDOR_URL    = 6;
    int AGENT_SIZE               = 7;

    // Extra fields appended to log record.
    int DURATION           = 0;
    int DOCUMENT_HOST      = 1;
    int CODE_HOST          = 2;
    int CONNECTIONS        = 3;
    int PUBLIC             = 4;
    int PRIVATE            = 5;
    int MONITOR            = 6;
    int EXTRA_SIZE         = 7;

    // Public room log format.
    int PUB_DATE           = 0;
    int PUB_DURATION       = 1;
    int PUB_ROOM           = 2;
    int PUB_USER_NAME      = 3;
    int PUB_USER_HOST      = 4;
    int PUBLIC_SIZE        = 5;

    // Private room log format.
    int PVT_DATE           = 0;
    int PVT_DURATION       = 1;
    int PVT_ROOM           = 2;
    int PVT_USER1_NAME     = 3;
    int PVT_USER1_HOST     = 4;
    int PVT_USER2_NAME     = 5;
    int PVT_USER2_HOST     = 6;
    int PRIVATE_SIZE       = 7;

    // Ban log format.
    int BAN_DATE           = 0;
    int BAN_ADDRESS        = 1;
    int BAN_TYPE           = 2;
    int BAN_ROOM_NAME      = 3;
    int BAN_USER_NAME      = 4;
    int BAN_MONITOR_NAME   = 5;
    int BAN_SIZE           = 6;

    // Heap memory usage fields.
    int HEAP_USED          = 0;
    int HEAP_TOTAL         = 1;
    int HEAP_PERCENTAGE    = 2;
    int HEAP_SIZE          = 3;

    // Thread and socket resource usage fields.
    int RES_THREADS        = 0;
    int RES_CONNECTIONS    = 1;
    int RES_UNIQUE         = 2;
    int RES_SIZE           = 3;

    // Status log format.
    int STATUS_DATE        =  0;
    int STATUS_HEAP        =  1;
    int STATUS_RESOURCES   =  2;
    int STATUS_ROOMS       =  3;
    int STATUS_PERSONAL    =  4;
    int STATUS_PRIVATE     =  5;
    int STATUS_RCVD_PERSEC =  6;
    int STATUS_SENT_PERSEC =  7;
    int STATUS_PKTS_PERSEC =  8;
    int STATUS_SIZE        =  9;

    // Chat transcript log format.
    int FROM_NAME          = 0;
    int TO_NAME            = 1;
    int MESSAGE            = 2;
    int MESSAGE_DATE       = 3;
    int TRANSCRIPT_SIZE    = 4;

    // System support property default values.
    String JAVA_VENDOR        = "";
    String JAVA_VENDOR_URL    = "";
    String JAVA_VERSION       = "";
    String JAVA_CLASS_VERSION = "";
    String JAVA_COMPILER      = "";
    String OS_NAME            = "";
    String OS_VERSION         = "";
    String OS_ARCH            = "";
    String USER_LANGUAGE      = "";
    String USER_REGION        = "";
    String FILE_ENCODING      = "";
    String FILE_ENCODING_PKG  = "";
    String USER_DIR           = "";

    // # Applet code base directory
    //
    // applet.codebase=webapps/ROOT/vcclient

    String APPLET_CODEBASE = "webapps/ROOT/vcclient";

    // # General properties
    //
    // server.host=
    // server.port=8000
    // server.password=
    // client.authentication=true
    // duplicate.addresses=true
    // admin.client.host=127.0.0.1
    // admin.port=8001
    // admin.password=
    // status.interval=60
    // # status.history=60
    // server.backlog=50
    // server.limit=0
    // server.timeout=10
    // server.verbose=false
    // server.trace=false
    // # server.nothreadgroups=false
    // address.broadcast=false
    // script.timeout=10
    // script.trace=false
    // room.limit=25
    // room.nodynamic=false
    // room.sweep.interval=15
    // length.chattext=400
    // length.profile=400
    // length.roomname=100
    // length.username=20

    String SERVER_HOST           =      "";
    String SERVER_PORT           =  "8000";
    String SERVER_PASSWORD       =      "";
    String CLIENT_AUTHENTICATION =  "true";
    String DUPLICATE_ADDRESSES   =  "true";
    String ADMIN_CLIENT_HOST     = "127.0.0.1";
    String ADMIN_PORT            =  "8001";
    String ADMIN_PASSWORD        =      "";
    String STATUS_INTERVAL       =    "60";
    String STATUS_HISTORY        =    "60";  // For 3.0
    String SERVER_BACKLOG        =    "50";
    String SERVER_LIMIT          =     "0";
    String SERVER_TIMEOUT        =    "10";
    String SERVER_VERBOSE        = "false";
    String SERVER_TRACE          = "false";
    String SERVER_NOTHREADGROUPS = "false";  // For old Mac Java VM
    String ADDRESS_BROADCAST     = "false";
    String SCRIPT_TIMEOUT        =    "10";
    String SCRIPT_TRACE          = "false";
    String ROOM_LIMIT            =    "25";
    String ROOM_NODYNAMIC        = "false";
    String ROOM_SWEEP_INTERVAL   =    "15";
    String USERNAME_MATCHCASE    =  "true";
    String LENGTH_CHATTEXT       =   "400";
    String LENGTH_PROFILE        =   "400";
    String LENGTH_ROOMNAME       =   "100";
    String LENGTH_USERNAME       =    "20";

    int    ADMIN_PORT_INT        = 8001;     // For Status and Shutdown
    int    STATUS_INTERVAL_INT   = 60;       // For Status

    // # Servlet runner
    //
    // servlet.port=8080
    // servlet.minprocessors=5
    // servlet.maxprocessors=20

    String SERVLET_PORT          =  "8080";
    String SERVLET_MINPROCESSORS =     "5";
    String SERVLET_MAXPROCESSORS =    "20";

    // # Flood control
    //
    // delay.access=0
    // delay.authenticate=0
    // delay.beep=1000
    // delay.chat=1000
    // delay.enter.private=1000
    // delay.enter.room=1000
    // delay.exit.private=0
    // delay.exit.room=0
    // delay.kick=0
    // delay.ping=0
    // delay.room.list=0
    // delay.user.list=0

    String DELAY_ACCESS        =    "0";
    String DELAY_AUTHENTICATE  =    "0";
    String DELAY_BEEP          = "1000";
    String DELAY_CHAT          = "1000";
    String DELAY_ENTER_PRIVATE = "1000";
    String DELAY_ENTER_ROOM    = "1000";
    String DELAY_EXIT_PRIVATE  =    "0";
    String DELAY_EXIT_ROOM     =    "0";
    String DELAY_KICK          =    "0";
    String DELAY_PING          =    "0";
    String DELAY_ROOM_LIST     =    "0";
    String DELAY_USER_LIST     =    "0";

    // # Banning control
    //
    // dnslist.denied=
    // dnslist.dynamic=dynablock.wirehub.net:127.0.0.2 blackholes.five-ten-sg.com:127.0.0.3 relays.osirusoft.com:127.0.0.3 no-more-funn.moensted.dk:127.0.0.3 dnsbl.njabl.org:127.0.0.3 spamguard.leadmon.net:127.0.0.2
    // ban.static.duration=1440
    // ban.dynamic.duration=60
    // ban.netblock.duration=60
    // ban.netblock.ipv4mask=255.0.0.0

    String DNSLIST_DENIED        = "";
    String DNSLIST_DYNAMIC       = "dynablock.wirehub.net:127.0.0.2 blackholes.five-ten-sg.com:127.0.0.3 relays.osirusoft.com:127.0.0.3 no-more-funn.moensted.dk:127.0.0.3 dnsbl.njabl.org:127.0.0.3 spamguard.leadmon.net:127.0.0.2";
    String BAN_STATIC_DURATION   = "1440";
    String BAN_DYNAMIC_DURATION  = "60";
    String BAN_NETBLOCK_DURATION = "60";
    String BAN_NETBLOCK_IPV4MASK = "255.0.0.0";

    // # Configuration files
    //
    // server.key=conf/key.txt
    // server.rooms=conf/rooms.txt
    // access.hosts.allow=conf/hosts-allow.txt
    // access.hosts.deny=conf/hosts-deny.txt
    // access.referrers.allow=conf/referrers-allow.txt
    // access.referrers.deny=conf/referrers-deny.txt

    String SERVER_KEY             = "conf/key.txt";
    String SERVER_ROOMS           = "conf/rooms.txt";
    String ACCESS_HOSTS_ALLOW     = "conf/hosts-allow.txt";
    String ACCESS_HOSTS_DENY      = "conf/hosts-deny.txt";
    String ACCESS_REFERRERS_ALLOW = "conf/referrers-allow.txt";
    String ACCESS_REFERRERS_DENY  = "conf/referrers-deny.txt";

    // # Log files
    //
    // log.directory=logs
    // log.access.prefix=access-
    // log.access.suffix=.log
    // log.error.prefix=error-
    // log.error.suffix=.log
    // log.public.prefix=
    // log.public.suffix=.log
    // log.private.prefix=
    // log.private.suffix=.log
    // log.banned.prefix=banned-
    // log.banned.suffix=.log
    // log.servlet.prefix=servlet-
    // log.servlet.suffix=.log
    // log.http.prefix=http-
    // log.http.suffix=.log
    // log.velocity.prefix=velocity-
    // log.velocity.suffix=.log
    //
    // log.support=logs/support.log
    // log.chat.public.dir=webapps/ROOT
    // log.chat.public.suffix=.html
    // log.chat.private=

    String LOG_DIRECTORY          = "logs";
    String LOG_ACCESS_PREFIX      = "access-";
    String LOG_ACCESS_SUFFIX      = ".log";
    String LOG_ERROR_PREFIX       = "error-";
    String LOG_ERROR_SUFFIX       = ".log";
    String LOG_PUBLIC_PREFIX      = "";
    String LOG_PUBLIC_SUFFIX      = ".log";
    String LOG_PRIVATE_PREFIX     = "";
    String LOG_PRIVATE_SUFFIX     = ".log";
    String LOG_BANNED_PREFIX      = "banned-";
    String LOG_BANNED_SUFFIX      = ".log";
    String LOG_SERVLET_PREFIX     = "servlet-";
    String LOG_SERVLET_SUFFIX     = ".log";
    String LOG_HTTP_PREFIX        = "http-";
    String LOG_HTTP_SUFFIX        = ".log";
    String LOG_VELOCITY_PREFIX    = "velocity-";
    String LOG_VELOCITY_SUFFIX    = ".log";

    String LOG_SUPPORT            = "logs/support.log";
    String LOG_CHAT_PUBLIC_DIR    = "webapps/ROOT";
    String LOG_CHAT_PUBLIC_SUFFIX = ".html";
    String LOG_CHAT_PRIVATE       = "";

    // # Room transcriptions
    //
    // transcribe.room.permanent=false
    // transcribe.room.dynamic=false
    // transcribe.room.event=false
    // transcribe.room.private=false

    String TRANSCRIBE_ROOM_PERMANENT = "false";
    String TRANSCRIBE_ROOM_DYNAMIC   = "false";
    String TRANSCRIBE_ROOM_EVENT     = "false";
    String TRANSCRIBE_ROOM_PRIVATE   = "false";

    // # Log file formats
    //
    // # format.access:           {0} Remote host, {1} Date, {2} Codebase, {3} Version, {4} Status, {5} Bytes, {6} Referrer, {7} Agent, {8} Extra, {9} Member
    // # format.access.agent:     {0} Java vendor, {1} Java version, {2} Java class version, {3} OS name, {4} OS version, {5} OS architecture, {6} Vendor URL
    // # format.access.extra:     {0} Duration in seconds, {1} Referrer host, {2} Applet host, {3} Connections, {4} Public rooms, {5} Private rooms, {6} Monitor host
    // # format.public:           {0} Date, {1} Duration in seconds, {2} Room name, {3} User name, {4} User host
    // # format.private:          {0} Date, {1} Duration in seconds, {2} Room name, {3} User1 name, {4} User1 host, {5} User2 name, {6} User2 host
    // # format.banned:           {0} Date, {1} Host address, {2} Address type, {3} Room name, {4} User name, {5} Monitor name
    // # format.status:           {0} Date, {1} Memory, {2} Resources, {3} Public rooms, {4} Personal rooms, {5} Private rooms, {6} Received, {7} Sent, {8} Total
    // # format.status.memory:    {0} Kilobytes used, {1} Kilobytes available, {2} Percentage used
    // # format.status.resources: {0} Threads, {2} Connections, {3} Unique hosts
    // # format.chat.public:      {0} From name, {1} To name, {2} Message, {3} Date
    // # format.chat.private:     {0} From name, {1} To name, {2} Message, {3} Date
    //
    // format.date=[dd/MMM/yyyy:HH:mm:ss z]
    // format.access={0} - {9} {1} "GET {2} HTTP/{3}" {4,number,0} {5,number,0} "{6}" "{7}" {8}
    // format.access.agent={0}/{1} API/{2} ({3}/{4} {5}) {6}
    // format.access.extra={0,number,0} {6}
    // format.public={0} {1,number,0} "{2}" "{3}" {4}
    // format.private={0} {1,number,0} "{2}" "{3}" {4} "{5}" {6}
    // format.banned={1}\={2,choice,0\#Static|1\#Dynamic|2\#Netblock} address banned at {0} as {4} in {3} by {5}
    // format.status={0} {1} {2} {3,number,0} {4,number,0} {5,number,0} {6,number,0} {7,number,0} {8,number,0}
    // format.status.memory={0,number,0}KB/{1,number,0}KB {2,number,0%}
    // format.status.resources={0,number,0} {1,number,0} ({2,number,0})
    // format.chat.public={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0}&gt;</b> {2}<br>
    // format.chat.private={3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0} -&gt; {1}&gt;</b> {2}<br>

    String FORMAT_DATE             = "[dd/MMM/yyyy:HH:mm:ss z]";
    String FORMAT_ACCESS           = "{0} - {9} {1} \"GET {2} HTTP/{3}\" {4,number,0} {5,number,0} \"{6}\" \"{7}\" {8}";
    String FORMAT_ACCESS_AGENT     = "{0}/{1} API/{2} ({3}/{4} {5}) {6}";
    String FORMAT_ACCESS_EXTRA     = "{0,number,0} {6}";
    String FORMAT_PUBLIC           = "{0} {1,number,0} \"{2}\" \"{3}\" {4}";
    String FORMAT_PRIVATE          = "{0} {1,number,0} \"{2}\" \"{3}\" {4} \"{5}\" {6}";
    String FORMAT_BANNED           = "{1}={2,choice,0#Static|1#Dynamic|2#Netblock} address banned at {0} as {4} in {3} by {5}";
    String FORMAT_STATUS           = "{0} {1} {2} {3,number,0} {4,number,0} {5,number,0} {6,number,0} {7,number,0} {8,number,0}";
    String FORMAT_STATUS_MEMORY    = "{0,number,0}KB/{1,number,0}KB {2,number,0%}";
    String FORMAT_STATUS_RESOURCES = "{0,number,0} {1,number,0} ({2,number,0})";
    String FORMAT_CHAT_PUBLIC      = "{3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0}&gt;</b> {2}<br>";
    String FORMAT_CHAT_PRIVATE     = "{3,date,[dd/MMM/yyyy:HH:mm:ss]} <b>&lt;{0} -&gt; {1}&gt;</b> {2}<br>";

    // # Member access
    //
    // # member.access: {0} Member name, {1} Member password
    // # member.name:   {0} Member name
    //
    // member.version=
    // member.only=false
    // member.access=
    // member.name=

    String MEMBER_VERSION     = "";
    String MEMBER_ONLY        = "false";
    String MEMBER_ACCESS      = "";
    String MEMBER_NAME        = "";

    String MEMBER_VERSION_2_1 = "2.1";

    // member.monitor.matchcase=false
    // member.monitor.multiuse=false
    // member.monitors=

    String MEMBER_MONITOR_MATCHCASE = "false";
    String MEMBER_MONITOR_MULTIUSE  = "false";
    String MEMBER_MONITORS          = "";

    // # Auditorium access and creation
    //
    // auditoriums.permanent=false
    // entrance.stage=

    String AUDITORIUMS_PERMANENT = "false";
    String ENTRANCE_STAGE        = "";

    // # Access to server events
    //
    // event.callback.prefix=

    String EVENT_CALLBACK_PREFIX = "";
}
