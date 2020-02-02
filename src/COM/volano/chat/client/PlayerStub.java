/*
 * PlayerStub.java - an applet stub for loading and running an applet.
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
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This component loads, initializes, and starts an applet when added to a
 * container, and then stops, destroys, and disposes of the applet when removed
 * from the container.
 *
 * @author  John Neffenger
 * @version 04 Apr 2001
 */

class PlayerStub extends Panel implements AppletStub, Runnable {
    private static final String NAME     = "PlayerStub";
    private static final String SUFFIX   = ".class";
    private static final String ERROR    = "Error in applet: ";
    private static final int    PAUSE    = 1 * 1000;
    private static final int    COUNT    = 5;

    // Cache of embedded applet properties with URL string as key.
    private static Object    tableLock = new Object();
    private static Hashtable table = new Hashtable();

    private Applet     applet;    // This applet
    private String     prefix;    // Prefix for the subapplet's parameters
    private String     code;      // Java class name of the subapplet
    private URL        params;    // Location of the subapplet's properties
    private int        width;     // Default width of this component
    private int        height;    // Default height of this component

    private Object     lock = new Object(); // Lock on removed
    private Properties properties; // Subapplet properties
    private Applet     subapplet;  // The embedded applet
    private boolean    added;      // This component has been added
    private boolean    removed;    // This component has been removed
    private boolean    active;     // The subapplet is active

    /**
     * Loads the embedded applet properties file just once for this Java VM by
     * maintaining a static cache of the properties.
     *
     * @param params  the URL to the embedded applet properties file.
     * @return  the embedded applet properties.
     */

    private static Properties loadProperties(URL params) {
        Properties properties = new Properties();
        if (params != null) {
            synchronized (tableLock) {
                Properties loaded = (Properties) table.get(params.toString());
                if (loaded != null) {
                    properties = loaded;
                } else {
                    try {
                        InputStream input = params.openStream();
                        properties.load(input);
                        input.close();
                        table.put(params.toString(), properties);
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }
        }
        return properties;
    }

    /**
     * Creates a new applet player stub.
     *
     * @param applet this applet.
     * @param prefix the prefix for defining parameters of the embedded applet as
     *               parameters of this applet.
     * @param code   the name of the Java class file containing the applet to
     *               load, with or without the ".class" file suffix.
     * @param params the location of the properties file containing the
     *               parameters of the embedded applet.
     * @param width  the width to reserve for displaying the applet.
     * @param height the height to reserve for displaying the applet.
     */

    PlayerStub(Applet applet, String prefix, String code, URL params, int width, int height) {
        this.applet = applet;
        this.prefix = prefix;
        this.code   = code;
        this.params = params;
        this.width  = width;
        this.height = height;
        setLayout(new BorderLayout());

        // Get parameters from properties file, if defined.
        properties = loadProperties(params);
    }

    /**
     * Gets the minimum size for this component.
     *
     * @return the component's mininum size.
     */

    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    /**
     * Gets the preferred size for this component.
     *
     * @return the component's preferred size.
     */

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Called when this component's peer is added.  This method starts the thread
     * which loads, initializes, and starts the embedded applet.
     */

    public void addNotify() {
        super.addNotify();
        if (! added) {
            added = true;
            Thread thread = new Thread(this, NAME);
            thread.start();
        }
    }

    /**
     * Called when this component's peer is removed.  This method notifies the
     * thread to stop, destroy, and dispose of the embedded applet.
     */

    public void removeNotify() {
        super.removeNotify();
        synchronized (lock) {
            if (! removed) {
                removed = true;
                lock.notify();
            }
        }
    }

    /**
     * The main body of the applet player stub.  This thread loads the applet
     * parameters as a properties file and then loads, initializes, and starts the
     * applet.  It then waits to be notified when to stop, destroy, and dispose of
     * the embedded applet.
     */

    public void run() {
        try {
            // Load
            if (code.endsWith(SUFFIX)) {      // Trim off .class file extension
                code = code.substring(0, code.length() - SUFFIX.length());
            }
            // See http://docs.oracle.com/javase/8/docs/technotes/guides/jweb/security/manifest.html#trusted_library
            subapplet = (Applet) Class.forName(code, true, Thread.currentThread().getContextClassLoader()).newInstance();
            subapplet.setStub(this);
            subapplet.setVisible(false);
            add("Center", subapplet);
            validate();

            // The IBM HotMedia applet sizes itself only once, so the first call
            // to the resize method must be correct, and that doesn't happen on
            // Internet Explorer 5.5 unless we're already visible.
            for (int i = 0; ! isShowing() && i < COUNT; i++) {
                Thread.sleep(PAUSE);
            }

            // Init
            subapplet.resize(getSize());
            subapplet.init();
            validate();

            // Start
            subapplet.resize(getSize());
            active = true;
            subapplet.start();
            validate();
            subapplet.setVisible(true);

            // Wait here until we're removed.
            synchronized (lock) {
                while (! removed) {
                    lock.wait();
                }
            }

            // Stop
            subapplet.setVisible(false);
            active = false;
            subapplet.stop();

            // Destroy
            subapplet.destroy();

            // Dispose
            remove(subapplet);
            subapplet = null;
        } catch (ThreadDeath e) {
            throw e;          // Rethrow for cleanup
        } catch (Throwable t) {
            synchronized (System.err) {
                System.err.println(ERROR + code);
                t.printStackTrace(System.err);
            }
        }
    }

    /**
     * Called when the applet wants to be resized.
     *
     * @param width  the new requested width for the applet.
     * @param height the new requested height for the applet.
     */

    public void appletResize(int width, int height) {
        this.width  = width;
        this.height = height;
        setSize(width, height);
        validate();
    }

    /**
     * Gets the applet's context.
     *
     * @return the applet's context.
     */

    public AppletContext getAppletContext() {
        return applet.getAppletContext();
    }

    /**
     * Gets the base URL.
     *
     * @return the base URL of the applet code.
     */

    public URL getCodeBase() {
        // Return base of properties file instead of code base, if defined.
        return params == null ? applet.getCodeBase() : params;
    }

    /**
     * Gets the document URL.
     *
     * @return the URL of the document containing the applet.
     */

    public URL getDocumentBase() {
        return applet.getDocumentBase();
    }

    /**
     * Returns the value of the named parameter in the HTML tag.  For example, if
     * an applet is specified as
     * <pre>
     * &lt;applet code="Clock" width=50 height=50&gt;<br>
     * &lt;param name="color" value="blue"&gt;<br>
     * &lt;/applet&gt;
     * </pre>
     * then a call to <code>getParameter("color")</code> returns the value
     * <code>"blue"</code>.
     *
     * @param name a parameter name.
     * @return the value of the named parameter.
     */

    public String getParameter(String name) {
        // Check for the subapplet parameter in the following places:
        //   1. First check whether it's specified on the Web page as a parameter of
        //      our applet using the subapplet prefix.
        //   2. Then check whether it's specified in its own property file without
        //      any prefix at all.
        String value = applet.getParameter(prefix + name);
        if (value == null) {
            value = properties.getProperty(name);
        }
        // Some Java VMs (like Sun's JDK 1.1.5) do not trim the parameter values.
        return value == null ? null : value.trim();
    }

    /**
     * Determines if the applet is active.  An applet is active just before its
     * <code>start</code> method is called.  It becomes inactive immediately after
     * its <code>stop</code> method is called.
     *
     * @return <code>true</code> if the applet is active; otherwise
     * <code>false</code>.
     */

    public boolean isActive() {
        return active;
    }
}
