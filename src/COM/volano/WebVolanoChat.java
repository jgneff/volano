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

package COM.volano;

import java.applet.Applet;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import COM.volano.IClient;
import COM.volano.Server;
import COM.volano.chat.client.*;
import COM.volano.chat.packet.*;
import COM.volano.chat.Build;
import COM.volano.util.*;
import COM.volano.awt.*;

/**
 *  WebVolanoChat applet.  Acts as the broker between AWT events and
 *  VolanoChat network events.
 *
 *  <P>At any given time, the applet will contain only one of only two
 *  possible panels:  An interrogation panel defined by WebMain,
 *  or a public room panel defined by WebPublicRoom.
 *
 *  <P>The applet can exist in three modes:  Interrogation mode,
 *  chat mode, and error mode.  The private mode field determines which
 *  mode the applet is in.
 *
 *  <P>This class also defines some static utility methods.
 */
public class WebVolanoChat extends Applet implements IClient,Runnable {

    // The capacity of the text areas.
    final public static int CAPACITY = 25000;

    final private static String BLANK = "";
    final private static char ECHO_CHAR = '*';  // for password fields

    // constants for mode of operation
    final private static int INTERROGATION_MODE = 0;
    final private static int CHAT_MODE          = 1;
    final private static int ERROR_MODE         = 2;
    // the applet is in "virgin mode" during its init() method.
    final private static int VIRGIN             = -1; //

    private static boolean infoPrinted = false;

    // references to the original values for username, password and
    // profile; these values need to be remembered since the user can
    // go through interrogation mode multiple times.
    private String originalUsername;
    private String originalPassword;
    private String originalProfile;

    private String requestedPrivateChat = "";

    // variables for widgets
    private GridBagLayout layout = new GridBagLayout();
    private WebMain main;
    private WebPublicRoom room;

    // variables to track internal state
    private Server server;       // Local representation of VolanoChat server.
    private Value value;         // Most of the state we need to remember.
    private Sounds sounds;       // A quick sound
    private int mode = VIRGIN;   // the mode of the applet.

    // Maps the server-id of a private chat room to its WebPrivateRoom object.
    private Hashtable privateRooms = new Hashtable();

    // used during interrogation mode to keep track of the host and port;
    // utilized to display error messages if a connection with the server
    // cannot be negotiated.
    private Vector hostAndPort;

    // The panel that should display status messages during access and
    // enter room attempts.
    private WebStatusPanel status;

    // Thread for accessing server.
    private boolean doneInterrogating;
    private Thread  thread;

    // For flood control in message text field.
    private Object messageLock  = new Object();
    private int    messageCount = 0;

    /**
     * Gets information about this applet.
     *
     * @return a string giving the applet name, version, copyright, and Web
     *         address.
     */

    public String getAppletInfo() {
        return getClass().getName() + " " + Build.VERSION + " " + Build.VOLANO_URL + "\n" +
               Build.APPLET_COPYRIGHT;
    }

    /**
     * Sets the delay pauses configured for each request type.
     *
     * @param value the applet property values.
     */

    private void setPauses(Value value) {
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
        RoomList.setWritePause(value.delayRoomList);
        UserList.setWritePause(value.delayUserList);
    }

    /**
     *  Initializes the applet.
     *  <P>This method constructs the Value and Sounds objects for the
     *  applet.  It is also the only place where originalUsername,
     *  originalPassword, and originalProfile are assigned.  Finally,
     *  it assigns the background color of the applet to
     *  <CODE>Value.color</CODE>.
     */
    public void init() {
        if (! infoPrinted) {
            System.out.println(getAppletInfo());
            infoPrinted = true;
        }
        setLayout(layout);
        value = new Value(this);
        setPauses(value);
        sounds = new Sounds(value);
        originalUsername = value.username;
        originalPassword = value.password;
        originalProfile = value.profile;
        /*
            if (value.group.equals(BLANK)) {
              // If no room name is specified, abort.
              mode = ERROR;
              return;
            }
        */
        setBackground(value.color);
    }


