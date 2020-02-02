/*
 * VolanoChat.java - an applet for Web based chatting.
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
import  java.util.*;
// For stepping outside Netscape's Java sandbox.
// import netscape.security.PrivilegeManager;

/**
 * This is the main class for the VolanoChat client applet.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class VolanoChat extends AppletBase {
    private static final int MEMBER_FIELD_SIZE   = 32;
    private static final int PASSWORD_FIELD_SIZE = 16;

    private static boolean infoPrinted = false;

    private Label               memberNameLabel;
    private ControlledTextField memberNameField;
    private Label               memberPasswordLabel;
    private ControlledTextField memberPasswordField;
    private Label               passwordLabel;
    private ControlledTextField passwordField;

    // For saving the member profile field between the Access confirmation and the
    // Authenticate confirmation.
    private String    memberProfile = "";

    private Hashtable focusNext = new Hashtable();
    private Hashtable focusPrev = new Hashtable();
    private Hashtable focusText = new Hashtable();

    /**
     * Returns a packet factory for this applet.
     *
     * @return a packet factory for this applet.
     */

    protected PacketFactory getPacketFactory() {
        return new VolanoChatPacketFactory();
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
        RoomList.setWritePause(value.delayRoomList);
        UserList.setWritePause(value.delayUserList);
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
        Panel memberPanel = null;
        Panel panel       = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, INSET, INSET));

        memberName     = value.username;
        memberPassword = value.password;

        // Prompt for the member name and password if this is the member version of
        // the applet and at least one of them is not defined as a parameter.
        if (value.member && (value.username.length() == 0 || value.password.length() == 0)) {
            memberPanel = new Panel();
            GridBagLayout layout = new GridBagLayout();
            memberPanel.setLayout(layout);

            Insets             insets      = new Insets(INSET, INSET, INSET, INSET);
            GridBagConstraints constraints = new GridBagConstraints();

            // Label for member name field (0, 0).
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
            memberNameLabel = new Label(value.textMemberName, Label.RIGHT);
            memberNameLabel.setFont(value.fontDefault);
            layout.setConstraints(memberNameLabel, constraints);
            memberPanel.add(memberNameLabel);

            // Label for member password field (0, 1).
            constraints.gridy      = 1;
            memberPasswordLabel = new Label(value.textMemberPassword, Label.RIGHT);
            memberPasswordLabel.setFont(value.fontDefault);
            layout.setConstraints(memberPasswordLabel, constraints);
            memberPanel.add(memberPasswordLabel);

            // Member name field (1, 0).
            constraints.gridx      = 1;
            constraints.gridy      = 0;
            constraints.fill       = GridBagConstraints.HORIZONTAL;
            constraints.anchor     = GridBagConstraints.WEST;
            // constraints.weightx    = 1.00d;
            memberNameField = new ControlledTextField(MEMBER_FIELD_SIZE);
            memberNameField.setLimit(value.lengthUsername);
            memberNameField.setFont(value.fontDefault);
            memberNameField.setText(value.username);
            layout.setConstraints(memberNameField, constraints);
            memberPanel.add(memberNameField);

            // Member password field (1, 1).
            constraints.gridy      = 1;
            memberPasswordField = new ControlledTextField(MEMBER_FIELD_SIZE);
            memberPasswordField.setFont(value.fontDefault);
            memberPasswordField.setEchoChar('*');
            memberPasswordField.setText(value.password);
            layout.setConstraints(memberPasswordField, constraints);
            memberPanel.add(memberPasswordField);

            focusText.put(memberNameField,     value.textStatusFocusMembername);
            focusText.put(memberPasswordField, value.textStatusFocusMemberpassword);
            focusNext.put(memberNameField,     memberPasswordField);
            focusNext.put(memberPasswordField, memberNameField);
            focusPrev.put(memberNameField,     memberPasswordField);
            focusPrev.put(memberPasswordField, memberNameField);
        }

        if (value.admin || value.monitor) {
            passwordLabel = new Label("", Label.RIGHT);
            passwordField = new ControlledTextField(PASSWORD_FIELD_SIZE);
            passwordLabel.setFont(value.fontDefault);
            passwordField.setFont(value.fontDefault);
            if (value.admin) {
                passwordLabel.setText(value.textButtonAdmin);
            } else {
                passwordLabel.setText(value.textButtonMonitor);
            }
            passwordField.setEchoChar('*');
            panel.add(passwordLabel);
            panel.add(passwordField);
        }
        panel.add(button);

        if (memberPanel != null) {
            add("North",  memberPanel);
            add("Center", panel);
            add("South",  label);
        } else {
            add("North",  panel);
            add("Center", label);
        }
    }

    /**
     * Set the keyboard focus on the correct sub-component.
     */

    protected void setFocus() {
        if (memberNameField != null) {
            memberNameField.requestFocus();
        } else if (passwordField != null) {
            passwordField.requestFocus();
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
        }
        return false;
    }

    /**
     * Handles actions on the applet components.
     *
     * @param event  the event information.
     * @param arg    depends on the event type.
     * @return <code>true</code> if the event was handled; otherwise
     *         <code>false</code>.
     */

    public boolean action(Event event, Object arg) {
        if (memberNameField != null) {
            memberName = memberNameField.getText().trim();
            if (memberName.length() == 0) {
                label.setText(value.textStatusEntermembername);
                memberNameField.requestFocus();
                return false;
            }
        }
        if (memberPasswordField != null) {
            memberPassword = memberPasswordField.getText().trim();
            if (memberPassword.length() == 0) {
                label.setText(value.textStatusEntermemberpassword);
                memberPasswordField.requestFocus();
                return false;
            }
        }
        if (passwordField != null) {
            password = passwordField.getText().trim();
            if (password.length() == 0) {
                label.setText(value.textStatusEnterpassword);
                passwordField.requestFocus();
                return false;
            }
        }
        synchronized (this) {
            buttonPushed = true;
            notify();         // Notify main applet thread
        }
        return true;
    }

    /**
     * Called when a packet is received from the server.
     *
     * @param observable  the connection to the server.
     * @param object      the packet received from the server.
     */

    public synchronized void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("VolanoChat update ...");
        }

        if (object instanceof Access) {
            accessConfirm((Connection) observable, (Access) object);
        } else if (object instanceof Authenticate) {
            authenticateConfirm((Connection) observable, (Authenticate) object);
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
        if (Build.UPDATE_TRACE)
            // System.out.println("VolanoChat accessConfirm ...");
        {
            System.out.println("VolanoChat update (" + confirm + ") ...");
        }

        int result = confirm.getResult();
        if (result == Access.OKAY) {
            // For obtaining privileges outside the Netscape sandbox.
            // try {
            //   PrivilegeManager.enablePrivilege("UniversalTopLevelWindow");
            // }
            // catch (Throwable t) {
            //   printError("Unable to obtain privilege.", t);
            // }

            // String memberProfile = "";
            if (confirm instanceof PasswordAccess) {
                memberProfile = ((PasswordAccess) confirm).getProfile();
                // The value of "isMonitor" determines whether we display the remove,
                // kick, and ban menu items in the chat rooms, but the server will allow
                // such functions only on an administrator or monitor connection.
                if (value.admin || value.monitor || value.memberMonitor)
                    // value.monitor = true;
                {
                    value.isMonitor = true;    // 2.1.8
                }
            }

            byte[] bytes = confirm.getBytes();
            if (bytes.length == 0) {         // No authenticate challenge from server
                showFrame(connection, confirm.getRooms());
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
        if (Build.UPDATE_TRACE)
            // System.out.println("VolanoChat authenticateConfirm ...");
        {
            System.out.println("VolanoChat update (" + confirm + ") ...");
        }

        int result = confirm.getResult();
        if (result == Authenticate.OKAY) {
            showFrame(connection, confirm.getRooms());
        }
    }

    /**
     * Shows the VolanoChat applet main window frame.
     */

    private void showFrame(Connection connection, String[] rooms) {
        frame = new Client(connection, this, value, memberName, memberProfile, rooms);
        frame.setTitle(value.textMainTitle);
        frame.pack();
        frame.setVisible(true);
        value.sounds.play(Sounds.START);
        label.setText("");
        state = CONNECTED;
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
            component = memberNameField;
        }
        if (component != null) {
            label.setText((String) focusText.get(component));
            component.requestFocus();
        }
    }
}
