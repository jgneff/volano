/*
 * HyperLabel.java - a canvas for emulating a label with hyperlink support.
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

package COM.volano.awt;
import  COM.volano.util.Message;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class is a canvas for displaying text with support for active, clickable
 * hyperlinks.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class HyperLabel extends Canvas implements Runnable {
    private static final String   DELIMITERS    = " <>[]{}(),";
    private static final String   NEWS          = "news";
    private static final String   MAILTO        = "mailto";
    private static final String   SELF_TARGET   = "_self";
    private static final String   BLANK_TARGET  = "_blank";
    private static final String   NULL_TEMPLATE = "%0";
    private static final String   EMPTY_STRING  = "";
    private static final int      INSET         =  2;
    private static final int      INDENT        =  5;
    private static final int      SCROLL        = 30;
    private static final int      MAX_WIDTH     = 468; // Width of banner images

    private AppletContext context;
    private String[]      prefixes;
    private String        label;

    private String    prefixText   = null;
    private String    prefixLink   = null;
    private String    linkTemplate = NULL_TEMPLATE;
    private boolean   linkDisable  = false;
    private boolean   showFont     = false;

    private String href        = "";
    private String textName    = "";
    private String linkName    = "";
    private String textDefault = "";
    private String linkDefault = "";

    private Frame     frame;
    private Color     linkColor = Color.blue;
    private int       indent = INDENT;
    private int       xdrag;
    private int       prefixStart;
    private int       prefixEnd;
    private boolean[] link;
    private String[]  text;
    private int[]     start;
    private int[]     end;

    /**
     * Creates a new hyperlink label.
     *
     * @param context       the applet context.
     * @param linkPrefixes  the list of link prefixes.
     */

    public HyperLabel(AppletContext context, String linkPrefixes) {
        this.context  = context;
        this.prefixes = getPrefixes(linkPrefixes);
        this.label    = "";
        parseLabel();
    }

    /**
     * Called when this component is added to its container, making the parent
     * frame and font metric information available.
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
     * Sets whether to display the font setting when the font is set.
     *
     * @param showFont <code>true</code> to display the font; otherwise
     *     <code>false</code>.
     */

    public void showFont(boolean showFont) {
        this.showFont = showFont;
    }

    /**
     * Sets the font.
     *
     * @param font the new font.
     */

    public void setFont(Font font) {
        super.setFont(font);
        if (showFont) {
            String style = (font.isBold() ? "bold" : "") + (font.isItalic() ? "italic" : "");
            String spec  = font.getName() + (style.length() > 0 ? "-" + style + "-" : "-") + font.getSize();
            setText(spec);
        }
    }

    /**
     * Gets the minimum size requested for this component.
     *
     * @return the minimum size that should be used to display this component.
     */

    public Dimension getMinimumSize() {
        FontMetrics metrics = getFontMetrics(getFont());
        // 2.6.3 - The private chat rooms open up super-wide if the other person
        // has a very long profile.  Put a cap (the banner width) on the width of
        // the private chat rooms.
        int width  = Math.min(metrics.stringWidth(label) + (2 * INSET), MAX_WIDTH);
        int height = metrics.getHeight() + (2 * INSET);
        return new Dimension(width, height);
    }

    /**
     * Gets the preferred size requested for this component.
     *
     * @return the preferred size that should be used to display this component.
     */

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Sets the color of linked Web addresses.
     *
     * @param color the color to set for linked URLs.
     */

    public void setLinkColor(Color color) {
        linkColor = color;
    }

    /**
     * Starts a thread which fetches the text and link as properties returned
     * in an HTTP response from the given URL.
     *
     * @param href         the URL from which to obtain the text and link.
     * @param textName     the name of the property whose value is the text.
     * @param linkName     the name of the property whose value is the link.
     * @param textDefault  the default text string to use if the given property
     *                     is not found in the HTTP response.
     * @param linkDefault  the default link to use if the given property is not
     *                     found in the HTTP response.
     */

    public void getText(String href, String textName, String linkName, String textDefault, String linkDefault) {
        this.href = href;
        this.textName = textName;
        this.linkName = linkName;
        this.textDefault = textDefault;
        this.linkDefault = linkDefault;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Fetches the text and link from the specified URL.  The HTTP response
     * returns a Java properties file containing the text and link as values
     * of the specified properties.
     */

    public void run() {
        try {
            URL url = new URL(href);
            InputStream input = url.openStream();
            Properties properties = new Properties();
            properties.load(input);
            input.close();
            String text = properties.getProperty(textName, textDefault);
            String link = properties.getProperty(linkName, linkDefault);
            setText(text, link);
        } catch (Exception e) {
            setText(e.toString());
        }
    }

    /**
     * Sets the label string with a linked prefix string.
     *
     * @param label        the string to display in the label.
     * @param prefixText   the prefix text to prepend to the label.
     * @param prefixLink   the hyperlink for the prefix text.
     * @param linkTemplate the template for formatting any URLs found in the text.
     * @param linkDisable  <code>true</code> to disable the linking of URLs;
     *                     otherwise <code>false</code>.
     */

    public synchronized void setText(String label, String prefixText, String prefixLink, String linkTemplate, boolean linkDisable) {
        this.label        = label;
        this.prefixText   = prefixText;
        this.prefixLink   = prefixLink;
        this.linkTemplate = linkTemplate;
        this.linkDisable  = linkDisable;
        indent = INDENT;
        parseLabel();
        repaint();
    }

    /**
     * Sets the label string with only linked prefix text.
     *
     * @param prefixText   the prefix text to prepend to the label.
     * @param prefixLink   the hyperlink for the prefix text.
     */

    public void setText(String prefixText, String prefixLink) {
        setText(EMPTY_STRING, prefixText, prefixLink, NULL_TEMPLATE, false);
    }

    /**
     * Sets the label string with no linked prefix string but with a special
     * template for formatting any linked Web addresses.
     *
     * @param label        the string to display in the label.
     * @param linkTemplate the template for formatting any URLs found in the text.
     * @param linkDisable  <code>true</code> to disable the linking of URLs;
     *                     otherwise <code>false</code>.
     */

    public void setText(String label, String linkTemplate, boolean linkDisable) {
        setText(label, null, null, linkTemplate, linkDisable);
    }

    /**
     * Sets the label string with no linked prefix and direct URL linking enabled.
     *
     * @param label  the string to display in the label.
     */

    public void setText(String label) {
        setText(label, null, null, NULL_TEMPLATE, false);
    }

    /**
     * Gets the link prefixes into an array of strings.
     *
     * @param list  the list of link prefixes.
     * @return  an array of strings with each element a link prefix.
     */

    private String[] getPrefixes(String list) {
        StringTokenizer tokenizer = new StringTokenizer(list);
        String[] array = new String[tokenizer.countTokens()];
        for (int i = 0; i < array.length; i++) {
            array[i] = tokenizer.nextToken();
        }
        return array;
    }

    /**
     * Parses the label string in order to identify any URL hyperlinks.
     */

    private void parseLabel() {
        StringTokenizer tokenizer = new StringTokenizer(label, DELIMITERS, true);
        int count = tokenizer.countTokens();
        link  = new boolean[count];
        text  = new String[count];
        start = new int[count];
        end   = new int[count];

        for (int i = 0; i < count; i++) {
            text[i] = tokenizer.nextToken();
            for (int j = 0; j < prefixes.length && ! link[i]; j++) {
                link[i] = ! linkDisable && text[i].startsWith(prefixes[j]);
            }
        }
    }

    /**
     * Gets the hyperlink associated with the horizontal pixel coordinate, if any.
     *
     * @param x  the pixel location to check.
     * @return the URL found at the horizontal pixel location in the label, or
     *         <code>null</code> if there is no hyperlink found at that location.
     */

    private synchronized String getLink(int x) {
        if (prefixText != null && prefixLink != null && prefixLink.length() > 0
                && x >= prefixStart && x <= prefixEnd) {
            return prefixLink;
        }
        for (int i = 0; i < text.length; i++) {
            if (link[i] && x >= start[i] && x <= end[i]) {
                return text[i];
            }
        }
        return null;
    }

    /**
     * Handles a key press event.
     *
     * @param event  the event information.
     * @param key    the key that was pressed.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean keyDown(Event event, int key) {
        if (event.id == Event.KEY_ACTION) {
            if (key == Event.UP || key == Event.LEFT) {
                indent -= SCROLL;
                repaint();
                return true;
            } else if (key == Event.DOWN || key == Event.RIGHT) {
                indent += SCROLL;
                repaint();
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the mouse cursor has entered this component.  If the mouse
     * is now over a hyperlink, this method displays the hand cursor and shows the
     * URL in the browser status field.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse entered.
     * @param y      the vertical pixel location where the mouse entered.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseEnter(Event event, int x, int y) {
        return mouseMove(event, x, y);
    }

    /**
     * Called when the mouse cursor has moved within this component.  If the mouse
     * is now over a hyperlink, this method displays the hand cursor and shows the
     * URL in the browser status field.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse moved.
     * @param y      the vertical pixel location where the mouse moved.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseMove(Event event, int x, int y) {
        String href = getLink(x);
        if (href != null) {
            if (frame != null) {
                frame.setCursor(Frame.HAND_CURSOR);
            }
            context.showStatus(href);
        } else {
            if (frame != null) {
                frame.setCursor(Frame.W_RESIZE_CURSOR);
            }
            context.showStatus(EMPTY_STRING);
        }
        return true;
    }

    /**
     * Called when the mouse cursor has left this component.  This method returns
     * the cursor to its default and clears the browser status field.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse left.
     * @param y      the vertical pixel location where the mouse left.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseExit(Event event, int x, int y) {
        if (frame != null) {
            frame.setCursor(Frame.DEFAULT_CURSOR);
        }
        context.showStatus(EMPTY_STRING);
        return true;
    }

    /**
     * Called when the mouse button is clicked.  This method simply requests the
     * keyboard focus for the component.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse was clicked.
     * @param y      the vertical pixel location where the mouse was clicked.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseDown(Event event, int x, int y) {
        xdrag = x;
        requestFocus();
        return true;
    }

    /**
     * Called when the mouse is dragged.  This method scrolls the status area
     * horizontally according to the mouse drag movement.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse was clicked.
     * @param y      the vertical pixel location where the mouse was clicked.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseDrag(Event event, int x, int y) {
        indent += xdrag == -1 ? 0 : x - xdrag;
        xdrag = x;
        repaint();
        return true;
    }

    /**
     * Called when the mouse button is clicked up.  If the mouse is over a
     * hyperlink when clicked, this method displays the URL in a new browser
     * window.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse was clicked.
     * @param y      the vertical pixel location where the mouse was clicked.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseUp(Event event, int x, int y) {
        xdrag = -1;
        String href = getLink(x);
        if (href != null) {
            try {
                URL    url      = new URL(Message.format(linkTemplate, href));
                String protocol = url.getProtocol();
                if (protocol.equals(MAILTO) || protocol.equals(NEWS)) {
                    context.showDocument(url, SELF_TARGET);
                } else {
                    context.showDocument(url, BLANK_TARGET);
                }
            } catch (MalformedURLException e) {}
        }
        return true;
    }

    /**
     * Called to paint this component, drawing hyperlink substrings in blue and
     * underlined.
     *
     * @param g  the graphics context for painting on the canvas.
     */

    public synchronized void paint(Graphics g) {
        FontMetrics metrics = getFontMetrics(getFont());
        int base    = metrics.getAscent() + INSET;
        int descent = base + metrics.getDescent();
        int offset  = indent;
        if (prefixText != null) {
            prefixStart  = offset;
            offset      += metrics.stringWidth(prefixText);
            prefixEnd    = offset;
            if (prefixLink != null && prefixLink.length() > 0) {
                g.setColor(linkColor);
                g.drawLine(prefixStart, descent, prefixEnd, descent);
            } else {
                g.setColor(getForeground());
            }
            g.drawString(prefixText, prefixStart, base);
            offset += metrics.stringWidth(" ");
        }
        for (int i = 0; i < text.length; i++) {
            start[i]  = offset;
            offset   += metrics.stringWidth(text[i]);
            end[i]    = offset;
            if (link[i]) {
                g.setColor(linkColor);
                g.drawLine(start[i], descent, end[i], descent);
            } else {
                g.setColor(getForeground());
            }
            g.drawString(text[i], start[i], base);
        }
    }
}
