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
package COM.volano.chat.security;

// DashO-Pro replaces COM.volano.chat.security.DSAAppletSecurity with
// COM/volano/j (for example), which fails on Sun Java 1.1.8 or later:
//   Sun Java 1.4.0 ...
//     java.lang.ClassNotFoundException: com/volano/b
//   Sun Java 1.3.1 ...
//     java.lang.ClassNotFoundException: com/volano/b
//   Sun Java 1.2.2 ...
//     java.lang.ClassNotFoundException: com/volano/b
//   Sun Java 1.1.8 ...
//     java.lang.IllegalArgumentException: com/volano/b
//   Sun Java 1.0.2 ...
//   Microsoft Java 1.1.4 ...

public class AppletSecurity {
    private static final String   DSA_APPLET_SECURITY = "COM.volano.chat.security.DSAAppletSecurity";
    private static final String[] REQUIRED_CLASSES    = {"java.security.SecureRandom"};

    private static AppletSecurity security = null;

    public static synchronized AppletSecurity getInstance() {
        if (security == null) {
            try {
                Class test;
                for (int i = 0; i < REQUIRED_CLASSES.length; i++) {
                    test = Class.forName(REQUIRED_CLASSES[i]);
                }
                security = (AppletSecurity) Class.forName(DSA_APPLET_SECURITY.replace('/', '.')).newInstance();
            }
            catch (ClassNotFoundException e) {
                // System.err.println(e);
            }
            // InstantiationException, IllegalAccessException
            catch (Exception e) {
                System.err.println(e);
            } finally {
                if (security == null) {
                    security = new AppletSecurity();
                }
            }
        }
        return security;
    }

    public void initialize() {}

    public byte[] sign(byte[] data) {
        return new byte[0];
    }
}
