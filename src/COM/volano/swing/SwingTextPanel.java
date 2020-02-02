/*
 * SwingTextPanel.java - a panel for displaying chat message text using Swing.
 * Copyright (C) 2004 John Neffenger
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

package COM.volano.swing;

import java.applet.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.*;

// import javax.swing.DebugGraphics;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import COM.volano.awt.TextPanel;

/**
 * This class is a default caret which can enable or disable the automatic
 * scrolling feature of the Swing text area.
 */
class ScrollingCaret extends DefaultCaret {
    private boolean autoscrolling = true;

    /**
     * Determines whether this caret is autoscrolling.
     *
     * @return true if this caret is autoscrolling; otherwise false.
     */
    public boolean isAutoscrolling() {
        return autoscrolling;
    }

    /**
     * Sets whether this caret is autoscrolling.
     *
     * @param b true enables autoscrolling; false disables autoscrolling.
     */
    public void setAutoscrolling(boolean b) {
        autoscrolling = b;
    }

    /**
     * Enables autoscrolling if the scrollbar is scrolled to the end.
     *
     * @param bar the vertical scrollbar associated with the text area.
     */
    public void setAutoscrolling(JScrollBar bar) {
        autoscrolling = bar.getValue() + bar.getVisibleAmount() == bar.getMaximum();
    }

    /**
     * Scrolls the scrollbar to the end of its track.
     *
     * @param bar the vertical scrollbar assoicated with the text area.
     */
    public static void scrollToEnd(JScrollBar bar) {
        bar.setValue(bar.getMaximum() - bar.getVisibleAmount());
    }

    /**
     * Called to give the opportunity for this caret to make itself visible by
     * scrolling the text area to its location.  If this caret is autoscrolling,
     * call the default caret implementation which will scroll to our location.
     * Otherwise, do nothing and leave the vertical scrollbar where it is.
     *
     * @param location the new location of the caret.
     */
    protected void adjustVisibility(Rectangle location) {
        if (autoscrolling) {
            super.adjustVisibility(location);
        }
    }
}

/**
 * This class is a component listener which is notified when the component
 * is resized.  The listener ensures that the vertical scrollbar of a text area
 * is scrolled to the last line, as a resize event might otherwise cause it to
 * scroll back.
 */

/*
The general rule for Swing is to retain the scrollbar value when
resizing the viewport.  Yet if the new size of the scrollbar thumb would
cause it to extend beyond the end its track (value + extent > maximum),
reduce its value so that it will fit (keeping its extent the same).

Height increased
    The scrollbar thumb grows its size to reflect the increase in the
    visible portion of the text area.

    Before
        javax.swing.JTextArea COMPONENT_RESIZED (0,-90 440x270) Scrollbar = 90
        Scrollbar value + extent = maximum.
    After
        javax.swing.JScrollPane COMPONENT_RESIZED (0,0 458x219) Scrollbar = 54
        Scrollbar value != old value, but value + extent = maximum.

Width increased
    The scrollbar thumb grows its size to reflect the increase in the
    visible portion of the text area.  The text area is reformatted to
    take advantage of the greater width, so its height is reduced.

    Before
        javax.swing.JTextArea COMPONENT_RESIZED (0,-360 440x540) Scrollbar = 360
        Scrollbar value + extent = maximum.
    After
        javax.swing.JScrollPane COMPONENT_RESIZED (0,0 676x183) Scrollbar = 360
        javax.swing.JTextArea COMPONENT_RESIZED (0,-360 658x540) Scrollbar = 360
        javax.swing.JTextArea COMPONENT_RESIZED (0,-180 658x360) Scrollbar = 180
        Scrollbar value != old value, but value + extent = maximum.

Height decreased
    The scrollbar thumb reduces its size to reflect the decrease in the
    visible portion of the text area.

    Before
        javax.swing.JTextArea COMPONENT_RESIZED (0,-90 440x270) Scrollbar = 90
        Scrollbar value + extent = maximum.
    After
        javax.swing.JScrollPane COMPONENT_RESIZED (0,0 458x118) Scrollbar = 90
        Scrollbar value = old value, but value + extent < maximum.
        --> Need to reset scrollbar to bottom.

Width decreased
    The scrollbar thumb reduces its size to reflect the decrease in the
    visible portion of the text area.  The text area is reformatted to
    fit into the the smaller width, so its height is increased.

    Before
        javax.swing.JTextArea COMPONENT_RESIZED (0,-90 440x270) Scrollbar = 90
        Scrollbar value + extent = maximum.
    After
        javax.swing.JScrollPane COMPONENT_RESIZED (0,0 315x183) Scrollbar = 90
        javax.swing.JTextArea COMPONENT_RESIZED (0,-90 297x270) Scrollbar = 90
        javax.swing.JTextArea COMPONENT_RESIZED (0,-90 297x360) Scrollbar = 90
        Scrollbar value = old value, but value + extent < maximum.
        --> Need to reset scrollbar to bottom.
*/

