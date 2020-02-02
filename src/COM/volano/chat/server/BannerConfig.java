/*
 * BannerConfig.java - an interface for Web-based configuration of the
 *                     BannerPlayer applet.
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
import  java.util.Vector;

/**
 * This class provides an interface for configuring the BannerPlayer applet
 * through a Web interface.  This class is written with particular attention to
 * making it easy to use with the Velocity template engine.  This class acts as
 * a model for views created using the Velocity template language.  Java
 * servlets can use this model to configure the applet properties.
 *
 * @author  John Neffenger
 * @version 27 Jul 2001
 */

public class BannerConfig extends Config {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    // Banner key prefix.
    private static final String KEY_BANNER            = "banner.";

    // Applet parameter names.
    private static final String KEY_BACKGROUND        = "background";
    private static final String KEY_FOREGROUND        = "foreground";

    private static final String KEY_BANNER_WIDTH      = "banner.width";
    private static final String KEY_BANNER_HEIGHT     = "banner.height";
    private static final String KEY_BANNER_CACHE      = "banner.cache";
    private static final String KEY_BANNER_ISMAP      = "banner.ismap";
    private static final String KEY_BANNER_CHAR       = "banner.char";

    private static final String KEY_BAR_WIDTH         = "bar.width";
    private static final String KEY_BAR_HEIGHT        = "bar.height";
    private static final String KEY_BAR_CACHE         = "bar.cache";
    private static final String KEY_BAR_ISMAP         = "bar.ismap";
    private static final String KEY_BAR_CHAR          = "bar.char";
    private static final String KEY_BAR_IMAGE         = "bar.image";

    // Applet parameter default values.  Use Internet Advertising Bureau full
    // banner size by default.
    private static final String DEFAULT_BACKGROUND    = "#ffffff";
    private static final String DEFAULT_FOREGROUND    = "#000000";

    private static final String DEFAULT_BANNER_WIDTH  =     "468";
    private static final String DEFAULT_BANNER_HEIGHT =      "60";
    private static final String DEFAULT_BANNER_CACHE  =    "true";
    private static final String DEFAULT_BANNER_ISMAP  =   "false";
    private static final String DEFAULT_BANNER_CHAR   =       "$";

    private static final String DEFAULT_BAR_WIDTH     =     "468";
    private static final String DEFAULT_BAR_HEIGHT    =      "16";
    private static final String DEFAULT_BAR_CACHE     =    "true";
    private static final String DEFAULT_BAR_ISMAP     =   "false";
    private static final String DEFAULT_BAR_CHAR      =       "$";
    private static final String DEFAULT_BAR_IMAGE     = "0 bar.gif about.html";

    private static final String HEADER =  "BannerPlayer Applet Properties - do not move or modify this line.";
    private static final String TEST   = "#BannerPlayer Applet Properties";

    private static String[] filelist = new String[0];

    static void refreshFileList(File base) throws IOException {
        filelist = getFileList(base, TEST);
    }

    public static String[] getFileList() {
        return filelist;
    }

    BannerConfig(File base, String path) throws IOException {
        super(base, path, filelist);
    }

    // Instance methods.

    public String getFile() {
        return path;
    }

    // background=#FFFFFF

    public String getBackground() {
        return getColor(KEY_BACKGROUND, DEFAULT_BACKGROUND);
    }

    public void setBackground(String background) throws NumberFormatException {
        setColor(KEY_BACKGROUND, background);
    }

    // foreground=#000000

    public String getForeground() {
        return getColor(KEY_FOREGROUND, DEFAULT_FOREGROUND);
    }

    public void setForeground(String foreground) throws NumberFormatException {
        setColor(KEY_FOREGROUND, foreground);
    }

    // banner.width=468

    public int getBannerWidth() {
        return getInteger(KEY_BANNER_WIDTH, DEFAULT_BANNER_WIDTH);
    }

    public void setBannerWidth(String bannerWidth) throws NumberFormatException {
        setInteger(KEY_BANNER_WIDTH, bannerWidth);
    }

    // banner.height=60

    public int getBannerHeight() {
        return getInteger(KEY_BANNER_HEIGHT, DEFAULT_BANNER_HEIGHT);
    }

    public void setBannerHeight(String bannerHeight) throws NumberFormatException {
        setInteger(KEY_BANNER_HEIGHT, bannerHeight);
    }

    // banner.cache=true

    public boolean getBannerCache() {
        return getBoolean(KEY_BANNER_CACHE, DEFAULT_BANNER_CACHE);
    }

    public void setBannerCache(String bannerCache) {
        setBoolean(KEY_BANNER_CACHE, bannerCache);
    }

    // banner.ismap=false

    public boolean getBannerIsmap() {
        return getBoolean(KEY_BANNER_ISMAP, DEFAULT_BANNER_ISMAP);
    }

    public void setBannerIsmap(String bannerIsmap) {
        setBoolean(KEY_BANNER_ISMAP, bannerIsmap);
    }

    // banner.char=$

    public String getBannerChar() {
        return getChars(KEY_BANNER_CHAR, DEFAULT_BANNER_CHAR);
    }

    public void setBannerChar(String bannerChar) throws IOException {
        setChars(KEY_BANNER_CHAR, bannerChar);
    }

    // banner.1=30 welcome.gif    http://www.volano.com/
    // banner.2=30 help.gif       help.html
    // banner.3=30 chatwithme.gif http://www.volano.com/

    public String[] getBannerList() {
        Vector list   = new Vector();
        int    index  = 1;
        String string = properties.getProperty(KEY_BANNER + index);
        while (string != null) {
            list.addElement(string);
            index  += 1;
            string = properties.getProperty(KEY_BANNER + index);
        }
        return (String[]) list.toArray(new String[0]);
    }

    public void setBanner(int index, String banner) {
        setString(KEY_BANNER + index, banner);
    }

    // bar.width=468

    public int getBarWidth() {
        return getInteger(KEY_BAR_WIDTH, DEFAULT_BAR_WIDTH);
    }

    public void setBarWidth(String barWidth) throws NumberFormatException {
        setInteger(KEY_BAR_WIDTH, barWidth);
    }

    // bar.height=16

    public int getBarHeight() {
        return getInteger(KEY_BAR_HEIGHT, DEFAULT_BAR_HEIGHT);
    }

    public void setBarHeight(String barHeight) throws NumberFormatException {
        setInteger(KEY_BAR_HEIGHT, barHeight);
    }

    // bar.cache=true

    public boolean getBarCache() {
        return getBoolean(KEY_BAR_CACHE, DEFAULT_BAR_CACHE);
    }

    public void setBarCache(String barCache) {
        setBoolean(KEY_BAR_CACHE, barCache);
    }

    // bar.ismap=false

    public boolean getBarIsmap() {
        return getBoolean(KEY_BAR_ISMAP, DEFAULT_BAR_ISMAP);
    }

    public void setBarIsmap(String barIsmap) {
        setBoolean(KEY_BAR_ISMAP, barIsmap);
    }

    // bar.char=$

    public String getBarChar() {
        return getChars(KEY_BAR_CHAR, DEFAULT_BAR_CHAR);
    }

    public void setBarChar(String barChar) throws IOException {
        setChars(KEY_BAR_CHAR, barChar);
    }

    // bar.image=0 bar.gif about.html

    public String getBarImage() {
        return getString(KEY_BAR_IMAGE, DEFAULT_BAR_IMAGE);
    }

    public void setBarImage(String barImage) {
        setString(KEY_BAR_IMAGE, barImage);
    }
}
