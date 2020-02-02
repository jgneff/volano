/*
 * Banner.java - a canvas for displaying a linked banner.
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
import  java.applet.*;
import  java.awt.*;
import  java.net.*;
import  java.util.*;

/**
 * This canvas loads and displays a linked image.  Links are displayed in a new
 * browser window with the <code>_blank</code> frame target name.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Banner extends Canvas {
    static final String ERROR_MSG = "Error loading image -- see Java Console.";

    private static final String NULL         = "null";    // Null link URL
    private static final String TARGET       = "_blank";  // Document target
    private static final int    TIME_DEFAULT = 10;

    private static Object counterLock = new Object();     // Synchronization lock
    private static int    counter;                        // Substitution counter

    private AppletContext context;        // To show status and documents
    private String        message;        // Message to display (or null)
    private URL           sourceURL;      // Image source URL
    private URL           linkURL;        // Image link URL
    private int           width;          // Width to display image
    private int           height;         // Height to display image
    private Color         background;     // Background color or null
    private Color         foreground;     // Foreground color or null
    private int           time;           // Display time in seconds

    private Frame   frame;                // To set cursor
    private Image   image;                // Image to display (or null)
    private Object  lock = new Object();  // Synchronization lock for loading
    private boolean loaded;               // Image loading is complete
    private boolean cache;                // Cache this image
    private boolean ismap;                // Add map coordinates to link URL
    private char    subchar;              // URL substitution character
    private boolean observing = true;     // We need to keep observing image

    /**
     * Returns the next integer for the URL substitution string.
     *
     * @return  the next unique integer.
     */

    private static int nextIndex() {
        synchronized (counterLock) {
            return ++counter;                 // Return value after increment
        }
    }

    /**
     * Replaces a character in a string with the string representation of the
     * specified integer.
     *
     * @param s  the string with characters to substitute.
     * @param c  the character to substitute.
     * @param i  the integer to substitute for the character.
     * @return  the new substituted string.
     */

    private static String substitute(String s, char c, int i) {
        int mark = 0;                       // Starting location of current scan
        int len  = s.length();              // Length of entire input string
        StringBuffer buffer = new StringBuffer();
        for (int n = s.indexOf(c); n != -1; n = s.indexOf(c, n + 1)) {
            buffer.append(s.substring(mark, n));      // Copy up to sub char
            buffer.append(i);                         // Replace with integer
            mark = n + 1;                             // Update marker past sub char
        }
        if (mark < len) {                           // If there's some left over
            buffer.append(s.substring(mark, len));    // Copy over remainder of string
        }
        return buffer.toString();
    }

    /**
     * Gets a Uniform Resource Locator (URL) based on the specified link and
     * applet code base.
     *
     * @param codeBase  the applet code base.
     * @param link      the relative or absolute link whose URL is requested.
     * @param required  <code>true</code> if the link is a required parameter;
     *                  otherwise <code>false</code>.
     * @return  the URL or <code>null</code> if the link is optional and "null" is
     *          specified.
     * @exception java.net.MalformedURLException
     *            if the link is mandatory and "null" is specified, or if a valid
     *            URL cannot be created from the link.
     */

    private static URL getURL(URL codeBase, String link, boolean required) throws MalformedURLException {
        URL url = null;
        if (! link.equalsIgnoreCase(NULL)) {
            url = link.indexOf(':') == -1 ? new URL(codeBase, link) : new URL(link);
        } else if (required) {
            throw new MalformedURLException(NULL);
        }
        return url;
    }

    /**
     * Gets an image map link mapped to the specified coordinates.
     *
     * @param link   the link to map.
     * @param x      the x coordinate.
     * @param y      the y coordinate.
     * @param ismap  add the map coordinates.
     * @return  the link mapped to the specified (x, y) coordinates if this is an
     *          image map; otherwise, the input link.
     * @exception java.net.MalformedURLException
     *            if an error occurs mapping the URL.
     */

    // Taken from Section 13.6.2, "Server-side image maps," of the HTML 4.0
    // Specification (http://www.w3.org/TR/REC-html40/).
    //
    // In the following example, the active region defines a server-side
    // link. Thus, a click anywhere on the image will cause the click's
    // coordinates to be sent to the server.
    //
    //   <P><A href="http://www.acme.com/cgi-bin/competition">
    //      <IMG src="game.gif" ismap alt="target"></A>
    //
    // The location clicked is passed to the server as follows. The user
    // agent derives a new URI from the URI specified by the href
    // attribute of the A element, by appending `?' followed by the x and
    // y coordinates, separated by a comma. The link is then followed
    // using the new URI. For instance, in the given example, if the user
    // clicks at the location x=10, y=27 then the derived URI is
    // "http://www.acme.com/cgi-bin/competition?10,27".

    private static String getMappedLink(String link, int x, int y, boolean ismap) {
        return ismap ? (link + "?" + x + "," + y) : link;
    }

    /**
     * Gets an image map URL mapped to the specified coordinates.
     *
     * @param link   the link to map.
     * @param x      the x coordinate.
     * @param y      the y coordinate.
     * @param ismap  add the map coordinates.
     * @return  the URL mapped to the specified (x, y) coordinates if this is an
     *          image map; otherwise, the URL of the link.
     * @exception java.net.MalformedURLException
     *            if an error occurs mapping the URL.
     */

    private static URL getMappedURL(String link, int x, int y, boolean ismap) throws MalformedURLException {
        return new URL(getMappedLink(link, x, y, ismap));
    }

    /**
     * Creates a new banner which displays the specified message.
     *
     * @param message  the message to display.
     * @param width    the width of the banner.
     * @param height   the height of the banner.
     */

    Banner(String message, int width, int height) {
        this.message = message;
        this.width   = width;
        this.height  = height;
        this.time    = TIME_DEFAULT;
        this.loaded  = true;
    }

    /**
     * Creates a new banner which displays the specified image or an error message
     * if unable to load the image.
     *
     * @param applet   the applet
     * @param time     the time to display the image.
     * @param source   the Web address for the image source.
     * @param link     the Web address for the image link.
     * @param width    the width to display the image.
     * @param height   the height to display the image.
     * @param cache    <code>true</code> if the image should be cached in the
     *                 browser; otherwise <code>false</code>.
     * @param ismap    <code>true</code> if the coordinates of the cursor should be
     *                 appended to the link URL; otherwise <code>false</code>.
     * @param subchar  the URL substitution character, or zero if no substitution.
     * @exception java.net.MalformedURLException
     *            if an error occurs forming the URL from the image source or link
     *            specification.
     */

    Banner(Applet applet, int time, String source, String link,
           int width, int height, Color background, Color foreground,
           boolean cache, boolean ismap, char subchar) throws MalformedURLException {
        this.context    = applet.getAppletContext();
        this.time       = time;
        this.width      = width;
        this.height     = height;
        this.background = background;
        this.foreground = foreground;
        this.cache      = cache;
        this.ismap      = ismap;
        this.subchar    = subchar;

        setBackground(background);
        setForeground(foreground);

        if (subchar != 0) {
            int index = nextIndex();
            source = substitute(source, subchar, index);
            link   = substitute(link, subchar, index);
        }
        URL codeBase   = applet.getCodeBase();
        this.sourceURL = getURL(codeBase, source, true);
        this.linkURL   = getURL(codeBase, link, false);

        this.image = context.getImage(sourceURL);

        // Netscape Communicator 4.07 returns null from getImage when the image
        // source is a different host than the applet codebase host, including when
        // the applet is loaded locally through a "file:" URL and the image is from
        // an "http:" URL.
        if (image == null) {                                // prj
            System.err.println("Unable to fetch image from " + source);
            this.message = ERROR_MSG;                         // prj
            this.time    = TIME_DEFAULT;
            this.loaded  = true;                              // prj
            return;
        }                                                   // prj

        // Note that the lines below are not the same as:
        //   loaded = prepareImage(image, width, height, this);
        // since an ERROR or ABORT image update can come in before the prepareImage
        // method completes!  We consider an ERROR or ABORT to mean the loading is
        // complete (loaded = true), but the prepareImage method will return false
        // for a failing image.
        if (prepareImage(image, width, height, this)) {
            loaded = true;    // Image is already successfully loaded
        }

        // When an image is flushed on Netscape Communicator 4.04 with the JDK 1.1
        // Update, it is removed from the memory cache but not from the disk cache.
        // Subsequent loading from the same source will result in a GET HTTP request
        // with an If-Modified-Since header, allowing the Web server to return a
        // "304 Not Modified" response code indicating the browser should use the
        // image in its disk cache.
        //
        // Yet when the image is flushed on Microsoft Internet Explorer 4.01, it is
        // removed from both the disk cache and the memory cache, causing the image
        // to be downloaded from scratch on a subsequent load.  So we're forced to
        // avoid flushing the image for static GIF and JPEG files.

        // Move all this to removeNotify in order to flush the image associated with
        // this URL after it has been displayed.  When the source URL is a CGI
        // script without a substitution character, entering a new room will not
        // force a new image to be loaded.  Rather, the new room will just
        // synchronize itself with the first room, and each of their rotations will
        // cause all room images to rotate.
        /*
            if (! cache) {                      // Go get new image if not caching
              try {                                     // prj
                image.flush();                          // prj
              }                                         // prj
              catch (SecurityException e) {
                // Netscape 4.05 will throw a security exception when you try to
                // flush an animated GIF, because its implementation of Image.flush
                // relies on a call to Thread.interrupt().  It's safe to swallow
                // the exception because the image resources are freed before the
                // Thread.interrupt() and the thread will eventually die
                // of natural causes.
              }                                         // prj
            }
        */
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
     * Gets the duration to display this image or message.
     *
     * @return  the display duration in seconds.
     */

    public int getTime() {
        return time;
    }

    /**
     * Waits for the image loading to complete (successfully or unsuccessfully).
     *
     * @exception java.lang.InterruptedException  if interrupted while waiting.
     */

    public void waitImage() throws InterruptedException {
        synchronized (lock) {
            while (! loaded) {
                lock.wait();
            }
        }
    }

    /**
     * Notifies a waiting thread that the image loading is complete.
     */

    private void notifyImage() {
        synchronized (lock) {
            loaded = true;
            lock.notify();
        }
    }

    /**
     * Called when this component has been added to its container.
     */

    public void addNotify() {
        super.addNotify();
        Container parent = getParent();
        while (! (parent instanceof Frame) && parent != null) {
            parent = parent.getParent();
        }
        if (parent instanceof Frame) {
            frame = (Frame) parent;
        }
    }

    /**
     * Called when this component has been removed from its container.
     */

    // 2.1.10
    // Make sure we managed to load an image before trying to flush it.
    // Otherwise, we can get the following error when the window is closed:
    //   Exception occurred during event dispatching:
    //   java.lang.NullPointerException
    //         at COM/volano/Banner.removeNotify
    //         at java/awt/Container.removeNotify
    //         at java/awt/Container.removeNotify
    //         at COM/volano/ak.removeNotify
    //         at java/awt/Container.removeNotify
    //         at java/awt/Container.removeNotify
    //         at java/awt/Window.dispose
    //         at java/awt/Frame.dispose
    //         at COM/volano/af.c
    //         at COM/volano/af.nf
    //         at COM/volano/af.handleEvent
    //         at java/awt/Window.postEvent
    //         at java/awt/Component.dispatchEventImpl
    //         at java/awt/Container.dispatchEventImpl
    //         at java/awt/Window.dispatchEventImpl
    //         at java/awt/Component.dispatchEvent
    //         at java/awt/EventDispatchThread.run

    public void removeNotify() {
        super.removeNotify();
        observing = false;  // Set flag so that we stop observing image
        if (! cache && image != null) {     // Check for null pointer (2.1.10)
            try {
                image.flush();
            }
            // For Netscape 4.05 calling Thread.interrupt on animated GIFs.
            catch (SecurityException e) {}
        }
    }

    /**
     * Gets the minimum size for this component.
     *
     * @return  the components's mininum size.
     */

    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    /**
     * Gets the preferred size for this component.
     *
     * @return  the component's preferred size.
     */

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Called when the mouse cursor enters this component.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location of the event.
     * @param y      the vertical pixel location of the event.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean mouseEnter(Event event, int x, int y) {
        mouseMove(event, x, y);
        return true;
    }

    /**
     * Called when the mouse cursor moves within this component.  If the image
     * link is defined, the link is displayed in the browser status area and the
     * mouse cursor is set to a hand.  Otherwise, the browser status area is
     * cleared and the mouse cursor is set to its default.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location of the event.
     * @param y      the vertical pixel location of the event.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean mouseMove(Event event, int x, int y) {
        if (linkURL == null) {
            if (frame != null) {
                frame.setCursor(Frame.DEFAULT_CURSOR);
            }
            if (context != null) {
                context.showStatus("");
            }
        } else {
            if (frame != null) {
                frame.setCursor(Frame.HAND_CURSOR);
            }
            if (context != null) {
                context.showStatus(getMappedLink(linkURL.toString(), x, y, ismap));
            }
        }
        return true;
    }

    /**
     * Called when the mouse button is clicked in this component.  If the image
     * link is defined, its document is shown with a target of
     * <code>_blank</code>.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location of the event.
     * @param y      the vertical pixel location of the event.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean mouseUp(Event event, int x, int y) {
        if (linkURL != null && context != null) {
            try {
                // Ignore the middle and right mouse buttons.
                if ((event.modifiers & Event.META_MASK) == 0 && (event.modifiers & Event.ALT_MASK) == 0) {
                    System.err.println("See " + linkURL);
                    context.showDocument(getMappedURL(linkURL.toString(), x, y, ismap), TARGET);
                }
            } catch (MalformedURLException e) {
                context.showDocument(linkURL, TARGET);
            }
        }
        return true;
    }

    /**
     * Called when the mouse cursor exits this component.  The browser status area
     * is cleared and the mouse cursor is set to its default.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location of the event.
     * @param y      the vertical pixel location of the event.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean mouseExit(Event event, int x, int y) {
        if (frame != null) {
            frame.setCursor(Frame.DEFAULT_CURSOR);
        }
        if (context != null) {
            context.showStatus("");
        }
        return true;
    }

    /**
     * Called when additional status information about the image loading is
     * available.  This method notifies a waiting thread when the image has
     * finished loading or fails to load.
     *
     * @param image   the image to which this information pertains.
     * @param flags   the type of status information for this update.
     * @param x       depends on flags.
     * @param y       depends on flags.
     * @param width   depends on flags.
     * @param height  depends on flags.
     * @return  <code>true</code> if further status updates are required;
     *          otherwise <code>false</code>.
     */

    public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
        if ((flags & ALLBITS) != 0) {
            repaint();                // Fixes bug with Internet Explorer on iMac
            notifyImage();
        }
        // Animated GIFs create a separate thread that continues to call this method
        // with FRAMEBITS for each frame until this method returns false.
        else if ((flags & FRAMEBITS) != 0) {
            if (loaded) {
                repaint();
            } else {
                notifyImage();
            }
        }
        // We receive the abort update when an image we're observing is flushed, so
        // we should really be catching only errors here.
        //   else if ((flags & (ERROR | ABORT)) != 0) {
        else if ((flags & ERROR) != 0) {
            System.err.println("Unable to load " + sourceURL);
            message = ERROR_MSG;
            time    = TIME_DEFAULT;
            linkURL = null;
            notifyImage();
        }

        // We need to trigger another image production sequence if the image was
        // aborted but no error occurred.  An image is aborted when it is flushed.
        // Make sure this check comes after the check for any errors, since an error
        // update sets the abort flag as well.
        // 2.6.3 - But if we did the flush the image in removeNotify when our window
        // was closed, we don't want to force another copy of the image to be loaded
        // on our way out!  This Banner canvas displays only one image during its
        // existence.  In any case, the way to avoid this mess when "cache=false"
        // is to add a cache-busting random parameter to the end of the URL with the
        // "$" substitution character, like "?num=$".
        /*
            else if ((flags & ABORT) != 0) {
              prepareImage(image, width, height, this);
            }
        */
        // We need to continue observing this image in case it's flushed.  Otherwise
        // we won't be notified to repaint the new image.
        //   return showing && (flags & (ALLBITS | ERROR | ABORT)) == 0;
        // But we don't need to observe it after we've been removed from our
        // container.
        //   return true;
        return observing;
    }

    /**
     * Updates this component when called in response to a call to
     * <code>repaint</code>.  Simply call the <code>paint</code> method in order
     * to avoid the flicker caused by the default implementation.
     *
     * @param g  the graphics context to use for updating this component.
     */

    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Paints this component.  If a message is defined, the message is displayed.
     * Otherwise if an image is defined, the image is displayed.
     *
     * @param g  the graphics context to use for painting this component.
     */

    public void paint(Graphics g) {
        FontMetrics metrics = getFontMetrics(getFont());
        if (message != null) {
            g.drawString(message, 5, metrics.getHeight());
        } else if (image != null) {
            g.drawImage(image, 0, 0, width, height, this);
        }
    }
}