class ResizeListener extends ComponentAdapter {
    private ScrollingCaret caret;
    private JScrollBar bar;

    /**
     * Creates a new component listener for resize events.
     *
     * @param panel the chat message text panel containing the text area.
     */
    public ResizeListener(SwingTextPanel panel) {
        caret = (ScrollingCaret) panel.getTextArea().getCaret();
        bar = panel.getScrollPane().getVerticalScrollBar();
    }

    /**
     * Notifies this listener that the component was resized.
     *
     * @param event the event describing the size change.
     */
    public void componentResized(ComponentEvent event) {
        Object object = event.getSource();
        if (object instanceof JScrollPane) {
            caret.setAutoscrolling(true);
            caret.scrollToEnd(bar);
        } else if (object instanceof JTextArea && caret.isAutoscrolling()) {
            caret.scrollToEnd(bar);
        }
    }
}

/**
 * This class is a document listener which is notified when text is added or
 * removed from the text area contained by this panel.  If the added text puts
 * the size over the specified limit, enough text is removed from the beginning
 * to put the size back to the limit minus the specified trim.  For example,
 * if the limit is 1000 and the trim is 100, and 100 characters are added to
 * 950 already in the text area (bringing the total to 1050), this class removes
 * 150 characters to reduce the the size to 900 (1000 - 100).
 */
class TrimmingListener implements DocumentListener {
    private JTextArea area;
    private int limit;
    private int trim;

    /**
     * Creates a new text-trimming listener.
     *
     * @param area the text area.
     * @param limit the limit on the text size which triggers the trimming.
     * @param trim the amount of text below the limit to remove.
     */
    public TrimmingListener(JTextArea area, int limit, int trim) {
        this.area = area;
        this.limit = limit;
        this.trim = trim;
    }

    /**
     * Notifies this listener that text was inserted into the text area.
     *
     * @param event the event describing the text insertion.
     */
    public void insertUpdate(DocumentEvent event) {
        SwingUtilities.invokeLater(new Trimmer(area, limit, trim));
    }

    public void removeUpdate(DocumentEvent event) {}
    public void changedUpdate(DocumentEvent event) {}
}

/**
 * This class is the actual runnable action which does the text trimming.
 */
class Trimmer implements Runnable {
    private static final String EMPTY_STRING = "";

    private JTextArea area;
    private int limit;
    private int trim;

    /**
     * Creates a new text-trimming action.
     *
     * @param area the text area.
     * @param limit the limit on the text size which triggers the trimming.
     * @param trim the amount of text below the limit to remove.
     */
    public Trimmer(JTextArea area, int limit, int trim) {
        this.area = area;
        this.limit = limit;
        this.trim = trim;
    }

    /**
     * Trims the text if its size is over the limit.
     */
    public void run() {
        try {
            int size = area.getText().length();
            if (size > limit) {
                int line = area.getLineOfOffset(trim + (size - limit));
                area.replaceRange(EMPTY_STRING, 0, area.getLineEndOffset(line));
            }
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }
}

/**
 * This class is a runnable action for appending to the text area.
 */
class Appender implements Runnable {
    private static final String LINE_SEPARATOR = "\n";
    private static final String EMPTY_STRING = "";

    private JTextArea area;
    private ScrollingCaret caret;
    private JScrollBar bar;
    private String string;

    /**
     * Creates a new appending action.
     *
     * @param panel the chat message text panel.
     * @param string the text to be added to the text area in the panel.
     */
    public Appender(SwingTextPanel panel, String string) {
        area = panel.getTextArea();
        caret = (ScrollingCaret) area.getCaret();
        bar = panel.getScrollPane().getVerticalScrollBar();
        this.string = string;
    }

