/*
 * PublicChat.java - a frame for public chatting.
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
import  COM.volano.chat.*;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  COM.volano.util.*;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.Date;
import  java.util.Enumeration;
import  java.util.Hashtable;
import  java.util.Observable;
import  java.util.StringTokenizer;
import  java.util.Vector;

/**
 * This class is a public or personal chat room window.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class PublicChat extends RoomFrame {
    private static final int  DEFAULT_ROOMSIZE = 30;
    private static final int  PRIVATE_LIMIT    =  5;
    private static final int  QUESTION_SIZE    = 10;

    private String      groupName;
    private int         roomType;
    private int         count;
    private String      userName;
    private String      profile;
    private Hashtable   privateList;

    private Vector      nameList = new Vector(DEFAULT_ROOMSIZE);
    private Vector      users    = new Vector(DEFAULT_ROOMSIZE);
    private Vector      recent   = new Vector(DEFAULT_ROOMSIZE);

    private Object      messageLock  = new Object();
    private int         messageCount = 0;

    private List        userList;
    private List        questionList;
    private RoomMenu    roomMenu;
    private OptionsMenu optionsMenu;
    private PeopleMenu  peopleMenu;
    private MonitorMenu monitorMenu;
    private ThemeMenu   themeMenu;
    private LinkMenu    linkMenu;
    private HelpMenu    helpMenu;

    /**
     * Creates a new public or personal chat room window.
     *
     * @param connection    the connection to the chat server.
     * @param owner         the owner of this frame, so that WINDOW_DESTROY events
     *                      can be proprogated to the owning component.
     * @param value         the applet parameter and property values.
     * @param users         the list of users in the room.
     * @param groupName     the name of this room.
     * @param roomType      the type of this room (public, personal, or event).
     * @param count         the count of people in this room (including audience).
     * @param userName      the name of this user.
     * @param profile       the profile of this user.
     * @param privateList   the list of all private chat rooms.
     */

    public PublicChat(Connection connection, Component owner, Value value, String[][] users,
                      String groupName, int roomType, int count, String userName, String profile, Hashtable privateList) {
        super(connection, owner, value);
        this.groupName   = groupName;
        this.roomType    = roomType;
        this.count       = count;
        this.userName    = userName;
        this.profile     = profile;
        this.privateList = privateList;

        this.userList = new List();
        for (int i = 0; i < users.length; i++) {
            this.userList.add(users[i][RoomPacket.NAME]);
            this.nameList.addElement(users[i][RoomPacket.NAME]);
            this.users.addElement(users[i]);
            this.recent.insertElementAt(users[i], 0);
        }
        if (recent.size() > value.recentUserLimit) {
            recent.setSize(value.recentUserLimit);
        }

        if (roomType == RoomPacket.NORMAL || value.stage) {
            status.setText(value.textChatStatus);    // Normal room or on stage
        } else {                                    // Member of audience
            status.setText(value.textChatEventStatus);
        }

        Panel southPanel = new Panel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add("North", talkText);
        // A moderator is an administrator who enters the stage of an event.
        if (value.admin && value.stage && roomType == RoomPacket.EVENT) {
            questionList = new List(QUESTION_SIZE, false);
            southPanel.add("Center", questionList);
        }
        // 2.6.4 Allow dynamic loading of text and link as properties from a URL.
        if (value.labelUrl.length() > 0 || value.labelText.length() > 0) {
            label = new HyperLabel(value.context, value.linkPrefix);
            if (value.labelUrl.length() > 0) {
                label.getText(value.labelUrl, value.labelUrlText, value.labelUrlLink, value.labelText, value.labelLink);
            } else {
                label.setText(value.labelText, value.labelLink);
            }
            Panel statusPanel = new Panel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.add("North", status);
            statusPanel.add("South", label);
            southPanel.add("South", statusPanel);
        } else {
            southPanel.add("South", status);
        }

        setLayout(new BorderLayout());
        if (banner != null) {
            add("North", banner);
        }
        add("Center", listenText);
        add("South", southPanel);
        add("East", userList);

        MenuBar menuBar = new MenuBar();
        roomMenu    = new RoomMenu(value);
        optionsMenu = new OptionsMenu(value);
        peopleMenu  = new PeopleMenu(value);
        monitorMenu = new MonitorMenu(value);
        themeMenu   = new ThemeMenu(value);
        linkMenu    = new LinkMenu(value);
        helpMenu    = new HelpMenu(value);

        if (value.textMenuRoom.length() > 0) {
            menuBar.add(roomMenu);
        }
        if (value.textMenuOptions.length() > 0) {
            menuBar.add(optionsMenu);
        }
        if (value.textMenuPeople.length() > 0 && (roomType == RoomPacket.NORMAL || value.stage)) {
            menuBar.add(peopleMenu);
        }
        if (value.textMenuMonitor.length() > 0 && (value.isMonitor || value.admin)) {
            menuBar.add(monitorMenu);
        }
        if (value.textMenuThemesTitle.length() > 0) {
            menuBar.add(themeMenu);
        }
        if (value.textMenuLinksTitle.length() > 0) {
            menuBar.add(linkMenu);
        }
        if (value.textMenuHelp.length() > 0) {
            menuBar.add(helpMenu);
        }
        if (menuBar.getMenuCount() > 0) {
            setMenuBar(menuBar);
        }

        focusNext.put(listenText, userList);
        focusNext.put(userList, talkText);
        focusNext.put(talkText, status);
        focusNext.put(status, listenText);
        focusPrev.put(status, talkText);
        focusPrev.put(talkText, userList);
        focusPrev.put(userList, listenText);
        focusPrev.put(listenText, status);
    }

    /**
     * Gets the name of this public or personal room.
     *
     * @return  the room name.
     */

    String groupName() {
        return groupName;
    }

    /**
     * Checks whether this room is accepting private chat requests.
     *
     * @return  <code>true</code> if the room is accepting private chat requests;
     *          otherwise <code>false</code>.
     */

    public boolean isAcceptingPrivate() {
        return optionsMenu.acceptPrivateCheckbox.getState();
    }

    /**
     * Checks whether the user is ignored.
     *
     * @return  <code>true</code> if the user is ignored; otherwise
     *          <code>false</code>.
     */

    public boolean isIgnored(String name) {
        return peopleMenu.isIgnored(name);
    }

    /**
     * Closes this frame, sending an exit room request to the server.
     */

    public void close() {
        super.close();
        send(new ExitRoom(groupName, userName));
    }

    /**
     * Sets the color and font theme of this window.
     *
     * @param theme the new color and font theme.
     */

    public void setTheme(Theme theme) {
        super.setTheme(theme);
        synchronized (messageLock) {
            if (messageCount > value.unconfirmedChat) {
                Theme.setInactive(talkText, theme);
            }
        }
        themeMenu.selectTheme(theme.getIndex());
        optionsMenu.selectFont(theme.getFontDefault());
    }

    /**
     * Sets the font name for this window.
     *
     * @param name  the font name.
     */

    void setFontName(String name) {
        super.setFontName(name);
        optionsMenu.selectFontName(name);
    }

    /**
     * Sets the font style for this window.
     *
     * @param name  the font style.
     */

    void setFontStyle(int style) {
        super.setFontStyle(style);
        optionsMenu.selectFontStyle(style);
    }

    /**
     * Sets the accept private option for this window.
     *
     * @param value  <code>true</code> to enable private chats; otherwise
     *               <code>false</code>.
     */

    void setAcceptPrivate(boolean value) {
        optionsMenu.acceptPrivateCheckbox.setState(value);
    }

    /**
     * Sets the entrance alert option for this window.
     *
     * @param value  <code>true</code> to enable entrance alerts; otherwise
     *               <code>false</code>.
     */

    void setEntranceAlert(boolean value) {
        optionsMenu.entranceAlertCheckbox.setState(value);
    }

    /**
     * Sets the audio alert option for this window.
     *
     * @param value  <code>true</code> to enable audio alerts; otherwise
     *               <code>false</code>.
     */

    void setAudioAlert(boolean value) {
        optionsMenu.audioAlertCheckbox.setState(value);
    }

    /**
     * Sets the count alert option for this window.
     *
     * @param value  <code>true</code> to enable count alerts; otherwise
     *               <code>false</code>.
     */

    void setCountAlert(boolean value) {
        optionsMenu.countAlertCheckbox.setState(value);
    }

    /**
     * Sets the Web touring option for this window.
     *
     * @param value  <code>true</code> to enable Web touring; otherwise
     *               <code>false</code>.
     */

    void setWebtouring(boolean value) {
        optionsMenu.webtouringCheckbox.setState(value);
    }

    /**
     * Called when a user interface action occurs.
     *
     * @param event   the event definition.
     * @param object  depends on the action.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    public boolean action(Event event, Object object) {
        if (event.target == userList) {
            if (roomType == RoomPacket.NORMAL || value.stage) {
                enterPrivate((String) object);
            }
            return true;
        } else if (event.target == questionList) {
            talkText.setText((String) object);
            talkText.requestFocus();
            return true;
        } else if (event.target == roomMenu.closeMenuItem) {
            event.id     = Event.WINDOW_DESTROY;
            event.target = this;
            return handleWindowDestroy(event);
        } else if (event.target == peopleMenu.ringMenuItem) {
            send(new Beep(groupName, userName, peopleMenu.getUserName()));
            return true;
        } else if (event.target == peopleMenu.ignoreMenuItem) {
            peopleMenu.setIgnored();
            return true;
        } else if (event.target == peopleMenu.countMenuItem) {
            status.setText(Message.format(value.textStatusRoomcount, new Integer(count)));
            return true;
        } else if (event.target == monitorMenu.removeMenuItem) {
            showMonitorDialog(value.textMonitorTitleRemove, getUsers(users), Kick.REMOVE);
            return true;
        } else if (event.target == monitorMenu.kickMenuItem) {
            showMonitorDialog(value.textMonitorTitleKick, getUsers(recent), Kick.KICK);
            return true;
        } else if (event.target == monitorMenu.banMenuItem) {
            showMonitorDialog(value.textMonitorTitleBan, getUsers(recent), Kick.BAN);
            return true;
        } else if (linkMenu.contains(event.target)) {
            URL url = linkMenu.getLink(event.target);
            if (url != null) {
                value.context.showDocument(url, Build.TARGET);
            }
            return true;
        } else if (event.target == helpMenu.topicsMenuItem) {
            value.context.showDocument(value.pageHelp, Build.TARGET);
            return true;
        } else if (event.target == helpMenu.aboutMenuItem) {
            value.context.showDocument(value.pageAbout, Build.TARGET);
            return true;
        } else if (event.target == optionsMenu.increaseFontMenuItem) {
            increaseFont();
            return true;
        } else if (event.target == optionsMenu.decreaseFontMenuItem) {
            decreaseFont();
            return true;
        } else if (event.target instanceof CheckboxMenuItem) {
            CheckboxMenuItem checkbox = (CheckboxMenuItem) event.target;
            MenuContainer    parent   = checkbox.getParent();
            if (parent == optionsMenu.fontNameMenu) {
                setFontName(checkbox.getLabel());
            } else if (parent == optionsMenu.fontStyleMenu) {
                setFontStyle(optionsMenu.getFontStyle(checkbox));
            } else if (parent == themeMenu) {
                setTheme(value.getTheme(themeMenu.getThemeIndex(checkbox)));
            }
            return true;
        }
        return false;
    }

    /**
     * Called when any user interface event occurs.
     *
     * @param event   the event definition.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    public boolean handleEvent(Event event) {
        if (event.target == userList && event.id == Event.LIST_SELECT) {
            handleUserList(event);
            return true;
        }
        return super.handleEvent(event);
    }

    /**
     * Sends the chat text to the server.
     *
     * @param text  the text to send.
     */

    protected void sendText(String text) {
        // Make sure we're under the limit on unconfirmed chat messages.
        // Also don't let user ramble if disconnected and don't send empty text.
        if (messageCount <= value.unconfirmedChat && connected && text.trim().length() != 0) {
            if (roomType == RoomPacket.NORMAL || value.stage) {
                text = prepareText(text, true);    // Normal room or on stage
            } else {
                text = prepareText(text, false);    // Member of audience
                status.setText(value.textChatEventSent);
            }
            // Bug Id 4096301, "Textfield allows keystrokes when (only) disabled"
            // http://developer.java.sun.com/developer/bugParade/bugs/4096301.html
            //   "As of 1.3.0-C, doing setEnabled(false) immediately both disables
            //    the TextField _and_ makes it non-editable."
            // So we can't use "talkText.setEnabled(false)" here.
            synchronized (messageLock) {
                send(new Chat(groupName, userName, text));
                if (++messageCount > value.unconfirmedChat) {
                    Theme.setInactive(talkText, theme);
                }
            }
        }
    }

    /**
     * Sends an enter private request to the server, limiting the total number of
     * private chat sessions to the value specified by "limit.private".
     *
     * @param toName  the other user for the private chat session.
     */

    private void enterPrivate(String toName) {
        if (connected && ! value.sendPrivateDisable && ! toName.equals(userName)) {
            // A value of -1 means no limit on the number of private chat rooms.
            if (value.limitPrivate != -1 && privateList.size() >= value.limitPrivate &&
                    ! value.admin && ! value.monitor) {
                status.setText(Message.format(value.textStatusPrivatelimit, new Integer(value.limitPrivate)));
            } else {
                status.setText(Message.format(value.textStatusEnteringprivate, toName));
                send(new EnterPrivate(groupName, userName, toName));
            }
        }
    }

    /**
     * Sends a kick request to remove, kick, or ban the selected user.
     *
     * @param name    the name of the user to remove or kick.
     * @param address the address of the user to ban.
     * @param method  either remove, kick, or ban.
     */

    void kick(String name, String address, int method) {
        if (connected && ! name.equals(userName)) {
            send(new Kick(groupName, userName, name, address, method));
        }
    }

    /**
     * Handles the selection of a user from the user list, displaying the user's
     * profile and saving the profiled user name.
     *
     * @param event  the event definition.
     */

    private void handleUserList(Event event) {
        String[] user = (String[]) users.elementAt(((Integer) event.arg).intValue());
        showProfile(value, user, userName);
        peopleMenu.setUser(user[RoomPacket.NAME], user[RoomPacket.HOST]);
        value.sounds.play(Sounds.PROFILE);
    }

    /**
     * Called to display any Web links if touring is enabled.
     *
     * @param text  the text of the chat message.
     */

    public void checkTouring(String text) {
        if (optionsMenu.webtouringCheckbox.getState()) {
            StringTokenizer tokenizer = new StringTokenizer(text);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                try {
                    URL url = new URL(token);
                    value.context.showDocument(url, Build.TARGET);
                } catch (MalformedURLException e) {}
            }
        }
    }

    /**
     * Called when a packet is received from the server.
     *
     * @param observable  the connection to the server.
     * @param object      the packet received from the server.
     */

    public void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " update ...");
        }

        Connection connection = (Connection) observable;
        if (object instanceof RoomPacket && object instanceof Packet) {
            RoomPacket roomPacket = (RoomPacket) object;
            Packet     packet     = (Packet) object;
            if (groupName.equals(roomPacket.getRoomName()) && ! packet.isHandled()) {
                // Check for packet type so we don't mark packets handled by accident.
                switch (packet.getType()) {
                case Packet.INDICATION:
                    if (packet instanceof Chat) {
                        chatIndication(connection, (Chat) packet);
                    } else if (packet instanceof EnterRoom) {
                        enterRoomIndication(connection, (EnterRoom) packet);
                    } else if (packet instanceof ExitRoom) {
                        exitRoomIndication(connection, (ExitRoom) packet);
                    } else if (packet instanceof Beep) {
                        beepIndication(connection, (Beep) packet);
                    }
                    break;

                case Packet.CONFIRM:
                    if (packet instanceof Chat) {
                        chatConfirm(connection, (Chat) packet);
                    } else if (packet instanceof EnterPrivate) {
                        enterPrivateConfirm();
                    }
                    break;
                }
            }
        } else if (object instanceof StreamableError) {
            streamableError(connection, (StreamableError) object);
        } else if (object == null) {
            handleDisconnect();
        }
    }

    /**
     * Handles a chat indication.
     *
     * @param connection  the connection to the server.
     * @param indication  the chat indication.
     */

    private void chatIndication(Connection connection, Chat indication) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " chatIndication ...");
        }

        indication.setHandled();
        String name = indication.getUserName();
        String text = indication.getText();
        text = substitute(text);
        if (name.length() == 0) {
            Vector subList = new Vector(2);
            subList.addElement(new Date());
            subList.addElement(text);
            listenText.append(Message.format(value.textSystemBroadcast, subList));
        } else if (! peopleMenu.isIgnored(name)) {
            if (questionList != null && indication.isQuestion()) {
                // Append at the top, so that the moderator does not need to scroll down
                // the list as each question is posted.
                questionList.add(name + ": " + text, 0);
            } else {
                listenText.append("<" + name + "> " + text);
                checkTouring(text);
            }
        }
    }

    /**
     * Handles a chat confirm.
     *
     * @param connection  the connection to the server.
     * @param confirm     the chat confirm.
     */

    private void chatConfirm(Connection connection, Chat confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " chatConfirm ...");
        }

        confirm.setHandled();
        synchronized (messageLock) {
            if (--messageCount <= value.unconfirmedChat) {
                Theme.setActive(talkText, theme);
            }
        }
    }

    /**
     * Handles an enter room indication.
     *
     * @param connection  the connection to the server.
     * @param confirm     the enter room indication.
     */

    private void enterRoomIndication(Connection connection, EnterRoom indication) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " enterRoomIndication ...");
        }

        count++;
        if (optionsMenu.countAlertCheckbox.getState()) {
            status.setText(Message.format(value.textStatusRoomcount, new Integer(count)));
        }
        indication.setHandled();
        String name = indication.getUserName();
        if (name.length() != 0) {           // If not audience member
            addUser(name, indication.getProfile(), indication.getAddress(), indication.isMember(), indication.showLink());
        }
    }

    /**
     * Handles an exit room indication.
     *
     * @param connection  the connection to the server.
     * @param confirm     the exit room indication.
     */

    private void exitRoomIndication(Connection connection, ExitRoom indication) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " exitRoomIndication ...");
        }

        count--;
        if (optionsMenu.countAlertCheckbox.getState()) {
            status.setText(Message.format(value.textStatusRoomcount, new Integer(count)));
        }
        indication.setHandled();
        String name = indication.getUserName();
        if (name.equals(userName)) {        // If I'm being removed from room
            handleWindowDestroy(new Event(this, Event.WINDOW_DESTROY, null));
        } else if (name.length() != 0) {    // If not audience member
            removeUser(name);
        }
    }

    /**
     * Handles a beep indication.
     *
     * @param connection  the connection to the server.
     * @param confirm     the beep indication.
     */

    private void beepIndication(Connection connection, Beep indication) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " beepIndication ...");
        }

        String name = indication.getFromName();
        if (optionsMenu.audioAlertCheckbox.getState() && ! peopleMenu.isIgnored(name)) {
            Vector subList = new Vector(2);
            subList.addElement(new Date());
            subList.addElement(name);
            listenText.append(Message.format(value.textSystemAudio, subList));
            value.sounds.alert();
        }
    }

    /**
     * Handles an enter private confirmation.
     */

    private void enterPrivateConfirm() {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " enterPrivateConfirm ...");
        }

        status.setText(value.textChatStatus);
    }

    /**
     * Handles a streamable error packet.
     *
     * @param connection  the connection to the server.
     * @param confirm     the streamable error packet.
     */

    private void streamableError(Connection connection, StreamableError error) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " streamableError ...");
        }

        status.setText("Server error (" + error.getText() + ").");
    }

    /**
     * Adds a user to the user list.
     *
     * @param name     the name of the user to add.
     * @param profile  the profile of the user.
     * @param host     the host name of the user.
     */

    private void addUser(String name, String profile, String host, String member, String linked) {
        String[] user = new String[RoomPacket.MAX_USERINFO];
        user[RoomPacket.NAME]    = name;
        user[RoomPacket.PROFILE] = profile;
        user[RoomPacket.HOST]    = host;
        user[RoomPacket.MEMBER]  = member;
        user[RoomPacket.LINK]    = linked;
        synchronized (users) {
            /*
                  if (optionsMenu.reverseNamesCheckbox.getState()) {
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
            recent.insertElementAt(user, 0);
            if (recent.size() > value.recentUserLimit) {
                recent.setSize(value.recentUserLimit);
            }
        }

        if (optionsMenu.entranceAlertCheckbox.getState()) {
            Vector subList = new Vector(4);
            subList.addElement(new Date());
            // subList.addElement(formatProfile(value, user));
            subList.addElement(name);
            subList.addElement(host);
            subList.addElement(profile);
            listenText.append(Message.format(value.textSystemEntrance, subList));
            if (optionsMenu.audioAlertCheckbox.getState()) {
                value.sounds.alert();
            }
        }
    }

    /**
     * Removes a user from the user list.
     *
     * @param name     the name of the user to remove.
     */

    private void removeUser(String name) {
        synchronized (users) {
            int index = nameList.indexOf(name);
            if (index != -1) {
                userList.remove(index);
                nameList.removeElementAt(index);
                users.removeElementAt(index);
            }
        }
    }

    /**
     * Gets a user by name.
     *
     * @param name the name of the user to get.
     * @return the user information array.
     */

    public String[] getUser(String name) {
        String[] user = null;
        synchronized (users) {
            int index = nameList.indexOf(name);
            if (index != -1) {
                user = (String[]) users.elementAt(index);
            }
        }
        return user;
    }

    /**
     * Get the list of users including name and address.
     *
     * @return the list of users.
     */

    private User[] getUsers(Vector list) {
        Vector vector = new Vector(list.size());
        synchronized (users) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                String[] user = (String[]) enumeration.nextElement();
                vector.addElement(new User(user[RoomPacket.NAME], user[RoomPacket.HOST]));
            }
        }
        User[] array = new User[vector.size()];
        vector.copyInto(array);
        return array;
    }

    /**
     * Get the list of users including name and address.
     *
     * @param title  the dialog window title.
     * @param list   the list of users to display in the dialog.
     * @param type   the type of dialog (remove, kick or ban).
     */

    private void showMonitorDialog(String title, User[] list, int type) {
        MonitorDialog dialog = new MonitorDialog(this, title, value, list, type);
        dialog.pack();
        Rectangle myBounds = getBounds();
        Rectangle dialogBounds = dialog.getBounds();
        dialog.setLocation(myBounds.x + (myBounds.width - dialogBounds.width) / 2,
                           myBounds.y + (myBounds.height - dialogBounds.height) / 2);
        dialog.setVisible(true);
    }

    /**
     * Clears a list control, working around bugs in Netscape.
     *
     * @param list  the list to clear.
     */

    private void clear(List list) {
        // Netscape Communicator on UNIX displays the following pop-up message if
        // you clear an empty list:
        //   Warning:
        //     Name: slist
        //     Class: XmList
        //     Invalid item(s) to delete.
        int count = list.getItemCount();
        if (count > 0) {
            // Netscape 2.02 fails to clear the list with List.removeAll, so you must
            // delete them explicitly.
            if (value.javaVersion.equals("1.021") && value.javaVendor.startsWith("Netscape")) {
                list.delItems(0, count - 1);
            } else {
                list.removeAll();
            }
        }
    }
}
