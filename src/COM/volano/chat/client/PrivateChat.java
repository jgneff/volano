/*
 * PrivateChat.java - a frame for private chatting.
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
import  java.util.*;

/**
 * This class is a private chat room window.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class PrivateChat extends RoomFrame {
    private int      groupId;
    private String   myName;
    private String[] user;
    private boolean  alone = false;
    private RoomMenu    roomMenu;
    private HelpMenu    helpMenu;

    /**
     * Creates a new private chat room window.
     *
     * @param connection    the connection to the chat server.
     * @param owner         the owner of this frame, so that WINDOW_DESTROY events
     *                      can be proprogated to the owning component.
     * @param value         the applet parameter and property values.
     * @param groupId       the identifier of this private chat session.
     * @param myName        the name of this user.
     * @param user          the user information for the other user.
     */

    public PrivateChat(Connection connection, Component owner, Value value,
                       int groupId, String myName, String[] user) {
        super(connection, owner, value);
        this.groupId   = groupId;
        this.myName    = myName;
        this.user      = user;

        showProfile(value, user, myName);

        Panel southPanel = new Panel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add("North", talkText);
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

        MenuBar menuBar = new MenuBar();
        roomMenu    = new RoomMenu(value);
        helpMenu    = new HelpMenu(value);

        if (value.textMenuRoom.length() > 0) {
            menuBar.add(roomMenu);
        }
        if (value.textMenuHelp.length() > 0) {
            menuBar.add(helpMenu);
        }
        if (menuBar.getMenuCount() > 0) {
            setMenuBar(menuBar);
        }

        focusNext.put(listenText, talkText);
        focusNext.put(talkText, status);
        focusNext.put(status, listenText);
        focusPrev.put(listenText, status);
        focusPrev.put(status, talkText);
        focusPrev.put(talkText, listenText);
    }

    /**
     * Gets the identifier of this private chat session.
     *
     * @return  the private chat room identifier.
     */

    public int id() {
        return groupId;
    }

    /**
     * Closes this frame, sending an exit private request to the server.
     */

    public void close() {
        super.close();
        send(new ExitPrivate(groupId, myName));
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
        if (event.target == roomMenu.closeMenuItem) {
            event.id     = Event.WINDOW_DESTROY;
            event.target = this;
            return handleWindowDestroy(event);
        } else if (event.target == helpMenu.topicsMenuItem) {
            value.context.showDocument(value.pageHelp, Build.TARGET);
            return true;
        } else if (event.target == helpMenu.aboutMenuItem) {
            value.context.showDocument(value.pageAbout, Build.TARGET);
            return true;
        }
        return false;
    }

    /**
     * Sends the chat text to the server.
     *
     * @param text  the text to send.
     */

    protected void sendText(String text) {
        // Don't let user ramble if alone or disconnected and don't send empty text.
        if (! alone && connected && text.trim().length() != 0) {
            send(new Chat(groupId, myName, prepareText(text, true)));
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
            System.out.println("Private " + groupId + " update ...");
        }

        if (object instanceof PrivatePacket && object instanceof Packet) {
            PrivatePacket privatePacket = (PrivatePacket) object;
            Packet        packet        = (Packet) object;
            if (privatePacket.getRoomId() == groupId && packet.getType() == Packet.INDICATION) {
                Connection connection = (Connection) observable;
                if (packet instanceof Chat) {
                    chatIndication(connection, (Chat) packet);
                } else if (packet instanceof ExitPrivate) {
                    exitPrivateIndication(connection, (ExitPrivate) packet);
                }
            }
        } else if (object == null) {
            handleDisconnect();
        }
    }

    /**
     * Handles a chat indication.
     *
     * @param connection  the connection to the server.
     * @param confirm     the chat indication.
     */

    private void chatIndication(Connection connection, Chat packet) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + groupId + " chatIndication ...");
        }

        packet.setHandled();
        listenText.append("<" + packet.getUserName() + "> " + substitute(packet.getText()));
    }

    /**
     * Handles an exit private indication.
     *
     * @param connection  the connection to the server.
     * @param confirm     the exit private indication.
     */

    private void exitPrivateIndication(Connection connection, ExitPrivate packet) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + groupId + " exitPrivateIndication ...");
        }

        packet.setHandled();
        alone = true;               // Mark alone
        Vector subList = new Vector(2);
        subList.addElement(new Date());
        subList.addElement(user[RoomPacket.NAME]);
        listenText.append(Message.format(value.textSystemPartnerleft, subList));
    }
}
