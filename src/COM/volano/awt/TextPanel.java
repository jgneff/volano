/*
 * TextPanel.java - a panel for displaying chat message text.
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
import java.awt.*;

/**
 * This class is a factory method for creating the appropriate panel to display
 * chat message text.  It creates a panel based on the Java Foundation Classes
 * (Swing) when those classes are available; otherwise, it creates a panel
 * based on the AWT of Java 1.1.
 *
 * @author  John Neffenger
 * @version 2.6.4
 */
public abstract class TextPanel extends Panel {
    // Swing classes required by COM.volano.swing.SwingTextPanel.
    private static final String[] REQUIRED_CLASSES = {
        "javax.swing.JPanel",
        "javax.swing.JScrollBar",
        "javax.swing.JScrollPane",
        "javax.swing.JTextArea",
        "javax.swing.ScrollPaneConstants",
        "javax.swing.SwingUtilities",
        "javax.swing.event.DocumentEvent",
        "javax.swing.event.DocumentListener",
        "javax.swing.text.BadLocationException",
        "javax.swing.text.DefaultCaret"
    };

    // Java versions with Java Bug 5003402.
    // REGRESSION: java.awt.TextArea stops scrolling when it loses the keyboard focus.
    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5003402
    private static final String J2SE50BETA = "1.5.0-beta";  // beta, beta2, ...
    private static final String J2SE50     = "1.5.0";
    private static final String J2SE50U1   = "1.5.0_01";

    // Concrete implementations of this class.
    // Including the SwingTextPanel in the CAB files adds another 2 KB each.
    // Including the AWTTextPanel in the JAR files adds another 2 KB each.
    private static final String SWING_TEXT_PANEL = "COM.volano.swing.SwingTextPanel";
    private static final String AWT_TEXT_PANEL = "COM.volano.awt.AWTTextPanel";

    // Does this Java VM have the Swing classes available?
    private static boolean gotSwing = false;

    /**
     * Static constructor to determine whether the Swing classes are available.
     */
    static {
        try {
            for (int i = 0; i < REQUIRED_CLASSES.length; i++) {
                Class test = Class.forName(REQUIRED_CLASSES[i]);
            }
            gotSwing = true;
        } catch (ClassNotFoundException e) {}
    }

    /**
     * Gets an appropriate instance of a chat message text panel, depending on
     * whether the Swing classes are available.
     *
     * @return a chat message text panel.
     */
    public static TextPanel getInstance(String javaVersion, boolean forceSwing) {
        TextPanel panel = null;
        boolean useSwing =
            forceSwing ||
            javaVersion.equals(J2SE50) ||
            javaVersion.equals(J2SE50U1) ||
            javaVersion.startsWith(J2SE50BETA);
        try {
            panel = (gotSwing && useSwing) ?
                    (TextPanel) Class.forName(SWING_TEXT_PANEL.replace('/', '.')).newInstance() :
                    (TextPanel) Class.forName(AWT_TEXT_PANEL.replace('/', '.')).newInstance();
        } catch (Exception e) {
            System.err.println(e);
        }
        return panel;
    }

    /**
     * Determines whether the text area contained by this panel is editable.
     *
     * @return true if the text area is editable; otherwise false.
     */
    public abstract boolean isEditable();           // java.awt.TextComponent

    /**
     * Sets whether the text area contained by this panel is editable.
     *
     * @param b true sets the text area editable; false sets it non-editable.
     */
    public abstract void setEditable(boolean b);    // java.awt.TextComponent

    /**
     * Appends the text message to the text area contained by this panel.
     *
     * @param string the text message to append.
     */
    public abstract void append(String string);     // java.awt.TextArea
}
