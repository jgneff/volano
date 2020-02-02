/*
 * MyVolanoChat.java - an applet for entering personal rooms.
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
import  COM.volano.awt.ControlledTextField;
import  COM.volano.chat.Build;
import  COM.volano.chat.client.*;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  COM.volano.util.Message;
import  java.awt.*;
import  java.io.*;
import  java.util.*;
import  java.net.*;

/**
 * This is the main class for the MyVolanoChat client applet.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class MyVolanoChat extends AppletBase {
    private static boolean infoPrinted = false;
    private static final int TEXT_FIELD_COLUMNS = 40;

    private Label               userNameLabel;
    private ControlledTextField userNameField;
    private Label               userPasswordLabel;
    private ControlledTextField userPasswordField;
    private Label               profileLabel;
    private ControlledTextField profileField;

    private Hashtable privateList = new Hashtable();
    private String    documentBase;
    private String    roomName;
    private String    userName;
    private String    userPassword;
    private String    profile;

    private Hashtable focusNext = new Hashtable();
    private Hashtable focusPrev = new Hashtable();
    private Hashtable focusText = new Hashtable();

    /**
     * Truncates the string to the specified limit, trimming off any preceding
     * or trailing blanks.
     *
     * @param text   the text to be truncated and trimmed.
     * @param limit  the maximum number of characters to return.
     * @return the truncated and trimmed text string.
     */

    private static String truncate(String text, int limit) {
        String truncated = text;
        if (text.length() > limit) {
            truncated = text.substring(0, limit).trim();
        }
        return truncated;
    }

    /**
     * Gets this applet's access version string for requesting access to the
     * server, identifying both the applet and its protocol version.
     *
     * @return the access version string for this applet.
     */

    public String getVersion() {
        return value.publicRoom ? Access.PUBLIC_VERSION : Access.PERSONAL_VERSION;
    }

    /**
     * Returns a packet factory for this applet.
     *
     * @return a packet factory for this applet.
     */

    protected PacketFactory getPacketFactory() {
        return new MyVolanoChatPacketFactory();
    }

    /**
     * Sets the delay pauses configured for each request type.
     *
     * @param value the applet property values.
     */

    protected void setPauses(Value value) {
        Access.setWritePause(value.delayAccess);
        Authenticate.setWritePause(value.delayAuthenticate);
        Beep.setWritePause(value.delayBeep);
        Chat.setWritePause(value.delayChat);
        EnterPrivate.setWritePause(value.delayEnterPrivate);
        EnterRoom.setWritePause(value.delayEnterRoom);
        ExitPrivate.setWritePause(value.delayExitPrivate);
        ExitRoom.setWritePause(value.delayExitRoom);
        Kick.setWritePause(value.delayKick);
        Ping.setWritePause(value.delayPing);
    }

    /**
     * Initializes this applet, called after the applet is loaded.
     */

    public void init() {
        super.init();
        if (! infoPrinted) {
            System.out.println(getAppletInfo());
            infoPrinted = true;
        }
        Panel panel = new Panel();

        if (value.member) {
            value.publicRoom = true;
        }
        userName = value.username;
        profile  = value.profile;
        memberName     = value.username;
        memberPassword = value.password;

        // If we've been told to prompt,
        // or this is not a membership applet and there's no username,
        // or this is a membership applet and there's no username or password:
        //   Create a panel for the button as well as the username, password,
        //   and profile prompts as appropriate.
        // Otherwise:
        //   Create a panel for just the button.
        if (value.prompt ||
                (! value.member && value.username.length() == 0) ||
                (value.member && (value.username.length() == 0 || value.password.length() == 0))) {
            GridBagLayout layout = new GridBagLayout();
            panel.setLayout(layout);
            Insets             insets      = new Insets(INSET, INSET, INSET, INSET);
            GridBagConstraints constraints = new GridBagConstraints();

            // Label for user name field (0, 0).
            constraints.gridx      = 0;                               // Default RELATIVE
            constraints.gridy      = 0;                               // Default RELATIVE
            // constraints.gridwidth  = 1;                            // Default 1
            // constraints.gridheight = 1;                            // Default 1
            // constraints.fill       = GridBagConstraints.NONE;      // Default NONE
            // constraints.ipadx      = 0;                            // Default 0
            // constraints.ipady      = 0;                            // Default 0
            constraints.insets     = insets;                          // Default (0, 0, 0, 0)
            constraints.anchor     = GridBagConstraints.EAST;         // Default CENTER
            // constraints.weightx    = 0.00d;                        // Default 0
            // constraints.weighty    = 0.00d;                        // Default 0
            userNameLabel = new Label(value.member ? value.textMemberName : value.textMainUsername, Label.RIGHT);
            userNameLabel.setFont(value.fontDefault);
            layout.setConstraints(userNameLabel, constraints);
            panel.add(userNameLabel);

            if (value.member) {
                // Label for member password field (0, 1).
                constraints.gridy      = 1;
                userPasswordLabel = new Label(value.textMemberPassword, Label.RIGHT);
                userPasswordLabel.setFont(value.fontDefault);
                layout.setConstraints(userPasswordLabel, constraints);
                panel.add(userPasswordLabel);
            }

            // Label for profile field (0, 1) or (0, 2).
            constraints.gridy      = value.member ? 2 : 1;
            profileLabel = new Label(value.textMainProfile, Label.RIGHT);
            profileLabel.setFont(value.fontDefault);
            layout.setConstraints(profileLabel, constraints);
            panel.add(profileLabel);

            // User name field (1, 0).
            constraints.gridx      = 1;
            constraints.gridy      = 0;
            constraints.fill       = GridBagConstraints.HORIZONTAL;
            constraints.anchor     = GridBagConstraints.WEST;
            constraints.weightx    = 1.00d;
            userNameField = new ControlledTextField(TEXT_FIELD_COLUMNS);
            userNameField.setLimit(value.lengthUsername);
            userNameField.setFont(value.fontDefault);
            userNameField.setText(value.username);
            userNameField.setEditable(value.memberEditableName);
            layout.setConstraints(userNameField, constraints);
            panel.add(userNameField);

            if (value.member) {
                // Member password field (1, 1).
                constraints.gridy      = 1;
                userPasswordField = new ControlledTextField(TEXT_FIELD_COLUMNS);
                userPasswordField.setFont(value.fontDefault);
                userPasswordField.setEchoChar('*');
                userPasswordField.setText(value.password);
                layout.setConstraints(userPasswordField, constraints);
                panel.add(userPasswordField);
            }

            // Profile field (1, 1) or (1, 2).
            constraints.gridy      = value.member ? 2 : 1;
            constraints.gridwidth  = 2;
            profileField = new ControlledTextField(TEXT_FIELD_COLUMNS);
            profileField.setLimit(value.lengthProfile);
            profileField.setFont(value.fontDefault);
            profileField.setText(value.profile);
            profileField.setEditable(value.memberEditableProfile);
            layout.setConstraints(profileField, constraints);
            panel.add(profileField);

            // Join button (2, 0).
            constraints.gridx      = 2;
            constraints.gridy      = 0;
            constraints.gridwidth  = 1;
            constraints.gridheight = value.member ? 2 : 1;
            constraints.fill       = GridBagConstraints.NONE;
            constraints.weightx    = 0.00d;
            layout.setConstraints(button, constraints);
            panel.add(button);

            focusText.put(userNameField, value.textStatusFocusUsername);
            focusText.put(profileField,  value.textStatusFocusProfile);
            focusNext.put(userNameField, profileField);
            focusNext.put(profileField,  userNameField);
            focusPrev.put(userNameField, profileField);
            focusPrev.put(profileField,  userNameField);
        } else {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, INSET, INSET));
            panel.add(button);
        }

        add("North",  panel);
        add("Center", label);
    }

    /**
     * Set the keyboard focus on the correct sub-component.
     */

    protected void setFocus() {
        if (userNameField != null) {
            userNameField.requestFocus();
        } else if (button != null) {
            button.requestFocus();
        }
    }

    /**
     * Called when this applet is shown.
     * Request the keyboard focus here, since it's ignored if we request it
     * before the applet is shown (such as in the start method).
     */

    public void show() {
        super.show();
        setFocus();
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
        if (key == Build.TAB) {
            if (event.shiftDown()) {
                changeFocus(focusPrev, (Component) event.target);
            } else {
                changeFocus(focusNext, (Component) event.target);
            }
            return true;
        } else if (key == Build.NEW_LINE || key == Build.RETURN) {
            joinRoom();
            return true;
        }
        return false;
    }

    /**
     * Handles an action event from a component.
     *
     * @param event  the event information.
     * @param key    the argument associated with the event.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean action(Event event, Object arg) {
        joinRoom();
        return true;
    }

    /**
     * Handles all component events.
     *
     * @param event  the event information.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public synchronized boolean handleEvent(Event event) {
        boolean handled = false;
        if (event.id == Event.WINDOW_DESTROY) {
            handled = exit(event);
        }
        return handled ? true : super.handleEvent(event);
    }

    /**
     * Handles a window destroy event.
     *
     * @param event  the event information.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    private boolean exit(Event event) {
        if (event.target == frame) {
            close();
            closePrivate();
            label.setText(value.textButtonMessage);
            value.sounds.play(Sounds.STOP);
            URL exitPage = value.wasDisconnected ? value.pageExitError : value.pageExit;
            if (exitPage != null && value.applet.isActive()) {
                System.out.println("See " + exitPage);
                if (value.pageNewwindow) {
                    value.context.showDocument(exitPage, Build.TARGET);
                } else {
                    value.context.showDocument(exitPage);
                }
            }
            value.wasDisconnected = false;
            return true;
        } else if (event.target instanceof PrivateChat) {
            PrivateChat privateChat = (PrivateChat) event.target;
            privateList.remove(new Integer(privateChat.id()));
            value.sounds.play(Sounds.EXIT);
            return true;
        }
        return false;
    }

    /**
     * Closes the personal chat room window and its associated connection.
     */

    private void close() {
        if (frame instanceof PublicChat) {
            ((PublicChat) frame).close();
            frame = null;
        }
        connection.close();
        state = IDLE;
    }

    /**
     * Closes all private chat windows.
     */

    private synchronized void closePrivate() {
        Enumeration enumeration = privateList.elements();
        while (enumeration.hasMoreElements()) {
            PrivateChat privateChat = (PrivateChat) enumeration.nextElement();
            privateChat.close();
        }
    }

    /**
     * Joins the personal room if a user name is provided and the user is not
     * already in the room.
     */

    private void joinRoom() {
        if (userNameField != null) {
            if (value.member) {
                userName = userNameField.getText().trim();
                memberName = userName;
            } else {
                userName = truncate(userNameField.getText().replace(Build.NON_BREAKING_SPACE, ' ').trim(), value.lengthUsername);
            }
        }
        if (userPasswordField != null) {
            memberPassword = userPasswordField.getText().trim();
        }
        if (profileField != null) {
            profile = truncate(profileField.getText().trim(), value.lengthProfile);
        }
        if (canJoin(userName)) {
            synchronized (this) {
                buttonPushed = true;
                notify();
            }
        }
    }

    /**
     * Checks whether users can enter the room.
     *
     * @param userName  the user name provided.
     * @return <code>true</code> if the user can join the room; <code>false</code>
     *         if the user is already in the room or the user name is an empty
     *         string.
     */

    private boolean canJoin(String userName) {
        if (userName.length() == 0) {
            // label.setText(Message.format(value.textStatusEntername, value.publicRoom ? value.group : value.documentBase.toString()));
            label.setText(Message.format(value.textStatusEntername, value.title));
            if (userNameField != null) {
                userNameField.requestFocus();
            }
        } else if (frame != null) {
            // label.setText(Message.format(value.textStatusAlreadyinroom, value.group));
            label.setText(Message.format(value.textStatusAlreadyinroom, value.title));
            frame.requestFocus();
        } else {
            return true;
        }

        return false;
    }

    /**
     * Called when a packet is received from the server.
     *
     * @param observable  the connection to the server.
     * @param object      the packet received from the server.
     */

    public synchronized void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("MyVolanoChat update ...");
        }

        if (object instanceof Packet) {
            Packet packet = (Packet) object;
            if (! packet.isHandled()) {

                // Check the packet type so that we don't try to process an EnterRoom
                // indication (when entering an event auditorium) as an EnterRoom
                // confirm.
                Connection connection = (Connection) observable;
                switch (packet.getType()) {
                case Packet.CONFIRM:
                    if (packet instanceof EnterPrivate) {
                        enterPrivate(connection, (EnterPrivate) packet);
                    } else if (packet instanceof EnterRoom) {
                        enterRoomConfirm(connection, (EnterRoom) packet);
                    } else if (object instanceof Access) {
                        accessConfirm(connection, (Access) packet);
                    } else if (object instanceof Authenticate) {
                        authenticateConfirm(connection, (Authenticate) object);
                    } else {
                        super.update(observable, object);
                    }
                    break;
                case Packet.INDICATION:
                    if (packet instanceof EnterPrivate) {
                        enterPrivate(connection, (EnterPrivate) packet);
                    } else {
                        super.update(observable, object);
                    }
                    break;
                default:
                    super.update(observable, object);
                    break;
                }
            }
        } else {
            super.update(observable, object);
        }
    }

    /**
     * Handles an access confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the access confirmation.
     */

    private void accessConfirm(Connection connection, Access confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println("MyVolanoChat accessConfirm ...");
        }

        int result = confirm.getResult();
        if (result == Access.OKAY) {
            // Use the real room name here -- not the "title" alias.
            // The real room name is defined by "group" when public
            // or by the document base otherwise.
            roomName = value.publicRoom ? value.group : value.documentBase.toString();
            byte[] bytes = confirm.getBytes();
            if (bytes.length == 0) {      // No authenticate challenge from server
                send(connection, new EnterRoom(roomName, userName, profile));
                // label.setText(Message.format(value.textStatusEnteringroom, value.group));
                label.setText(Message.format(value.textStatusEnteringroom, value.title));
                state = CONNECTED;
            } else {
                label.setText(value.textButtonAuthenticating);
                byte[] signature = sign(bytes);
                send(connection, new Authenticate(signature));
                if (signature.length == 0) {   // Missing java.security package
                    showError(value.pageJavaVersion);
                }
            }
        } else {
            accessDenied(result);    // Display error Web page
        }
    }

    /**
     * Handles an authenticate confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the authenticate confirmation.
     */

    private void authenticateConfirm(Connection connection, Authenticate confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println("MyVolanoChat authenticateConfirm ...");
        }

        int result = confirm.getResult();
        if (result == Authenticate.OKAY) {
            send(connection, new EnterRoom(roomName, userName, profile));
            // label.setText(Message.format(value.textStatusEnteringroom, value.group));
            label.setText(Message.format(value.textStatusEnteringroom, value.title));
            state = CONNECTED;
        }
    }

    /**
     * Handles an enter room confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the enter room confirmation.
     */

    private void enterRoomConfirm(Connection connection, EnterRoom confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println("MyVolanoChat enterRoomConfirm ...");
        }

        String userName = confirm.getUserName();
        int    result   = confirm.getResult();
        if (result == EnterRoom.OKAY) {
            frame = new PublicChat(connection, this, value, confirm.getUsers(), confirm.getRoomName(), confirm.getRoomType(),
                                   confirm.getCount(), userName, confirm.getProfile(), privateList);
            // frame.setTitle(value.group + " (" + userName + ")");
            frame.setTitle(value.title + " (" + userName + ")");
            RoomFrame roomFrame = (RoomFrame) frame;
            roomFrame.setTheme(value.getTheme(Theme.DEFAULT));
            roomFrame.showFont(true);
            frame.pack();
            frame.setVisible(true);
            label.setText("");
            value.sounds.play(Sounds.ENTER);
        } else if (result == EnterRoom.ROOM_FULL)
            // label.setText(Message.format(value.textStatusRoomfull, value.group));
        {
            label.setText(Message.format(value.textStatusRoomfull, value.title));
        } else if (result == EnterRoom.NAME_TAKEN) {
            Vector subList = new Vector(2);
            subList.addElement(userName);
            // subList.addElement(value.group);
            subList.addElement(value.title);
            label.setText(Message.format(value.textStatusNametaken, subList));
        } else if (result == EnterRoom.MEMBER_TAKEN) {
            label.setText(Message.format(value.textStatusMembertaken, userName));
        } else if (result == EnterRoom.NO_SUCH_ROOM) {
            label.setText(value.textStatusNosuchroom);
        }

        if (result != EnterRoom.OKAY) {
            close();
        }
    }

    /**
     * Handles an enter private indication or confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the enter private indication or confirmation.
     */

    private void enterPrivate(Connection connection, EnterPrivate packet) {
        if (Build.UPDATE_TRACE) {
            System.out.println("MyVolanoChat enterPrivate ...");
        }

        int         roomId = packet.getRoomId();
        PrivateChat window = (PrivateChat) privateList.get(new Integer(roomId));
        if (window != null) {
            window.requestFocus();
        } else {
            String toName   = packet.getToName();
            String fromName = packet.getFromName();
            PublicChat room = (PublicChat) frame;
            if (room != null) {
                if (packet.getType() == Packet.INDICATION) {
                    String[] user = room.getUser(fromName);
                    if (user == null || ! room.isAcceptingPrivate() || room.isIgnored(fromName) ||
                            (value.limitPrivate != -1 && privateList.size() >= value.limitPrivate)) {
                        send(connection, new ExitPrivate(roomId, toName));
                    } else {
                        String title = fromName + " (" + toName + ")";
                        openPrivateWindow(title, roomId, toName, user);
                    }
                } else {        // Packet.CONFIRM
                    String[] user = room.getUser(toName);
                    if (user == null) {
                        send(connection, new ExitPrivate(roomId, fromName));
                    } else {
                        String title = toName + " (" + fromName + ")";
                        openPrivateWindow(title, roomId, fromName, user);
                    }
                }
            }
        }
    }

    /**
     * Opens a private chat room window.
     *
     * @param title         the text for the window title bar.
     * @param roomId        the private chat session identifier.
     * @param myName        the name of this user.
     * @param theirName     the name of the other user.
     * @param theirHost     the name or IP address of the other user's host.
     * @param theirProfile  the profile of the other user.
     */

    private void openPrivateWindow(String title, int roomId, String myName, String[] user) {
        RoomFrame frame = new PrivateChat(connection, this, value, roomId, myName, user);
        privateList.put(new Integer(roomId), frame);
        frame.setTitle(title);
        RoomFrame roomFrame = (RoomFrame) frame;
        roomFrame.setTheme(value.getTheme(Theme.DEFAULT));
        roomFrame.showFont(true);
        frame.pack();
        frame.setVisible(true);
        value.sounds.play(Sounds.ENTER);
    }

    /**
     * Changes focus to the previous or next user interface component.
     *
     * @param table    the hashtable for accessing the previous or next component.
     * @param current  the current component.
     */

    private void changeFocus(Hashtable table, Component current) {
        Component component = (Component) table.get(current);
        if (component == null) {
            component = userNameField;
        }
        if (component != null) {
            label.setText((String) focusText.get(component));
            component.requestFocus();
        }
    }
}
