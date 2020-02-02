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

package COM.volano.awt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import COM.volano.util.History;
import COM.volano.util.Message;

/**
 * A text field that tracks history, handles macros, and prevents flooding.
 */

public class ControlledTextField extends TextField implements KeyListener {
    private static final int    HISTORY_SIZE = 50;
    private static final int    NO_LIMIT  = -1;
    private static final long   NO_DELAY  = 0;
    private static final String NO_IGNORE = "";
    private static final String NO_TEXT   = "";

    private int       limit          = NO_LIMIT;
    private long      delay          = NO_DELAY;
    private String    keyIgnoreAlt   = NO_IGNORE;
    private String    keyIgnoreCtrl  = NO_IGNORE;
    private String    keyIgnoreMeta  = NO_IGNORE;
    private String    keyIgnoreShift = NO_IGNORE;
    private History   history        = null;
    private Hashtable macros         = new Hashtable();

    /**
     * Constructs a new empty ControlledTextField with the specified number of
     * columns.
     *
     * @param columns the number of columns.
     */

    public ControlledTextField(int columns) {
        super(columns);
        addKeyListener(this);
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setIgnoreAlt(String string) {
        keyIgnoreAlt = string;
    }

    public void setIgnoreCtrl(String string) {
        keyIgnoreCtrl = string;
    }

    public void setIgnoreMeta(String string) {
        keyIgnoreMeta = string;
    }

    public void setIgnoreShift(String string) {
        keyIgnoreShift = string;
    }

    public void enableHistory(boolean enable) {
        history = enable ? new History(HISTORY_SIZE) : null;
    }

    public void addMacro(int key, String value) {
        macros.put(new Integer(getNewFKeyCode(key)), value);
    }

    /**
     * Check whether the key combination should be ignored.
     *
     * @param event  the event definition.
     * @return  <code>true</code> if the key should be ignored; otherwise
     *          <code>false</code>.
     */

    private boolean ignoreKey(KeyEvent event) {
        int keyCode = event.getKeyCode();
        return (event.isAltDown() && keyIgnoreAlt.indexOf(keyCode) != -1)
                || (event.isControlDown() && keyIgnoreCtrl.indexOf(keyCode) != -1)
                || (event.isMetaDown() && keyIgnoreMeta.indexOf(keyCode) != -1)
                || (event.isShiftDown() && keyIgnoreShift.indexOf(keyCode) != -1);
    }

    /**
     * Formats a "hot key" macro with the substitution variables provided.
     *
     * @param macro      the macro pattern string.
     * @param variables  the variables, separated by spaces or tabs.
     */

    private void formatMacro(int key, String text) {
        String template = (String) macros.get(new Integer(key));
        if (template != null && template.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(text);
            Vector list = new Vector();
            while (tokenizer.hasMoreTokens()) {
                list.addElement(tokenizer.nextToken());
            }
            setText(Message.format(template, list));
        }
    }

    /**
     * Delay by sleeping for the configured time period.
     */

    private void pause() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
        }
    }

    private int getNewFKeyCode(int oldCode) {
        int newCode = KeyEvent.VK_UNDEFINED;
        switch (oldCode) {
            case Event.F1:
                newCode = KeyEvent.VK_F1;
                break;
            case Event.F2:
                newCode = KeyEvent.VK_F2;
                break;
            case Event.F3:
                newCode = KeyEvent.VK_F3;
                break;
            case Event.F4:
                newCode = KeyEvent.VK_F4;
                break;
            case Event.F5:
                newCode = KeyEvent.VK_F5;
                break;
            case Event.F6:
                newCode = KeyEvent.VK_F6;
                break;
            case Event.F7:
                newCode = KeyEvent.VK_F7;
                break;
            case Event.F8:
                newCode = KeyEvent.VK_F8;
                break;
            case Event.F9:
                newCode = KeyEvent.VK_F9;
                break;
            case Event.F10:
                newCode = KeyEvent.VK_F10;
                break;
            case Event.F11:
                newCode = KeyEvent.VK_F11;
                break;
            case Event.F12:
                newCode = KeyEvent.VK_F12;
                break;
            default:
                throw new IllegalArgumentException(Integer.toString(oldCode));
        }
        return newCode;
    }

    public void keyTyped(KeyEvent event) {
        char keyChar = event.getKeyChar();
        if (keyChar != KeyEvent.VK_TAB
                && keyChar != KeyEvent.VK_BACK_SPACE
                && keyChar != KeyEvent.VK_DELETE
                && keyChar != KeyEvent.VK_ENTER
                && keyChar != KeyEvent.VK_ESCAPE) {
            if (limit == NO_LIMIT || getText().length() < limit) {
                pause();    // Pace keys under limit
            } else {
                event.consume();
            }
        }
    }

    public void keyPressed(KeyEvent event) {
        if (ignoreKey(event)) {
            event.consume();
        } else {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_ENTER:
                    if (history != null) {
                        history.add(getText());
                        history.reset();
                    }
                    // Pass synthetic event to parent using AWT 1.0 inheritance event model.
                    getParent().deliverEvent(new Event(this, System.currentTimeMillis(), Event.KEY_PRESS, 0, 0, Event.ENTER, 0));
                    event.consume();
                    break;
                case KeyEvent.VK_UP:
                    if (history != null) {
                        setText(history.back());
                    }
                    event.consume();
                    break;
                case KeyEvent.VK_DOWN:
                    if (history != null) {
                        setText(history.forward());
                    }
                    event.consume();
                    break;
                case KeyEvent.VK_ESCAPE:
                    setText(NO_TEXT);
                    if (history != null) {
                        history.reset();
                    }
                    event.consume();
                    break;
                case KeyEvent.VK_F1:
                case KeyEvent.VK_F2:
                case KeyEvent.VK_F3:
                case KeyEvent.VK_F4:
                case KeyEvent.VK_F5:
                case KeyEvent.VK_F6:
                case KeyEvent.VK_F7:
                case KeyEvent.VK_F8:
                case KeyEvent.VK_F9:
                case KeyEvent.VK_F10:
                case KeyEvent.VK_F11:
                case KeyEvent.VK_F12:
                    formatMacro(keyCode, getText());
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }

    public void keyReleased(KeyEvent event) {
    }
}
