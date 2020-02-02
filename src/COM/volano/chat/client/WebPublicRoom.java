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

package COM.volano.chat.client;

import java.awt.*;
import java.net.*;
import java.util.Date;
import java.util.Vector;

import COM.volano.*;
import COM.volano.awt.*;
import COM.volano.util.*;
import COM.volano.chat.*;

/**
 *  Defines the public chat room used by the WebVolanoChat applet.
 *
 *  <P>This panel triggers action events when the user sends chat text,
 *  or when the user double-clicks a name in the user list.
 */
public class WebPublicRoom extends Panel implements WebStatusPanel {
    private static final int DEFAULT_ROOMSIZE = 30;
    private static final int MESSAGE_FIELD_COLUMNS = 60;

    final public static String TRUE = "true";
    final public static int CAPACITY = 25000;
    final private static int MARGIN = 6;
    final private static String BLANK = "";

    private Value value;
    private Sounds sounds;
    // private String[][] users;  // The users in this public room
    private String userName = "";
    private Vector nameList = new Vector(DEFAULT_ROOMSIZE);
    private Vector users    = new Vector(DEFAULT_ROOMSIZE);

    private GridBagLayout layout = new GridBagLayout();
    private Focus focus;
    private Label title = new Label();
    // private FormattedTextArea area = new FormattedTextArea(CAPACITY);
    // private TextPanel area = TextPanel.getInstance();
    private TextPanel area;

    // private List userList = new List();
    // private List userList = new List(LIST_COLUMNS, false);
    private List userList;
    private ControlledTextField field;
    private HyperLabel hyper;
    private Button button;

    private int type  = IClient.ROOM_UNKNOWN;
    private int count = 0;

    /**
     *  Constructs a new <CODE>WebPublicRoom</CODE>.
     *
     *  @param value  The <CODE>Value</CODE> object that defines most of our state.
     *  @param sounds The <CODE>Sounds</CODE> object used for audio alerts.
     */
    public WebPublicRoom(Value value, Sounds sounds) {
        this.value = value;
        this.sounds = sounds;

        area = TextPanel.getInstance(value.javaVersion, value.textareaSwingForce);
        field = new ControlledTextField(MESSAGE_FIELD_COLUMNS);
        field.setLimit(value.lengthChattext);
        field.setDelay(value.delayKeystroke);
        field.setIgnoreAlt(value.keyIgnoreAlt);
        field.setIgnoreCtrl(value.keyIgnoreCtrl);
        field.setIgnoreMeta(value.keyIgnoreMeta);
        field.setIgnoreShift(value.keyIgnoreShift);
        field.enableHistory(value.historyEnable);
        field.addMacro(Event.F1, value.textF1);
        field.addMacro(Event.F2, value.textF2);
        field.addMacro(Event.F3, value.textF3);
        field.addMacro(Event.F4, value.textF4);
        field.addMacro(Event.F5, value.textF5);
        field.addMacro(Event.F6, value.textF6);
        field.addMacro(Event.F7, value.textF7);
        field.addMacro(Event.F8, value.textF8);
        field.addMacro(Event.F9, value.textF9);
        field.addMacro(Event.F10, value.textF10);
        field.addMacro(Event.F11, value.textF11);
        field.addMacro(Event.F12, value.textF12);

        userName = value.username;
        userList = new List(value.lengthUsername, false);
        hyper = new HyperLabel(value.context, value.linkPrefix);

        /*
            WebVolanoChat.setColorsAndFont(this, value);
            WebVolanoChat.setColorsAndFont(title, value);
            WebVolanoChat.setColorsAndFont(area, value);
            WebVolanoChat.setColorsAndFont(userList, value);
            WebVolanoChat.setColorsAndFont(field, value);
            WebVolanoChat.setColorsAndFont(hyper, value);
        */

        // title.setText(value.group);
        title.setText(value.title);
        hyper.setText(value.textChatStatus);
        area.setEditable(false);
        focus = new Focus(field);
        //focus.add(hyper);
        focus.add(area);
        focus.add(userList);
        setLayout(layout);
        add(title, 0, 0, 2, true, false);
        add(area, 0, 1, 1, true, true);
        add(userList, 1, 1, 1, false, true);
        add(field, 0, 2, 2, true, false);
        add(hyper, 0, 3, 2, true, false);
        add(focus, 0, 4, 2, true, false);

        // Set the background and foreground of each component, not just this panel.
        Theme.setTheme(this, value.getTheme(Theme.DEFAULT));
    }

