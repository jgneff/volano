/*
 * Value.java - a class for getting parameter and property values.
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
import  COM.volano.chat.Build;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class gets and stores applet parameters and properties.  It also stores
 * some global variables relating to the applet's environment.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Value {
    // For a description of the bugs related to storing non-Western characters in
    // property files, see the following JavaSoft bugs:
    //   4035543, "Properties.load() doesn't work correctly against multi-byte
    //             character strings."
    //   4075955, "java.util.Properties does not encode characters in the 'key'
    //             properly."

    // Variables initialized in this class constructor.
    public Applet        applet;
    public AppletContext context;
    public URL           codeBase;        // Base for applet classes and archives
    public URL           documentBase;    // Base for Web page containing applet
    public URL           resourceBase;    // Base for applet resources
    public String        codeHost;

    // Global applet variables.
    public Properties properties;
    public Sounds     sounds;
    public boolean    acceptPrivateEnabled;
    public boolean    entranceAlertsEnabled;
    public boolean    audioAlertsEnabled;
    public boolean    countAlertsEnabled;
    public boolean    webtouringEnabled;
    public boolean    isMonitor;          // 2.1.8
    public boolean    wasDisconnected;    // 2.1.9

    // System properties.
    public String   javaVendor;
    public String   javaVendorUrl;
    public String   javaVersion;
    public String   javaClassVersion;
    public String   osName;
    public String   osVersion;
    public String   osArch;

    // Applet parameters.
    public boolean  monitor;
    public boolean  admin;
    public boolean  member;
    public boolean  stage;
    public boolean  publicRoom;
    public boolean  prompt;
    public Color    color;                // Null for default background color
    public Color    foreground;           // Null for default foreground color
    public String   group;
    public String   topic;
    public String   title;
    public URL      text;
    public String   username;
    public String   profile;
    public String   password;

    // Applet properties.
    public boolean  overrideMyvolanochat;

    public int      serverPort;

    public int      limitPublic;
    public int      limitPrivate;

    public boolean  historyEnable;
    public boolean  filterEnable;
    public boolean  sendPrivateDisable;
    public boolean  textareaSwingForce;

    public String   memberDocument;
    public boolean  memberEditableName;
    public boolean  memberEditableProfile;
    public boolean  memberMonitor;        // 2.1.8

    public String   labelText;
    public String   labelLink;
    public String   labelUrl;             // 2.6.4
    public String   labelUrlText;         // 2.6.4
    public String   labelUrlLink;         // 2.6.4

    public String   bannerCode;
    public URL      bannerParameters;     // Null for no parameters
    public int      bannerWidth;
    public int      bannerHeight;

    public String   logoCode;
    public URL      logoParameters;       // Null for no parameters
    public int      logoWidth;
    public int      logoHeight;

    public String   stringFontDefault;

    public Color    colorBackground;      // Null for default system window colors
    public Color    colorBackgroundButton;
    public Color    colorBackgroundList;
    public Color    colorBackgroundText;
    public Color    colorBackgroundTextEditable;

    public Color    colorForeground;
    public Color    colorForegroundButton;
    public Color    colorForegroundList;
    public Color    colorForegroundText;
    public Color    colorForegroundTextEditable;
    public Color    colorForegroundTextEditableInactive;
    public Color    colorForegroundTextLink;

    public Font     fontDefault;         // Null for default system font

    public boolean  acceptPrivateDefault;
    public boolean  alertEntranceDefault;
    public boolean  alertAudioDefault;
    public boolean  alertCountDefault;
    public boolean  webtouringDefault;

    public String   linkPrefix;
    public boolean  linkProfileDisable;
    public String   linkProfileUrl;
    public boolean  linkReferrerDisable;
    public String   linkReferrerUrl;

    public boolean  imageButtonBorder;
    public URL      imageButton1;
    public URL      imageButton2;
    public int      imageButtonWidth;
    public int      imageButtonHeight;
    public URL      imageLogo;
    public int      imageLogoWidth;
    public int      imageLogoHeight;
    public Color    imageLogoBackground;

    public String   textMemberName;
    public String   textMemberPassword;
    public String   textMemberProfile;

    public int      lengthChattext;
    public int      lengthProfile;
    public int      lengthRoomname;
    public int      lengthUsername;

    public long     delayKeystroke;
    public long     delayAccess;
    public long     delayAuthenticate;
    public long     delayBeep;
    public long     delayChat;
    public long     delayEnterPrivate;
    public long     delayEnterRoom;
    public long     delayExitPrivate;
    public long     delayExitRoom;
    public long     delayKick;
    public long     delayPing;
    public long     delayRoomList;
    public long     delayUserList;

    public int      unconfirmedChat;

    public boolean  pageNewwindow;
    public boolean  pageUseDocumentBase;
    public URL      pageAccessDocument;
    public URL      pageAccessHost;
    public URL      pageAccessPassword;
    public URL      pageAccessUnable;
    public URL      pageAccessVersion;
    public URL      pageAccessDuplicate;
    public URL      pageJavaVersion;
    public URL      pageHelp;
    public URL      pageAbout;
    public URL      pageExit;
    public URL      pageExitError;

    public URL      soundStart;           // Null for no sound
    public URL      soundStop;
    public URL      soundEnter;
    public URL      soundExit;
    public URL      soundRooms;
    public URL      soundUsers;
    public URL      soundProfile;
    public URL      soundAlert;

    public String   keyIgnoreAlt;
    public String   keyIgnoreCtrl;
    public String   keyIgnoreMeta;
    public String   keyIgnoreShift;

    public boolean  charReplaceNonprintable;
    public String   charReplaceOld;
    public String   charReplaceNew;

    public String   textF1;
    public String   textF2;
    public String   textF3;
    public String   textF4;
    public String   textF5;
    public String   textF6;
    public String   textF7;
    public String   textF8;
    public String   textF9;
    public String   textF10;
    public String   textF11;
    public String   textF12;

    public String   textButtonStatus;
    public String   textButtonMessage;
    public String   textButtonConnecting;
    public String   textButtonAccessing;
    public String   textButtonAuthenticating;
    public String   textButtonNotconnected;
    public String   textButtonAdmin;
    public String   textButtonMonitor;

    public String   textMainTitle;
    public String   textMainLogo;

    public String   textMainRooms;
    public String   textMainNorooms;
    public String   textMainOneroom;
    public String   textMainManyrooms;
    public String   textMainUsers;
    public String   textMainNousers;
    public String   textMainOneuser;
    public String   textMainManyusers;
    public String   textMainOnstage;
    public String   textMainFilter;
    public String   textMainUsername;
    public String   textMainProfile;
    public String   textMainBroadcast;
    public String   textMainGetrooms;
    public String   textMainEnter;
    public String   textMainConnect;
    public String   textMainDisconnect;

    public String   textChatStatus;
    public String   textChatEventStatus;
    public String   textChatEventSent;

    public String   textMenuPlaces;
    public String   textMenuGetrooms;
    public String   textMenuEnter;
    public String   textMenuExit;
    public String   textMenuOptions;
    public String   textMenuFontName;
    public String   textMenuFontStyle;
    public String   textMenuFontRegular;
    public String   textMenuFontItalic;
    public String   textMenuFontBold;
    public String   textMenuFontBolditalic;
    public String   textMenuFontIncrease;
    public String   textMenuFontDecrease;
    public String   textMenuAcceptPrivate;
    public String   textMenuAlertEntrance;
    public String   textMenuAlertAudio;
    public String   textMenuAlertCount;
    public String   textMenuWebtouring;
    public String   textMenuHelp;
    public String   textMenuTopics;
    public String   textMenuAbout;

    public String   textMenuRoom;
    public String   textMenuClose;
    public String   textMenuPeople;
    public String   textMenuPeopleRing;
    public String   textMenuPeopleIgnore;
    public String   textMenuPeopleUnignore;
    public String   textMenuPeopleCount;
    public String   textMenuMonitor;
    public String   textMenuMonitorRemove;
    public String   textMenuMonitorKick;
    public String   textMenuMonitorBan;

    public String   textMenuLinksTitle;
    public String   textMenuLinksNames;
    public String   textMenuLinksLocations;

    public String   textMenuThemesTitle;
    public String   textMenuThemesNames;
    public String   textMenuThemesDefault;

    public int      recentUserLimit;
    public String   textMonitorTitleRemove;
    public String   textMonitorTitleKick;
    public String   textMonitorTitleBan;
    public String   textMonitorLabelRemove;
    public String   textMonitorLabelKick;
    public String   textMonitorLabelBan;
    public String   textMonitorOkay;
    public String   textMonitorCancel;

    public String   textStatusFocusRooms;
    public String   textStatusFocusUsers;
    public String   textStatusFocusFilter;
    public String   textStatusFocusUsername;
    public String   textStatusFocusProfile;
    public String   textStatusFocusGetrooms;
    public String   textStatusFocusEnter;
    public String   textStatusFocusMembername;
    public String   textStatusFocusMemberpassword;

    public String   textStatusSelectroom;
    public String   textStatusEntername;
    public String   textStatusEnterpassword;
    public String   textStatusEntermembername;
    public String   textStatusEntermemberpassword;
    public String   textStatusEnterprofile;
    public String   textStatusEnter;
    public String   textStatusGettingrooms;
    public String   textStatusGettingusers;
    public String   textStatusGettingprofile;
    public String   textStatusEnteringroom;
    public String   textStatusEnteringprivate;
    public String   textStatusNosuchroom;
    public String   textStatusNosuchuser;
    public String   textStatusAlreadyinroom;
    public String   textStatusNametaken;
    public String   textStatusMembertaken;
    public String   textStatusRoomfull;
    public String   textStatusRoomcount;
    public String   textStatusPubliclimit;
    public String   textStatusPrivatelimit;
    public String   textStatusNoprofile;
    public String   textStatusProfile;
    public String   textStatusClosing;

    public String   textSystemEntrance;
    public String   textSystemAudio;
    public String   textSystemBroadcast;
    public String   textSystemPartnerleft;
    public String   textSystemDisconnected;

    /**
     * Formats and prints an error message describing the problem with the
     * applet property or parameter.
     *
     * @param key  the key of the property or parameter in error.
     * @param t    the error or exception describing the problem.
     */

    private static void printError(String key, Throwable t) {
        System.err.println("Invalid value for " + key + " (" + t + ").");
    }

    /**
     * Gets a string applet parameter, returning its default if undefined or
     * empty.  The returned string is trimmed of whitespace and ASCII control
     * characters (any characters with values less than '\u0020').
     *
     * @param applet        the applet.
     * @param key           the name of the applet parameter.
     * @param defaultValue  the default value for the parameter if undefined.
     * @return  the parameter value.
     */

    private static String getNonEmptyString(Applet applet, String key, String defaultValue) {
        String value = applet.getParameter(key);
        if (value != null) {
            value.trim();
        }
        return (value == null || value.length() == 0) ? defaultValue : value;
    }

    /**
     * Gets a string applet property, returning its default if undefined or empty.
     * The returned string is trimmed of whitespace and ASCII control characters
     * (any characters with values less than '\u0020').
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet property.
     * @param defaultValue  the default value for the property if undefined.
     * @return  the property value.
     */

    private static String getNonEmptyString(Properties properties, String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            value.trim();
        }
        return (value == null || value.length() == 0) ? defaultValue : value;
    }

    /**
     * Gets a string applet parameter, returning its default if undefined.
     *
     * @param applet        the applet.
     * @param key           the name of the applet parameter.
     * @param defaultValue  the default value for the parameter if undefined, or
     *                      <code>null</code> for no default.
     * @return  the parameter value, or <code>null</code> if the parameter is
     *          undefined and no default is provided.
     */

    private static String getString(Applet applet, String key, String defaultValue) {
        // Netscape Communicator 4.04 with the JDK 1.1 Update and Microsoft Internet
        // Explorer 4.01 both trim the parameter strings, but the JDK 1.1.5
        // appletviewer does not.

        String value = applet.getParameter(key);
        return value == null ? defaultValue : value.trim();
    }

    /**
     * Gets a string applet property, returning its default if undefined.
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet property.
     * @param defaultValue  the default value for the property if undefined, or
     *                      <code>null</code> for no default.
     * @param trim          whether to return the string trimmed of any leading
     *                      whitespace and ASCII control characters.
     * @return  the property value, or <code>null</code> if the property is
     *          undefined and no default is provided.
     */

    private static String getString(Properties properties, String key, String defaultValue, boolean trim) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : (trim ? value.trim() : value);
    }

    /**
     * Gets a string applet property, returning its default if undefined.  The
     * returned string is trimmed of whitespace and ASCII control characters (any
     * characters with values less than '\u0020').
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet property.
     * @param defaultValue  the default value for the property if undefined, or
     *                      <code>null</code> for no default.
     * @return  the property value, or <code>null</code> if the property is
     *          undefined and no default is provided.
     */

    private static String getString(Properties properties, String key, String defaultValue) {
        return getString(properties, key, defaultValue, true);
    }

    /**
     * Decodes a font specification of the form:
     * <pre>
     * name[-style][-size]
     * </pre>
     *
     * @param key     the name of the applet font property.
     * @param string  the font specification.
     * @return  the font.
     */

    private static Font decodeFont(String key, String string) {
        Font font = null;
        if (string != null && string.length() > 0) {
            String fontName  = string;
            int    fontStyle = Font.PLAIN;
            int    fontSize  = 13;

            int i = string.indexOf('-');
            if (i >= 0) {
                fontName = string.substring(0, i);
                string   = string.substring(i + 1);
                if ((i = string.indexOf('-')) >= 0) {
                    if (string.startsWith("bold-")) {
                        fontStyle = Font.BOLD;
                    } else if (string.startsWith("italic-")) {
                        fontStyle = Font.ITALIC;
                    } else if (string.startsWith("bolditalic-")) {
                        fontStyle = Font.BOLD | Font.ITALIC;
                    }
                    string = string.substring(i + 1);
                }
                try {
                    fontSize = Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    printError(key, e);
                }
            }
            font = new Font(fontName, fontStyle, fontSize);
        }
        return font;
    }

    /**
     * Gets a font applet property, returning its default if undefined or blank.
     * The default value must not be <code>null</code>.
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet font property.
     * @param defaultValue  the default value for the font specification.
     * @return  the font property value, or the default font if undefined or
     *          blank.
     */

    private static Font getFont(Properties properties, String key, String defaultValue) {
        return decodeFont(key, getNonEmptyString(properties, key, defaultValue));
    }

    /**
     * Decodes an integer specification, with hexadecimal values preceeded by "#"
     * or "0x", and octal values preceeded by a zero.
     *
     * @param string  the integer specification.
     * @return  the integer value.
     * @exception NumberFormatException  if the specification is not an integer.
     */

    private static Integer decodeInt(String string) throws NumberFormatException {
        Integer integer = null;
        if (string != null && string.length() > 0) {
            if (string.startsWith("#")) {
                integer = Integer.valueOf(string.substring(1), 16);
            } else if (string.startsWith("0x")) {
                integer = Integer.valueOf(string.substring(2), 16);
            } else if (string.startsWith("0") && string.length() > 1) {
                integer = Integer.valueOf(string.substring(1), 8);
            } else {
                integer = Integer.valueOf(string);
            }
        }
        return integer;
    }

    /**
     * Gets an integer value from a string, returning the specified default if the
     * given string is null, empty, or malformed.  An error message is printed if
     * the given string is a malformed integer.
     *
     * @param key           the name of the applet integer property or parameter.
     * @param value         the value of the applet property or parameter.
     * @param defaultValue  the default specification for the integer.
     * @return  the integer value, or the default integer value if the given
     *          string was null, blank, or malformed.
     * @exception NumberFormatException  if the given default is not an integer.
     */

    private static Integer getInteger(String key, String value, String defaultValue) throws NumberFormatException {
        Integer integer = null;
        try {
            integer = decodeInt(value);
            if (integer == null) {
                integer = decodeInt(defaultValue);
            }
        } catch (NumberFormatException e) {
            printError(key, e);
            integer = decodeInt(defaultValue);
        }
        return integer;
    }

    /**
     * Gets an integer applet parameter, returning its default if undefined,
     * blank, or malformed.
     *
     * @param applet        the applet.
     * @param key           the name of the applet integer parameter.
     * @param defaultValue  the default specification for the integer.
     * @return  the integer value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static Integer getInteger(Applet applet, String key, String defaultValue) {
        String value = getString(applet, key, defaultValue);
        return getInteger(key, value, defaultValue);
    }

    /**
     * Gets an integer applet property, returning its default if undefined,
     * blank, or malformed.
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet integer property.
     * @param defaultValue  the default specification for the integer.
     * @return  the integer value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static Integer getInteger(Properties properties, String key, String defaultValue) {
        String value = getString(properties, key, defaultValue);
        return getInteger(key, value, defaultValue);
    }

    /**
     * Gets a color applet parameter, returning its default if undefined, blank,
     * or malformed.
     *
     * @param applet        the applet.
     * @param key           the name of the applet color parameter.
     * @param defaultValue  the default specification for the color.
     * @return  the color value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static Color getColor(Applet applet, String key, String defaultValue) {
        Integer integer = getInteger(applet, key, defaultValue);
        return new Color(integer.intValue());
    }

    /**
     * Gets a color applet property, returning its default if undefined, blank, or
     * malformed.
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet color property.
     * @param defaultValue  the default specification for the color.
     * @return  the color value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static Color getColor(Properties properties, String key, String defaultValue) {
        Integer integer = getInteger(properties, key, defaultValue);
        return new Color(integer.intValue());
    }

    /**
     * Gets a color applet property, returning its default if undefined, blank, or
     * malformed.
     *
     * @param properties    the applet properties.
     * @param key           the name of the applet color property.
     * @param defaultColor  the default color.
     * @return  the color value, or the default color if undefined, blank, or
     *          malformed.
     */

    private static Color getColor(Properties properties, String key, Color defaultColor) {
        Integer integer = getInteger(properties, key, null);
        return integer == null ? defaultColor : new Color(integer.intValue());
    }

    /**
     * Decodes a URL specification into an absolute URL.  Relative specifications
     * are assumed to be relative to the given base URL.
     *
     * @param base    the base URL.
     * @param string  the URL specification.
     * @return  the absolute URL.
     * @exception MalformedURLException  if the specification is not a valid URL.
     */

    private static URL decodeURL(URL base, String string) throws MalformedURLException {
        URL url = null;
        if (string != null && string.length() > 0) {
            if (string.indexOf(':') != -1) { // An absolute URL
                url = new URL(string);
            } else if (string.startsWith("/")) { // An absolute file location
                url = new URL(base.getProtocol(), base.getHost(), base.getPort(), string);
            } else { // A relative file location
                url = new URL(base, string);
            }
        }
        return url;
    }

    /**
     * Gets a URL applet property or parameter, returning its default if
     * undefined, blank, or malformed.
     *
     * @param base          the base URL.
     * @param key           the name of the applet URL property or parameter.
     * @param value         the value of the applet property or parameter.
     * @param defaultValue  the default specification for the URL.
     * @return  the URL value, or the default value if undefined, blank, or
     *          malformed.
     * @exception MalformedURLException  if the given default is not a valid URL.
     */

    private static URL getURL(URL base, String key, String value, String defaultValue) {
        URL url = null;
        try {
            url = decodeURL(base, value);
            if (url == null) {
                url = decodeURL(base, defaultValue);
            }
        } catch (MalformedURLException e) {
            printError(key, e);
            try {
                url = decodeURL(base, defaultValue);
            } catch (MalformedURLException e2) {
                throw new IllegalArgumentException(e2.toString());
            }
        }
        return url;
    }

    /**
     * Gets a URL applet parameter, returning its default if undefined, blank, or
     * malformed.  The default value may be <code>null</code>.
     *
     * @param applet        the applet.
     * @param base          the base URL.
     * @param key           the name ofs the applet URL parameter.
     * @param defaultValue  the default specification for the URL, or
     *                      <code>null</code> for no default.
     * @return  the URL value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static URL getURL(Applet applet, URL base, String key, String defaultValue) {
        String value = getString(applet, key, defaultValue);
        return getURL(base, key, value, defaultValue);
    }

    /**
     * Gets a URL applet property, returning its default if undefined, blank, or
     * malformed.  The default value may be <code>null</code>.
     *
     * @param properties    the applet properties.
     * @param base          the base URL.
     * @param key           the name ofs the applet URL property.
     * @param defaultValue  the default specification for the URL, or
     *                      <code>null</code> for no default.
     * @return  the URL value, or the default value if undefined, blank, or
     *          malformed.
     */

    private static URL getURL(Properties properties, URL base, String key, String defaultValue) {
        String value = getString(properties, key, defaultValue);
        return getURL(base, key, value, defaultValue);
    }

    /**
     * Loads the applet properties file.
     *
     * @param properties  the default properties.
     * @param key         the parameter name of the properties file.
     * @param url         the URL where the properties file can be found.
     * @return  the loaded properties with their defaults.
     */

    private static Properties loadProperties(Properties properties, String key, URL url) {
        if (url != null) {
            try {
                InputStream input = url.openStream();
                properties.load(input);
                input.close();
            } catch (Exception e) {
                printError(key, e);
            }
        }
        return properties;
    }

    /**
     * Creates a new value object, reading in all parameter and property values
     * and initializing all global variables.
     */

    public Value(Applet applet) {
        this.applet  = applet;
        context      = applet.getAppletContext();
        codeBase     = applet.getCodeBase();
        documentBase = applet.getDocumentBase();
        codeHost     = codeBase.getHost();

        // Get the system properties.
        javaVendor       = System.getProperty(Key.JAVA_VENDOR,       Default.JAVA_VENDOR);
        javaVendorUrl    = System.getProperty(Key.JAVA_VENDOR_URL,   Default.JAVA_VENDOR_URL);
        javaVersion      = System.getProperty(Key.JAVA_VERSION,      Default.JAVA_VERSION);
        javaClassVersion = System.getProperty(Key.JAVA_CLASS_VERSION,Default.JAVA_CLASS_VERSION);
        osName           = System.getProperty(Key.OS_NAME,           Default.OS_NAME);
        osVersion        = System.getProperty(Key.OS_VERSION,        Default.OS_VERSION);
        osArch           = System.getProperty(Key.OS_ARCH,           Default.OS_ARCH);

        // Get the applet parameters.
        monitor    = Boolean.valueOf(getNonEmptyString(applet, Key.MONITOR, Default.MONITOR)).booleanValue();
        admin      = Boolean.valueOf(getNonEmptyString(applet, Key.ADMIN,   Default.ADMIN)).booleanValue();
        member     = Boolean.valueOf(getNonEmptyString(applet, Key.MEMBER,  Default.MEMBER)).booleanValue();
        stage      = Boolean.valueOf(getNonEmptyString(applet, Key.STAGE,   Default.STAGE)).booleanValue();
        publicRoom = Boolean.valueOf(getNonEmptyString(applet, Key.PUBLIC,  Default.PUBLIC)).booleanValue();
        prompt     = Boolean.valueOf(getNonEmptyString(applet, Key.PROMPT,  Default.PROMPT)).booleanValue();
        group      = getString(applet, Key.GROUP, Default.GROUP);
        topic      = getString(applet, Key.TOPIC, Default.TOPIC);
        title      = getString(applet, Key.TITLE, group);   // Default title is room name
        color      = getColor(applet, Key.COLOR, Default.COLOR);
        foreground = getColor(applet, Key.FOREGROUND, Default.FOREGROUND);
        text       = getURL(applet, codeBase, Key.TEXT, Default.TEXT);
        username   = getString(applet, Key.USERNAME, Default.USERNAME);
        profile    = getString(applet, Key.PROFILE,  Default.PROFILE);
        password   = getString(applet, Key.PASSWORD, Default.PASSWORD);


        // Base all resources relative to the location of the properties file, if
        // present.
        if (text == null) {
            resourceBase = codeBase;
        } else {
            resourceBase = text;
        }

        // For Verio, remain backward compatible with VolanoChat 2.0 so that Verio's
        // customers who placed the "english.txt" file in their own Web directories
        // will still pick up the secondary files from the applet code base.

        // Base all resources relative to the applet code base if Verio.
        if (Build.VERIO) {
            resourceBase = codeBase;
        }

        // If this room is public, make sure we have a room name.
        publicRoom = publicRoom && group.length() > 0;

        // Get and initialize the applet properties.
        Properties properties = loadProperties(new Properties(), Key.TEXT, text);
        init(properties);
    }

    /**
     * Initializes all applet properties with values in the given property object.
     *
     * @param properties  the new properties whose values this object should
     *                    assume.
     */

    void init(Properties properties) {
        this.properties = properties;

        overrideMyvolanochat = Boolean.valueOf(getNonEmptyString(properties, Key.OVERRIDE_MYVOLANOCHAT, Default.OVERRIDE_MYVOLANOCHAT)).booleanValue();
        if (overrideMyvolanochat && applet.getClass().getName().equals(Build.MY_VOLANO_CHAT)) {
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String name  = (String) enumeration.nextElement();
                String value = getString(applet, name, null);
                if (value != null) {
                    properties.put(name, value);
                }
            }
        }

        serverPort = getInteger(properties, Key.SERVER_PORT, Default.SERVER_PORT).intValue();

        limitPublic  = getInteger(properties, Key.LIMIT_PUBLIC,  Default.LIMIT_PUBLIC).intValue();
        limitPrivate = getInteger(properties, Key.LIMIT_PRIVATE, Default.LIMIT_PRIVATE).intValue();

        historyEnable        = Boolean.valueOf(getNonEmptyString(properties, Key.HISTORY_ENABLE, Default.HISTORY_ENABLE)).booleanValue();
        filterEnable         = Boolean.valueOf(getNonEmptyString(properties, Key.FILTER_ENABLE, Default.FILTER_ENABLE)).booleanValue();
        sendPrivateDisable   = Boolean.valueOf(getNonEmptyString(properties, Key.SEND_PRIVATE_DISABLE, Default.SEND_PRIVATE_DISABLE)).booleanValue();
        textareaSwingForce   = Boolean.valueOf(getNonEmptyString(properties, Key.TEXTAREA_SWING_FORCE, Default.TEXTAREA_SWING_FORCE)).booleanValue();

        memberDocument        = getString(properties, Key.MEMBER_DOCUMENT, Default.MEMBER_DOCUMENT);
        memberEditableName    = Boolean.valueOf(getNonEmptyString(properties, Key.MEMBER_EDITABLE_NAME,    Default.MEMBER_EDITABLE_NAME)).booleanValue();
        memberEditableProfile = Boolean.valueOf(getNonEmptyString(properties, Key.MEMBER_EDITABLE_PROFILE, Default.MEMBER_EDITABLE_PROFILE)).booleanValue();
        memberMonitor         = Boolean.valueOf(getNonEmptyString(properties, Key.MEMBER_MONITOR, Default.MEMBER_MONITOR)).booleanValue();

        labelText    = getString(properties, Key.LABEL_TEXT, Default.LABEL_TEXT);
        labelLink    = getString(properties, Key.LABEL_LINK, Default.LABEL_LINK);
        labelUrl     = getString(properties, Key.LABEL_URL, Default.LABEL_URL);
        labelUrlText = getString(properties, Key.LABEL_URL_TEXT, Default.LABEL_URL_TEXT);
        labelUrlLink = getString(properties, Key.LABEL_URL_LINK, Default.LABEL_URL_LINK);

        bannerCode       = getString(properties, Key.BANNER_CODE, Default.BANNER_CODE);
        bannerParameters = getURL(properties, resourceBase, Key.BANNER_PARAMETERS, Default.BANNER_PARAMETERS);
        bannerWidth      = getInteger(properties, Key.BANNER_WIDTH,  Default.BANNER_WIDTH).intValue();
        bannerHeight     = getInteger(properties, Key.BANNER_HEIGHT, Default.BANNER_HEIGHT).intValue();

        logoCode       = getString(properties, Key.LOGO_CODE, Default.LOGO_CODE);
        logoParameters = getURL(properties, resourceBase, Key.LOGO_PARAMETERS, Default.LOGO_PARAMETERS);
        logoWidth      = getInteger(properties, Key.LOGO_WIDTH,  Default.LOGO_WIDTH).intValue();
        logoHeight     = getInteger(properties, Key.LOGO_HEIGHT, Default.LOGO_HEIGHT).intValue();

        stringFontDefault = getString(properties, Key.FONT_DEFAULT, Default.FONT_DEFAULT);

        // Be careful with color theme defaults for Verio!

        // Use general background color as default for component-specific colors.
        colorBackground             = getColor(properties, Key.COLOR_BACKGROUND,               Default.COLOR_BACKGROUND);
        colorBackgroundButton       = getColor(properties, Key.COLOR_BACKGROUND_BUTTON,        colorBackground);
        colorBackgroundList         = getColor(properties, Key.COLOR_BACKGROUND_LIST,          colorBackground);
        colorBackgroundText         = getColor(properties, Key.COLOR_BACKGROUND_TEXT,          colorBackground);
        colorBackgroundTextEditable = getColor(properties, Key.COLOR_BACKGROUND_TEXT_EDITABLE, colorBackground);

        // Use general foreground color as default for component-specific colors.
        colorForeground                     = getColor(properties, Key.COLOR_FOREGROUND,                        Default.COLOR_FOREGROUND);
        colorForegroundButton               = getColor(properties, Key.COLOR_FOREGROUND_BUTTON,                 colorForeground);
        colorForegroundList                 = getColor(properties, Key.COLOR_FOREGROUND_LIST,                   colorForeground);
        colorForegroundText                 = getColor(properties, Key.COLOR_FOREGROUND_TEXT,                   colorForeground);
        colorForegroundTextEditable         = getColor(properties, Key.COLOR_FOREGROUND_TEXT_EDITABLE,          colorForeground);
        colorForegroundTextEditableInactive = getColor(properties, Key.COLOR_FOREGROUND_TEXT_EDITABLE_INACTIVE, Default.COLOR_FOREGROUND_TEXT_EDITABLE_INACTIVE);
        colorForegroundTextLink             = getColor(properties, Key.COLOR_FOREGROUND_TEXT_LINK,              Default.COLOR_FOREGROUND_TEXT_LINK);

        fontDefault = getFont(properties, Key.FONT_DEFAULT, Default.FONT_DEFAULT);

        acceptPrivateDefault = Boolean.valueOf(getNonEmptyString(properties, Key.ACCEPT_PRIVATE_DEFAULT, Default.ACCEPT_PRIVATE_DEFAULT)).booleanValue();
        alertEntranceDefault = Boolean.valueOf(getNonEmptyString(properties, Key.ALERT_ENTRANCE_DEFAULT, Default.ALERT_ENTRANCE_DEFAULT)).booleanValue();
        alertAudioDefault    = Boolean.valueOf(getNonEmptyString(properties, Key.ALERT_AUDIO_DEFAULT,    Default.ALERT_AUDIO_DEFAULT)).booleanValue();
        alertCountDefault    = Boolean.valueOf(getNonEmptyString(properties, Key.ALERT_COUNT_DEFAULT,    Default.ALERT_COUNT_DEFAULT)).booleanValue();
        webtouringDefault    = Boolean.valueOf(getNonEmptyString(properties, Key.WEBTOURING_DEFAULT,     Default.WEBTOURING_DEFAULT)).booleanValue();

        linkPrefix           = getString(properties, Key.LINK_PREFIX, Default.LINK_PREFIX);
        linkProfileDisable   = Boolean.valueOf(getNonEmptyString(properties, Key.LINK_PROFILE_DISABLE,  Default.LINK_PROFILE_DISABLE)).booleanValue();
        linkProfileUrl       = getString(properties, Key.LINK_PROFILE_URL, Default.LINK_PROFILE_URL);
        linkReferrerDisable  = Boolean.valueOf(getNonEmptyString(properties, Key.LINK_REFERRER_DISABLE, Default.LINK_REFERRER_DISABLE)).booleanValue();
        linkReferrerUrl      = getString(properties, Key.LINK_REFERRER_URL, Default.LINK_REFERRER_URL);

        imageButtonBorder   = Boolean.valueOf(getNonEmptyString(properties, Key.IMAGE_BUTTON_BORDER, Default.IMAGE_BUTTON_BORDER)).booleanValue();
        imageButton1        = getURL(properties, resourceBase, Key.IMAGE_BUTTON1, Default.IMAGE_BUTTON1);
        imageButton2        = getURL(properties, resourceBase, Key.IMAGE_BUTTON2, Default.IMAGE_BUTTON2);
        imageButtonWidth    = getInteger(properties, Key.IMAGE_BUTTON_WIDTH,  Default.IMAGE_BUTTON_WIDTH).intValue();
        imageButtonHeight   = getInteger(properties, Key.IMAGE_BUTTON_HEIGHT, Default.IMAGE_BUTTON_HEIGHT).intValue();
        imageLogo           = getURL(properties, resourceBase, Key.IMAGE_LOGO, Default.IMAGE_LOGO);
        imageLogoWidth      = getInteger(properties, Key.IMAGE_LOGO_WIDTH,  Default.IMAGE_LOGO_WIDTH).intValue();
        imageLogoHeight     = getInteger(properties, Key.IMAGE_LOGO_HEIGHT, Default.IMAGE_LOGO_HEIGHT).intValue();
        imageLogoBackground = getColor(properties, Key.IMAGE_LOGO_BACKGROUND, Default.IMAGE_LOGO_BACKGROUND);

        lengthChattext = getInteger(properties, Key.LENGTH_CHATTEXT, Default.LENGTH_CHATTEXT).intValue();
        lengthProfile  = getInteger(properties, Key.LENGTH_PROFILE,  Default.LENGTH_PROFILE).intValue();
        lengthRoomname = getInteger(properties, Key.LENGTH_ROOMNAME, Default.LENGTH_ROOMNAME).intValue();
        lengthUsername = getInteger(properties, Key.LENGTH_USERNAME, Default.LENGTH_USERNAME).intValue();

        delayKeystroke      = getInteger(properties, Key.DELAY_KEYSTROKE,       Default.DELAY_KEYSTROKE).longValue();
        delayAccess         = getInteger(properties, Key.DELAY_ACCESS,          Default.DELAY_ACCESS).longValue();
        delayAuthenticate   = getInteger(properties, Key.DELAY_AUTHENTICATE,    Default.DELAY_AUTHENTICATE).longValue();
        delayBeep           = getInteger(properties, Key.DELAY_BEEP,            Default.DELAY_BEEP).longValue();
        delayChat           = getInteger(properties, Key.DELAY_CHAT,            Default.DELAY_CHAT).longValue();
        delayEnterPrivate   = getInteger(properties, Key.DELAY_ENTER_PRIVATE,   Default.DELAY_ENTER_PRIVATE).longValue();
        delayEnterRoom      = getInteger(properties, Key.DELAY_ENTER_ROOM,      Default.DELAY_ENTER_ROOM).longValue();
        delayExitPrivate    = getInteger(properties, Key.DELAY_EXIT_PRIVATE,    Default.DELAY_EXIT_PRIVATE).longValue();
        delayExitRoom       = getInteger(properties, Key.DELAY_EXIT_ROOM,       Default.DELAY_EXIT_ROOM).longValue();
        delayKick           = getInteger(properties, Key.DELAY_KICK,            Default.DELAY_KICK).longValue();
        delayPing           = getInteger(properties, Key.DELAY_PING,            Default.DELAY_PING).longValue();
        delayRoomList       = getInteger(properties, Key.DELAY_ROOM_LIST,       Default.DELAY_ROOM_LIST).longValue();
        delayUserList       = getInteger(properties, Key.DELAY_USER_LIST,       Default.DELAY_USER_LIST).longValue();

        unconfirmedChat     = getInteger(properties, Key.UNCONFIRMED_CHAT,      Default.UNCONFIRMED_CHAT).intValue();

        pageNewwindow       = Boolean.valueOf(getNonEmptyString(properties, Key.PAGE_NEWWINDOW, Default.PAGE_NEWWINDOW)).booleanValue();
        pageUseDocumentBase = Boolean.valueOf(getNonEmptyString(properties, Key.PAGE_USE_DOCUMENT_BASE, Default.PAGE_USE_DOCUMENT_BASE)).booleanValue();
        URL pageBase = pageUseDocumentBase ? documentBase : resourceBase;
        pageAccessDocument  = getURL(properties, pageBase, Key.PAGE_ACCESS_DOCUMENT,  Default.PAGE_ACCESS_DOCUMENT);
        pageAccessHost      = getURL(properties, pageBase, Key.PAGE_ACCESS_HOST,      Default.PAGE_ACCESS_HOST);
        pageAccessPassword  = getURL(properties, pageBase, Key.PAGE_ACCESS_PASSWORD,  Default.PAGE_ACCESS_PASSWORD);
        pageAccessUnable    = getURL(properties, pageBase, Key.PAGE_ACCESS_UNABLE,    Default.PAGE_ACCESS_UNABLE);
        pageAccessVersion   = getURL(properties, pageBase, Key.PAGE_ACCESS_VERSION,   Default.PAGE_ACCESS_VERSION);
        pageAccessDuplicate = getURL(properties, pageBase, Key.PAGE_ACCESS_DUPLICATE, Default.PAGE_ACCESS_DUPLICATE);
        pageJavaVersion     = getURL(properties, pageBase, Key.PAGE_JAVA_VERSION,     Default.PAGE_JAVA_VERSION);
        pageHelp            = getURL(properties, pageBase, Key.PAGE_HELP,             Default.PAGE_HELP);
        pageAbout           = getURL(properties, pageBase, Key.PAGE_ABOUT,            Default.PAGE_ABOUT);
        pageExit            = getURL(properties, pageBase, Key.PAGE_EXIT,             Default.PAGE_EXIT);
        pageExitError       = getURL(properties, pageBase, Key.PAGE_EXIT_ERROR,       Default.PAGE_EXIT_ERROR);

        soundStart   = getURL(properties, resourceBase, Key.SOUND_START,   Default.SOUND_START);
        soundStop    = getURL(properties, resourceBase, Key.SOUND_STOP,    Default.SOUND_STOP);
        soundEnter   = getURL(properties, resourceBase, Key.SOUND_ENTER,   Default.SOUND_ENTER);
        soundExit    = getURL(properties, resourceBase, Key.SOUND_EXIT,    Default.SOUND_EXIT);
        soundRooms   = getURL(properties, resourceBase, Key.SOUND_ROOMS,   Default.SOUND_ROOMS);
        soundUsers   = getURL(properties, resourceBase, Key.SOUND_USERS,   Default.SOUND_USERS);
        soundProfile = getURL(properties, resourceBase, Key.SOUND_PROFILE, Default.SOUND_PROFILE);
        soundAlert   = getURL(properties, resourceBase, Key.SOUND_ALERT,   Default.SOUND_ALERT);

        keyIgnoreAlt   = getString(properties, Key.KEY_IGNORE_ALT,   Default.KEY_IGNORE_ALT, false);
        keyIgnoreCtrl  = getString(properties, Key.KEY_IGNORE_CTRL,  Default.KEY_IGNORE_CTRL, false);
        keyIgnoreMeta  = getString(properties, Key.KEY_IGNORE_META,  Default.KEY_IGNORE_META, false);
        keyIgnoreShift = getString(properties, Key.KEY_IGNORE_SHIFT, Default.KEY_IGNORE_SHIFT, false);

        charReplaceNonprintable = Boolean.valueOf(getNonEmptyString(properties, Key.CHAR_REPLACE_NONPRINTABLE, Default.CHAR_REPLACE_NONPRINTABLE)).booleanValue();
        charReplaceOld = getString(properties, Key.CHAR_REPLACE_OLD, Default.CHAR_REPLACE_OLD, false);
        charReplaceNew = getString(properties, Key.CHAR_REPLACE_NEW, Default.CHAR_REPLACE_NEW, false);
        int size = Math.min(charReplaceOld.length(), charReplaceNew.length());
        charReplaceOld = charReplaceOld.substring(0, size);
        charReplaceNew = charReplaceNew.substring(0, size);

        textF1  = getString(properties, Key.TEXT_F1,  Default.TEXT_F1);
        textF2  = getString(properties, Key.TEXT_F2,  Default.TEXT_F2);
        textF3  = getString(properties, Key.TEXT_F3,  Default.TEXT_F3);
        textF4  = getString(properties, Key.TEXT_F4,  Default.TEXT_F4);
        textF5  = getString(properties, Key.TEXT_F5,  Default.TEXT_F5);
        textF6  = getString(properties, Key.TEXT_F6,  Default.TEXT_F6);
        textF7  = getString(properties, Key.TEXT_F7,  Default.TEXT_F7);
        textF8  = getString(properties, Key.TEXT_F8,  Default.TEXT_F8);
        textF9  = getString(properties, Key.TEXT_F9,  Default.TEXT_F9);
        textF10 = getString(properties, Key.TEXT_F10, Default.TEXT_F10);
        textF11 = getString(properties, Key.TEXT_F11, Default.TEXT_F11);
        textF12 = getString(properties, Key.TEXT_F12, Default.TEXT_F12);

        textButtonStatus         = getString(properties, Key.TEXT_BUTTON_STATUS,       Default.TEXT_BUTTON_STATUS);
        textButtonMessage        = getString(properties, Key.TEXT_BUTTON_MESSAGE,      Default.TEXT_BUTTON_MESSAGE);
        textButtonConnecting     = getString(properties, Key.TEXT_BUTTON_CONNECTING,   Default.TEXT_BUTTON_CONNECTING);
        textButtonAccessing      = getString(properties, Key.TEXT_BUTTON_ACCESSING,    Default.TEXT_BUTTON_ACCESSING);
        textButtonAuthenticating = getString(properties, Key.TEXT_BUTTON_AUTHENTICATING,    Default.TEXT_BUTTON_AUTHENTICATING);
        textButtonNotconnected   = getString(properties, Key.TEXT_BUTTON_NOTCONNECTED, Default.TEXT_BUTTON_NOTCONNECTED);
        textButtonAdmin          = getString(properties, Key.TEXT_BUTTON_ADMIN,        Default.TEXT_BUTTON_ADMIN);
        textButtonMonitor        = getString(properties, Key.TEXT_BUTTON_MONITOR,      Default.TEXT_BUTTON_MONITOR);

        textMemberName     = getString(properties, Key.TEXT_MEMBER_NAME,     Default.TEXT_MEMBER_NAME);
        textMemberPassword = getString(properties, Key.TEXT_MEMBER_PASSWORD, Default.TEXT_MEMBER_PASSWORD);
        textMemberProfile  = getString(properties, Key.TEXT_MEMBER_PROFILE,  Default.TEXT_MEMBER_PROFILE);

        textMainTitle      = getString(properties, Key.TEXT_MAIN_TITLE,      Default.TEXT_MAIN_TITLE);
        textMainLogo       = getString(properties, Key.TEXT_MAIN_LOGO,       Default.TEXT_MAIN_LOGO);
        textMainRooms      = getString(properties, Key.TEXT_MAIN_ROOMS,      Default.TEXT_MAIN_ROOMS);
        textMainNorooms    = getString(properties, Key.TEXT_MAIN_NOROOMS,    Default.TEXT_MAIN_NOROOMS);
        textMainOneroom    = getString(properties, Key.TEXT_MAIN_ONEROOM,    Default.TEXT_MAIN_ONEROOM);
        textMainManyrooms  = getString(properties, Key.TEXT_MAIN_MANYROOMS,  Default.TEXT_MAIN_MANYROOMS);
        textMainUsers      = getString(properties, Key.TEXT_MAIN_USERS,      Default.TEXT_MAIN_USERS);
        textMainNousers    = getString(properties, Key.TEXT_MAIN_NOUSERS,    Default.TEXT_MAIN_NOUSERS);
        textMainOneuser    = getString(properties, Key.TEXT_MAIN_ONEUSER,    Default.TEXT_MAIN_ONEUSER);
        textMainManyusers  = getString(properties, Key.TEXT_MAIN_MANYUSERS,  Default.TEXT_MAIN_MANYUSERS);
        textMainOnstage    = getString(properties, Key.TEXT_MAIN_ONSTAGE,    Default.TEXT_MAIN_ONSTAGE);
        textMainFilter     = getString(properties, Key.TEXT_MAIN_FILTER,     Default.TEXT_MAIN_FILTER);
        textMainUsername   = getString(properties, Key.TEXT_MAIN_USERNAME,   Default.TEXT_MAIN_USERNAME);
        textMainProfile    = getString(properties, Key.TEXT_MAIN_PROFILE,    Default.TEXT_MAIN_PROFILE);
        textMainBroadcast  = getString(properties, Key.TEXT_MAIN_BROADCAST,  Default.TEXT_MAIN_BROADCAST);
        textMainGetrooms   = getString(properties, Key.TEXT_MAIN_GETROOMS,   Default.TEXT_MAIN_GETROOMS);
        textMainEnter      = getString(properties, Key.TEXT_MAIN_ENTER,      Default.TEXT_MAIN_ENTER);
        textMainConnect    = getString(properties, Key.TEXT_MAIN_CONNECT,    Default.TEXT_MAIN_CONNECT);
        textMainDisconnect = getString(properties, Key.TEXT_MAIN_DISCONNECT, Default.TEXT_MAIN_DISCONNECT);

        textChatStatus      = getString(properties, Key.TEXT_CHAT_STATUS,       Default.TEXT_CHAT_STATUS);
        textChatEventStatus = getString(properties, Key.TEXT_CHAT_EVENT_STATUS, Default.TEXT_CHAT_EVENT_STATUS);
        textChatEventSent   = getString(properties, Key.TEXT_CHAT_EVENT_SENT,   Default.TEXT_CHAT_EVENT_SENT);

        textMenuPlaces         = getString(properties, Key.TEXT_MENU_PLACES,          Default.TEXT_MENU_PLACES);
        textMenuGetrooms       = getString(properties, Key.TEXT_MENU_GETROOMS,        Default.TEXT_MENU_GETROOMS);
        textMenuEnter          = getString(properties, Key.TEXT_MENU_ENTER,           Default.TEXT_MENU_ENTER);
        textMenuExit           = getString(properties, Key.TEXT_MENU_EXIT,            Default.TEXT_MENU_EXIT);
        textMenuOptions        = getString(properties, Key.TEXT_MENU_OPTIONS,         Default.TEXT_MENU_OPTIONS);
        textMenuFontName       = getString(properties, Key.TEXT_MENU_FONT_NAME,       Default.TEXT_MENU_FONT_NAME);
        textMenuFontStyle      = getString(properties, Key.TEXT_MENU_FONT_STYLE,      Default.TEXT_MENU_FONT_STYLE);
        textMenuFontRegular    = getString(properties, Key.TEXT_MENU_FONT_REGULAR,    Default.TEXT_MENU_FONT_REGULAR);
        textMenuFontItalic     = getString(properties, Key.TEXT_MENU_FONT_ITALIC,     Default.TEXT_MENU_FONT_ITALIC);
        textMenuFontBold       = getString(properties, Key.TEXT_MENU_FONT_BOLD,       Default.TEXT_MENU_FONT_BOLD);
        textMenuFontBolditalic = getString(properties, Key.TEXT_MENU_FONT_BOLDITALIC, Default.TEXT_MENU_FONT_BOLDITALIC);
        textMenuFontIncrease   = getString(properties, Key.TEXT_MENU_FONT_INCREASE,   Default.TEXT_MENU_FONT_INCREASE);
        textMenuFontDecrease   = getString(properties, Key.TEXT_MENU_FONT_DECREASE,   Default.TEXT_MENU_FONT_DECREASE);
        textMenuAcceptPrivate  = getString(properties, Key.TEXT_MENU_ACCEPT_PRIVATE,  Default.TEXT_MENU_ACCEPT_PRIVATE);
        textMenuAlertEntrance  = getString(properties, Key.TEXT_MENU_ALERT_ENTRANCE,  Default.TEXT_MENU_ALERT_ENTRANCE);
        textMenuAlertAudio     = getString(properties, Key.TEXT_MENU_ALERT_AUDIO,     Default.TEXT_MENU_ALERT_AUDIO);
        textMenuAlertCount     = getString(properties, Key.TEXT_MENU_ALERT_COUNT,     Default.TEXT_MENU_ALERT_COUNT);
        textMenuWebtouring     = getString(properties, Key.TEXT_MENU_WEBTOURING,      Default.TEXT_MENU_WEBTOURING);
        textMenuHelp           = getString(properties, Key.TEXT_MENU_HELP,            Default.TEXT_MENU_HELP);
        textMenuTopics         = getString(properties, Key.TEXT_MENU_TOPICS,          Default.TEXT_MENU_TOPICS);
        textMenuAbout          = getString(properties, Key.TEXT_MENU_ABOUT,           Default.TEXT_MENU_ABOUT);

        textMenuRoom           = getString(properties, Key.TEXT_MENU_ROOM,            Default.TEXT_MENU_ROOM);
        textMenuClose          = getString(properties, Key.TEXT_MENU_CLOSE,           Default.TEXT_MENU_CLOSE);
        textMenuPeople         = getString(properties, Key.TEXT_MENU_PEOPLE,          Default.TEXT_MENU_PEOPLE);
        textMenuPeopleRing     = getString(properties, Key.TEXT_MENU_PEOPLE_RING,     Default.TEXT_MENU_PEOPLE_RING);
        textMenuPeopleIgnore   = getString(properties, Key.TEXT_MENU_PEOPLE_IGNORE,   Default.TEXT_MENU_PEOPLE_IGNORE);
        textMenuPeopleUnignore = getString(properties, Key.TEXT_MENU_PEOPLE_UNIGNORE, Default.TEXT_MENU_PEOPLE_UNIGNORE);
        textMenuPeopleCount    = getString(properties, Key.TEXT_MENU_PEOPLE_COUNT,    Default.TEXT_MENU_PEOPLE_COUNT);
        textMenuMonitor        = getString(properties, Key.TEXT_MENU_MONITOR,         Default.TEXT_MENU_MONITOR);
        textMenuMonitorRemove  = getString(properties, Key.TEXT_MENU_MONITOR_REMOVE,  Default.TEXT_MENU_MONITOR_REMOVE);
        textMenuMonitorKick    = getString(properties, Key.TEXT_MENU_MONITOR_KICK,    Default.TEXT_MENU_MONITOR_KICK);
        textMenuMonitorBan     = getString(properties, Key.TEXT_MENU_MONITOR_BAN,     Default.TEXT_MENU_MONITOR_BAN);

        textMenuLinksTitle     = getString(properties, Key.TEXT_MENU_LINKS_TITLE,     Default.TEXT_MENU_LINKS_TITLE);
        textMenuLinksNames     = getString(properties, Key.TEXT_MENU_LINKS_NAMES,     Default.TEXT_MENU_LINKS_NAMES);
        textMenuLinksLocations = getString(properties, Key.TEXT_MENU_LINKS_LOCATIONS, Default.TEXT_MENU_LINKS_LOCATIONS);

        textMenuThemesTitle    = getString(properties, Key.TEXT_MENU_THEMES_TITLE,   Default.TEXT_MENU_THEMES_TITLE);
        textMenuThemesNames    = getString(properties, Key.TEXT_MENU_THEMES_NAMES,   Default.TEXT_MENU_THEMES_NAMES);
        textMenuThemesDefault  = getString(properties, Key.TEXT_MENU_THEMES_DEFAULT, Default.TEXT_MENU_THEMES_DEFAULT);

        recentUserLimit      = getInteger(properties, Key.RECENT_USER_LIMIT, Default.RECENT_USER_LIMIT).intValue();
        textMonitorTitleRemove = getString(properties, Key.TEXT_MONITOR_TITLE_REMOVE, Default.TEXT_MONITOR_TITLE_REMOVE);
        textMonitorTitleKick   = getString(properties, Key.TEXT_MONITOR_TITLE_KICK,   Default.TEXT_MONITOR_TITLE_KICK);
        textMonitorTitleBan    = getString(properties, Key.TEXT_MONITOR_TITLE_BAN,    Default.TEXT_MONITOR_TITLE_BAN);
        textMonitorLabelRemove = getString(properties, Key.TEXT_MONITOR_LABEL_REMOVE, Default.TEXT_MONITOR_LABEL_REMOVE);
        textMonitorLabelKick   = getString(properties, Key.TEXT_MONITOR_LABEL_KICK,   Default.TEXT_MONITOR_LABEL_KICK);
        textMonitorLabelBan    = getString(properties, Key.TEXT_MONITOR_LABEL_BAN,    Default.TEXT_MONITOR_LABEL_BAN);
        textMonitorOkay        = getString(properties, Key.TEXT_MONITOR_OKAY,         Default.TEXT_MONITOR_OKAY);
        textMonitorCancel      = getString(properties, Key.TEXT_MONITOR_CANCEL,       Default.TEXT_MONITOR_CANCEL);

        textStatusFocusRooms          = getString(properties, Key.TEXT_STATUS_FOCUS_ROOMS,    Default.TEXT_STATUS_FOCUS_ROOMS);
        textStatusFocusUsers          = getString(properties, Key.TEXT_STATUS_FOCUS_USERS,    Default.TEXT_STATUS_FOCUS_USERS);
        textStatusFocusFilter         = getString(properties, Key.TEXT_STATUS_FOCUS_FILTER,   Default.TEXT_STATUS_FOCUS_FILTER);
        textStatusFocusUsername       = getString(properties, Key.TEXT_STATUS_FOCUS_USERNAME, Default.TEXT_STATUS_FOCUS_USERNAME);
        textStatusFocusProfile        = getString(properties, Key.TEXT_STATUS_FOCUS_PROFILE,  Default.TEXT_STATUS_FOCUS_PROFILE);
        textStatusFocusGetrooms       = getString(properties, Key.TEXT_STATUS_FOCUS_GETROOMS,  Default.TEXT_STATUS_FOCUS_GETROOMS);
        textStatusFocusEnter          = getString(properties, Key.TEXT_STATUS_FOCUS_ENTER,    Default.TEXT_STATUS_FOCUS_ENTER);
        textStatusFocusMembername     = getString(properties, Key.TEXT_STATUS_FOCUS_MEMBERNAME,  Default.TEXT_STATUS_FOCUS_MEMBERNAME);
        textStatusFocusMemberpassword = getString(properties, Key.TEXT_STATUS_FOCUS_MEMBERPASSWORD,  Default.TEXT_STATUS_FOCUS_MEMBERPASSWORD);

        textStatusSelectroom            = getString(properties, Key.TEXT_STATUS_SELECTROOM,      Default.TEXT_STATUS_SELECTROOM);
        textStatusEntername             = getString(properties, Key.TEXT_STATUS_ENTERNAME,       Default.TEXT_STATUS_ENTERNAME);
        textStatusEnterpassword         = getString(properties, Key.TEXT_STATUS_ENTERPASSWORD,   Default.TEXT_STATUS_ENTERPASSWORD);
        textStatusEntermembername       = getString(properties, Key.TEXT_STATUS_ENTERMEMBERNAME,       Default.TEXT_STATUS_ENTERMEMBERNAME);
        textStatusEntermemberpassword   = getString(properties, Key.TEXT_STATUS_ENTERMEMBERPASSWORD,   Default.TEXT_STATUS_ENTERMEMBERPASSWORD);
        textStatusEnterprofile          = getString(properties, Key.TEXT_STATUS_ENTERPROFILE,    Default.TEXT_STATUS_ENTERPROFILE);
        textStatusEnter                 = getString(properties, Key.TEXT_STATUS_ENTER,           Default.TEXT_STATUS_ENTER);
        textStatusEnteringroom          = getString(properties, Key.TEXT_STATUS_ENTERINGROOM,    Default.TEXT_STATUS_ENTERINGROOM);
        textStatusEnteringprivate       = getString(properties, Key.TEXT_STATUS_ENTERINGPRIVATE, Default.TEXT_STATUS_ENTERINGPRIVATE);
        textStatusGettingrooms          = getString(properties, Key.TEXT_STATUS_GETTINGROOMS,    Default.TEXT_STATUS_GETTINGROOMS);
        textStatusGettingusers          = getString(properties, Key.TEXT_STATUS_GETTINGUSERS,    Default.TEXT_STATUS_GETTINGUSERS);
        textStatusGettingprofile        = getString(properties, Key.TEXT_STATUS_GETTINGPROFILE,  Default.TEXT_STATUS_GETTINGPROFILE);
        textStatusNosuchroom            = getString(properties, Key.TEXT_STATUS_NOSUCHROOM,      Default.TEXT_STATUS_NOSUCHROOM);
        textStatusNosuchuser            = getString(properties, Key.TEXT_STATUS_NOSUCHUSER,      Default.TEXT_STATUS_NOSUCHUSER);
        textStatusNametaken             = getString(properties, Key.TEXT_STATUS_NAMETAKEN,       Default.TEXT_STATUS_NAMETAKEN);
        textStatusMembertaken           = getString(properties, Key.TEXT_STATUS_MEMBERTAKEN,     Default.TEXT_STATUS_MEMBERTAKEN);
        textStatusAlreadyinroom         = getString(properties, Key.TEXT_STATUS_ALREADYINROOM,   Default.TEXT_STATUS_ALREADYINROOM);
        textStatusRoomfull              = getString(properties, Key.TEXT_STATUS_ROOMFULL,        Default.TEXT_STATUS_ROOMFULL);
        textStatusRoomcount             = getString(properties, Key.TEXT_STATUS_ROOMCOUNT,       Default.TEXT_STATUS_ROOMCOUNT);
        textStatusPubliclimit           = getString(properties, Key.TEXT_STATUS_PUBLICLIMIT,     Default.TEXT_STATUS_PUBLICLIMIT);
        textStatusPrivatelimit          = getString(properties, Key.TEXT_STATUS_PRIVATELIMIT,    Default.TEXT_STATUS_PRIVATELIMIT);
        textStatusNoprofile             = getString(properties, Key.TEXT_STATUS_NOPROFILE,       Default.TEXT_STATUS_NOPROFILE);
        textStatusProfile               = getString(properties, Key.TEXT_STATUS_PROFILE,         Default.TEXT_STATUS_PROFILE);
        textStatusClosing               = getString(properties, Key.TEXT_STATUS_CLOSING,         Default.TEXT_STATUS_CLOSING);

        textSystemEntrance              = getString(properties, Key.TEXT_SYSTEM_ENTRANCE,        Default.TEXT_SYSTEM_ENTRANCE);
        textSystemAudio                 = getString(properties, Key.TEXT_SYSTEM_AUDIO,           Default.TEXT_SYSTEM_AUDIO);
        textSystemBroadcast             = getString(properties, Key.TEXT_SYSTEM_BROADCAST,       Default.TEXT_SYSTEM_BROADCAST);
        textSystemPartnerleft           = getString(properties, Key.TEXT_SYSTEM_PARTNERLEFT,     Default.TEXT_SYSTEM_PARTNERLEFT);
        textSystemDisconnected          = getString(properties, Key.TEXT_SYSTEM_DISCONNECTED,    Default.TEXT_SYSTEM_DISCONNECTED);

        if (group.length() > lengthRoomname) {
            group = group.substring(0, lengthRoomname).trim();
        }
        if (username.length() > lengthUsername) {
            username = username.substring(0, lengthUsername).trim();
        }
        if (profile.length() > lengthProfile) {
            profile = profile.substring(0, lengthProfile).trim();
        }

        sounds = new Sounds(this);
        acceptPrivateEnabled  = acceptPrivateDefault;
        entranceAlertsEnabled = alertEntranceDefault;
        audioAlertsEnabled    = alertAudioDefault;
        countAlertsEnabled    = alertCountDefault;
        webtouringEnabled     = webtouringDefault;
        isMonitor             = false;      // 2.1.8
        wasDisconnected       = false;      // 2.1.9

        /*
            // To check that the defaults are equal to the english.txt file.
            import java.lang.reflect.*;
            try {
                PrintWriter writer = new PrintWriter(new FileWriter("Value.txt"));
                Field[] fields = getClass().getFields();
                for (int i = 0; i < fields.length; i++) {
                    writer.println(fields[i] + " = " + fields[i].get(this));
                }
                writer.flush();
                writer.close();
            }
            catch (Exception e) {
                System.err.println(e);
            }
        */
    }

    /**
     * Gets the theme specified by the index.
     *
     * @param index the theme index.
     * @return a newly created theme for the specified index.
     */

    public Theme getTheme(int index) {
        String prefix = (index == 0 ? "" : Key.THEME_PREFIX + index + ".");
        Theme  theme  = new Theme(index);

        Color colorBackground                     = getColor(properties, prefix + Key.COLOR_BACKGROUND,                        this.colorBackground);
        Color colorBackgroundButton               = getColor(properties, prefix + Key.COLOR_BACKGROUND_BUTTON,                 this.colorBackgroundButton);
        Color colorBackgroundList                 = getColor(properties, prefix + Key.COLOR_BACKGROUND_LIST,                   this.colorBackgroundList);
        Color colorBackgroundText                 = getColor(properties, prefix + Key.COLOR_BACKGROUND_TEXT,                   this.colorBackgroundText);
        Color colorBackgroundTextEditable         = getColor(properties, prefix + Key.COLOR_BACKGROUND_TEXT_EDITABLE,          this.colorBackgroundTextEditable);

        Color colorForeground                     = getColor(properties, prefix + Key.COLOR_FOREGROUND,                        this.colorForeground);
        Color colorForegroundButton               = getColor(properties, prefix + Key.COLOR_FOREGROUND_BUTTON,                 this.colorForegroundButton);
        Color colorForegroundList                 = getColor(properties, prefix + Key.COLOR_FOREGROUND_LIST,                   this.colorForegroundList);
        Color colorForegroundText                 = getColor(properties, prefix + Key.COLOR_FOREGROUND_TEXT,                   this.colorForegroundText);
        Color colorForegroundTextEditable         = getColor(properties, prefix + Key.COLOR_FOREGROUND_TEXT_EDITABLE,          this.colorForegroundTextEditable);
        Color colorForegroundTextEditableInactive = getColor(properties, prefix + Key.COLOR_FOREGROUND_TEXT_EDITABLE_INACTIVE, this.colorForegroundTextEditableInactive);
        Color colorForegroundTextLink             = getColor(properties, prefix + Key.COLOR_FOREGROUND_TEXT_LINK,              this.colorForegroundTextLink);

        Font  fontDefault = getFont(properties, prefix + Key.FONT_DEFAULT, stringFontDefault);

        theme.setColorBackground(colorBackground);
        theme.setColorBackgroundButton(colorBackgroundButton);
        theme.setColorBackgroundList(colorBackgroundList);
        theme.setColorBackgroundText(colorBackgroundText);
        theme.setColorBackgroundTextEditable(colorBackgroundTextEditable);

        theme.setColorForeground(colorForeground);
        theme.setColorForegroundButton(colorForegroundButton);
        theme.setColorForegroundList(colorForegroundList);
        theme.setColorForegroundText(colorForegroundText);
        theme.setColorForegroundTextEditable(colorForegroundTextEditable);
        theme.setColorForegroundTextEditableInactive(colorForegroundTextEditableInactive);
        theme.setColorForegroundTextLink(colorForegroundTextLink);

        theme.setFontDefault(fontDefault);

        return theme;
    }
}
