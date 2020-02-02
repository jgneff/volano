/*
 * Default.java - an interface for defining parameter and property defaults.
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

package COM.volano.chat.client;

/**
 * This interface defines the applet parameter and property default values.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Default {
    // System property keys.
    String  JAVA_VENDOR                = "";
    String  JAVA_VENDOR_URL            = "";
    String  JAVA_VERSION               = "";
    String  JAVA_CLASS_VERSION         = "";
    String  OS_NAME                    = "";
    String  OS_VERSION                 = "";
    String  OS_ARCH                    = "";

    // Applet parameter defaults.
    String MONITOR                     = "false";
    String ADMIN                       = "false";
    String MEMBER                      = "false";
    String STAGE                       = "false";
    String PUBLIC                      = "false";
    String PROMPT                      = "false";
    String COLOR                       = "#FFFFFF";
    String FOREGROUND                  = "#000000";
    String GROUP                       = "";
    String TOPIC                       = "";
    String TITLE                       = "";
    String TEXT                        = "english.txt";
    String USERNAME                    = "";
    String PROFILE                     = "";
    String PASSWORD                    = "";

    // Applet property defaults, with images sizes based on the Internet
    // Advertising Bureau's proposal for voluntary model banner sizes:
    //   http://www.iab.net/iab_banner_standards/bannersource.html

    // override.myvolanochat=true
    // server.port=8000
    // limit.public=5
    // limit.private=5
    // history.enable=false
    // filter.enable=false
    // send.private.disable=false
    // textarea.swing.force=false

    String OVERRIDE_MYVOLANOCHAT       = "true";
    String SERVER_PORT                 =  "8000";
    String LIMIT_PUBLIC                =     "5";
    String LIMIT_PRIVATE               =     "5";
    String HISTORY_ENABLE              = "false";
    String FILTER_ENABLE               = "false";
    String SEND_PRIVATE_DISABLE        = "false";
    String TEXTAREA_SWING_FORCE        = "false";

    // member.document=
    // member.editable.name=true
    // member.editable.profile=true
    // member.monitor=false

    String MEMBER_DOCUMENT             = "";
    String MEMBER_EDITABLE_NAME        = "true";
    String MEMBER_EDITABLE_PROFILE     = "true";
    String MEMBER_MONITOR              = "false";

    // label.text=
    // label.link=
    // label.url=
    // label.url.text=
    // label.url.link=

    String LABEL_TEXT                  = "";
    String LABEL_LINK                  = "";
    String LABEL_URL                   = "";
    String LABEL_URL_TEXT              = "";
    String LABEL_URL_LINK              = "";

    // banner.code=COM.volano.BannerPlayer.class
    // banner.parameters=BannerPlayer.txt
    // banner.width=468
    // banner.height=60

    String BANNER_CODE                 = "COM.volano.BannerPlayer.class";
    String BANNER_PARAMETERS           = "BannerPlayer.txt";
    String BANNER_WIDTH                =   "468";
    String BANNER_HEIGHT               =    "60";

    // logo.code=COM.volano.BannerPlayer.class
    // logo.parameters=LogoPlayer.txt
    // logo.width=100
    // logo.height=220

    String LOGO_CODE                   = "COM.volano.BannerPlayer.class";
    String LOGO_PARAMETERS             = "LogoPlayer.txt";
    String LOGO_WIDTH                  =   "100";
    String LOGO_HEIGHT                 =   "220";

    // color.background=#A0B8C8
    // color.background.button=#FFFFCC
    // color.background.list=#DCDCDC
    // color.background.text=#DCDCDC
    // color.background.text.editable=#FFFFFF

    String COLOR_BACKGROUND                        = "#A0B8C8";
    String COLOR_BACKGROUND_BUTTON                 = "#FFFFCC";
    String COLOR_BACKGROUND_LIST                   = "#DCDCDC";
    String COLOR_BACKGROUND_TEXT                   = "#DCDCDC";
    String COLOR_BACKGROUND_TEXT_EDITABLE          = "#FFFFFF";

    // color.foreground=#000000
    // color.foreground.button=#000000
    // color.foreground.list=#000000
    // color.foreground.text=#000000
    // color.foreground.text.editable=#000000
    // color.foreground.text.editable.inactive=#FF0000
    // color.foreground.text.link=#0000FF

    String COLOR_FOREGROUND                        = "#000000";
    String COLOR_FOREGROUND_BUTTON                 = "#000000";
    String COLOR_FOREGROUND_LIST                   = "#000000";
    String COLOR_FOREGROUND_TEXT                   = "#000000";
    String COLOR_FOREGROUND_TEXT_EDITABLE          = "#000000";
    String COLOR_FOREGROUND_TEXT_EDITABLE_INACTIVE = "#FF0000";
    String COLOR_FOREGROUND_TEXT_LINK              = "#0000FF";

    // font.default=SansSerif-13

    String FONT_DEFAULT                            = "SansSerif-13";

    // accept.private.default=true
    // alert.entrance.default=false
    // alert.audio.default=false
    // alert.count.default=false
    // webtouring.default=false

    String ACCEPT_PRIVATE_DEFAULT      = "true";
    String ALERT_ENTRANCE_DEFAULT      = "false";
    String ALERT_AUDIO_DEFAULT         = "false";
    String ALERT_COUNT_DEFAULT         = "false";
    String WEBTOURING_DEFAULT          = "false";

    // link.prefix=http:// ftp:// news: mailto:
    // link.profile.disable=false
    // link.profile.url=%0
    // link.referrer.disable=false
    // link.referrer.url=%0

    String LINK_PREFIX                 = "http:// ftp:// news: mailto:";
    String LINK_PROFILE_DISABLE        = "false";
    String LINK_PROFILE_URL            = "%0";
    String LINK_REFERRER_DISABLE       = "false";
    String LINK_REFERRER_URL           = "%0";

    // image.button.border=true
    // image.button1=button1.gif
    // image.button2=button2.gif
    // image.button.width=88
    // image.button.height=31
    // image.logo=logo.gif
    // image.logo.width=100
    // image.logo.height=200
    // image.logo.background=#FFFFFF

    String IMAGE_BUTTON_BORDER         = "true";
    String IMAGE_BUTTON1               = "button1.gif";
    String IMAGE_BUTTON2               = "button2.gif";
    String IMAGE_BUTTON_WIDTH          =    "88"; // 88 x 31 micro button
    String IMAGE_BUTTON_HEIGHT         =    "31";
    String IMAGE_LOGO                  = "logo.gif";
    String IMAGE_LOGO_WIDTH            =   "100"; // 120 x 240 vertical banner
    String IMAGE_LOGO_HEIGHT           =   "200";
    String IMAGE_LOGO_BACKGROUND       = "#FFFFFF";

    // length.chattext=200
    // length.profile=200
    // length.roomname=100
    // length.username=20

    String LENGTH_CHATTEXT             =  "200";
    String LENGTH_PROFILE              =  "200";
    String LENGTH_ROOMNAME             =  "100";
    String LENGTH_USERNAME             =   "20";

    // delay.keystroke=0
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

    String DELAY_KEYSTROKE             = "0";
    String DELAY_ACCESS                = "0";
    String DELAY_AUTHENTICATE          = "0";
    String DELAY_BEEP                  = "1000";
    String DELAY_CHAT                  = "1000";
    String DELAY_ENTER_PRIVATE         = "1000";
    String DELAY_ENTER_ROOM            = "1000";
    String DELAY_EXIT_PRIVATE          = "0";
    String DELAY_EXIT_ROOM             = "0";
    String DELAY_KICK                  = "0";
    String DELAY_PING                  = "0";
    String DELAY_ROOM_LIST             = "0";
    String DELAY_USER_LIST             = "0";

    // unconfirmed.chat=0

    String UNCONFIRMED_CHAT            = "0";

    // page.newwindow=false
    // page.use.document.base=false
    // page.access.document=document.html
    // page.access.host=host.html
    // page.access.password=password.html
    // page.access.unable=unable.html
    // page.access.version=version.html
    // page.access.duplicate=duplicate.html
    // page.java.version=java.html
    // page.help=help.html
    // page.about=about.html
    // page.exit=
    // page.exit.error=

    String PAGE_NEWWINDOW              = "false";
    String PAGE_USE_DOCUMENT_BASE      = "false";
    String PAGE_ACCESS_DOCUMENT        = "document.html";
    String PAGE_ACCESS_HOST            = "host.html";
    String PAGE_ACCESS_PASSWORD        = "password.html";
    String PAGE_ACCESS_UNABLE          = "unable.html";
    String PAGE_ACCESS_VERSION         = "version.html";
    String PAGE_ACCESS_DUPLICATE       = "duplicate.html";
    String PAGE_JAVA_VERSION           = "java.html";
    String PAGE_HELP                   = "help.html";
    String PAGE_ABOUT                  = "about.html";
    String PAGE_EXIT                   = "";
    String PAGE_EXIT_ERROR             = "";

    // sound.start=
    // sound.stop=
    // sound.enter=
    // sound.exit=
    // sound.rooms=
    // sound.users=
    // sound.profile=
    // sound.alert=drip.au

    String SOUND_START                 = "";
    String SOUND_STOP                  = "";
    String SOUND_ENTER                 = "";
    String SOUND_EXIT                  = "";
    String SOUND_ROOMS                 = "";
    String SOUND_USERS                 = "";
    String SOUND_PROFILE               = "";
    String SOUND_ALERT                 = "drip.au";

    // key.ignore.alt=
    // key.ignore.ctrl=\\u0016
    // key.ignore.meta=\\u0076
    // key.ignore.shift=\\u0401

    String KEY_IGNORE_ALT              = "";
    String KEY_IGNORE_CTRL             = "\u0016";
    String KEY_IGNORE_META             = "\u0076";
    String KEY_IGNORE_SHIFT            = "\u0401";

    // char.replace.nonprintable=false
    // char.replace.old=\\u000a\\u000d\\u00a0
    // char.replace.new=\\u0020\\u0020\\u0020

    String CHAR_REPLACE_NONPRINTABLE   = "false";
    String CHAR_REPLACE_OLD            = "\n\r\u00a0";
    String CHAR_REPLACE_NEW            = "   ";

    // text.f1=
    // text.f2=
    // text.f3=
    // text.f4=
    // text.f5=
    // text.f6=
    // text.f7=
    // text.f8=
    // text.f9=
    // text.f10=
    // text.f11=
    // text.f12=

    String TEXT_F1                     = "";
    String TEXT_F2                     = "";
    String TEXT_F3                     = "";
    String TEXT_F4                     = "";
    String TEXT_F5                     = "";
    String TEXT_F6                     = "";
    String TEXT_F7                     = "";
    String TEXT_F8                     = "";
    String TEXT_F9                     = "";
    String TEXT_F10                    = "";
    String TEXT_F11                    = "";
    String TEXT_F12                    = "";

    // text.button.status=Start VolanoChat
    // text.button.message=Click button to start.
    // text.button.connecting=Contacting host %0...
    // text.button.accessing=Host contacted, requesting access...
    // text.button.authenticating=Access granted, authenticating client...
    // text.button.notconnected=Unable to connect to host %0 on port %1.
    // text.button.admin=Administrator password:
    // text.button.monitor=Monitor password:

    String TEXT_BUTTON_STATUS          = "Start VolanoChat";
    String TEXT_BUTTON_MESSAGE         = "Click button to start.";
    String TEXT_BUTTON_CONNECTING      = "Contacting host %0...";
    String TEXT_BUTTON_ACCESSING       = "Host contacted, requesting access...";
    String TEXT_BUTTON_AUTHENTICATING  = "Access granted, authenticating client...";
    String TEXT_BUTTON_NOTCONNECTED    = "Unable to connect to host %0 on port %1.";
    String TEXT_BUTTON_ADMIN           = "Administrator password:";
    String TEXT_BUTTON_MONITOR         = "Monitor password:";

    // text.member.name=Member name:
    // text.member.password=Member password:
    // text.member.profile=[Member Profile]

    String TEXT_MEMBER_NAME            = "Member name:";
    String TEXT_MEMBER_PASSWORD        = "Member password:";
    String TEXT_MEMBER_PROFILE         = "[Member Profile]";

    // text.main.title=VolanoChat Applet
    // text.main.logo=
    // text.main.rooms=Rooms:
    // text.main.norooms=Rooms:
    // text.main.oneroom=1 room:
    // text.main.manyrooms=%0 rooms:
    // text.main.users=People:
    // text.main.nousers=People:
    // text.main.oneuser=1 person:
    // text.main.manyusers=%0 persons:
    // text.main.onstage=On stage:
    // text.main.filter=Room filter:
    // text.main.username=Your name:
    // text.main.profile=Your profile:
    // text.main.broadcast=Broadcast:
    // text.main.getrooms=Get Rooms
    // text.main.enter=Enter Room
    // text.main.connect=Connect
    // text.main.disconnect=Disconnect

    String TEXT_MAIN_TITLE             = "VolanoChat Applet";
    String TEXT_MAIN_LOGO              = "";
    String TEXT_MAIN_ROOMS             = "Rooms:";
    String TEXT_MAIN_NOROOMS           = "Rooms:";
    String TEXT_MAIN_ONEROOM           = "1 room:";
    String TEXT_MAIN_MANYROOMS         = "%0 rooms:";
    String TEXT_MAIN_USERS             = "People:";
    String TEXT_MAIN_NOUSERS           = "People:";
    String TEXT_MAIN_ONEUSER           = "1 person:";
    String TEXT_MAIN_MANYUSERS         = "%0 persons:";
    String TEXT_MAIN_ONSTAGE           = "On stage:";
    String TEXT_MAIN_FILTER            = "Room filter:";
    String TEXT_MAIN_USERNAME          = "Your name:";
    String TEXT_MAIN_PROFILE           = "Your profile:";
    String TEXT_MAIN_BROADCAST         = "Broadcast:";
    String TEXT_MAIN_GETROOMS          = "Get Rooms";
    String TEXT_MAIN_ENTER             = "Enter Room";
    String TEXT_MAIN_CONNECT           = "Connect";
    String TEXT_MAIN_DISCONNECT        = "Disconnect";

    // text.chat.status=Select a name for the profile. Double click a name for private chat.
    // text.chat.event.status=Type your question and press Enter to send it.
    // text.chat.event.sent=Your question has been submitted to the moderator.

    String TEXT_CHAT_STATUS            = "Select a name for the profile. Double click a name for private chat.";
    String TEXT_CHAT_EVENT_STATUS      = "Type your question and press Enter to send it.";
    String TEXT_CHAT_EVENT_SENT        = "Your question has been submitted to the moderator.";

    // text.menu.places=Places
    // text.menu.getrooms=Get Rooms
    // text.menu.enter=Enter Room
    // text.menu.exit=Exit
    // text.menu.options=Options
    // text.menu.font.name=Font Name
    // text.menu.font.style=Font Style
    // text.menu.font.regular=Regular
    // text.menu.font.italic=Italic
    // text.menu.font.bold=Bold
    // text.menu.font.bolditalic=Bold Italic
    // text.menu.font.increase=Increase Font
    // text.menu.font.decrease=Decrease Font
    // text.menu.accept.private=Accept Private Chats
    // text.menu.alert.entrance=Entrance Alerts
    // text.menu.alert.audio=Audio Alerts
    // text.menu.alert.count=Show Count Changes
    // text.menu.webtouring=Web Touring
    // text.menu.help=Help
    // text.menu.topics=Help Contents
    // text.menu.about=About VolanoChat

    String TEXT_MENU_PLACES            = "Places";
    String TEXT_MENU_GETROOMS          = "Get Rooms";
    String TEXT_MENU_ENTER             = "Enter Room";
    String TEXT_MENU_EXIT              = "Exit";
    String TEXT_MENU_OPTIONS           = "Options";
    String TEXT_MENU_FONT_NAME         = "Font Name";
    String TEXT_MENU_FONT_STYLE        = "Font Style";
    String TEXT_MENU_FONT_REGULAR      = "Regular";
    String TEXT_MENU_FONT_ITALIC       = "Italic";
    String TEXT_MENU_FONT_BOLD         = "Bold";
    String TEXT_MENU_FONT_BOLDITALIC   = "Bold Italic";
    String TEXT_MENU_FONT_INCREASE     = "Increase Font";
    String TEXT_MENU_FONT_DECREASE     = "Decrease Font";
    String TEXT_MENU_ACCEPT_PRIVATE    = "Accept Private Chats";
    String TEXT_MENU_ALERT_ENTRANCE    = "Entrance Alerts";
    String TEXT_MENU_ALERT_AUDIO       = "Audio Alerts";
    String TEXT_MENU_ALERT_COUNT       = "Show Count Changes";
    String TEXT_MENU_WEBTOURING        = "Web Touring";
    String TEXT_MENU_HELP              = "Help";
    String TEXT_MENU_TOPICS            = "Help Contents";
    String TEXT_MENU_ABOUT             = "About VolanoChat";

    // Leave the defaults for remove, kick and ban as empty strings so that
    // administrators can control what functions monitors have in the applet.
    // With a non-empty string default, monitors can remove the "text" applet
    // parameter to obtain the menu items.

    // text.menu.room=Room
    // text.menu.close=Close
    // text.menu.people=People
    // text.menu.people.ring=Ring %0
    // text.menu.people.ignore=Ignore %0
    // text.menu.people.unignore=Unignore %0
    // text.menu.people.count=Count
    // text.menu.monitor=Monitor
    // text.menu.monitor.remove=
    // text.menu.monitor.kick=
    // text.menu.monitor.ban=

    String TEXT_MENU_ROOM              = "Room";
    String TEXT_MENU_CLOSE             = "Close";
    String TEXT_MENU_PEOPLE            = "People";
    String TEXT_MENU_PEOPLE_RING       = "Ring %0";
    String TEXT_MENU_PEOPLE_IGNORE     = "Ignore %0";
    String TEXT_MENU_PEOPLE_UNIGNORE   = "Unignore %0";
    String TEXT_MENU_PEOPLE_COUNT      = "Count";
    String TEXT_MENU_MONITOR           = "Monitor";
    String TEXT_MENU_MONITOR_REMOVE    = "";
    String TEXT_MENU_MONITOR_KICK      = "";
    String TEXT_MENU_MONITOR_BAN       = "";

    // text.menu.links.title=Links
    // text.menu.links.names=Java - Get It Now!
    // text.menu.links.locations=http://www.java.com/

    String TEXT_MENU_LINKS_TITLE       = "Links";
    String TEXT_MENU_LINKS_NAMES       = "Java - Get It Now!";
    String TEXT_MENU_LINKS_LOCATIONS   = "http://www.java.com/";

    // text.menu.themes.title=Themes
    // text.menu.themes.names=Plum | Desert | Marine | Pumpkin | Lime | - | High Contrast Black | High Contrast Black (large) | High Contrast White | High Contrast White (large)
    // text.menu.themes.default=Standard

    // Note: different from "english.txt" file!
    // The list of theme names is the only property in this file that is different
    // from the "english.txt" file.  Don't list themes that aren't present when
    // we use the internal defaults.
    String TEXT_MENU_THEMES_TITLE      = "Themes";
    String TEXT_MENU_THEMES_NAMES      = "";
    String TEXT_MENU_THEMES_DEFAULT    = "Standard";

    // recent.user.limit=50
    // text.monitor.title.remove=Remove From Room
    // text.monitor.title.kick=Kick From Server
    // text.monitor.title.ban=Ban From Server
    // text.monitor.label.remove=Select the name to remove:
    // text.monitor.label.kick=Select the address to disconnect:
    // text.monitor.label.ban=Select the address to ban:
    // text.monitor.okay=OK
    // text.monitor.cancel=Cancel

    String RECENT_USER_LIMIT           = "50";
    String TEXT_MONITOR_TITLE_REMOVE   = "Remove From Room";
    String TEXT_MONITOR_TITLE_KICK     = "Kick From Server";
    String TEXT_MONITOR_TITLE_BAN      = "Ban From Server";
    String TEXT_MONITOR_LABEL_REMOVE   = "Select the name to remove:";
    String TEXT_MONITOR_LABEL_KICK     = "Select the address to disconnect:";
    String TEXT_MONITOR_LABEL_BAN      = "Select the address to ban:";
    String TEXT_MONITOR_OKAY           = "OK";
    String TEXT_MONITOR_CANCEL         = "Cancel";

    // text.status.focus.rooms=List of rooms.
    // text.status.focus.users=List of people in room.
    // text.status.focus.filter=Filter for list of room names.
    // text.status.focus.username=Your name or nickname.
    // text.status.focus.profile=Optional personal information such as a Web or e-mail address.
    // text.status.focus.getrooms=Get list of room names matching filter.
    // text.status.focus.enter=Enter a room.
    // text.status.focus.membername=Your member name.
    // text.status.focus.memberpassword=Your member password.

    String TEXT_STATUS_FOCUS_ROOMS          = "List of rooms.";
    String TEXT_STATUS_FOCUS_USERS          = "List of people in room.";
    String TEXT_STATUS_FOCUS_FILTER         = "Filter for list of room names.";
    String TEXT_STATUS_FOCUS_USERNAME       = "Your name or nickname.";
    String TEXT_STATUS_FOCUS_PROFILE        = "Optional personal information such as a Web or e-mail address.";
    String TEXT_STATUS_FOCUS_GETROOMS       = "Get list of room names matching filter.";
    String TEXT_STATUS_FOCUS_ENTER          = "Enter a room.";
    String TEXT_STATUS_FOCUS_MEMBERNAME     = "Your member name.";
    String TEXT_STATUS_FOCUS_MEMBERPASSWORD = "Your member password.";

    // text.status.selectroom=Select a room to enter.
    // text.status.entername=Enter a name to join %0.
    // text.status.enterpassword=Enter your password.
    // text.status.entermembername=Enter your member name.
    // text.status.entermemberpassword=Enter your member password.
    // text.status.enterprofile=Enter an optional profile.
    // text.status.enter=Press Enter Room to enter %0.
    // text.status.enteringroom=Entering %0...
    // text.status.enteringprivate=Starting private chat with %0...
    // text.status.gettingrooms=Getting list of rooms...
    // text.status.gettingusers=Getting list of people in room...
    // text.status.gettingprofile=Getting %0's profile...
    // text.status.nosuchroom=Room no longer exists. Press Get Rooms.
    // text.status.nosuchuser=User is no longer in room.
    // text.status.nametaken=The name %0 is already taken in %1.
    // text.status.membertaken=The name %0 belongs to a member. Please choose another name.
    // text.status.alreadyinroom=Already in %0.
    // text.status.roomfull=%0 is full. Select another room or try again later.
    // text.status.roomcount=Room count = %0.
    // text.status.publiclimit=You are limited to %0 chat rooms.
    // text.status.privatelimit=You are limited to %0 private chat sessions.
    // text.status.noprofile=%1 %0 has no profile.
    // text.status.profile=%1 %0: %2
    // text.status.closing=Closing VolanoChat...

    String TEXT_STATUS_SELECTROOM          = "Select a room to enter.";
    String TEXT_STATUS_ENTERNAME           = "Enter a name to join %0.";
    String TEXT_STATUS_ENTERPASSWORD       = "Enter your password.";
    String TEXT_STATUS_ENTERMEMBERNAME     = "Enter your member name.";
    String TEXT_STATUS_ENTERMEMBERPASSWORD = "Enter your member password.";
    String TEXT_STATUS_ENTERPROFILE        = "Enter an optional profile.";
    String TEXT_STATUS_ENTER               = "Press Enter Room to enter %0.";
    String TEXT_STATUS_ENTERINGROOM        = "Entering %0...";
    String TEXT_STATUS_ENTERINGPRIVATE     = "Starting private chat with %0...";
    String TEXT_STATUS_GETTINGROOMS        = "Getting list of rooms...";
    String TEXT_STATUS_GETTINGUSERS        = "Getting list of people in room...";
    String TEXT_STATUS_GETTINGPROFILE      = "Getting %0's profile...";
    String TEXT_STATUS_NOSUCHROOM          = "Room no longer exists. Press Get Rooms.";
    String TEXT_STATUS_NOSUCHUSER          = "User is no longer in room.";
    String TEXT_STATUS_NAMETAKEN           = "The name %0 is already taken in %1.";
    String TEXT_STATUS_MEMBERTAKEN         = "The name %0 belongs to a member. Please choose another name.";
    String TEXT_STATUS_ALREADYINROOM       = "Already in %0.";
    String TEXT_STATUS_ROOMFULL            = "%0 is full. Select another room or try again later.";
    String TEXT_STATUS_ROOMCOUNT           = "Room count = %0.";
    String TEXT_STATUS_PUBLICLIMIT         = "You are limited to %0 chat rooms.";
    String TEXT_STATUS_PRIVATELIMIT        = "You are limited to %0 private chat sessions.";
    String TEXT_STATUS_NOPROFILE           = "%1 %0 has no profile.";
    String TEXT_STATUS_PROFILE             = "%1 %0: %2";
    String TEXT_STATUS_CLOSING             = "Closing VolanoChat...";

    // text.system.entrance=[%0] %2 %1: %3
    // text.system.audio=[%0] Audio alert from %1.
    // text.system.broadcast=[%0] %1
    // text.system.partnerleft=[%0] %1 left private chat.
    // text.system.disconnected=[%0] Disconnected. Close VolanoChat and restart.

    String TEXT_SYSTEM_ENTRANCE            = "[%0] %2 %1: %3";
    String TEXT_SYSTEM_AUDIO               = "[%0] Audio alert from %1.";
    String TEXT_SYSTEM_BROADCAST           = "[%0] %1";
    String TEXT_SYSTEM_PARTNERLEFT         = "[%0] %1 left private chat.";
    String TEXT_SYSTEM_DISCONNECTED        = "[%0] Disconnected. Close VolanoChat and restart.";
}