    /**
     *  Starts the applet.
     *  <P>Plays a sound, and if the applet has never been started before,
     *  enters interrogation mode.
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this, getClass().getName());
            thread.start();
        }
        sounds.play(Sounds.START);
        if (mode == VIRGIN) {
            interrogationMode();
        }
    }


    /**
     *  Stops the applet.
     *  <P>Plays a sound, and if the applet is in chat mode, switches
     *  to error mode.
     */

    // Disconnect only when the applet is destroyed, since the stop method is
    // called even when the applet's browser window is minimized.
    // NO -- we must disconnect when the applet is stopped, otherwise you pretty
    // much have to quit Netscape in order to have the person removed from the
    // room.  Internet Explorer 5.0 stops and destroys the applet as soon as you
    // leave its page.  Netscape Communicator 4.7 stops the applet as soon as you
    // leave its page, but calls destroy much later (if ever).  See:
    //   http://developer.netscape.com/docs/technote/java/appletlife.html
    //   http://java.sun.com/docs/books/tutorial/applet/overview/lifeCycle.html
    // public void destroy() {
    public void stop() {
        if (thread != null) {
            Thread temp = thread;
            thread = null;
            // Thread.interrupt throws java.lang.NoSuchMethodError using Java 1.021 on
            // Netscape 2.02. (version 2.2)
            try {
                temp.interrupt();
            } catch (Throwable t) {}
        }
        sounds.play(Sounds.STOP);
        if (mode == CHAT_MODE) {
            errorMode();
        } // what if we're interrogating...?
        else {                      // jgn
            disconnect();             // jgn
        }
    }

    /**
     *  Tells our thread to go connect to the server.
     */
    private synchronized void goConnect() {
        doneInterrogating = true;
        notify();
    }


