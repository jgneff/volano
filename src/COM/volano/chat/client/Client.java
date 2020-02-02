/*
 * Client.java - the main applet window.
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
import  COM.volano.util.Message;
import  java.applet.*;
import  java.awt.*;
import  java.io.*;
import  java.net.*;
import  java.util.Date;
import  java.util.Enumeration;
import  java.util.Hashtable;
import  java.util.Observable;
import  java.util.Observer;
import  java.util.Vector;

/**
 * This class serves as the main chat window for the VolanoChat client applet.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Client extends Frame implements Observer {
    private static final int    INSET            =  2;
    private static final int    TEXT_FIELD_WIDTH = 30;
    private static final double LOGO_WEIGHT      = 0.00d;
    private static final double GROUP_WEIGHT     = 0.75d;
    private static final double USER_WEIGHT      = 0.25d;
    private static final String URL_PREFIX       = "http://";

    private Object     enableLock = new Object(); // Lock for enable, disable

    private Connection connection;
    private Component  owner;
    private Value      value;
    private Theme      theme;

    private String     selectedRoom;
    private int        roomType = RoomPacket.UNKNOWN;
    private String     memberName;
    private String     memberProfile;

    private Hashtable  publicList  = new Hashtable();
    private Hashtable  privateList = new Hashtable();
    private Hashtable  focusNext   = new Hashtable();
    private Hashtable  focusPrev   = new Hashtable();
    private Hashtable  focusText   = new Hashtable();
    private String[][] users;     // User names, profiles, hosts, and whether they're members

    // Column 0.
    private Label      logoLabel;
    private Component  logo;
    private Label      filterLabel;
    private Label      userNameLabel;
    private Label      profileLabel;
    private Label      broadcastLabel;

    // Column 1.
    private Label      groupLabel;
    private List       groupList;
    private ControlledTextField filterField;
    private ControlledTextField userNameField;
    private ControlledTextField profileField;
    private ControlledTextField broadcastField;

    // Column 2.
    private Label      userLabel;
    private List       userList;
    private Button     filterButton;
    private Button     joinButton;

    // Border layout main components.
    private Panel      panel;
    private HyperLabel status;
    private HyperLabel label;

    // Menu bar.
    private PlacesMenu  placesMenu;
    private OptionsMenu optionsMenu;
    private ThemeMenu   themeMenu;
    private LinkMenu    linkMenu;
    private HelpMenu    helpMenu;

    /**
     * Truncates the string to the specified limit, trimming off any preceding
     * or trailing blanks.
     *
     * @param text   the text to be truncated and trimmed.
     * @param limit  the maximum number of characters to return.
     * @return  the truncated and trimmed text string.
     */

    private static String truncate(String text, int limit) {
        String truncated = text;
        if (text.length() > limit) {
            truncated = text.substring(0, limit).trim();
        }
        return truncated;
    }

    /**
     * Creates a new main client applet window.
     *
     * @param connection  the connection to the chat server.
     * @param owner       the owner of this frame, so that WINDOW_DESTROY events
     *                    can be proprogated to the owning component.
     * @param value       the applet parameter and property values.
     * @param roomNames   the list of rooms in the server.
     */

    public Client(Connection connection, Component owner, Value value,
                  String memberName, String memberProfile, String[] roomNames) {
        this.connection    = connection;
        this.owner         = owner;
        this.value         = value;
        this.memberName    = memberName.length()    == 0 ? value.username : memberName;
        this.memberProfile = memberProfile.length() == 0 ? value.profile  : memberProfile;
        this.selectedRoom  = value.group;

        status = new HyperLabel(value.context, value.linkPrefix);

        Insets        insets = new Insets(INSET, INSET, INSET, INSET);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel = new Panel();
        panel.setLayout(layout);

        // Column 0.

        // Label for logo (0, 0).
        constraints.gridx      = 0;                                 // Default RELATIVE
        constraints.gridy      = 0;                                 // Default RELATIVE
        // constraints.gridwidth  = 1;                              // Default 1
        // constraints.gridheight = 1;                              // Default 1
        // constraints.fill       = GridBagConstraints.NONE;        // Default NONE
        // constraints.ipadx      = 0;                              // Default 0
        // constraints.ipady      = 0;                              // Default 0
        constraints.insets     = insets;                            // Default (0, 0, 0, 0)
        constraints.anchor     = GridBagConstraints.EAST;           // Default CENTER
        constraints.weightx    = LOGO_WEIGHT;                       // Default 0
        // constraints.weighty    = 0.00d;                          // Default 0
        logoLabel = new Label();
        logoLabel.setAlignment(Label.LEFT);
        layout.setConstraints(logoLabel, constraints);
        panel.add(logoLabel);

        // Logo area (0, 1).
        constraints.gridy      = GridBagConstraints.RELATIVE;
        constraints.fill       = GridBagConstraints.VERTICAL;
        constraints.weighty    = 1.00d;
        if (value.logoCode.length() > 0)
            logo = new PlayerStub(value.applet, Key.LOGO_PARAM_PREFIX,
                                  value.logoCode, value.logoParameters,
                                  value.logoWidth, value.logoHeight);
        else {
            logo = new Logo();
        }
        layout.setConstraints(logo, constraints);
        panel.add(logo);

        // Constraints for labels in remaining rows of column 0.
        constraints.fill       = GridBagConstraints.NONE;
        constraints.weighty    = 0.00d;

        // Label for group name field (0, 2).
        if (value.filterEnable) {
            filterLabel = new Label();
            filterLabel.setAlignment(Label.RIGHT);
            layout.setConstraints(filterLabel, constraints);
            panel.add(filterLabel);
        }

        // Label for user name field (0, 3).
        userNameLabel = new Label();
        userNameLabel.setAlignment(Label.RIGHT);
        layout.setConstraints(userNameLabel, constraints);
        panel.add(userNameLabel);

        // Label for profile field (0, 4).
        profileLabel = new Label();
        profileLabel.setAlignment(Label.RIGHT);
        layout.setConstraints(profileLabel, constraints);
        panel.add(profileLabel);

        if (value.admin) {
            // Label for broadcast field (0, 5).
            broadcastLabel = new Label();
            broadcastLabel.setAlignment(Label.RIGHT);
            layout.setConstraints(broadcastLabel, constraints);
            panel.add(broadcastLabel);
        }

        // Column 1.

        // Label for group list (1, 0).
        constraints.gridx      = 1;
        constraints.gridy      = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.WEST;
        constraints.weightx    = GROUP_WEIGHT;
        groupLabel = new Label();
        groupLabel.setAlignment(Label.LEFT);
        layout.setConstraints(groupLabel, constraints);
        panel.add(groupLabel);

        // Group list (1, 1).
        constraints.gridy      = GridBagConstraints.RELATIVE;
        constraints.fill       = GridBagConstraints.BOTH;
        groupList = new List();
        layout.setConstraints(groupList, constraints);
        panel.add(groupList);

        // Group name field (1, 2).
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        if (value.filterEnable) {
            filterField = new ControlledTextField(TEXT_FIELD_WIDTH);
            filterField.setLimit(value.lengthRoomname);
            layout.setConstraints(filterField, constraints);
            panel.add(filterField);
        }

        // User name field (1, 3).
        userNameField = new ControlledTextField(TEXT_FIELD_WIDTH);
        userNameField.setLimit(value.lengthUsername);
        userNameField.setText(this.memberName);
        userNameField.setEditable(value.memberEditableName);
        layout.setConstraints(userNameField, constraints);
        panel.add(userNameField);

        // Profile field (1 - 2, 4).
        if (value.filterEnable) {
            constraints.gridwidth = 2;
        }
        profileField = new ControlledTextField(TEXT_FIELD_WIDTH);
        profileField.setLimit(value.lengthProfile);
        profileField.setText(this.memberProfile);
        profileField.setEditable(value.memberEditableProfile);
        layout.setConstraints(profileField, constraints);
        panel.add(profileField);

        if (value.admin) {
            // Broadcast field (1 - 2, 5).
            broadcastField = new ControlledTextField(TEXT_FIELD_WIDTH);
            layout.setConstraints(broadcastField, constraints);
            panel.add(broadcastField);
        }

        // Column 2.

        // Label for user list (2, 0).
        constraints.gridx      = 2;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.weightx    = USER_WEIGHT;
        userLabel = new Label();
        userLabel.setAlignment(Label.LEFT);
        layout.setConstraints(userLabel, constraints);
        panel.add(userLabel);

        // User list (2, 1).
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill  = GridBagConstraints.BOTH;
        userList = new List();
        layout.setConstraints(userList, constraints);
        panel.add(userList);

        // Filter button (2, 2).
        constraints.fill = GridBagConstraints.HORIZONTAL;
        filterButton = new Button();
        layout.setConstraints(filterButton, constraints);
        panel.add(filterButton);

        // Join button (2, 3).
        joinButton = new Button();
        layout.setConstraints(joinButton, constraints);
        panel.add(joinButton);

        setLayout(new BorderLayout(INSET, INSET));
        add("Center", panel);
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
            add("South", statusPanel);
        } else {
            add("South", status);
        }

        focusNext.put(groupList, userList);
        if (filterField != null) {
            focusNext.put(userList, filterField);
            focusNext.put(filterField, userNameField);
        } else {
            focusNext.put(userList, userNameField);
        }
        focusNext.put(userNameField, profileField);
        focusNext.put(profileField, filterButton);
        focusNext.put(filterButton, joinButton);
        focusNext.put(joinButton, status);
        focusNext.put(status, groupList);

        focusPrev.put(status, joinButton);
        focusPrev.put(joinButton, filterButton);
        focusPrev.put(filterButton, profileField);
        focusPrev.put(profileField, userNameField);
        if (filterField != null) {
            focusPrev.put(userNameField, filterField);
            focusPrev.put(filterField, userList);
        } else {
            focusPrev.put(userNameField, userList);
        }
        focusPrev.put(userList, groupList);
        focusPrev.put(groupList, status);

        setProperties(value);
        updateRoomList(roomNames);
        // Workaround for Bug Id 4853457:
        // REGRESSION: List setEnabled(true) does not redraw Items on Windows 2000
        // http://developer.java.sun.com/developer/bugParade/bugs/4853457.html
        // If we don't call it here, the room list changes appearance if you
        // select a room name (the width between list item lines gets smaller).
        groupList.setFont(groupList.getFont());
        value.wasDisconnected = false;
        connection.addObserver(this);
    }

    /**
     * Initializes with a new set of applet parameters and properties, redisplays
     *  this window with its new values.
     *
     * @param value  the new applet parameter and property values.
     */

    private void setProperties(Value value) {
        this.value = value;

        logoLabel.setText(value.textMainLogo);
        if (logo instanceof Logo) {
            Logo canvas = (Logo) logo;
            canvas.setLogo(value.imageLogo, value.imageLogoWidth, value.imageLogoHeight, value.imageLogoBackground);
        }
        if (filterLabel != null) {
            filterLabel.setText(value.textMainFilter);
        }
        userNameLabel.setText(value.textMainUsername);
        profileLabel.setText(value.textMainProfile);
        if (broadcastLabel != null) {
            broadcastLabel.setText(value.textMainBroadcast);
        }
        groupLabel.setText(value.textMainRooms);
        userLabel.setText(value.textMainUsers);
        filterButton.setLabel(value.textMainGetrooms);
        joinButton.setLabel(value.textMainEnter);
        status.setText(Build.APPLET_COPYRIGHT);

        MenuBar menuBar = new MenuBar();
        placesMenu  = new PlacesMenu(value);
        optionsMenu = new OptionsMenu(value);
        themeMenu   = new ThemeMenu(value);
        linkMenu    = new LinkMenu(value);
        helpMenu    = new HelpMenu(value);
        if (value.textMenuPlaces.length() > 0) {
            menuBar.add(placesMenu);
        }
        if (value.textMenuOptions.length() > 0) {
            menuBar.add(optionsMenu);
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
        optionsMenu.selectFont(panel.getFont());

        focusText.put(groupList, value.textStatusFocusRooms);
        focusText.put(userList, value.textStatusFocusUsers);
        if (filterField != null) {
            focusText.put(filterField, value.textStatusFocusFilter);
        }
        focusText.put(userNameField, value.textStatusFocusUsername);
        focusText.put(profileField, value.textStatusFocusProfile);
        focusText.put(filterButton, value.textStatusFocusGetrooms);
        focusText.put(joinButton, value.textStatusFocusEnter);
        focusText.put(status, Build.APPLET_COPYRIGHT);

        setTheme(value.getTheme(Theme.DEFAULT));
        status.showFont(true);
    }

    /**
     * Shows this frame.
     */

    public void show() {
        super.show();
        userNameField.requestFocus();
    }

    /**
     * Requests the focus for this frame.
     */

    public void requestFocus() {
        super.requestFocus();
        userNameField.requestFocus();
    }

    // Don't synchronize on this object when locking and unlocking the components
    // since it results in a deadlock on JDK 1.2.2 where the AWT thread has the
    // tree lock but needs the Frame while the receive thread has the frame (with
    // the synchronized lock method) but needs the tree lock.
    //
    //  5. (java.awt.EventDispatchThread)0x109 AWT-EventQueue-0 waiting in a monitor
    // 10. (java.lang.Thread)0x10e             Receiver-2       waiting in a monitor
    //
    // > where 5
    //   [1] java.awt.Frame.removeNotify (Frame:494)
    //   [2] java.awt.Window$1$DisposeAction.run (Window$1$DisposeAction:359)
    //   [3] java.awt.Window.dispose (Window:365)
    //   [4] COM.volano.chat.client.Client.close (Client:647)
    //   [5] COM.volano.chat.client.Client.exit (Client:930)
    //   [6] COM.volano.chat.client.Client.handleEvent (Client:790)
    //   [7] java.awt.Window.postEvent (Window:757)
    //   [8] java.awt.Component.dispatchEventImpl (Component:2406)
    //   [9] java.awt.Container.dispatchEventImpl (Container:1035)
    //   [10] java.awt.Window.dispatchEventImpl (Window:749)
    //   [11] java.awt.Component.dispatchEvent (Component:2307)
    //   [12] java.awt.EventQueue.dispatchEvent (EventQueue:287)
    //   [13] java.awt.EventDispatchThread.pumpOneEvent (EventDispatchThread:101)
    //   [14] java.awt.EventDispatchThread.pumpEvents (EventDispatchThread:92)
    //   [15] java.awt.EventDispatchThread.run (EventDispatchThread:83)
    // > where 10
    //   [1] java.awt.Component.disable (Component:723)
    //   [2] COM.volano.chat.client.Client.lock (Client:464)
    //   [3] COM.volano.chat.client.Client.nullObject (Client:1156)
    //   [4] COM.volano.chat.client.Client.update (Client:1004)
    //   [5] java.util.Observable.notifyObservers (Observable:146)
    //   [6] COM.volano.net.Connection.run (Connection:814)
    //   [7] java.lang.Thread.run (Thread:479)

    /**
     * Locks the user interface by disabling its components.  Lock the interface
     * components before sending request packets.
     */

    private void lock() {
        synchronized (enableLock) {
            placesMenu.filterMenuItem.setEnabled(false);
            placesMenu.enterMenuItem.setEnabled(false);
            groupList.setEnabled(false);
            filterButton.setEnabled(false);
            joinButton.setEnabled(false);
            if (filterField != null) {
                filterField.setEnabled(false);
            }
            userNameField.setEnabled(false);
            profileField.setEnabled(false);
        }
    }

    /**
     * Unlocks the user interface by enabling its components.  Unlock the
     * interface components after handling the confirmation packet.
     */

    private void unlock() {
        synchronized (enableLock) {
            placesMenu.filterMenuItem.setEnabled(true);
            placesMenu.enterMenuItem.setEnabled(true);
            groupList.setEnabled(true);
            filterButton.setEnabled(true);
            joinButton.setEnabled(true);
            if (filterField != null) {
                filterField.setEnabled(true);
            }
            userNameField.setEnabled(true);
            profileField.setEnabled(true);
            // Workaround for Bug Id 4853457:
            // REGRESSION: List setEnabled(true) does not redraw Items on Windows 2000
            // http://developer.java.sun.com/developer/bugParade/bugs/4853457.html
            groupList.setFont(groupList.getFont());
        }
    }

    /**
     * Sets the color and font theme of this window.
     *
     * @param theme the new color and font theme.
     */

    void setTheme(Theme theme) {
        try {
            this.theme = (Theme) theme.clone();    // Save new theme
        } catch (CloneNotSupportedException e) {}
        Theme.setTheme(this, theme);    // Do this frame
        setTheme(publicList, theme);    // Propogate to child frames
        setTheme(privateList, theme);
        themeMenu.selectTheme(theme.getIndex());
        optionsMenu.selectFont(theme.getFontDefault());
    }

    /**
     * Sets the theme of all frames in the list.
     *
     * @param list  the list of frames.
     * @param name  the theme.
     */

    private void setTheme(Hashtable list, Theme theme) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.setTheme(theme);
            }
        }
    }

    /**
     * Sets the font name.
     *
     * @param name  the font name.
     */

    private void setFontName(String name) {
        Font font = theme.getFontDefault();
        Font newFont = new Font(name, font.getStyle(), font.getSize());
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
        setFontName(publicList, name);      // Propagate to child frames
        setFontName(privateList, name);
        optionsMenu.selectFontName(name);
    }

    /**
     * Sets the font name of all frames in the list.
     *
     * @param list  the list of frames.
     * @param name  the font name.
     */

    private void setFontName(Hashtable list, String name) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.setFontName(name);
            }
        }
    }

    /**
     * Sets the font style.
     *
     * @param name  the font style.
     */

    private void setFontStyle(int style) {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), style, font.getSize());
        theme.setFontDefault(newFont);      // Save new font
        Theme.setFont(this, newFont);       // Do this frame
        setFontStyle(publicList, style);    // Propagate to child frames
        setFontStyle(privateList, style);
        optionsMenu.selectFontStyle(style);
    }

    /**
     * Sets the font style of all frames in the list.
     *
     * @param list  the list of frames.
     * @param name  the font style.
     */

    private void setFontStyle(Hashtable list, int style) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.setFontStyle(style);
            }
        }
    }

    /**
     * Increases the font size.
     */

    private void increaseFont() {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() + 1);
        theme.setFontDefault(newFont);    // Save new font
        Theme.setFont(this, newFont);     // Do this frame
        increaseFont(publicList);         // Propagate to child frames
        increaseFont(privateList);
    }

    /**
     * Increases the font size of all frames in the list.
     *
     * @param list  the list of frames.
     */

    private void increaseFont(Hashtable list) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.increaseFont();
            }
        }
    }

    /**
     * Decreases the font size.
     */

    private void decreaseFont() {
        Font font = theme.getFontDefault();
        Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        theme.setFontDefault(newFont);    // Save new font
        Theme.setFont(this, newFont);     // Do this frame
        decreaseFont(publicList);         // Propagate to child frames
        decreaseFont(privateList);
    }

    /**
     * Decreases the font size of all frames in the list.
     *
     * @param list  the list of frames.
     */

    private void decreaseFont(Hashtable list) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.decreaseFont();
            }
        }
    }

    /**
     * Sets the accept private chat option of all public chat frames.
     *
     * @param value  <code>true</code> to enable private chats; otherwise
     *               <code>false</code>.
     */

    private void setAcceptPrivate(boolean value) {
        synchronized (publicList) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat frame = (PublicChat) enumeration.nextElement();
                frame.setAcceptPrivate(value);
            }
        }
    }

    /**
     * Sets the entrance alert option of all public chat frames.
     *
     * @param value  <code>true</code> to enable entrance alerts; otherwise
     *               <code>false</code>.
     */

    private void setEntranceAlert(boolean value) {
        synchronized (publicList) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat frame = (PublicChat) enumeration.nextElement();
                frame.setEntranceAlert(value);
            }
        }
    }

    /**
     * Sets the audio alert option of all public chat frames.
     *
     * @param value  <code>true</code> to enable audio alerts; otherwise
     *               <code>false</code>.
     */

    private void setAudioAlert(boolean value) {
        synchronized (publicList) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat frame = (PublicChat) enumeration.nextElement();
                frame.setAudioAlert(value);
            }
        }
    }

    /**
     * Sets the count alert option of all public chat frames.
     *
     * @param value  <code>true</code> to enable count alerts; otherwise
     *               <code>false</code>.
     */

    private void setCountAlert(boolean value) {
        synchronized (publicList) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat frame = (PublicChat) enumeration.nextElement();
                frame.setCountAlert(value);
            }
        }
    }

    /**
     * Sets the Web touring option of all public chat frames.
     *
     * @param value  <code>true</code> to enable Web touring; otherwise
     *               <code>false</code>.
     */

    private void setWebtouring(boolean value) {
        synchronized (publicList) {
            Enumeration enumeration = publicList.elements();
            while (enumeration.hasMoreElements()) {
                PublicChat frame = (PublicChat) enumeration.nextElement();
                frame.setWebtouring(value);
            }
        }
    }

    /**
     * Closes all frames in the list.
     *
     * @param list  the list of frames.
     */

    private void close(Hashtable list) {
        synchronized (list) {
            Enumeration enumeration = list.elements();
            while (enumeration.hasMoreElements()) {
                RoomFrame frame = (RoomFrame) enumeration.nextElement();
                frame.close();
            }
        }
    }

    /**
     * Closes this frame and all secondary frames, and closes the connection.
     */

    private void close() {
        status.setText(value.textStatusClosing);
        close(privateList);
        close(publicList);
        // Call this before close.  Otherwise we always think we're disconnected.
        URL exitPage = value.wasDisconnected ? value.pageExitError : value.pageExit;
        connection.close();
        dispose();
        value.sounds.play(Sounds.STOP);
        if (exitPage != null && value.applet.isActive()) {
            System.out.println("See " + exitPage);
            if (value.pageNewwindow) {
                value.context.showDocument(exitPage, Build.TARGET);
            } else {
                value.context.showDocument(exitPage);
            }
        }
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
        if (key == Build.TAB) {
            if (event.shiftDown()) {
                changeFocus(focusPrev, (Component) event.target);
            } else {
                changeFocus(focusNext, (Component) event.target);
            }
            return true;
        }
        // We still receive KEY_PRESS events even when the component is disabled, so
        // double check the component state here.
        else if (key == Build.NEW_LINE || key == Build.RETURN) {
            if (event.target == filterField && filterField != null && filterField.isEnabled()) {
                getRooms();
            } else if (event.target == filterButton && filterButton.isEnabled()) {
                getRooms();
            } else if (event.target == broadcastField && broadcastField.isEnabled()) {
                broadcast();
            } else if (joinButton.isEnabled()) {
                joinRoom();
            }
            return true;
        }
        return false;
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
        if ((event.target == joinButton) || (event.target == placesMenu.enterMenuItem)) {
            joinRoom();
            return true;
        } else if ((event.target == filterButton) || (event.target == placesMenu.filterMenuItem)) {
            getRooms();
            return true;
        } else if (event.target == placesMenu.exitMenuItem) {
            event.id     = Event.WINDOW_DESTROY;
            event.target = this;
            exit(event);
            return true;
        } else if (event.target == helpMenu.topicsMenuItem) {
            value.context.showDocument(value.pageHelp, Build.TARGET);
            return true;
        } else if (event.target == helpMenu.aboutMenuItem) {
            value.context.showDocument(value.pageAbout, Build.TARGET);
            return true;
        } else if (event.target == optionsMenu.acceptPrivateCheckbox) {
            value.acceptPrivateEnabled = optionsMenu.acceptPrivateCheckbox.getState();
            setAcceptPrivate(value.acceptPrivateEnabled);     // Propagate to child frames
            return true;
        } else if (event.target == optionsMenu.entranceAlertCheckbox) {
            value.entranceAlertsEnabled = optionsMenu.entranceAlertCheckbox.getState();
            setEntranceAlert(value.entranceAlertsEnabled);    // Propagate to child frames
            return true;
        } else if (event.target == optionsMenu.audioAlertCheckbox) {
            value.audioAlertsEnabled = optionsMenu.audioAlertCheckbox.getState();
            setAudioAlert(value.audioAlertsEnabled);          // Propagate to child frames
            return true;
        } else if (event.target == optionsMenu.countAlertCheckbox) {
            value.countAlertsEnabled = optionsMenu.countAlertCheckbox.getState();
            setCountAlert(value.countAlertsEnabled);          // Propagate to child frames
            return true;
        } else if (event.target == optionsMenu.webtouringCheckbox) {
            value.webtouringEnabled = optionsMenu.webtouringCheckbox.getState();
            setWebtouring(value.webtouringEnabled);           // Propagate to child frames
            return true;
        } else if (event.target == optionsMenu.increaseFontMenuItem) {
            increaseFont();
            return true;
        } else if (event.target == optionsMenu.decreaseFontMenuItem) {
            decreaseFont();
            return true;
        } else if (linkMenu.contains(event.target)) {
            URL url = linkMenu.getLink(event.target);
            if (url != null) {
                value.context.showDocument(url, Build.TARGET);
            }
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
        boolean handled = false;
        if (event.id == Event.WINDOW_DESTROY) {
            // System.out.println(event);
            handled = exit(event);
        } else if (event.target == groupList && event.id == Event.LIST_SELECT) {
            getUsers(event);
            handled = true;
        } else if (event.target == userList && event.id == Event.LIST_SELECT) {
            getProfile(event);
            handled = true;
        }
        return handled ? true : super.handleEvent(event);
    }

    /**
     * Sends a room list request to the server.
     */

    private void getRooms() {
        status.setText(value.textStatusGettingrooms);
        lock();
        if (filterField != null) {
            send(new RoomList(filterField.getText().trim()));
        } else {
            send(new RoomList());
        }
        value.sounds.play(Sounds.ROOMS);
    }

    /**
     * Sends a broadcast chat request to the server.
     */

    private void broadcast() {
        String text = broadcastField.getText().trim();
        if (text.length() != 0) {
            broadcastField.setText("");
            send(new Chat(text));
        }
    }

    /**
     * Checks whether there is enough information to enter a room.
     *
     * @param roomName  the name of the room to enter.
     * @param userName  the user name.
     * @return  <code>true</code> if the room name and user name are provided, and
     *          the user is not already in the room; otherwise <code>false</code>.
     */

    private boolean canJoin(String roomName, String userName) {
        boolean okToJoin = false;
        if (roomName.length() == 0) {
            status.setText(value.textStatusSelectroom);
        } else if (userName.length() == 0) {
            status.setText(Message.format(value.textStatusEntername, selectedRoom));
        } else {
            PublicChat frame = (PublicChat) publicList.get(roomName);
            if (frame != null) {
                status.setText(Message.format(value.textStatusAlreadyinroom, roomName));
                frame.requestFocus();
            }
            // 2.1 - a value of -1 or 0 means no limit on the number of public rooms.
            // 2.2 - a value of -1 means unlimited, while zero now means zero.
            // else if (value.limitPublic > 0 && publicList.size() >= value.limitPublic) {
            else if (value.limitPublic != -1 && publicList.size() >= value.limitPublic) {
                status.setText(Message.format(value.textStatusPubliclimit, new Integer(value.limitPublic)));
            } else {
                okToJoin = true;
            }
        }
        return okToJoin;
    }

    /**
     * Sends an enter room request if the room name and user name are provided and
     * the user is not already in the room.
     */

    private void joinRoom() {
        String roomName = selectedRoom;
        String userName = memberName;
        if (userName.length() == 0 || value.memberEditableName) {
            userName = truncate(userNameField.getText().replace(Build.NON_BREAKING_SPACE, ' ').trim(), value.lengthUsername);
        }
        if (canJoin(roomName, userName)) {
            status.setText(Message.format(value.textStatusEnteringroom, roomName));
            String profile = memberProfile;
            if (profile.length() == 0 || value.memberEditableProfile) {
                profile = truncate(profileField.getText().trim(), value.lengthProfile);
            }
            lock();
            send(new EnterRoom(roomName, userName, profile));
        }
    }

    /**
     * Sends a user list request to the server.
     */

    private void getUsers(Event event) {
        // if (userList.getItemCount() > 0)
        //   userList.removeAll();
        clear(userList);
        status.setText(value.textStatusGettingusers);
        selectedRoom = groupList.getItem(((Integer) event.arg).intValue());
        // filterField.setText(selectedRoom);
        lock();
        send(new UserList(selectedRoom));
        value.sounds.play(Sounds.USERS);
    }

    /**
     * Shows the profile in the status area.
     *
     * @param value  the applet property values.
     * @param user   the user information.
     */

    private void showProfile(Value value, String[] user) {
        if (value.memberDocument.length() > 0 &&
                Boolean.valueOf(user[RoomPacket.MEMBER]).booleanValue() &&
                Boolean.valueOf(user[RoomPacket.LINK]).booleanValue()) {
            Vector subList = new Vector(2);
            subList.addElement(URLEncoder.encode(user[RoomPacket.NAME]));
            subList.addElement(URLEncoder.encode(memberName));
            String href = Message.format(value.memberDocument, subList);
            status.setText(RoomFrame.formatProfile(value, user), value.textMemberProfile, href, value.linkProfileUrl, value.linkProfileDisable);
        } else {
            status.setText(RoomFrame.formatProfile(value, user), value.linkProfileUrl, value.linkProfileDisable);
        }
    }

    /**
     * Displays the profile of the selected user.
     *
     * @param event  the event definition.
     */

    private void getProfile(Event event) {
        String[] user = users[((Integer) event.arg).intValue()];
        showProfile(value, user);
        value.sounds.play(Sounds.PROFILE);
    }

    /**
     * Handles a window exit, closing the applet if the main chat window has been
     * exited.
     *
     * @param event  the event definition.
     * @return  <code>true</code> if the event was handled; otherwise
     *          <code>false</code>.
     */

    private boolean exit(Event event) {
        if (event.target == this) {
            close();
            owner.deliverEvent(event);
            return true;
        } else if (event.target instanceof PublicChat) {
            PublicChat publicChat = (PublicChat) event.target;
            publicList.remove(publicChat.groupName());
            value.sounds.play(Sounds.EXIT);
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
     * Sends a packet to the server, displaying a "disconnected" error message if
     * the connection is closed.
     *
     * @param packet  the packet to send.
     */

    private void send(Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {
            status.setText(Message.format(value.textSystemDisconnected, new Date()));
            connection.deleteObserver(this);
            value.wasDisconnected = true;
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
            System.out.println("Client update ...");
        }

        Connection connection = (Connection) observable;
        if (object instanceof Packet) {
            Packet packet = (Packet) object;
            if (! packet.isHandled()) {
                // Check for packet type so we don't mark packets handled by accident.
                switch (packet.getType()) {
                case Packet.CONFIRM:
                    if (packet instanceof RoomList) {
                        roomListConfirm(connection, (RoomList) packet);
                    } else if (packet instanceof UserList) {
                        userListConfirm(connection, (UserList) packet);
                    } else if (packet instanceof EnterRoom) {
                        enterRoomConfirm(connection, (EnterRoom) packet);
                    } else if (packet instanceof EnterPrivate) {
                        enterPrivate(connection, (EnterPrivate) packet);
                    }
                    break;

                case Packet.INDICATION:
                    if (packet instanceof EnterPrivate) {
                        enterPrivate(connection, (EnterPrivate) packet);
                    }
                    break;
                }
            }
        } else if (object instanceof StreamableError) {
            streamableError(connection, (StreamableError) object);
        } else if (object == null) {
            nullObject(connection);
        }
    }

    /**
     * Handles a room list confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the room list confirmation.
     */

    private void roomListConfirm(Connection connection, RoomList confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Client roomListConfirm ...");
        }

        updateRoomList(confirm.getRooms());
        unlock();
        setStatus();
    }

    /**
     * Handles a user list confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the user list confirmation.
     */

    private void userListConfirm(Connection connection, UserList confirm) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Client userListConfirm ...");
        }

        int result = confirm.getResult();
        unlock();
        if (result == UserList.OKAY) {
            roomType = confirm.getRoomType();
            updateUserList(roomType, confirm.getUsers());
            String docBase  = confirm.getDocumentBase();
            if (docBase.startsWith(URL_PREFIX) || roomType == RoomPacket.EVENT) {
                status.setText(docBase, value.linkReferrerUrl, value.linkReferrerDisable);
            } else {
                setStatus();
            }
        } else if (result == UserList.NO_SUCH_ROOM) {
            userLabel.setText(value.textMainUsers);
            status.setText(value.textStatusNosuchroom);
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
            System.out.println("Client enterRoomConfirm ...");
        }

        String roomName = confirm.getRoomName();
        String userName = confirm.getUserName();
        String profile  = confirm.getProfile();
        int    result   = confirm.getResult();
        unlock();
        if (result == EnterRoom.OKAY) {
            roomType = confirm.getRoomType();
            updateUserList(roomType, confirm.getUsers());
            String title = roomName + " (" + userName + ")";
            openRoomWindow(title, roomName, roomType, confirm.getCount(), userName, profile, confirm.getUsers());
            setStatus();
        } else if (result == EnterRoom.ROOM_FULL) {
            status.setText(Message.format(value.textStatusRoomfull, roomName));
        } else if (result == EnterRoom.NAME_TAKEN) {
            Vector subList = new Vector(2);
            subList.addElement(userName);
            subList.addElement(roomName);
            status.setText(Message.format(value.textStatusNametaken, subList));
        } else if (result == EnterRoom.MEMBER_TAKEN) {
            status.setText(Message.format(value.textStatusMembertaken, userName));
        } else if (result == EnterRoom.NO_SUCH_ROOM) {
            userLabel.setText(value.textMainUsers);
            status.setText(value.textStatusNosuchroom);
        }
    }

    /**
     * Handles an enter private packet.
     *
     * @param connection  the connection to the server.
     * @param confirm     the enter private indication or confirmation.
     */

    private void enterPrivate(Connection connection, EnterPrivate packet) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Client enterPrivate ...");
        }

        int         roomId = packet.getRoomId();
        PrivateChat window = (PrivateChat) privateList.get(new Integer(roomId));
        if (window != null) {
            window.requestFocus();
        } else {
            String toName   = packet.getToName();
            String fromName = packet.getFromName();
            PublicChat room = (PublicChat) publicList.get(packet.getRoomName());
            if (room != null) {
                if (packet.getType() == Packet.INDICATION) {
                    String[] user = room.getUser(fromName);
                    if (user == null || ! room.isAcceptingPrivate() || room.isIgnored(fromName) ||
                            (value.limitPrivate != -1 && privateList.size() >= value.limitPrivate)) {
                        send(new ExitPrivate(roomId, toName));
                    } else {
                        String title = fromName + " (" + toName + ")";
                        openPrivateWindow(title, roomId, toName, user);
                    }
                } else {        // Packet.CONFIRM
                    String[] user = room.getUser(toName);
                    if (user == null) {
                        send(new ExitPrivate(roomId, fromName));
                    } else {
                        String title = toName + " (" + fromName + ")";
                        openPrivateWindow(title, roomId, fromName, user);
                    }
                }
            }
        }
    }

    /**
     * Handles a streamable error packet.
     *
     * @param connection  the connection to the server.
     * @param confirm     the streamable error packet.
     */

    private void streamableError(Connection connection, StreamableError error) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Client streamableError ...");
        }

        error.setHandled();
        status.setText("Server error (" + error.getText() + ").");
        unlock();
    }

    /**
     * Handles the <code>null</code> object, indicating the connection was closed.
     *
     * @param connection  the connection to the server.
     */

    private void nullObject(Connection connection) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Client nullObject ...");
        }

        status.setText(Message.format(value.textSystemDisconnected, new Date()));
        connection.deleteObserver(this);
        lock();
        value.wasDisconnected = true;
    }

    /**
     * Updates the room list with the list of room names.
     *
     * @param names  the list of room names.
     */

    private void updateRoomList(String[] names) {
        // if (userList.getItemCount() > 0)
        //   userList.removeAll();
        clear(userList);
        // if (groupList.getItemCount() > 0)
        //   groupList.removeAll();
        clear(groupList);
        boolean found = false;
        int index = 0;
        for (int i = 0; i < names.length; i++) {
            groupList.add(names[i]);
            if (! found && selectedRoom.equals(names[i])) {
                found = true;
                index = i;
            }
        }
        switch (names.length) {
        case 0:
            groupLabel.setText(value.textMainNorooms);
            break;
        case 1:
            groupLabel.setText(value.textMainOneroom);
            break;
        default:
            groupLabel.setText(Message.format(value.textMainManyrooms, new Integer(names.length)));
        }
        userLabel.setText(value.textMainUsers);
        if (found) {
            groupList.select(index);
        } else {
            selectedRoom = "";
        }
    }

    /**
     * Updates the user list with the list of user names.
     *
     * @param roomType  the type of the room, such as public, personal, or event.
     * @param names     the list of user names.
     */

    private void updateUserList(int roomType, String[][] users) {
        /*
            if (optionsMenu.reverseNamesCheckbox.getState()) {
              this.users = new String[users.length][];
              for (int i = 0; i < users.length; i++)
                this.users[i] = users[users.length - 1 - i];
            }
            else
              this.users = users;
        */
        this.users = users;
        clear(userList);
        for (int i = 0; i < this.users.length; i++) {
            userList.add(this.users[i][RoomPacket.NAME]);
        }
        if (roomType == RoomPacket.EVENT) {
            userLabel.setText(value.textMainOnstage);
        } else {
            switch (users.length) {
            case 0:
                userLabel.setText(value.textMainNousers);
                break;
            case 1:
                userLabel.setText(value.textMainOneuser);
                break;
            default:
                userLabel.setText(Message.format(value.textMainManyusers, new Integer(users.length)));
            }
        }
    }

    /**
     * 2.1.10 - Gets the user by name.
     *
     * @param list  the list of users.
     * @param name  the name of the user to get.
     * @returns  the user with the specified name.
     */

    private String[] getUser(String name) {
        for (int i = 0; i < users.length; i++)
            if (users[i][RoomPacket.NAME].equals(name)) {
                return users[i];
            }
        return null;
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

    /**
     * Opens a public chat room window.
     *
     * @param title      the string for the window title bar.
     * @param roomName   the name of the room.
     * @param roomType   the type of the room (public, personal, or event).
     * @param userName   the name of this user.
     * @param profile    the profile of this user.
     * @param users      the list of users in the room.
     */

    private void openRoomWindow(String title, String roomName, int roomType, int count, String userName, String profile, String[][] users) {
        RoomFrame frame = new PublicChat(connection, this, value, users, roomName, roomType, count, userName, profile, privateList);
        publicList.put(roomName, frame);
        showFrame(frame, title);
    }

    /**
     * Opens a private chat room window.
     *
     * @param title         the string for the window title bar.
     * @param roomId        the identifier of the private chat room.
     * @param myName        the name of this user.
     * @param theirName     the name of the other user.
     * @param theirHost     the host name of the other user.
     * @param theirProfile  the profile of the other user.
     */

    private void openPrivateWindow(String title, int roomId, String myName, String[] user) {
        RoomFrame frame = new PrivateChat(connection, this, value, roomId, myName, user);
        privateList.put(new Integer(roomId), frame);
        showFrame(frame, title);
    }

    /**
     * Shows the chat room window.
     *
     * @param frame      the window frame to show.
     * @param title      the string for the window title bar.
     */

    private void showFrame(RoomFrame frame, String title) {
        frame.setTitle(title);
        RoomFrame roomFrame = (RoomFrame) frame;
        roomFrame.setTheme(theme);
        roomFrame.showFont(true);
        frame.pack();
        // Causes window to open maximized or minimized on some systems.
        // Point location = getLocation();
        // frame.setLocation(location.x + offset, location.y + offset);
        frame.setVisible(true);
        frame.requestFocus();       // 2.1.11
        value.sounds.play(Sounds.ENTER);
    }

    /**
     * Sets the status area to an appropriate default message.
     */

    private void setStatus() {
        if (selectedRoom.length() == 0) {
            status.setText(value.textStatusSelectroom);
        } else if (userNameField.getText().replace(Build.NON_BREAKING_SPACE, ' ').trim().length() == 0) {
            status.setText(Message.format(value.textStatusEntername, selectedRoom));
        } else {
            status.setText(Message.format(value.textStatusEnter, selectedRoom));
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
            component = userNameField;
        }
        status.setText((String) focusText.get(component));
        component.requestFocus();
    }
}
