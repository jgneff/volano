/*
 * RoomFrame.java - a frame representing a chat room.
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
import  COM.volano.awt.*;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  COM.volano.util.*;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class is the base class for chat room windows.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public abstract class RoomFrame extends Frame implements Observer {
    protected static final int  TEXT_AREA_CAPACITY = 25000;
    private   static final int  MESSAGE_FIELD_COLUMNS = 60;
    private   static final char ASCII_SPACE = '\u0020';

    protected Connection connection;
    private   Component  owner;
    protected Value      value;
    protected Theme      theme;

    protected boolean    connected = true;
    protected Hashtable  focusNext = new Hashtable();
    protected Hashtable  focusPrev = new Hashtable();

    protected PlayerStub          banner;
    // protected FormattedTextArea   listenText;
    protected TextPanel           listenText;
    protected ControlledTextField talkText;
    protected HyperLabel          status;
    protected HyperLabel          label;

    /**
     * Formats a user profile string.
     *
     * @param user  the user information.
     * @return  the formatted profile string.
     */

    static String formatProfile(Value value, String[] user) {
        String profile = "";
        Vector subList = new Vector(3);
        subList.addElement(user[RoomPacket.NAME]);
        subList.addElement(user[RoomPacket.HOST]);
        if (user[RoomPacket.PROFILE].length() == 0) {
            profile = Message.format(value.textStatusNoprofile, subList);
        } else {
            subList.addElement(user[RoomPacket.PROFILE]);
            profile = Message.format(value.textStatusProfile, subList);
        }
        return profile;
    }

    /**
     * Creates the basis for a new chat room window.
     *
     * @param connection  the connection to the chat server.
     * @param owner       the owner of this frame, so that WINDOW_DESTROY events
     *                    can be proprogated to the owning component.
     * @param value       the applet parameter and property values.
     */

    protected RoomFrame(Connection connection, Component owner, Value value) {
        this.connection = connection;
        this.owner      = owner;
        this.value      = value;

        if (value.bannerCode.length() > 0)
            banner = new PlayerStub(value.applet, Key.BANNER_PARAM_PREFIX,
                                    value.bannerCode, value.bannerParameters,
                                    value.bannerWidth, value.bannerHeight);
        // listenText = new FormattedTextArea(TEXT_AREA_CAPACITY);
        listenText = TextPanel.getInstance(value.javaVersion, value.textareaSwingForce);

        talkText = new ControlledTextField(MESSAGE_FIELD_COLUMNS);
        talkText.setLimit(value.lengthChattext);
        talkText.setDelay(value.delayKeystroke);
        talkText.setIgnoreAlt(value.keyIgnoreAlt);
        talkText.setIgnoreCtrl(value.keyIgnoreCtrl);
        talkText.setIgnoreMeta(value.keyIgnoreMeta);
        talkText.setIgnoreShift(value.keyIgnoreShift);
        talkText.enableHistory(value.historyEnable);
        talkText.addMacro(Event.F1, value.textF1);
        talkText.addMacro(Event.F2, value.textF2);
        talkText.addMacro(Event.F3, value.textF3);
        talkText.addMacro(Event.F4, value.textF4);
        talkText.addMacro(Event.F5, value.textF5);
        talkText.addMacro(Event.F6, value.textF6);
        talkText.addMacro(Event.F7, value.textF7);
        talkText.addMacro(Event.F8, value.textF8);
        talkText.addMacro(Event.F9, value.textF9);
        talkText.addMacro(Event.F10, value.textF10);
        talkText.addMacro(Event.F11, value.textF11);
        talkText.addMacro(Event.F12, value.textF12);

        status = new HyperLabel(value.context, value.linkPrefix);
        listenText.setEditable(false);
        connection.addObserver(this);
    }

    /**
     * Shows this frame.
     */

    public void show() {
        super.show();
        talkText.requestFocus();
    }

    /**
     * Requests the focus for this frame.
     */

    public void requestFocus() {
        super.requestFocus();
        talkText.requestFocus();
    }

    /**
     * Sets whether to display the font setting when the font is set.
     *
     * @param showFont <code>true</code> to display the font; otherwise
     *     <code>false</code>.
     */

    public void showFont(boolean showFont) {
        status.showFont(showFont);
    }

    /**
     * Gets the color and font theme of this window.
     *
     * @return  the color and font theme.
     */

    Theme getTheme() {
        return theme;
    }

    /**
     * Sets the color and font theme of this window.
     *
     * @param theme the new color and font theme.
     */

    public void setTheme(Theme theme) {
        try {
            this.theme = (Theme) theme.clone();    // Save new theme
        } catch (CloneNotSupportedException e) {}
        Theme.setTheme(this, theme);
    }

    /**
     * Sets the font name for this window.
     *
     * @param name  the font name.
     */

    void setFontName(String name) {
        Font font = theme.getFontDefault();
        Font newFont = new Font(name, font.getStyle(), font.getSize());
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
    }

    /**
     * Sets the font style for this window.
     *
     * @param name  the font style.
     */

    void setFontStyle(int style) {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), style, font.getSize());
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
    }

    /**
     * Increases the font size for this window.
     */

    void increaseFont() {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() + 1);
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
    }

    /**
     * Decreases the font size for this window.
     */

    void decreaseFont() {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
    }

    /**
     * Closes this window.
     */

    void close() {
        if (connected) {
            connection.deleteObserver(this);
        }
        dispose();
    }

    /**
     * Called when a key is pressed.
     *
     * @param event  the event definition.
     * @param key    the key that was pressed.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    public boolean keyDown(Event event, int key) {
        boolean handled = false;
        switch (key) {
        case Build.TAB:
            if (event.shiftDown()) {
                changeFocus(focusPrev, (Component) event.target);
            } else {
                changeFocus(focusNext, (Component) event.target);
            }
            handled = true;
            break;
        case Build.NEW_LINE:
        case Build.RETURN:
            if (event.target == talkText) {
                sendText(talkText.getText());
            } else {
                talkText.requestFocus();
            }
            // Avoid beep (boinc?) when using Java 1.6.0-beta with
            // Internet Explorer 6.0 or Firefox 1.5.
            handled = true;
            break;
        }
        return handled;
    }

    /**
     * Called when any user interface event occurs.
     *
     * @param event   the event definition.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    public boolean handleEvent(Event event) {
        boolean handled = false;
        if (event.id == Event.WINDOW_DESTROY) {
            handled = handleWindowDestroy(event);
        }
        return handled ? true : super.handleEvent(event);
    }

    /**
     * Handles a WINDOW_DESTROY event, closing this window and propagating the
     * event to the owner of this component.
     *
     * @param event  the event definition.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    protected boolean handleWindowDestroy(Event event) {
        if (event.target == this) {
            close();
            owner.deliverEvent(event);
            return true;
        }
        return false;
    }

    private static boolean isReplaceable(char c) {
        boolean replaceable = false;
        int type = Character.getType(c);
        if (type == Character.CONTROL ||                    // Cc
                type == Character.FORMAT ||                 // Cf
                type == Character.LINE_SEPARATOR ||         // Zl
                type == Character.PARAGRAPH_SEPARATOR ||    // Zp
                type == Character.SPACE_SEPARATOR ||        // Zs
                type == Character.PRIVATE_USE ||            // Co
                type == Character.SURROGATE ||              // Cs
                type == Character.UNASSIGNED) {             // Cn
            replaceable = true;
        }
        return replaceable;
    }

    /**
     * Substitutes each character defined by the <code>chat.replace.old</code>
     * property with its corresponding character in the
     * <code>char.replace.new</code> property.
     *
     * @param text  the text string.
     * @return  the new text string with the substituted characters.
     */

    protected String substitute(String text) {
        // Assumes the old and new character replacement strings are the same length.
        for (int i = 0; i < value.charReplaceOld.length(); i++) {
            text = text.replace(value.charReplaceOld.charAt(i), value.charReplaceNew.charAt(i));
        }

        // Replace all Unicode control, format, private use, surrogate,
        // and unassigned characters, along with line, paragraph, and
        // space separators, with the normal space character. See:
        //   http://www.unicode.org/Public/5.1.0/ucd/PropList.txt
        if (value.charReplaceNonprintable) {
            char[] array = text.toCharArray();
            for (int i = 0; i < array.length; i++) {
                int type = Character.getType(array[i]);
                if (type == Character.CONTROL ||              // Cc
                        type == Character.FORMAT ||               // Cf
                        type == Character.LINE_SEPARATOR ||       // Zl
                        type == Character.PARAGRAPH_SEPARATOR ||  // Zp
                        type == Character.SPACE_SEPARATOR ||      // Zs
                        type == Character.PRIVATE_USE ||          // Co
                        type == Character.SURROGATE ||            // Cs
                        type == Character.UNASSIGNED) {           // Cn
                    array[i] = ASCII_SPACE;
                }
                text = new String(array);
            }
        }
        return text;
    }

    /**
     * Appends the chat text to the conversation and prepares the text for sending
     * to the server.
     *
     * @param user  the user information.
     * @return  the formatted profile string.
     */

    protected String prepareText(String text, boolean append) {
        talkText.setText("");
        text = text.substring(0, Math.min(value.lengthChattext, text.length()));
        text = substitute(text);
        if (append)
        {
            listenText.append("> " + text);
        }
        return text;
    }

    /**
     * Sends the chat text to the server.  This method must be implemented by
     * subclasses.
     *
     * @param text  the text to send.
     */

    protected abstract void sendText(String text);

    /**
     * Sends a packet to the server, displaying a "disconnected" error message if
     * the connection is closed.
     *
     * @param packet  the packet to send.
     */

    protected void send(Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {
            handleDisconnect();
        }
    }

    /**
     * Displays a "disconnected" error message in the chat conversation.
     */

    protected void handleDisconnect() {
        connected = false;          // Mark disconnected
        listenText.append(Message.format(value.textSystemDisconnected, new Date()));
        connection.deleteObserver(this);
        value.wasDisconnected = true;
    }

    /**
     * Formats a "hot key" macro pattern with the substitution variables provided.
     *
     * @param macro      the macro pattern string.
     * @param variables  the variables, separated by spaces or tabs.
     * @return  the formatted macro.
     */

    protected String formatMacro(String macro, String variables) {
        StringTokenizer tokenizer = new StringTokenizer(variables);
        Vector subList = new Vector();
        while (tokenizer.hasMoreTokens()) {
            subList.addElement(tokenizer.nextToken());
        }
        return Message.format(macro, subList);
    }

    /**
     * Shows the profile in the status area.
     *
     * @param value  the applet property values.
     * @param user   the user information.
     */

    protected void showProfile(Value value, String[] user, String userName) {
        if (value.memberDocument.length() > 0 &&
                Boolean.valueOf(user[RoomPacket.MEMBER]).booleanValue() &&
                Boolean.valueOf(user[RoomPacket.LINK]).booleanValue()) {
            Vector subList = new Vector(2);
            subList.addElement(URLEncoder.encode(user[RoomPacket.NAME]));
            subList.addElement(URLEncoder.encode(userName));
            String href = Message.format(value.memberDocument, subList);
            status.setText(formatProfile(value, user), value.textMemberProfile, href, value.linkProfileUrl, value.linkProfileDisable);
        } else {
            status.setText(formatProfile(value, user), value.linkProfileUrl, value.linkProfileDisable);
        }
    }

    /**
     * Changes component focus to the next or previous component.
     *
     * @param table    the table for associating components.
     * @param current  the current component.
     */

    private void changeFocus(Hashtable table, Component current) {
        Component component = (Component) table.get(current);
        if (component == null) {
            component = talkText;
        }
        component.requestFocus();
    }
}