    /**
     * Appends the text with autoscrolling if the vertical scrollbar is already
     * at the bottom, or without autoscrolling if the scrollbar has moved back
     * in the chat message history.
     */
    public void run() {
        caret.setAutoscrolling(bar);
        area.append((area.getText().length() > 0 ? LINE_SEPARATOR : EMPTY_STRING) + string);
    }
}

/**
 * This class is a Swing-based panel to display chat message text.
 *
 * @author  John Neffenger
 * @version 2.6.4
 */
public class SwingTextPanel extends TextPanel {
    private static final int ROWS = 10;
    private static final int COLUMNS = 30;
    private static final int LIMIT = 25000;
    private static final int TRIM = 5000;

    private static final int TOP_MARGIN = 0;
    private static final int LEFT_MARGIN = 5;
    private static final int BOTTOM_MARGIN = 0;
    private static final int RIGHT_MARGIN = 5;

    private JScrollPane pane;
    private JTextArea area;

    /**
     * Creates a new chat message text panel based on Swing.
     */
    public SwingTextPanel() {
        // Containment is:
        //   JTextArea -> JViewport -> JScrollPane -> JPanel -> SwingTextPanel
        area = new JTextArea(ROWS, COLUMNS);
        pane = new JScrollPane(area,
                               ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaret(new ScrollingCaret());
        area.setMargin(new Insets(TOP_MARGIN, LEFT_MARGIN, BOTTOM_MARGIN, RIGHT_MARGIN));
        area.getDocument().addDocumentListener(new TrimmingListener(area, LIMIT, TRIM));
        area.addComponentListener(new ResizeListener(this));
        pane.addComponentListener(new ResizeListener(this));

        setLayout(new BorderLayout());
        // We need a top-level Swing container -- JFrame, JDialog, JApplet, or
        // JPanel -- for automatic flicker-free double-buffered painting.
        // pane.setDoubleBuffered(true);
        // add(pane);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(pane);
        add(panel);

        // Works for getting the Swing JTextArea in the focus traversal
        // in Java 1.3, 1.5, and 1.6.
        try {
            // This method is available starting in Java 1.4.
            area.setFocusable(true);
        } catch (Throwable t) {
            // System.err.println(t);
        }
        /*
                area.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent event) {
                        System.out.println("Got the focus ...");
                    }
                    public void focusLost(FocusEvent event) {
                        System.out.println("Lost the focus ...");
                    }
                });
        */
    }

    /**
     * Gets the scroll pane contained by this panel.
     *
     * @return the scroll pane.
     */
    public JScrollPane getScrollPane() {
        return pane;
    }

    /**
     * Gets the text area contained by this panel.
     *
     * @return the text area;
     */
    public JTextArea getTextArea() {
        return area;
    }

    /**
     * Sets the background color of this panel and its scroll pane, text area,
     * and scrollbar.
     *
     * @param color the new background color.
     */
    public void setBackground(Color color) {
        super.setBackground(color);
        pane.setBackground(color);
        area.setBackground(color);
        pane.getVerticalScrollBar().setBackground(color);
    }

    /**
     * Sets the foreground color of this panel and its scroll pane, text area,
     * and scrollbar.
     *
     * @param color the new foreground color.
     */
    public void setForeground(Color color) {
        super.setForeground(color);
        pane.setForeground(color);
        area.setForeground(color);
        pane.getVerticalScrollBar().setForeground(color);
    }

    /**
     * Sets the font of the text area contained by this panel.
     *
     * @param font the new font.
     */
    public void setFont(Font font) {
        area.setFont(font);
    }

    /**
     * Determines whether the text area contained by this panel is editable.
     *
     * @return true if the text area is editable; otherwise false.
     */
    public boolean isEditable() {
        return area.isEditable();
    }

    /**
     * Sets whether the text area contained by this panel is editable.
     *
     * @param b true sets the text area editable; false sets it non-editable.
     */
    public void setEditable(boolean b) {
        area.setEditable(b);
    }

    /**
     * Appends the text message to the text area contained by this panel.
     *
     * @param string the text message to append.
     */
    public void append(String string) {
        SwingUtilities.invokeLater(new Appender(this, string));
    }
}
