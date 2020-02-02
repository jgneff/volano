/*
 * Key.java - an interface for defining server property keys.
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
 * This interface defines the server property names.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Key {
    // System support property keys.
    String JAVA_VENDOR           = "java.vendor";
    String JAVA_VENDOR_URL       = "java.vendor.url";
    String JAVA_VERSION          = "java.version";
    String JAVA_CLASS_VERSION    = "java.class.version";
    String JAVA_COMPILER         = "java.compiler";
    String OS_NAME               = "os.name";
    String OS_VERSION            = "os.version";
    String OS_ARCH               = "os.arch";
    String USER_LANGUAGE         = "user.language";
    String USER_REGION           = "user.region";
    String FILE_ENCODING         = "file.encoding";
    String FILE_ENCODING_PKG     = "file.encoding.pkg";
    String USER_DIR              = "user.dir";

    // InstallShield launch script property keys.
    String INSTALL_ROOT          = "install.root";

    // Applet codebase directory.
    String APPLET_CODEBASE       = "applet.codebase";

    // General properties.
    String SERVER_HOST           = "server.host";
    String SERVER_PORT           = "server.port";
    String SERVER_PASSWORD       = "server.password";
    String CLIENT_AUTHENTICATION = "client.authentication";
    String DUPLICATE_ADDRESSES   = "duplicate.addresses";
    String ADMIN_CLIENT_HOST     = "admin.client.host";
    String ADMIN_PORT            = "admin.port";
    String ADMIN_PASSWORD        = "admin.password";
    String STATUS_INTERVAL       = "status.interval";
    String STATUS_HISTORY        = "status.history";
    String SERVER_BACKLOG        = "server.backlog";
    String SERVER_LIMIT          = "server.limit";
    String SERVER_TIMEOUT        = "server.timeout";
    String SERVER_VERBOSE        = "server.verbose";
    String SERVER_TRACE          = "server.trace";
    String SERVER_NOTHREADGROUPS = "server.nothreadgroups";
    String ADDRESS_BROADCAST     = "address.broadcast";
    String SCRIPT_TIMEOUT        = "script.timeout";
    String SCRIPT_TRACE          = "script.trace";
    String ROOM_LIMIT            = "room.limit";
    String ROOM_NODYNAMIC        = "room.nodynamic";
    String ROOM_SWEEP_INTERVAL   = "room.sweep.interval";
    String USERNAME_MATCHCASE    = "username.matchcase";
    String LENGTH_CHATTEXT       = "length.chattext";
    String LENGTH_PROFILE        = "length.profile";
    String LENGTH_ROOMNAME       = "length.roomname";
    String LENGTH_USERNAME       = "length.username";

    // Servlet runner.
    String SERVLET_PORT          = "servlet.port";
    String SERVLET_MINPROCESSORS = "servlet.minprocessors";
    String SERVLET_MAXPROCESSORS = "servlet.maxprocessors";

    // Flood control.
    String DELAY_ACCESS          = "delay.access";
    String DELAY_AUTHENTICATE    = "delay.authenticate";
    String DELAY_BEEP            = "delay.beep";
    String DELAY_CHAT            = "delay.chat";
    String DELAY_ENTER_PRIVATE   = "delay.enter.private";
    String DELAY_ENTER_ROOM      = "delay.enter.room";
    String DELAY_EXIT_PRIVATE    = "delay.exit.private";
    String DELAY_EXIT_ROOM       = "delay.exit.room";
    String DELAY_KICK            = "delay.kick";
    String DELAY_PING            = "delay.ping";
    String DELAY_ROOM_LIST       = "delay.room.list";
    String DELAY_USER_LIST       = "delay.user.list";

    // Banning control.
    String DNSLIST_DENIED        = "dnslist.denied";
    String DNSLIST_DYNAMIC       = "dnslist.dynamic";
    String BAN_STATIC_DURATION   = "ban.static.duration";
    String BAN_DYNAMIC_DURATION  = "ban.dynamic.duration";
    String BAN_NETBLOCK_DURATION = "ban.netblock.duration";
    String BAN_NETBLOCK_IPV4MASK = "ban.netblock.ipv4mask";

    // Configuration files.
    String SERVER_KEY             = "server.key";
    String SERVER_ROOMS           = "server.rooms";
    String ACCESS_HOSTS_ALLOW     = "access.hosts.allow";
    String ACCESS_HOSTS_DENY      = "access.hosts.deny";
    String ACCESS_REFERRERS_ALLOW = "access.referrers.allow";
    String ACCESS_REFERRERS_DENY  = "access.referrers.deny";

    // Log files.
    String LOG_DIRECTORY          = "log.directory";
    String LOG_ACCESS_PREFIX      = "log.access.prefix";
    String LOG_ACCESS_SUFFIX      = "log.access.suffix";
    String LOG_ERROR_PREFIX       = "log.error.prefix";
    String LOG_ERROR_SUFFIX       = "log.error.suffix";
    String LOG_PUBLIC_PREFIX      = "log.public.prefix";
    String LOG_PUBLIC_SUFFIX      = "log.public.suffix";
    String LOG_PRIVATE_PREFIX     = "log.private.prefix";
    String LOG_PRIVATE_SUFFIX     = "log.private.suffix";
    String LOG_BANNED_PREFIX      = "log.banned.prefix";
    String LOG_BANNED_SUFFIX      = "log.banned.suffix";
    String LOG_SERVLET_PREFIX     = "log.servlet.prefix";
    String LOG_SERVLET_SUFFIX     = "log.servlet.suffix";
    String LOG_HTTP_PREFIX        = "log.http.prefix";
    String LOG_HTTP_SUFFIX        = "log.http.suffix";
    String LOG_VELOCITY_PREFIX    = "log.velocity.prefix";
    String LOG_VELOCITY_SUFFIX    = "log.velocity.suffix";

    String LOG_SUPPORT            = "log.support";
    String LOG_CHAT_PUBLIC_DIR    = "log.chat.public.dir";
    String LOG_CHAT_PUBLIC_SUFFIX = "log.chat.public.suffix";
    String LOG_CHAT_PRIVATE       = "log.chat.private";

    // Room transcriptions.
    String TRANSCRIBE_ROOM_PERMANENT = "transcribe.room.permanent";
    String TRANSCRIBE_ROOM_DYNAMIC   = "transcribe.room.dynamic";
    String TRANSCRIBE_ROOM_PERSONAL  = "transcribe.room.personal";
    String TRANSCRIBE_ROOM_EVENT     = "transcribe.room.event";
    String TRANSCRIBE_ROOM_PRIVATE   = "transcribe.room.private";

    // Log file formats.
    String FORMAT_DATE             = "format.date";
    String FORMAT_ACCESS           = "format.access";
    String FORMAT_ACCESS_AGENT     = "format.access.agent";
    String FORMAT_ACCESS_EXTRA     = "format.access.extra";
    String FORMAT_PUBLIC           = "format.public";
    String FORMAT_PRIVATE          = "format.private";
    String FORMAT_BANNED           = "format.banned";
    String FORMAT_STATUS           = "format.status";
    String FORMAT_STATUS_MEMORY    = "format.status.memory";
    String FORMAT_STATUS_RESOURCES = "format.status.resources";
    String FORMAT_CHAT_PUBLIC      = "format.chat.public";
    String FORMAT_CHAT_PRIVATE     = "format.chat.private";

    // Member access.
    String MEMBER_VERSION           = "member.version";
    String MEMBER_ONLY              = "member.only";
    String MEMBER_ACCESS            = "member.access";
    String MEMBER_NAME              = "member.name";

    String MEMBER_MONITOR_MATCHCASE = "member.monitor.matchcase";
    String MEMBER_MONITOR_MULTIUSE  = "member.monitor.multiuse";
    String MEMBER_MONITORS          = "member.monitors";

    // Auditorium access and creation.
    String AUDITORIUMS_PERMANENT = "auditoriums.permanent";
    String ENTRANCE_STAGE        = "entrance.stage";

    // Access to server events.
    String EVENT_CALLBACK_PREFIX = "event.callback.prefix";
}
