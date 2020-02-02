/*
 * Key.java - an interface for defining parameter and property keys.
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
 * This interface defines the applet parameter and property names.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Key {
    // System property keys.
    String  JAVA_VENDOR                = "java.vendor";
    String  JAVA_VENDOR_URL            = "java.vendor.url";
    String  JAVA_VERSION               = "java.version";
    String  JAVA_CLASS_VERSION         = "java.class.version";
    String  OS_NAME                    = "os.name";
    String  OS_VERSION                 = "os.version";
    String  OS_ARCH                    = "os.arch";

    // Applet parameter keys.
    String MONITOR                     = "monitor";
    String ADMIN                       = "admin";
    String MEMBER                      = "member";
    String STAGE                       = "stage";
    String PUBLIC                      = "public";
    String PROMPT                      = "prompt";
    String COLOR                       = "color";
    String FOREGROUND                  = "foreground";
    String GROUP                       = "group";
    String TOPIC                       = "topic";
    String TITLE                       = "title";
    String TEXT                        = "text";
    String USERNAME                    = "username";
    String PROFILE                     = "profile";
    String PASSWORD                    = "password";

    // Applet property keys.
    String OVERRIDE_MYVOLANOCHAT       = "override.myvolanochat";
    String SERVER_PORT                 = "server.port";
    String LIMIT_PUBLIC                = "limit.public";
    String LIMIT_PRIVATE               = "limit.private";
    String HISTORY_ENABLE              = "history.enable";
    String FILTER_ENABLE               = "filter.enable";
    String SEND_PRIVATE_DISABLE        = "send.private.disable";
    String TEXTAREA_SWING_FORCE        = "textarea.swing.force";


    String MEMBER_DOCUMENT             = "member.document";
    String MEMBER_EDITABLE_NAME        = "member.editable.name";
    String MEMBER_EDITABLE_PROFILE     = "member.editable.profile";
    String MEMBER_MONITOR              = "member.monitor";

    String LABEL_TEXT                  = "label.text";
    String LABEL_LINK                  = "label.link";
    String LABEL_URL                   = "label.url";
    String LABEL_URL_TEXT              = "label.url.text";
    String LABEL_URL_LINK              = "label.url.link";

    String BANNER_CODE                 = "banner.code";
    String BANNER_PARAMETERS           = "banner.parameters";
    String BANNER_WIDTH                = "banner.width";
    String BANNER_HEIGHT               = "banner.height";
    String BANNER_PARAM_PREFIX         = "banner.param.";

    String LOGO_CODE                   = "logo.code";
    String LOGO_PARAMETERS             = "logo.parameters";
    String LOGO_WIDTH                  = "logo.width";
    String LOGO_HEIGHT                 = "logo.height";
    String LOGO_PARAM_PREFIX           = "logo.param.";

    String THEME_PREFIX                = "theme.";

    String COLOR_BACKGROUND                        = "color.background";
    String COLOR_BACKGROUND_BUTTON                 = "color.background.button";
    String COLOR_BACKGROUND_LIST                   = "color.background.list";
    String COLOR_BACKGROUND_TEXT                   = "color.background.text";
    String COLOR_BACKGROUND_TEXT_EDITABLE          = "color.background.text.editable";

    String COLOR_FOREGROUND                        = "color.foreground";
    String COLOR_FOREGROUND_BUTTON                 = "color.foreground.button";
    String COLOR_FOREGROUND_LIST                   = "color.foreground.list";
    String COLOR_FOREGROUND_TEXT                   = "color.foreground.text";
    String COLOR_FOREGROUND_TEXT_EDITABLE          = "color.foreground.text.editable";
    String COLOR_FOREGROUND_TEXT_EDITABLE_INACTIVE = "color.foreground.text.editable.inactive";
    String COLOR_FOREGROUND_TEXT_LINK              = "color.foreground.text.link";

    String FONT_DEFAULT                            = "font.default";

    String ACCEPT_PRIVATE_DEFAULT      = "accept.private.default";
    String ALERT_ENTRANCE_DEFAULT      = "alert.entrance.default";
    String ALERT_AUDIO_DEFAULT         = "alert.audio.default";
    String ALERT_COUNT_DEFAULT         = "alert.count.default";
    String WEBTOURING_DEFAULT          = "webtouring.default";
    String RECENT_USER_LIMIT           = "recent.user.limit";

    String LINK_PREFIX                 = "link.prefix";
    String LINK_PROFILE_DISABLE        = "link.profile.disable";
    String LINK_PROFILE_URL            = "link.profile.url";
    String LINK_REFERRER_DISABLE       = "link.referrer.disable";
    String LINK_REFERRER_URL           = "link.referrer.url";

    String IMAGE_BUTTON_BORDER         = "image.button.border";
    String IMAGE_BUTTON1               = "image.button1";
    String IMAGE_BUTTON2               = "image.button2";
    String IMAGE_BUTTON_WIDTH          = "image.button.width";
    String IMAGE_BUTTON_HEIGHT         = "image.button.height";
    String IMAGE_LOGO                  = "image.logo";
    String IMAGE_LOGO_WIDTH            = "image.logo.width";
    String IMAGE_LOGO_HEIGHT           = "image.logo.height";
    String IMAGE_LOGO_BACKGROUND       = "image.logo.background";

    String LENGTH_CHATTEXT             = "length.chattext";
    String LENGTH_PROFILE              = "length.profile";
    String LENGTH_ROOMNAME             = "length.roomname";
    String LENGTH_USERNAME             = "length.username";

    String DELAY_KEYSTROKE             = "delay.keystroke";
    String DELAY_ACCESS                = "delay.access";
    String DELAY_AUTHENTICATE          = "delay.authenticate";
    String DELAY_BEEP                  = "delay.beep";
    String DELAY_CHAT                  = "delay.chat";
    String DELAY_ENTER_PRIVATE         = "delay.enter.private";
    String DELAY_ENTER_ROOM            = "delay.enter.room";
    String DELAY_EXIT_PRIVATE          = "delay.exit.private";
    String DELAY_EXIT_ROOM             = "delay.exit.room";
    String DELAY_KICK                  = "delay.kick";
    String DELAY_PING                  = "delay.ping";
    String DELAY_ROOM_LIST             = "delay.room.list";
    String DELAY_USER_LIST             = "delay.user.list";

    String UNCONFIRMED_CHAT            = "unconfirmed.chat";

    String PAGE_NEWWINDOW              = "page.newwindow";
    String PAGE_USE_DOCUMENT_BASE      = "page.use.document.base";
    String PAGE_ACCESS_DOCUMENT        = "page.access.document";
    String PAGE_ACCESS_HOST            = "page.access.host";
    String PAGE_ACCESS_PASSWORD        = "page.access.password";
    String PAGE_ACCESS_UNABLE          = "page.access.unable";
    String PAGE_ACCESS_VERSION         = "page.access.version";
    String PAGE_ACCESS_DUPLICATE       = "page.access.duplicate";
    String PAGE_JAVA_VERSION           = "page.java.version";
    String PAGE_HELP                   = "page.help";
    String PAGE_ABOUT                  = "page.about";
    String PAGE_EXIT                   = "page.exit";
    String PAGE_EXIT_ERROR             = "page.exit.error";

    String SOUND_START                 = "sound.start";
    String SOUND_STOP                  = "sound.stop";
    String SOUND_ENTER                 = "sound.enter";
    String SOUND_EXIT                  = "sound.exit";
    String SOUND_ROOMS                 = "sound.rooms";
    String SOUND_USERS                 = "sound.users";
    String SOUND_PROFILE               = "sound.profile";
    String SOUND_ALERT                 = "sound.alert";

    String KEY_IGNORE_ALT              = "key.ignore.alt";
    String KEY_IGNORE_CTRL             = "key.ignore.ctrl";
    String KEY_IGNORE_META             = "key.ignore.meta";
    String KEY_IGNORE_SHIFT            = "key.ignore.shift";

    String CHAR_REPLACE_NONPRINTABLE   = "char.replace.nonprintable";
    String CHAR_REPLACE_OLD            = "char.replace.old";
    String CHAR_REPLACE_NEW            = "char.replace.new";

    String TEXT_F1                     = "text.f1";
    String TEXT_F2                     = "text.f2";
    String TEXT_F3                     = "text.f3";
    String TEXT_F4                     = "text.f4";
    String TEXT_F5                     = "text.f5";
    String TEXT_F6                     = "text.f6";
    String TEXT_F7                     = "text.f7";
    String TEXT_F8                     = "text.f8";
    String TEXT_F9                     = "text.f9";
    String TEXT_F10                    = "text.f10";
    String TEXT_F11                    = "text.f11";
    String TEXT_F12                    = "text.f12";

    String TEXT_BUTTON_STATUS          = "text.button.status";
    String TEXT_BUTTON_MESSAGE         = "text.button.message";
    String TEXT_BUTTON_CONNECTING      = "text.button.connecting";
    String TEXT_BUTTON_ACCESSING       = "text.button.accessing";
    String TEXT_BUTTON_AUTHENTICATING  = "text.button.authenticating";
    String TEXT_BUTTON_NOTCONNECTED    = "text.button.notconnected";
    String TEXT_BUTTON_ADMIN           = "text.button.admin";
    String TEXT_BUTTON_MONITOR         = "text.button.monitor";

    String TEXT_MEMBER_NAME            = "text.member.name";
    String TEXT_MEMBER_PASSWORD        = "text.member.password";
    String TEXT_MEMBER_PROFILE         = "text.member.profile";

    String TEXT_MAIN_TITLE             = "text.main.title";
    String TEXT_MAIN_LOGO              = "text.main.logo";
    String TEXT_MAIN_ROOMS             = "text.main.rooms";
    String TEXT_MAIN_NOROOMS           = "text.main.norooms";
    String TEXT_MAIN_ONEROOM           = "text.main.oneroom";
    String TEXT_MAIN_MANYROOMS         = "text.main.manyrooms";
    String TEXT_MAIN_USERS             = "text.main.users";
    String TEXT_MAIN_NOUSERS           = "text.main.nousers";
    String TEXT_MAIN_ONEUSER           = "text.main.oneuser";
    String TEXT_MAIN_MANYUSERS         = "text.main.manyusers";
    String TEXT_MAIN_ONSTAGE           = "text.main.onstage";
    String TEXT_MAIN_FILTER            = "text.main.filter";
    String TEXT_MAIN_USERNAME          = "text.main.username";
    String TEXT_MAIN_PROFILE           = "text.main.profile";
    String TEXT_MAIN_BROADCAST         = "text.main.broadcast";
    String TEXT_MAIN_GETROOMS          = "text.main.getrooms";
    String TEXT_MAIN_ENTER             = "text.main.enter";
    String TEXT_MAIN_CONNECT           = "text.main.connect";
    String TEXT_MAIN_DISCONNECT        = "text.main.disconnect";

    String TEXT_CHAT_STATUS            = "text.chat.status";
    String TEXT_CHAT_EVENT_STATUS      = "text.chat.event.status";
    String TEXT_CHAT_EVENT_SENT        = "text.chat.event.sent";

    String TEXT_MENU_PLACES            = "text.menu.places";
    String TEXT_MENU_GETROOMS          = "text.menu.getrooms";
    String TEXT_MENU_ENTER             = "text.menu.enter";
    String TEXT_MENU_EXIT              = "text.menu.exit";
    String TEXT_MENU_OPTIONS           = "text.menu.options";
    String TEXT_MENU_FONT_NAME         = "text.menu.font.name";
    String TEXT_MENU_FONT_STYLE        = "text.menu.font.style";
    String TEXT_MENU_FONT_REGULAR      = "text.menu.font.regular";
    String TEXT_MENU_FONT_ITALIC       = "text.menu.font.italic";
    String TEXT_MENU_FONT_BOLD         = "text.menu.font.bold";
    String TEXT_MENU_FONT_BOLDITALIC   = "text.menu.font.bolditalic";
    String TEXT_MENU_FONT_INCREASE     = "text.menu.font.increase";
    String TEXT_MENU_FONT_DECREASE     = "text.menu.font.decrease";
    String TEXT_MENU_ACCEPT_PRIVATE    = "text.menu.accept.private";
    String TEXT_MENU_ALERT_ENTRANCE    = "text.menu.alert.entrance";
    String TEXT_MENU_ALERT_AUDIO       = "text.menu.alert.audio";
    String TEXT_MENU_ALERT_COUNT       = "text.menu.alert.count";
    String TEXT_MENU_WEBTOURING        = "text.menu.webtouring";
    String TEXT_MENU_HELP              = "text.menu.help";
    String TEXT_MENU_TOPICS            = "text.menu.topics";
    String TEXT_MENU_ABOUT             = "text.menu.about";

    String TEXT_MENU_ROOM              = "text.menu.room";
    String TEXT_MENU_CLOSE             = "text.menu.close";
    String TEXT_MENU_PEOPLE            = "text.menu.people";
    String TEXT_MENU_PEOPLE_RING       = "text.menu.people.ring";
    String TEXT_MENU_PEOPLE_IGNORE     = "text.menu.people.ignore";
    String TEXT_MENU_PEOPLE_UNIGNORE   = "text.menu.people.unignore";
    String TEXT_MENU_PEOPLE_COUNT      = "text.menu.people.count";
    String TEXT_MENU_MONITOR           = "text.menu.monitor";
    String TEXT_MENU_MONITOR_REMOVE    = "text.menu.monitor.remove";
    String TEXT_MENU_MONITOR_KICK      = "text.menu.monitor.kick";
    String TEXT_MENU_MONITOR_BAN       = "text.menu.monitor.ban";

    String TEXT_MENU_LINKS_TITLE       = "text.menu.links.title";
    String TEXT_MENU_LINKS_NAMES       = "text.menu.links.names";
    String TEXT_MENU_LINKS_LOCATIONS   = "text.menu.links.locations";

    String TEXT_MENU_THEMES_TITLE      = "text.menu.themes.title";
    String TEXT_MENU_THEMES_NAMES      = "text.menu.themes.names";
    String TEXT_MENU_THEMES_DEFAULT    = "text.menu.themes.default";

    String TEXT_MONITOR_TITLE_REMOVE   = "text.monitor.title.remove";
    String TEXT_MONITOR_TITLE_KICK     = "text.monitor.title.kick";
    String TEXT_MONITOR_TITLE_BAN      = "text.monitor.title.ban";
    String TEXT_MONITOR_LABEL_REMOVE   = "text.monitor.label.remove";
    String TEXT_MONITOR_LABEL_KICK     = "text.monitor.label.kick";
    String TEXT_MONITOR_LABEL_BAN      = "text.monitor.label.ban";
    String TEXT_MONITOR_OKAY           = "text.monitor.okay";
    String TEXT_MONITOR_CANCEL         = "text.monitor.cancel";

    String TEXT_STATUS_FOCUS_ROOMS          = "text.status.focus.rooms";
    String TEXT_STATUS_FOCUS_USERS          = "text.status.focus.users";
    String TEXT_STATUS_FOCUS_FILTER         = "text.status.focus.filter";
    String TEXT_STATUS_FOCUS_USERNAME       = "text.status.focus.username";
    String TEXT_STATUS_FOCUS_PROFILE        = "text.status.focus.profile";
    String TEXT_STATUS_FOCUS_GETROOMS       = "text.status.focus.getrooms";
    String TEXT_STATUS_FOCUS_ENTER          = "text.status.focus.enter";
    String TEXT_STATUS_FOCUS_MEMBERNAME     = "text.status.focus.membername";
    String TEXT_STATUS_FOCUS_MEMBERPASSWORD = "text.status.focus.memberpassword";

    String TEXT_STATUS_SELECTROOM          = "text.status.selectroom";
    String TEXT_STATUS_ENTERNAME           = "text.status.entername";
    String TEXT_STATUS_ENTERPASSWORD       = "text.status.enterpassword";
    String TEXT_STATUS_ENTERMEMBERNAME     = "text.status.entermembername";
    String TEXT_STATUS_ENTERMEMBERPASSWORD = "text.status.entermemberpassword";
    String TEXT_STATUS_ENTERPROFILE        = "text.status.enterprofile";
    String TEXT_STATUS_ENTER               = "text.status.enter";
    String TEXT_STATUS_ENTERINGROOM        = "text.status.enteringroom";
    String TEXT_STATUS_ENTERINGPRIVATE     = "text.status.enteringprivate";
    String TEXT_STATUS_GETTINGROOMS        = "text.status.gettingrooms";
    String TEXT_STATUS_GETTINGUSERS        = "text.status.gettingusers";
    String TEXT_STATUS_GETTINGPROFILE      = "text.status.gettingprofile";
    String TEXT_STATUS_NOSUCHROOM          = "text.status.nosuchroom";
    String TEXT_STATUS_NOSUCHUSER          = "text.status.nosuchuser";
    String TEXT_STATUS_NAMETAKEN           = "text.status.nametaken";
    String TEXT_STATUS_MEMBERTAKEN         = "text.status.membertaken";
    String TEXT_STATUS_ALREADYINROOM       = "text.status.alreadyinroom";
    String TEXT_STATUS_ROOMFULL            = "text.status.roomfull";
    String TEXT_STATUS_ROOMCOUNT           = "text.status.roomcount";
    String TEXT_STATUS_PUBLICLIMIT         = "text.status.publiclimit";
    String TEXT_STATUS_PRIVATELIMIT        = "text.status.privatelimit";
    String TEXT_STATUS_NOPROFILE           = "text.status.noprofile";
    String TEXT_STATUS_PROFILE             = "text.status.profile";
    String TEXT_STATUS_CLOSING             = "text.status.closing";

    String TEXT_SYSTEM_ENTRANCE            = "text.system.entrance";
    String TEXT_SYSTEM_AUDIO               = "text.system.audio";
    String TEXT_SYSTEM_BROADCAST           = "text.system.broadcast";
    String TEXT_SYSTEM_PARTNERLEFT         = "text.system.partnerleft";
    String TEXT_SYSTEM_DISCONNECTED        = "text.system.disconnected";
}
