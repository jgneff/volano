/*
 * BannerPlayer.java - an applet for rotating through banner images.
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

package COM.volano;
import  COM.volano.chat.Build;  // For getAppletInfo
import  java.applet.*;
import  java.awt.*;
import  java.net.*;
import  java.util.*;

/**
 * This applet rotates through a set of banner images.  The applet parameters
 * are:
 *
 * <dl>
 * <dt><code>background</code>
 * <dd>the background color of the applet, shown when the image is smaller than
 * the applet size or when displaying a transparent image.  The default is the
 * background color of the window.
 *
 * <dt><code>foreground</code>
 * <dd>the foreground color of the applet, used to display error message text
 * when the applet fails to load an image.  The default is the foreground color
 * of the window.
 *
 * <dt><code>banner.width</code>
 * <dd>the width in pixels at which to display the banner images.  The default
 * is 468 pixels.
 *
 * <dt><code>banner.height</code>
 * <dd>the height in pixels at which to display the banner images.  The default
 * is 60 pixels.
 *
 * <dt><code>banner.cache</code>
 * <dd><code>true</code> if the banner images should be cached; otherwise
 * <code>false</code>.  The default is <code>false</code>.  A value of
 * <code>true</code> flushes all images previously displayed from the URL before
 * invoking the URL for another image.
 *
 * <dt><code>banner.ismap</code>
 * <dd><code>true</code> if the banner is an image map; otherwise
 * <code>false</code>.  The default value is <code>false</code>.
 *
 * <dt><code>banner.char</code>
 * <dd>a character that will be substituted for a unique integer counter if
 * present in either the source or link URL.  The default is no character
 * substition in the URLs.  Only the first character of the string is used.
 *
 * <dt><code>banner.<i>n</i></code>
 * <dd>the image specifications, where <i>n</i> is a positive integer.
 *
 * <dt><code>bar.width</code>
 * <dd>the width in pixels at which to display an optional navigation bar.  The
 * default is 468 pixels.
 *
 * <dt><code>bar.height</code>
 * <dd>the height in pixels at which to display an optional navigation bar.  The
 * default is 16 pixels.
 *
 * <dt><code>bar.cache</code>
 * <dd><code>true</code> if the bar image should be cached; otherwise
 * <code>false</code>.  The default is <code>false</code>.  A value of
 * <code>true</code> flushes all images previously displayed from the URL before
 * invoking the URL for another image.
 *
 * <dt><code>bar.ismap</code>
 * <dd><code>true</code> if the navigation bar is an image map; otherwise
 * <code>false</code>.  The default value is <code>false</code>.
 *
 * <dt><code>bar.char</code>
 * <dd>a character that will be substituted for a unique integer counter if
 * present in either the source or link URL.  The default is no character
 * substition in the URLs.  Only the first character of the string is used.
 *
 * <dt><code>bar.image</code>
 * <dd>the image specifications for the bar, where the time value is ignored.
 * </dl>
 *
 * <p>The image specifications start with <code>banner.1</code> and end with the
 * first integer not found.  The specification consists of three elements
 * separated by spaces or tabs:
 * <pre>
 * <i>time src href</i>
 * </pre>
 * where:
 *
 * <dl>
 * <dt><i>time</i>
 * <dd>is the time for the image to be displayed, in seconds.  A time of zero
 * means to display the image forever.
 *
 * <dt><i>src</i>
 * <dd>is the absolute or relative image source URL.  A relative URL is assumed
 * to be relative to the applet code base.
 *
 * <dt><i>href</i>
 * <dd>is the absolute or relative URL to which this image is linked, or the
 * keyword <code>null</code> if the image has no link.  A relative URL is
 * assumed to be relative to the applet code base.
 * </dl>
 *
 * @author  John Neffenger
 * @version 16 Oct 1998
 */

public class BannerPlayer extends Applet implements Runnable {
    // Applet parameter names.
    private static final String BACKGROUND_KEY = "background";
    private static final String FOREGROUND_KEY = "foreground";
    // Banner and navigation bar key prefixes.
    private static final String BANNER_KEY     = "banner.";
    private static final String BAR_KEY        = "bar.";
    // Banner and navigation bar key suffixes.
    private static final String WIDTH_KEY      = "width";
    private static final String HEIGHT_KEY     = "height";
    private static final String CACHE_KEY      = "cache";
    private static final String ISMAP_KEY      = "ismap";
    private static final String CHAR_KEY       = "char";
    private static final String IMAGE_KEY      = "image";

    // Applet parameter default values.  Use Internet Advertising Bureau full
    // banner size by default.
    private static final int  DEFAULT_BANNER_WIDTH  =  468;
    private static final int  DEFAULT_BANNER_HEIGHT =   60;
    private static final int  DEFAULT_BAR_WIDTH     =  468;
    private static final int  DEFAULT_BAR_HEIGHT    =   16;
    private static final int  MILLIS_PER_SEC        = 1000;

