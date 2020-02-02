/*
 * AppletBase.java - a base class for VolanoChat applets.
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
import  COM.volano.awt.ImageButton;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  COM.volano.util.Message;
import  COM.volano.chat.security.AppletSecurity;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class serves as a base class for the various VolanoChat client applets.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public abstract class AppletBase extends Applet implements Runnable, Observer {
    protected static final int INSET = 2;    // Component spacing

    // Codes for the current applet state.
    protected static final int IDLE       = 0;
    private   static final int CONNECTING = 1;
    protected static final int CONNECTED  = 2;

    // Move these fields to static if we want to have only one connected
    // VolanoChat or MyVolanoChat applet per Java VM.
    // private   static Object frameLock = new Object();
    // protected static int    state     = IDLE;
    // protected static Frame  frame     = null;

    protected Value      value;
    protected Connection connection;
    protected Component  button;
    protected Label      label;
    protected boolean    buttonPushed;
    protected int        state = IDLE;
    protected Frame      frame;
    private   Thread     frameCreator;
    protected String     memberName     = "";
    protected String     memberPassword = "";
    protected String     password       = "";

    /**
     * Prints an error message and stack trace to the Java console.
     *
     * @param message  the error message text.
     * @param t        the exception associated with the error.
     */

    private static void printError(String message, Throwable t) {
        synchronized (System.err) {
            System.err.println(message);
            t.printStackTrace(System.err);
        }
    }

    /**
     * Gets this applet's access version string for requesting access to the
     * server, identifying both the applet and its protocol version.  Subclasses
     * should override this method to return their own access version strings.
     *
     * @return the access version string for this applet.
     */

    public String getVersion() {
        return Access.PUBLIC_VERSION;
    }

    /**
     * Gets information about this applet.
     *
     * @return a string giving the applet name, version, copyright, and Web
     *         address.
     */

    public String getAppletInfo() {
        return getClass().getName() + " " + Build.VERSION + " " + Build.VOLANO_URL + "\n" +
               Build.APPLET_COPYRIGHT;
    }

    /**
     * Returns a packet factory for this applet. Subclasses should override this
     * method and return a packet factory that creates only the packets used by
     * the applet.
     *
     * @return a packet factory for this applet.
     */

    protected abstract PacketFactory getPacketFactory();

    /**
     * Sets the delay pauses configured for each request type.
     *
     * @param value the applet property values.
     */

    protected abstract void setPauses(Value value);

    /**
     * Initializes this applet, called after the applet is loaded.
     */

    public void init() {
        Connection.setPacketFactory(getPacketFactory());
        Connection.verbose = true;
        value = new Value(this);
        setPauses(value);
        if (value.color != null) {
            setBackground(value.color);
        }
        if (value.foreground != null) {
            setForeground(value.foreground);
        }
        if (value.fontDefault != null) {
            setFont(value.fontDefault);
        }
        setLayout(new BorderLayout());
        button = new ImageButton(value.context, value.imageButton1, value.imageButton2, value.imageButtonBorder,
                                 value.textButtonStatus, value.imageButtonWidth, value.imageButtonHeight);
        label = new Label(value.textButtonMessage, Label.CENTER);
        // label.setFont(value.fontDefault);
    }

    /**
     * Starts this applet, called after the applet is initialized and when the
     * browser returns to the applet's Web page.
     */

    public void start() {
        if (frameCreator == null) {
            frameCreator = new Thread(this, getClass().getName());
            frameCreator.start();
        }
    }

    /**
     * Destroys the applet, called when the applet is removed from memory by the
     * browser (knows as <i>trimming</i> or <i>pruning</i> of the applet).
     */

    public void stop() {
        if (frame != null) {
            frame.deliverEvent(new Event(frame, Event.WINDOW_DESTROY, null));
        }
        if (frameCreator != null) {
            Thread thread = frameCreator;
            frameCreator = null;
            try {
                thread.interrupt();
            } catch (Throwable t) {
                // Thread.interrupt throws java.lang.NoSuchMethodError using Java 1.021 on
                // Netscape 2.02. (version 2.2)
            }
        }
    }

    /**
     * The main body of the applet user interface thread.  When the applet's Web
     * page button is clicked, it connects to the chat server.  If the connection
     * was successful, it displays the main applet frame.  Otherwise, it displays
     * a Web page describing the error.
     */

    public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (frameCreator == thisThread) {
                synchronized (this) {
                    while (! buttonPushed) {
                        wait();
                    }
                    buttonPushed = false;
                }
                if (state == IDLE) {
                    state = CONNECTING;
                    enter();
                } else if (frame != null) {
                    frame.requestFocus();
                }
            }
        } catch (InterruptedException e) {
            // Caught when applet is stopped, interrupting this thread.
        } catch (ThreadDeath e) {
            throw e;                  // Rethrow for cleanup
        } catch (Throwable t) {
            printError("Applet thread error", t);
            showError(value.pageAccessUnable);
        }
    }

    /**
     * Replaces the Web page with the document specified and displays an error
     * message below the button stating that the connection was unsuccessful.
     *
     * @param href  the address of the Web page describing the error.
     */

    protected void showError(URL href) {
        System.err.println("See " + href);
        if (value.pageNewwindow) {
            value.context.showDocument(href, Build.TARGET);
        } else {
            value.context.showDocument(href);
        }
        Vector subs = new Vector(2);
        subs.addElement(value.codeHost);
        subs.addElement(new Integer(value.serverPort));
        label.setText(Message.format(value.textButtonNotconnected, subs));
        state = IDLE;
    }
    /**
     * Enters the chat server by connecting and requesting access.
     */

    private void enter() {
        try {
            connect();
            requestAccess();
        } catch (IOException e) {
            if (connection != null) {
                connection.deleteObserver(this);
                connection.close();
            }
            showError(value.pageAccessUnable);
        }
    }

    /**
     * Creates a connection with the chat server.
     *
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private void connect() throws IOException {
        Vector subs = new Vector(2);
        subs.addElement(value.codeHost.length() == 0 ? "localhost" : value.codeHost);
        subs.addElement(new Integer(value.serverPort));
        label.setText(Message.format(value.textButtonConnecting, subs));
        connection = new Connection(value.codeHost, value.serverPort, false);
        connection.addObserver(this);
        connection.startSending(Thread.NORM_PRIORITY);
        connection.startReceiving(Thread.NORM_PRIORITY);
    }

    /**
     * Sends an access request to the chat server.
     *
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private void requestAccess() throws IOException {
        label.setText(value.textButtonAccessing);
        Packet packet        = null;
        String appletVersion = getVersion();
        String documentBase  = value.documentBase.toString();
        String codeBase      = value.codeBase.toString();
        if (value.member || value.admin || value.monitor || value.stage)
            packet = new PasswordAccess(value.group, appletVersion, documentBase, codeBase,
                                        value.javaVendor, value.javaVendorUrl, value.javaVersion, value.javaClassVersion,
                                        value.osName, value.osVersion, value.osArch,
                                        value.member, value.monitor, value.admin, memberName, memberPassword, password,
                                        value.stage, value.topic);
        else
            packet = new Access(value.group, appletVersion, documentBase, codeBase,
                                value.javaVendor, value.javaVendorUrl, value.javaVersion, value.javaClassVersion,
                                value.osName, value.osVersion, value.osArch);
        connection.send(packet);
    }

    /**
     * Reset the focus when the main applet window closes.
     * This method must be overridden by any subclasses.
     */

    protected abstract void setFocus();

    /**
     * Handle closing of the main applet window.
     */

    public boolean handleEvent(Event event) {
        if ((event.id == Event.WINDOW_DESTROY) && (event.target == frame)) {
            label.setText(value.textButtonMessage);
            state = IDLE;
            frame = null;
            setFocus();
            return true;
        }
        return super.handleEvent(event);
    }

    /**
     * Called when a packet is received from the server.
     *
     * @param observable  the connection to the server.
     * @param object      the packet received from the server.
     */

    public void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AppletBase update ...");
        }

        Connection connection = (Connection) observable;
        if (object instanceof Ping) {
            Ping ping = (Ping) object;
            ping.response();
            send(connection, ping);
        } else if (object instanceof StreamableError) {
            StreamableError error = (StreamableError) object;
            label.setText("Server error (" + error.getText() + ").");
            connection.deleteObserver(this);
            connection.close();
            state = IDLE;
        } else if (object == null) {
            if (state == CONNECTING) {
                showError(value.pageAccessUnable);
            }
        }
    }

    /**
     * Sends a packet on a connection.
     *
     * @param connection  the connection on which to send the packet.
     * @param packet      the packet to send.
     */

    protected void send(Connection connection, Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {}
    }

    /**
     * Signs the data with the applet's private key.
     *
     * @param data  the data to sign.
     * @return  the digital signature of the data, or a zero-length array if no
     *          signature could be created.
     */

    protected byte[] sign(byte[] data) {
        AppletSecurity security = AppletSecurity.getInstance();
        security.initialize();
        return security.sign(data);
    }

    /**
     * Displays an error Web page based on the negative access confirmation
     * result code.
     *
     * @param result  the negative access confirmation result code.
     */

    protected void accessDenied(int result) {
        connection.close();
        if (result == Access.HOST_DENIED) {
            showError(value.pageAccessHost);
        } else if (result == Access.DOCUMENT_DENIED) {
            showError(value.pageAccessDocument);
        } else if (result == Access.VERSION_DENIED) {
            showError(value.pageAccessVersion);
        } else if (result == Access.BAD_PASSWORD) {
            showError(value.pageAccessPassword);
        } else if (result == Access.BAD_JAVA_VERSION) {
            showError(value.pageJavaVersion);
        } else if (result == Access.HOST_DUPLICATE) {
            showError(value.pageAccessDuplicate);
        }
    }
}