    /**
     *  Adds a component to this container with the specified constraints.
     *
     *  @param c  The component to add.
     *  @param x  The <CODE>gridx</CODE> value for the component's GridBagConstraints.
     *  @param y  The <CODE>gridy</CODE> value for the component's GridBagConstraints.
     *  @param w  The <CODE>gridwidth</CODE> value for the component's GridBagConstraints.
     *  @param h  <CODE>true</CODE> if the component should resize horizontally.
     *  @param v  <CODE>true</CODE> if the component should resize vertically.
     */
    private void add(Component c, int x, int y, int w, boolean h, boolean v) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = gbc.NORTHWEST;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = gbc.BOTH;
        gbc.weightx = h ? 1 : 0;
        gbc.weighty = v ? 1 : 0;
        gbc.gridwidth = w;
        gbc.gridheight = 1;
        layout.setConstraints(c, gbc);
        add(c);
    }


    /**
     *  Returns this component's margins.
     *
     *  @return This component's margins.
     */
    public Insets getInsets() {
        return new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
    }

    /**
     * Sets this room inactive.
     */
    public void setInactive() {
        // Theme.setInactive(field, value.getTheme(Theme.DEFAULT));
        field.setForeground(value.colorForegroundTextEditableInactive);
    }

    /**
     * Sets this room active.
     */
    public void setActive() {
        // Theme.setActive(field, value.getTheme(Theme.DEFAULT));
        field.setForeground(value.colorForegroundTextEditable);
    }

    /**
     *  Delivers action events.
     *  <P>If the action event was triggered by the text field or by the
     *  list, this method transmits it to the parent container.
     *
     *  @param ev   The event object
     *  @param what Ignored.
     */
    public boolean action(Event ev, Object what) {
        if (ev.target == field) {
            return field.getText().length() == 0;
        }
        return (ev.target != userList) && (ev.target != button);
    }


    /**
     *  Intercepts the tab and shift-tab keys to handle focus.
     *
     *  @param ev   The event object.
     *  @param key  The key that was pressed.
     */
    public boolean keyDown(Event ev, int key) {
        boolean handled = false;
        if (key == Build.TAB) {
            if (ev.shiftDown()) {
                focus.previous();
            } else {
                focus.next();
            }
            handled = true;
        } else if (key == Build.NEW_LINE || key == Build.RETURN) {
            postEvent(new Event(field, Event.ACTION_EVENT, ""));
            handled = true;
        }
        return handled;
    }


    /**
     *  Handles <CODE>LIST_SELECT</CODE> events.
     *  <P>When a user in the list is selected, that user's profile and
     *  member information is displayed in the hyper label.
     *
     *  @param ev  The event object.
     */
    public boolean handleEvent(Event ev) {
        if ((ev.target == userList) && (ev.id == Event.LIST_SELECT)) {
            handleUserList(ev);
            return true;
        }
        return super.handleEvent(ev);
    }


    /**
     * Handles the selection of a user from the user list, displaying the user's
     * profile and saving the profiled user name.
     *
     * @param event  the event definition.
     */
    private void handleUserList(Event event) {
        String[] user = (String[]) users.elementAt(((Integer) event.arg).intValue());
        if (value.memberDocument.length() > 0 &&
                Boolean.valueOf(user[IClient.USER_MEMBER]).booleanValue() &&
                Boolean.valueOf(user[IClient.USER_LINK]).booleanValue()) {
            Vector subList = new Vector(2);
            subList.addElement(URLEncoder.encode(user[IClient.USER_NAME]));
            subList.addElement(URLEncoder.encode(userName));
            String href = Message.format(value.memberDocument, subList);
            hyper.setText(formatProfile(value, user), value.textMemberProfile, href, value.linkProfileUrl, value.linkProfileDisable);
        } else {
            hyper.setText(formatProfile(value, user), value.linkProfileUrl, value.linkProfileDisable);
        }
        value.sounds.play(Sounds.PROFILE);
    }

    /**
     *  Sets the user list of this public chat room.
     *
     *  @param userList The list of users in the public chat room.
     */
    public void setUserList(String[][] users) {
        /*
            if (value.reverseNamesEnabled) {
              for (int i = 0; i < users.length; i++) {
                this.userList.add(users[i][IClient.USER_NAME], 0);
                this.nameList.insertElementAt(users[i][IClient.USER_NAME], 0);
                this.users.insertElementAt(users[i], 0);
              }
            }
            else {
              for (int i = 0; i < users.length; i++) {
                this.userList.add(users[i][IClient.USER_NAME]);
                this.nameList.addElement(users[i][IClient.USER_NAME]);
                this.users.addElement(users[i]);
              }
            }
        */
        for (int i = 0; i < users.length; i++) {
            this.userList.add(users[i][IClient.USER_NAME]);
            this.nameList.addElement(users[i][IClient.USER_NAME]);
            this.users.addElement(users[i]);
        }
    }

    /**
     *  Sets the room information.
     *
     *  @param name  The name of the room.
     *  @param type  The type of the room.
     *  @param count The count of people in the room, which could be greater than
     *               the user list for auditoriums.
     */
    public void setInfo(String name, int type, int count) {
        this.type  = type;
        this.count = count;
    }

    /**
     *  Gets the room type.
     *
     *  @return the type of the room.
     */
    public int getType() {
        return type;
    }

    /**
     *  Adds a new user to this public chat room's user list.
     *
     *  @param name     The name of the user to add.
     *  @param profile  The profile of the user to add.
     *  @param host     The host of the user to add.
     *  @param isMember <CODE>true</CODE> if the user is a member.
     *  @param showLink <CODE>true</CODE> if the member link is to be displayed.
     */
    public void addUser(String name, String profile, String host, boolean isMember, boolean showLink) {
        String[] user = new String[IClient.USER_MAXINFO];
        user[IClient.USER_NAME]    = name;
        user[IClient.USER_PROFILE] = profile;
        user[IClient.USER_HOST]    = host;
        user[IClient.USER_MEMBER]  = String.valueOf(isMember);
        user[IClient.USER_LINK]    = String.valueOf(showLink);
        /*
            if (value.reverseNamesEnabled) {
              userList.add(name, 0);
              nameList.insertElementAt(name, 0);
              users.insertElementAt(user, 0);
            }
            else {
              userList.add(name);
              nameList.addElement(name);
              users.addElement(user);
            }
        */
        userList.add(name);
        nameList.addElement(name);
        users.addElement(user);

        if (value.entranceAlertsEnabled) {
            // now notify the room of the new user
            Vector vector = new Vector(4);
            vector.addElement(new Date());
            // vector.addElement(formatProfile(value, user));
            vector.addElement(name);
            vector.addElement(host);
            vector.addElement(profile);
            area.append(Message.format(value.textSystemEntrance, vector));
            if (value.audioAlertsEnabled) {
                sounds.alert();
            }
        }
    }

    /**
     *  Removes a user from the user list.
     *
     *  @param name The name of the user to remove.
     */
    public void removeUser(String name) {
        int index = nameList.indexOf(name);
        if (index != -1) {
            userList.remove(index);
            nameList.removeElementAt(index);
            users.removeElementAt(index);
        }
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
        // Assumes the old and new character strings are the same length.
        for (int i = 0; i < value.charReplaceOld.length(); i++) {
            text = text.replace(value.charReplaceOld.charAt(i), value.charReplaceNew.charAt(i));
        }
        return text;
    }

    /**
     *  Appends chat text from the given user to the room.
     *
     *  @param user The user sending the chat text
     *  @param text The chat text
     */
    public void appendChatText(String user, String text) {
        area.append("<" + user + "> " + substitute(text));
    }


    /**
     *  Appends chat text from the current user to the room.
     *
     *  @param text The chat text.
     */
    public void appendChatText(String text) {
        area.append("> " + substitute(text));
    }


    /**
     *  Appends broadcast text to the room.
     *
     *  @param text The broadcast text.
     */
    public void append(String text) {
        area.append(substitute(text));
    }


    /**
     *  Retrieves and clears this public room's text field.
     *
     *  @return The value of this room's text field.
     */
    public String getChatText() {
        String result = field.getText();
        field.setText(BLANK);
        return result;
    }


    /**
     *  Closes this public chat room.
     *  <P>The "reconnect" button will appear at the end of this method.
     */
    public void close() {
        remove(field);
        button = new Button(value.textMainConnect);

        // WebVolanoChat.setColorsAndFont(button, value);
        Theme.setTheme(button, value.getTheme(Theme.DEFAULT));

        add(button, 0, 2, 2, true, false);
        focus.replace(field, button);
        invalidate();
        validate();
    }


    /**
     *  Sets the status label to the given string.
     *
     *  @param s  The new status string.
     */
    public void setStatus(String s) {
        hyper.setText(s);
    }

    /**
     * Formats a user profile string.
     *
     * @param user  the user information.
     * @return  the formatted profile string.
     */
    private String formatProfile(Value value, String[] user) {
        String profile = "";
        Vector subList = new Vector(3);
        subList.addElement(user[IClient.USER_NAME]);
        subList.addElement(user[IClient.USER_HOST]);
        if (user[IClient.USER_PROFILE].length() == 0) {
            profile = Message.format(value.textStatusNoprofile, subList);
        } else {
            subList.addElement(user[IClient.USER_PROFILE]);
            profile = Message.format(value.textStatusProfile, subList);
        }
        return profile;
    }
}