    // For displaying error messages in the banner area.
    private static final Banner ERROR_BANNER = new Banner(Banner.ERROR_MSG, DEFAULT_BANNER_WIDTH, DEFAULT_BANNER_HEIGHT);

    private static boolean infoPrinted = false;

    private GridBagLayout layout;         // Layout manager
    private Color         background;     // Background color or null
    private Color         foreground;     // Foreground color or null
    private Thread        player;         // Thread to rotate through images
    private int           index;          // Image index
    private Banner        banner;         // Current banner image
    private boolean       bannerCache;    // Cache banner image by source URL
    private boolean       bannerIsmap;    // Banner is an image map
    private char          bannerChar;     // Banner URL substitution character
    private boolean       barCache;       // Cache bar image by source URL
    private boolean       barIsmap;       // Bar is an image map
    private char          barChar;        // Bar URL substitution character

    /**
     * Gets information about this applet.
     *
     * @return a string giving the applet name, version, URL, and copyright.
     */

    public String getAppletInfo() {
        return getClass().getName() + " " + Build.VERSION + " " + Build.VOLANO_URL + "\n" +
               Build.APPLET_COPYRIGHT;
    }

    /**
     * Gets the integer defined by the applet parameter.
     *
     * @param applet  the applet.
     * @param key     the applet parameter name.
     * @return  the integer defined by the applet parameter, or <code>null</code>
     *          if the parameter is not defined or not a valid number.
     */

