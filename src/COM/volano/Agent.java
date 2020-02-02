/*
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

import java.applet.*;
import java.awt.*;

public class Agent extends Applet {
    private static final String FONT_NAME  = "Courier";
    private static final int    FONT_STYLE = Font.PLAIN;
    private static final int    FONT_SIZE  = 13;

    private static final String JAVA_VENDOR        = "java.vendor";
    private static final String JAVA_VENDOR_URL    = "java.vendor.url";
    private static final String JAVA_VERSION       = "java.version";
    private static final String JAVA_CLASS_VERSION = "java.class.version";
    private static final String OS_NAME            = "os.name";
    private static final String OS_VERSION         = "os.version";
    private static final String OS_ARCH            = "os.arch";

    private String   javaVendor;
    private String   javaVendorUrl;
    private String   javaVersion;
    private String   javaClassVersion;
    private String   osName;
    private String   osVersion;
    private String   osArch;
    private TextArea area;

    public void init() {
        javaVendor       = System.getProperty(JAVA_VENDOR, "");
        javaVendorUrl    = System.getProperty(JAVA_VENDOR_URL, "");
        javaVersion      = System.getProperty(JAVA_VERSION, "");
        javaClassVersion = System.getProperty(JAVA_CLASS_VERSION, "");
        osName           = System.getProperty(OS_NAME, "");
        osVersion        = System.getProperty(OS_VERSION, "");
        osArch           = System.getProperty(OS_ARCH, "");

        area = new TextArea();
        area.setBackground(Color.white);
        area.setForeground(Color.black);
        area.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        area.setEditable(false);
        setLayout(new BorderLayout());
        add("Center", area);
    }

    public void start() {
        area.append(javaVendor + " Java version " + javaVersion + " (" + javaClassVersion + ")" + "\n");
        area.append(osName + " version " + osVersion + " (" + osArch + ")" + "\n");
        area.append(javaVendorUrl);
    }

    public void stop() {
        area.setText("");
    }
}