    /**
     *  Connects to the VolanoChat server.
     *  <P>This needs to be a Runnable because Internet Explorer raises
     *  a SecurityException if new ThreadGroups are created within the
     *  AWT event thread (?).
     */
    public synchronized void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (thread == thisThread) {
                while (! doneInterrogating) {
                    wait();
                }
                doneInterrogating = false;
                if (value.group.equals(BLANK)) {
                    status.setStatus("Missing \"" + Key.GROUP + "\" applet parameter. Check HTML source.");
                } else {
                    connect();
                }
            }
        } catch (InterruptedException e) {} // Caught when applet stopped
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
    public void add(Component c, int x, int y, int w, boolean h, boolean v) {
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
     *  Handles special AWT events of interest.
     *  <P>Specifically:
     *  <UL><LI><CODE>WINDOW_DESTROY</CODE> events for private rooms;
     *  <LI><CODE>ACTION_EVENT</CODE> events for private rooms.
     *  </UL>
     */
    public boolean handleEvent(Event ev) {
        if (ev.target instanceof WebPrivateRoom) {
            WebPrivateRoom p = (WebPrivateRoom)ev.target;
            switch (ev.id) {
            case Event.WINDOW_DESTROY:
                dispose(p.getRoomId());
                return true;
            case Event.ACTION_EVENT:
                String s = truncate(p.getChatText(), value.lengthChattext);
                p.appendChatText(s);
                try {
                    server.chat(p.getRoomId(), value.username, s);
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMode();
                }
                return true;
            default:
                return false;
            }
        } else {
            return super.handleEvent(ev);
        }
    }

    /**
     *  Handles action events that have propagated to this container.
     *  <P>If we're in interrogation mode, an action event means that
     *  the user has pressed enter on the main panel, and all of the
     *  fields are valid in the main panel.
     *
     *  <P>If we're chat mode, either the user has double clicked on a
     *  user in the user list, or the user has pressed enter to send text
     *  to the server.
     *
     *  <P>If we're in error mode, the user has clicked the "reconnect" button.
     */
    public boolean action(Event ev, Object what) {
        switch (mode) {
        case INTERROGATION_MODE:
            if (ev.target instanceof Button) {
                value.username = main.getUsername();
                value.password = main.getPassword();
                value.profile = main.getProfile();
                // new Thread(this).start(); // ie, connect();
                // System.out.println("action event = " + ev + ", object = " + what);
                goConnect();
                return true;
            }
            break;
        case CHAT_MODE:
            if (ev.target instanceof TextField) {
                if (messageCount <= value.unconfirmedChat) {
                    String s = truncate(room.getChatText(), value.lengthChattext);
                    // Don't post messages into moderated events.
                    if (room.getType() == IClient.ROOM_EVENT) {
                        room.setStatus(value.textChatEventSent);
                    } else {
                        room.appendChatText(s);
                    }
                    try {
                        synchronized (messageLock) {
                            server.chat(value.group, value.username, s);
                            if (++messageCount > value.unconfirmedChat) {
                                room.setInactive();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        errorMode();
                    }
                }
                return true;
            }
            if (ev.target instanceof List) {
                try {
                    String to = ((List)ev.target).getSelectedItem();
                    if (! value.sendPrivateDisable && ! to.equals(value.username)) {    // jgn - no self private chat
                        // A value of -1 means no limit on the number of private chat rooms.
                        if (value.limitPrivate != -1 && privateRooms.size() >= value.limitPrivate) {
                            status.setStatus(Message.format(value.textStatusPrivatelimit, new Integer(value.limitPrivate)));
                        } else {
                            requestedPrivateChat = to;
                            status.setStatus(Message.format(value.textStatusEnteringprivate, to));
                            server.enterPrivate(value.group, value.username, to);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMode();
                }
                return true;
            }
            break;
        case ERROR_MODE:
            if (ev.target instanceof Button) {
                interrogationMode();
                return true;
            }
            break;
        }
        return false;
    }


    /**
     *  Connects to the server, and accesses it.
     *  <P>This actually winds up being complicated enough to warrant its
     *  own method.
     */
    private void connect() {
        status.setEnabled(false);
        // Unfortunately, since the host and port information needs to be
        // displayed in the event of an error, and since error messages can
        // only be displayed in response to an external event, the hostAndPort
        // must be global...
        hostAndPort = new Vector();
        hostAndPort.addElement(value.codeHost);
        hostAndPort.addElement(new Integer(value.serverPort));
        status.setStatus(Message.format(value.textButtonConnecting, hostAndPort));
        try {
            disconnect();             // jgn - disconnect if already connected
            server = new Server(this, value.codeHost, value.serverPort);
            status.setStatus(value.textButtonAccessing);
            if (value.member)
                server.access(value.group,      // jgn - create room if it doesn't exist
                              Server.APPLET_PUBLIC,
                              value.documentBase.toExternalForm(),
                              value.codeBase.toExternalForm(),
                              value.javaVendor,
                              value.javaVendorUrl,
                              value.javaVersion,
                              value.javaClassVersion,
                              value.osName,
                              value.osVersion,
                              value.osArch,
                              value.member,
                              false,        // monitors not allowed
                              false,        // administrators not allowed
                              value.username,
                              value.password,
                              BLANK,        // monitor or administrator password
                              false,        // is a stage entrance
                              BLANK);       // topic for stage entrance
            else
                server.access(value.group,      // jgn - create room if it doesn't exist
                              Server.APPLET_PUBLIC,
                              value.documentBase.toExternalForm(),
                              value.codeBase.toExternalForm(),
                              value.javaVendor,
                              value.javaVendorUrl,
                              value.javaVersion,
                              value.javaClassVersion,
                              value.osName,
                              value.osVersion,
                              value.osArch);
        } catch (IOException e) {
            e.printStackTrace();
            errorConnecting(value.pageAccessUnable);
        }
    }

    /**
     *  Disconnects from the server - new method by JGN.
     */
    private void disconnect() {
        if (server != null) {               // If already connected
            try {
                server.close();                 // Close the connection
                server = null;                  // Mark the connection closed
            } catch (IOException e) {}
        }
    }


    /**
     *  Shows the given URL and displays an error message in the status panel.
     *
     *  @param url  The url to display in the user's web browser.
     */
    private void errorConnecting(URL url) {
        System.err.println("See " + url);
        status.setStatus(Message.format(value.textButtonNotconnected, hostAndPort));
        status.setEnabled(true);
        // server = null; jgn
        if (value.pageNewwindow) {
            value.context.showDocument(url, Build.TARGET);
        } else {
            value.context.showDocument(url);
        }
        if (status == room) {
            errorMode();
        } else {
            disconnect();    // jgn - disconnect if still connected
        }
        return;
    }


    /**
     *  Adds a new public room to this container.
     *  <P>If an interrogation panel is onscreen, this method will
     *  remove it before adding the public room.
     */
    private void addPublicRoom() {
        removeAll();
        main = null;
        room = new WebPublicRoom(value, sounds);
        add(room, 0, 0, 1, true, true);
        invalidate();
        validate();
        status = room;
    }


    /**
     *  Adds a new interrogation panel to this container.
     *  <P>If a public room is onscreen, this method will remove it
     *  before adding the interrogation panel.
     */
    private void addMain() {
        removeAll();
        room = null;
        main = new WebMain(value);
        add(main, 0, 0, 2, true, false);
        invalidate();
        validate();
        main.setEditableName(value.memberEditableName);
        // main.setEditablePassword(originalPassword.equals(BLANK));
        main.setEditableProfile(value.memberEditableProfile);
        status = main;
    }


    /**
     *  Enters interrogation mode.
     *  <P>Note that we might leave interrogation mode just as quickly,
     *  if the values of <CODE>originalUsername</CODE>, <CODE>originalPassword</CODE>,
     *  and <CODE>originalProfile</CODE> are preset.  If all the relevant
     *  values are present, no interrogation panel will ever be added,
     *  and all status messages will be displayed on the public room's status
     *  label.
     */
    private void interrogationMode() {
        mode = INTERROGATION_MODE;
        if (value.member) {
            if (value.memberEditableProfile) {
                if (originalUsername.equals(BLANK) || originalPassword.equals(BLANK) || originalProfile.equals(BLANK)) {
                    addMain();
                } else {
                    addPublicRoom();
                    goConnect();
                }
            } else {
                if (originalUsername.equals(BLANK) || originalPassword.equals(BLANK)) {
                    addMain();
                } else {
                    addPublicRoom();
                    goConnect();
                }
            }
        } else {
            if (originalUsername.equals(BLANK) || originalProfile.equals(BLANK)) {
                addMain();
            } else {
                addPublicRoom();
                goConnect();
            }
        }
    }


    /**
     *  Enters chat mode.
     *
     *  @param users  The list of users in the public room.
     */
    private void chatMode(String roomName, int roomType, int count, String[][] users) {
        mode = CHAT_MODE;
        // The room may already have been created, if we skipped
        // the interrogation panel.
        if (room == null) {
            addPublicRoom();
        }
        room.setStatus(value.textChatStatus);
        room.setInfo(roomName, roomType, count);
        room.setUserList(users);
    }


    /**
     *  Enters error mode.
     *  <P>This method severs the connect to the server, and broadcasts
     *  the disconnected message to all public and private rooms.
     */
    private void errorMode() {
        mode = ERROR_MODE;

        // jgn rewrite
        //try {
        //  server.exitRoom(value.group, value.username);
        //  server.close();
        //} catch (IOException e) {
        //} catch (NullPointerException e) {
        //}
        //server = null;

        if (server != null) {                               // jgn
            try {                                             // jgn
                server.exitRoom(value.group, value.username);   // jgn
            } catch (IOException e) {}                        // jgn
            disconnect();                                     // jgn
        }                                                   // jgn

        if (room != null) {
            broadcast(Message.format(value.textSystemDisconnected, new Date()));
            room.close();
        }
    }

    // IClient implementation begins here.

    /**
     *  Called when the server acknowledges an access request.
     *
     *  @param result  The result code of the access attempt.
     *  @param rooms   The public room list; ignored by this applet.
     *  @param profile The member profile of the user, or an empty string if the user is not a member.
     */
    public void access(int result, String[] rooms, String profile) {
        switch (result) {
        case ACCESS_OKAY:
            status.setStatus(Message.format(value.textStatusEnteringroom, value.group));
            try {
                // Use the profile returned by the membership access unless the member
                // provides a new profile when entering.
                if (value.member && (value.profile.length() == 0)) {
                    value.profile = profile;
                }
                // so far so good, now attempt to enter the public room...
                server.enterRoom(value.group, value.username, value.profile);
            } catch (IOException e) {
                e.printStackTrace();
                status.setStatus(Message.format(value.textButtonNotconnected, hostAndPort));
            }
            break;
        case ACCESS_HOST_DENIED:
            errorConnecting(value.pageAccessHost);
            break;
        case ACCESS_DOCUMENT_DENIED:
            errorConnecting(value.pageAccessDocument);
            break;
        case ACCESS_VERSION_DENIED:
            errorConnecting(value.pageAccessVersion);
            break;
        case ACCESS_BAD_PASSWORD:
            errorConnecting(value.pageAccessPassword);
            break;
        case ACCESS_BAD_JAVA_VERSION:
            errorConnecting(value.pageJavaVersion);
            break;
        case ACCESS_HOST_DUPLICATE:
            errorConnecting(value.pageAccessDuplicate);
            break;
        }
    }

    /**
     *  Ignored.
     *
     *  @param rooms Ignored.
     */
    public void roomList(String[] rooms) {
    }


    /**
     *  Ignored.
     *
     *  @param result Ignored.
     *  @param documentBase Ignored.
     *  @param users Ignored.
     */
    public void userList(int result, String documentBase, String[][] users) {
    }

    /**
     *  Invoked when the server acknowledges an enter room request.
     *
     *  @param result The result code of the enter attempt
     *  @param roomName The name of the room that is entered.
     *  @param roomType The type of the room.
     *  @param count    The count of people in the room.
     *  @param users    The list of visible users in the room.
     */
    public void enterRoom(int result, String roomName, int roomType, int count, String[][] users) {
        switch (result) {
        case ROOM_OKAY:
            sounds.play(Sounds.ENTER);
            status.setEnabled(true);
            chatMode(roomName, roomType, count, users);
            break;
        case ROOM_FULL:
            status.setStatus(Message.format(value.textStatusRoomfull, roomName));
            status.setEnabled(true);
            if (status == room) {
                errorMode();
            } else {
                disconnect();    // jgn - disconnect if still connected
            }
            break;
        case ROOM_MEMBER_TAKEN:
            status.setStatus(Message.format(value.textStatusMembertaken, value.username));
            status.setEnabled(true);
            if (status == room) {
                errorMode();
            } else {
                disconnect();    // jgn - disconnect if still connected
            }
            break;
        case ROOM_NAME_TAKEN:
            Vector vector = new Vector();
            vector.addElement(value.username);
            vector.addElement(roomName);
            status.setStatus(Message.format(value.textStatusNametaken, vector));
            status.setEnabled(true);
            if (status == room) {
                errorMode();
            } else {
                disconnect();    // jgn - disconnect if still connected
            }
            break;
        case ROOM_NOT_FOUND:
            status.setStatus(value.textStatusNosuchroom);
            status.setEnabled(true);
            if (status == room) {
                errorMode();
            } else {
                disconnect();    // jgn - disconnect if still connected
            }
            break;
        }
    }


    /**
     *  Invoked when a user enters the public room.
     *
     *  @param roomName Ignored.
     *  @param userName The name of the new user.
     *  @param profile  The profile of the new user.
     *  @param host     The host of the new user.
     *  @param isMember <CODE>true</CODE> if the new user is a member.
     */
    public void enterRoom(String roomName, String userName, String profile, String host, boolean isMember, boolean showLink) {
        if (userName.length() != 0) {       // If not audience member
            room.addUser(userName, profile, host, isMember, showLink);
        }
    }


    /**
     *  Invoked when a users exits the public room.
     *
     *  @param roomName Ignored.
     *  @param userName The name of the user who left.
     */
    public void exitRoom(String roomName, String userName) {
        room.removeUser(userName);
    }


    /**
     *  Invoked when the server acknowledges a private chat request.
     *
     *  @param roomId  The unique id of the private chat room.
     *  @param userName  The userName of the other user in the private chat room.
     *  @param profile The profile of the other user in the private chat room.
     *  @param address The host of the other user in the private chat room.
     */
    public void enterPrivate(int roomId, String userName, String profile, String address) {
        // 2.1.8 - Check the private chat limit to prevent this user from being
        // flooded by private chat requests.
        boolean allowPrivateChat = requestedPrivateChat.equals(userName) || value.acceptPrivateDefault;
        requestedPrivateChat = "";
        if (! allowPrivateChat || (value.limitPrivate != -1 && privateRooms.size() >= value.limitPrivate)) {
            try {
                server.exitPrivate(roomId, value.username);
            } catch (IOException e) {
                e.printStackTrace();
                errorMode();
            }
        } else {
            Integer I = new Integer(roomId);
            WebPrivateRoom p = new WebPrivateRoom(value, this);
            p.setTitle(userName + " (" + value.username + ")");
            p.setRoomId(roomId);
            p.setUsername(userName);
            privateRooms.put(I, p);
            p.pack();
            p.setVisible(true);
            status.setStatus(value.textChatStatus);
        }
    }


    /**
     *  Invoked when a user exits a private chat room.
     *
     *  @param roomId The unique Id of the private chat room.
     *  @param userName The username of the other user in the private chat room.
     */
    public void exitPrivate(int roomId, String userName) {
        WebPrivateRoom p = (WebPrivateRoom)privateRooms.get(new Integer(roomId));
        if (p != null) {
            Vector vector = new Vector();
            vector.addElement(new Date());
            vector.addElement(p.getUsername());
            p.append(Message.format(value.textSystemPartnerleft, vector));
            p.setOpen(false);
        }
    }

    /**
     *  Invoked to confirm a chat message.
     *
     *  @param roomName Ignored.
     */
    public void chatConfirm(String roomName) {
        synchronized (messageLock) {
            if (--messageCount <= value.unconfirmedChat) {
                room.setActive();
            }
        }
    }


    /**
     *  Invoked when a user chats in the public chat room.
     *
     *  @param roomName Ignored.
     *  @param userName The name of the user who sent the text.
     *  @param text The chat text.
     */
    public void chat(String roomName, String userName, String text) {
        room.appendChatText(userName, text);
    }


    /**
     *  Invoked when a user chats in a private chat room.
     *
     *  @param roomId  The unique id of the private chat room.
     *  @param userName  The username of the user who sent the text.
     *  @param text  The chat text.
     */
    public void chat(int roomId, String userName, String text) {
        WebPrivateRoom p = (WebPrivateRoom)privateRooms.get(new Integer(roomId));
        if (p != null) {
            p.appendChatText(userName, text);
        }
    }


    /**
     *  Invoked when broadcast text is sent.
     *
     *  @param text  The text that has been broadcast.
     */
    public void chat(String text) {
        Vector vector = new Vector();
        vector.addElement(new Date());
        vector.addElement(text);
        broadcast(Message.format(value.textSystemBroadcast, vector));
    }

    /**
     *  Invoked when a user whispers to you.
     *
     *  @param roomName  Ignored.
     *  @param userName  The name of the user who whispered.
     *  @param text      The whisper text.
     */
    public void whisper(String roomName, String userName, String text) {
        room.appendChatText(userName, text);
    }

    /**
     *  Invoked when a user beeps you.
     *
     *  @param roomName  Ignored.
     *  @param userName  The name of the user who beeped.
     */
    public void beep(String roomName, String userName) {
        if (value.alertAudioDefault) {
            Vector vector = new Vector();
            vector.addElement(new Date());
            vector.addElement(userName);
            broadcast(Message.format(value.textSystemAudio, vector));
            sounds.alert();
        }
    }


    /**
     *  Invoked when the server closes the connection.
     */
    public void close() {
        sounds.play(Sounds.EXIT);
        if (mode != ERROR_MODE) {
            errorMode();
        }
    }

    // End IClient implementation.


    /**
     *  Broadcasts the given message to the public room and all private rooms.
     *
     *  @param msg  The message to broadcast.
     */
    public void broadcast(String msg) {
        if (room != null) {
            room.append(msg);
        }
        for (Enumeration e = privateRooms.elements(); e.hasMoreElements();) {
            WebPrivateRoom p = (WebPrivateRoom)e.nextElement();
            p.append(msg);
        }
    }


    /**
     *  Disposes of a private chat room.
     *
     *  @param roomId  The roomId of the private chat room.
     */
    public void dispose(int roomId) {
        WebPrivateRoom p = (WebPrivateRoom)privateRooms.remove(new Integer(roomId));
        if (p.isOpen()) {
            try {
                server.exitPrivate(roomId, value.username);
            } catch (IOException e) {
                e.printStackTrace();
                errorMode();
            }
        }
        if (p != null) {
            p.setVisible(false);
            p.dispose();
        }
    }

    // Begin static utility methods.

    /**
     *  Utility method for setting the colors and fonts of various panels.
     *
     *  @param c  The component whose colors and fonts should be sent.
     *  @param value  The <CODE>Value</CODE> object that defines the colors and font.
     */
    /*
      public static void setColorsAndFont(Component c, Value value) {
        if (value.colorBackground != null) {
          c.setBackground(value.colorBackground);
        }
        if (value.colorForeground != null) {
          c.setForeground(value.colorForeground);
        }
        if (value.fontDefault != null) {
          c.setFont(value.fontDefault);
        }
      }
    */

    /**
     *  Truncates the given string to the given length.
     *  <P>If the given string is longer than the given length, this
     *  method returns the first <CODE>max</CODE> characters of the
     *  given string.  Otherwise, the original string is returned.
     *
     *  @param s    The string to truncate.
     *  @param max  The maximum number of characters in the string.
     *  @return  The truncated string, or the original string if no truncation ocurred.
     */
    public static String truncate(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.trim().substring(0, max);
    }


    /**
     *  Gets the macro list from the specified Value object.
     *
     *  @param value  The Value object that contains the macro list.
     *  @return The macros from the specified value object.
     */
    /*
      public static String[] getMacros(Value value) {
        String[] macros = new String[12];
        macros[0] = value.textF1;
        macros[1] = value.textF2;
        macros[2] = value.textF3;
        macros[3] = value.textF4;
        macros[4] = value.textF5;
        macros[5] = value.textF6;
        macros[6] = value.textF7;
        macros[7] = value.textF8;
        macros[8] = value.textF9;
        macros[9] = value.textF10;
        macros[10] = value.textF11;
        macros[11] = value.textF12;
        return macros;
      }
    */
}