    private static Integer getInteger(Applet applet, String key) {
        Integer value  = null;
        String  string = applet.getParameter(key);
        if (string != null && string.length() > 0) {
            try {
                string = string.trim();
                if (string.startsWith("0x")) {
                    value = Integer.valueOf(string.substring(2), 16);
                } else if (string.startsWith("#")) {
                    value = Integer.valueOf(string.substring(1), 16);
                } else if (string.startsWith("0") && string.length() > 1) {
                    value = Integer.valueOf(string.substring(1), 8);
                } else {
                    value = Integer.valueOf(string);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error getting integer (" + e + ").");
            }
        }
        return value;
    }

    /**
     * Gets the integer value specified by the applet parameter.
     *
     * @param applet        the applet.
     * @param key           the applet parameter name.
     * @param defaultValue  the default value for the integer.
     * @return  the integer value of the named applet parameter, or the default
     *          value if the parameter is not defined or not a valid number.
     */

    private static int getInteger(Applet applet, String key, int defaultValue) {
        Integer value = getInteger(applet, key);
        return value == null ? defaultValue : value.intValue();
    }

    /**
     * Gets an image banner.  If an error occurs, this method returns a banner
     * which displays the error message.
     *
     * @param applet         this applet.
     * @param prefix         the parameter prefix for this image.
     * @param suffix         the parameter suffix for this image description.
     * @param defaultWidth   the default width of the image.
     * @param defaultHeight  the default height of the image.
     * @param cache          <code>true</code> if the image should be cached;
     *                       otherwise <code>false</code>.
     * @param ismap          <code>true</code> if the banner is an image map;
     *                       otherwise <code>false</code>.
     * @param subchar        the URL substitution character.
     * @return  the banner to display the image or error message.
     */

    private static Banner getBanner(Applet applet, String prefix, String suffix,
                                    int defaultWidth, int defaultHeight, Color background, Color foreground,
                                    boolean cache, boolean ismap, char subchar) {
        Banner canvas = null;
        String key    = prefix + suffix;
        String banner = applet.getParameter(key);
        if (banner != null) {
            int width  = getInteger(applet, prefix + WIDTH_KEY,  defaultWidth);
            int height = getInteger(applet, prefix + HEIGHT_KEY, defaultHeight);
            try {
                StringTokenizer tokens = new StringTokenizer(banner);
                int time = Integer.parseInt(tokens.nextToken());
                if (time < 0) {
                    throw new NumberFormatException(Integer.toString(time));
                }
                String source = tokens.nextToken();
                String link   = tokens.nextToken();
                canvas = new Banner(applet, time, source, link, width, height, background, foreground, cache, ismap, subchar);
            } catch (MalformedURLException e) {       // An IOException
                System.err.println("Invalid URL in " + key + " (" + e + ").");
                canvas = ERROR_BANNER;
            } catch (NumberFormatException e) {       // An IllegalArgumentException and RuntimeException
                System.err.println("Invalid time in " + key + " (" + e + ").");
                canvas = ERROR_BANNER;
            } catch (NoSuchElementException e) {      // A RuntimeException
                System.err.println("Incomplete specification in " + key + ".");
                canvas = ERROR_BANNER;
            }
        }
        return canvas;
    }


    /**
     * Gets the next banner image.  If an error occurs, this method returns a
     * banner which displays the error message.
     *
     * @param applet  this applet.
     * @return  the banner to display the image or error message.
     */

    private Banner getNextBanner(Applet applet) {
        Banner canvas = null;
        do {
            canvas = getBanner(applet, BANNER_KEY, Integer.toString(++index),
                               DEFAULT_BANNER_WIDTH, DEFAULT_BANNER_HEIGHT, background, foreground,
                               bannerCache, bannerIsmap, bannerChar);
            if (canvas == null && index == 1) {
                System.err.println("Unable to find " + BANNER_KEY + index + ".");
                canvas = ERROR_BANNER;
            } else if (canvas == null) {
                index = 0;
            }
        } while (index == 0);
        return canvas;
    }

    /**
     * Initializes this applet, called after the applet is loaded.
     */

    public void init() {
        if (! infoPrinted) {
            System.out.println(getAppletInfo());
            infoPrinted = true;
        }
        banner = null;      // Initialize banner for remove(oldBanner) below

        Integer backgroundValue = getInteger(this, BACKGROUND_KEY);
        Integer foregroundValue = getInteger(this, FOREGROUND_KEY);
        background = backgroundValue == null ? null : new Color(backgroundValue.intValue());
        foreground = foregroundValue == null ? null : new Color(foregroundValue.intValue());
        setBackground(background);
        setForeground(foreground);

        bannerCache = Boolean.valueOf(getParameter(BANNER_KEY + CACHE_KEY)).booleanValue();
        bannerIsmap = Boolean.valueOf(getParameter(BANNER_KEY + ISMAP_KEY)).booleanValue();
        barCache    = Boolean.valueOf(getParameter(BAR_KEY + CACHE_KEY)).booleanValue();
        barIsmap    = Boolean.valueOf(getParameter(BAR_KEY + ISMAP_KEY)).booleanValue();

        String bannerString = getParameter(BANNER_KEY + CHAR_KEY);
        String barString    = getParameter(BAR_KEY    + CHAR_KEY);
        bannerString = bannerString == null ? "" : bannerString.trim();
        barString    = barString    == null ? "" : barString.trim();
        bannerChar   = bannerString.length() == 0 ? (char) 0 : bannerString.charAt(0);
        barChar      = barString.length()    == 0 ? (char) 0 : barString.charAt(0);

        layout = new GridBagLayout();
        setLayout(layout);
    }

    /**
     * Starts this applet, creating and starting its image playing thread.
     */

    public void start() {
        if (player == null) {
            player = new Thread(this);
            player.start();
        }
    }

    /**
     * Stops this applet, stopping the image playing thread.
     */

    public void stop() {
        try {
            if (player != null) {
                player.stop();
                player = null;
            }
        } catch (Throwable t) {}
    }

    /**
     * Sets the background color.
     *
     * @param color the background color.
     */

    public void setBackground(Color color) {
        if (background == null || color == background) {
            super.setBackground(color);
        }
    }

    /**
     * Sets the foreground color.
     *
     * @param color the foreground color.
     */

    public void setForeground(Color color) {
        if (foreground == null || color == foreground) {
            super.setForeground(color);
        }
    }

    /**
     * The body of the image playing thread.  This thread gets the next image to
     * display and waits for the image to finish loading.  It then sleeps for the
     * specified image time.
     */

    public void run() {
        try {
            boolean needToAddBar = false;
            Banner  bar = getBanner(this, BAR_KEY, IMAGE_KEY,
                                    DEFAULT_BAR_WIDTH, DEFAULT_BAR_HEIGHT, background, foreground,
                                    barCache, barIsmap, barChar);
            if (bar != null) {
                needToAddBar = true;
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 1;
                layout.setConstraints(bar, constraints);
            }

            index = 0;
            while (player == Thread.currentThread()) {
                Banner oldBanner = banner;
                banner = getNextBanner(this);
                banner.waitImage();
                if (oldBanner != null) {
                    remove(oldBanner);
                }
                add(banner);
                if (needToAddBar) {
                    add(bar);                     // Add navigation bar after the banner
                    needToAddBar = false;
                    // The banner needs repainted again after the bar is added since, if
                    // its image is already loaded, it gets painted as the only component,
                    // too far down in the container.
                    validate();
                    banner.repaint();
                }
                validate();
                int time = banner.getTime();
                if (time == 0) {
                    player = null;
                } else {
                    Thread.sleep(time * MILLIS_PER_SEC);
                }
            }
        } catch (InterruptedException e) {}
    }
}
