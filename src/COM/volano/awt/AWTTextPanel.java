/*
 * AWTTextPanel.java - a panel for displaying chat message text using the AWT.
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

package COM.volano.awt;

import java.applet.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.TextListener;
import java.awt.event.TextEvent;

/**
 * This class is a component listener which is notified when the component
 * is resized.  The listener ensures that the vertical scrollbar of a text area
 * is scrolled to the last line, as a resize event might otherwise cause it to
 * scroll back.
 */
class ResizeListener extends ComponentAdapter {

    /**
     * Notifies this listener that the text area was resized.
     *
     * @param event the event describing the size change.
     */
    public void componentResized(ComponentEvent event) {
        Component component = event.getComponent();
        if (component instanceof TextArea) {
            TextArea area = (TextArea) component;
            area.setCaretPosition(area.getText().length());
        }
    }
}

/**
 * This class is a text listener which is notified when text is added or
 * removed from the text area contained by this panel.  If the added text puts
 * the size over the specified limit, enough text is removed from the beginning
 * to put the size back to the limit minus the specified trim.  For example,
 * if the limit is 1000 and the trim is 100, and 100 characters are added to
 * 950 already in the text area (bringing the total to 1050), this class removes
 * 150 characters to reduce the the size to 900 (1000 - 100).
 */
class TrimmingListener implements TextListener {
    private static final String EMPTY_STRING = "";

    private int limit;
    private int trim;

    /**
     * Creates a new text-trimming listener.
     *
     * @param limit the limit on the text size which triggers the trimming.
     * @param trim the amount of text below the limit to remove.
     */
    public TrimmingListener(int limit, int trim) {
        this.limit = limit;
        this.trim = trim;
    }

    /**
     * Notifies this listener that the text area changed.
     *
     * @param event the event describing the text changes.
     */
    public void textValueChanged(TextEvent event) {
        Object object = event.getSource();
        if (object instanceof TextArea) {
            TextArea area = (TextArea) object;
            int size = area.getText().length();
            if (size > limit) {
                area.replaceRange(EMPTY_STRING, 0, trim + (size - limit));
            }
        }
    }
}

/**
 * This class is an AWT-based panel to display chat message text.
 *
 * @author  John Neffenger
 * @version 2.6.4
 */
public class AWTTextPanel extends TextPanel {
    private static final String LINE_SEPARATOR = "\n";
    private static final String EMPTY_STRING = "";

    private static final int ROWS = 10;
    private static final int COLUMNS = 30;
    private static final int LIMIT = 25000;
    private static final int TRIM = 5000;

    private TextArea area;

    /**
     * Creates a new chat message text panel based on the AWT in Java 1.1.
     */
    public AWTTextPanel() {
        area = new TextArea(EMPTY_STRING, ROWS, COLUMNS, TextArea.SCROLLBARS_VERTICAL_ONLY);
        area.addComponentListener(new ResizeListener());
        area.addTextListener(new TrimmingListener(LIMIT, TRIM));
        setLayout(new BorderLayout());
        add(area);
    }

    /**
     * Sets the background color of this panel and its text area.
     *
     * @param color the new background color.
     */
    public void setBackground(Color color) {
        super.setBackground(color);
        area.setBackground(color);
    }

    /**
     * Sets the foreground color of this panel and its text area.
     *
     * @param color the new foreground color.
     */
    public void setForeground(Color color) {
        super.setForeground(color);
        area.setForeground(color);
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
        area.append((area.getText().length() > 0 ? LINE_SEPARATOR : EMPTY_STRING) + string);
    }
}
