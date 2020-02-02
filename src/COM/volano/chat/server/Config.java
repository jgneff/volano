/*
 * Config.java - a base class for configuring applet properties through the Web.
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
import  COM.volano.io.TextFileFilter;
import  COM.volano.io.UnicodeReader;
import  java.awt.Color;
import  java.awt.Font;
import  java.io.*;
import  java.net.URL;
import  java.net.MalformedURLException;
import  java.util.Properties;
import  java.util.Vector;

/**
 * This class provides a base class for configuring the applet properties
 * through a Web interface.  This class is written with particular attention to
 * making it easy to use with the Velocity template engine.  This class acts as
 * a model for views created using the Velocity template language.  Java
 * servlets can use this model to configure the applet properties.
 *
 * @author  John Neffenger
 * @version 03 Aug 2001
 */

class Config {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final char[] HEX_DIGIT =
    {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    protected Properties properties = new Properties();
    protected String     path       = "";   // Default empty string for Velocity

    protected static String[] getFileList(File base, String test) throws IOException {
        Vector list  = new Vector();
        File[] files = base.listFiles(new TextFileFilter());
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(files[i]));
                String         line   = reader.readLine();
                if (line != null && line.startsWith(test)) {
                    list.addElement(files[i].getName());
                }
                reader.close();
            }
        }
        return (String[]) list.toArray(new String[0]);
    }

    private static char toHex(int nibble) {
        return HEX_DIGIT[(nibble & 0xF)];
    }

    protected Config(File base, String path, String[] filelist) throws IOException {
        if (path != null && path.length() != 0) { // Use specified file
            this.path = path;
            load(base, this.path);
        } else if (filelist.length > 0) {         // Otherwise, get first file found
            this.path = filelist[0];
            load(base, this.path);
        }
    }

    private void load(File base, String path) throws IOException {
        InputStream input = new FileInputStream(new File(base, path));
        properties.load(input);
        input.close();
    }

    // Character values with Unicode escape sequences (may contain empty strings).

    protected String getChars(String key, String defaultValue) {
        String       string = properties.getProperty(key, defaultValue);
        int          len    = string.length();
        StringBuffer buffer = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            switch (c) {
            case '\\':
                buffer.append("\\\\");
                break;
            case '\t':
                buffer.append("\\t");
                break;
            case '\n':
                buffer.append("\\n");
                break;
            case '\r':
                buffer.append("\\r");
                break;
            default:
                if (c < '\u0020' || c > '\u007E') {
                    buffer.append('\\');
                    buffer.append('u');
                    buffer.append(toHex((c >> 12) & 0xF));
                    buffer.append(toHex((c >>  8) & 0xF));
                    buffer.append(toHex((c >>  4) & 0xF));
                    buffer.append(toHex( c        & 0xF));
                } else {
                    buffer.append(c);
                }
                break;
            }
        }
        return buffer.toString();
    }

    protected void setChars(String key, String value) throws IOException {
        int len = value.length();
        BufferedReader reader = new BufferedReader(new UnicodeReader(new StringReader(value)), len);
        StringBuffer   buffer = new StringBuffer(len);
        String         string = reader.readLine();
        while (string != null) {
            buffer.append(string);
            string = reader.readLine();
        }
        properties.setProperty(key, buffer.toString());
    }

    // String values (may be set to empty string).

    protected String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    protected void setString(String key, String value) {
        properties.setProperty(key, value.trim());
    }

    // Boolean values ("true" or "false").

    protected boolean getBoolean(String key, String defaultValue) {
        return Boolean.valueOf(properties.getProperty(key, defaultValue)).booleanValue();
    }

    protected void setBoolean(String key, String value) {
        properties.setProperty(key, Boolean.valueOf(value).toString());
    }

    // Integer values (must have a value).

    protected int getInteger(String key, String defaultValue) {
        int i = 0;
        try {
            i = Integer.parseInt(defaultValue);
            i = Integer.parseInt(properties.getProperty(key, defaultValue));
        } catch (NumberFormatException e) {
            properties.setProperty(key, defaultValue);
        }
        return i;
    }

    protected void setInteger(String key, String value) throws NumberFormatException {
        properties.setProperty(key, Integer.valueOf(value).toString());
    }

    // Color values (may be set to an empty string).

    protected String getColor(String key, String defaultValue) {
        String s = "";
        try {
            Color  color = defaultValue.length() == 0 ? null : Color.decode(defaultValue);
            String value = properties.getProperty(key, defaultValue);
            color = value.length() == 0 ? null : Color.decode(value);
            s = color == null ? "" : "#" + Integer.toHexString(color.getRGB()).substring(2);
        } catch (NumberFormatException e) {
            properties.setProperty(key, defaultValue);
        }
        return s;
    }

    protected void setColor(String key, String value) throws NumberFormatException {
        properties.setProperty(key, Color.decode(value).toString());
    }

    // Font values (may be set to an empty string).

    protected String getFont(String key, String defaultValue) {
        Font   font  = Font.decode(properties.getProperty(key, defaultValue));
        String style = "";
        switch (font.getStyle()) {
        case Font.BOLD | Font.ITALIC:
            style = "bold";
            break;
        case Font.BOLD:
            style = "bold";
            break;
        case Font.ITALIC:
            style = "bolditalic";
            break;
        case Font.PLAIN:
            style = "plain";
            break;
        default:
            style = "";
            break;
        }
        return font.getName() + "-" + style + "-" + font.getSize();
    }

    protected void setFont(String key, String value) {
        properties.setProperty(key, Font.decode(value).toString());
    }

    // URL values (may be set to an empty string).

    protected String getURL(String key, String defaultValue) {
        String s = null;
        try {
            s = defaultValue.length() == 0 ? "" : new URL(defaultValue).toString();
            String value = properties.getProperty(key, defaultValue);
            s = value.length() == 0 ? "" : new URL(value).toString();
        } catch (MalformedURLException e) {
            properties.setProperty(key, defaultValue);
        }
        return s;
    }

    protected void setURL(String key, String value) throws MalformedURLException {
        properties.setProperty(key, new URL(value).toString());
    }

    // File values (may be set to an empty string).

    protected String getFile(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        return value.length() == 0 ? "" : new File(value).toString();
    }

    protected void setFile(String key, String value) throws FileNotFoundException {
        String s = "";
        if (value.length() != 0) {
            s = new File(value).toString();
        }
        properties.setProperty(key, s);
    }
}
