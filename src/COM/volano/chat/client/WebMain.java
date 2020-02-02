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
import COM.volano.*;
import COM.volano.awt.*;
import COM.volano.util.*;
import COM.volano.chat.Build;


/**
 *  Defines the interrogation panel used by the WebVolanoChat applet.
 *
 *  <P>This panel will trigger an action event if the user presses the
 *  "Enter Room" button, and all the fields contain valid data.
 */
public class WebMain extends Panel implements WebStatusPanel {
    private static final int TEXT_FIELD_COLUMNS = 40;

    final private static String BLANK = "";
    final private static int MARGIN = 6; // the margin around the panel
    // final private static int ENTER = 10; // Key code for the enter key

    private Value value;  // most of the state we need to display the panel.

    private GridBagLayout layout = new GridBagLayout(); // layout manager
    private Focus focus;                                // tracks focus

    // widgets.
    private ControlledTextField  username  = new ControlledTextField(TEXT_FIELD_COLUMNS);
    private ControlledTextField  profile   = new ControlledTextField(TEXT_FIELD_COLUMNS);
    private ControlledTextField  password  = new ControlledTextField(TEXT_FIELD_COLUMNS);
    private Button     enter     = new Button();
    private HyperLabel status;


    // Set to true to prevent the gotFocus() method from displaying a
    // a message.
    private boolean skipFocusMessage = true;


    /**
     *  Constructs a new WebMain panel.
     *
     *  @param value  The <CODE>Value</CODE> object that contains all relevant state.
     */
    public WebMain(Value value) {
        this.value = value;
        status = new HyperLabel(value.context, value.linkPrefix);

        /*
            WebVolanoChat.setColorsAndFont(this, value);
            WebVolanoChat.setColorsAndFont(username, value);
            WebVolanoChat.setColorsAndFont(profile, value);
            WebVolanoChat.setColorsAndFont(password, value);
            WebVolanoChat.setColorsAndFont(enter, value);
            WebVolanoChat.setColorsAndFont(status, value);
        */

        // The "blank" canvas is used to fill up vertical space with the background color.
        Canvas blank = new Canvas();
        setLayout(layout);

        username.setLimit(value.lengthUsername);
        profile.setLimit(value.lengthProfile);

        enter.setLabel(value.textMainEnter);
        status.setText(Build.APPLET_COPYRIGHT);
        username.setText(value.username);
        profile.setText(value.profile);
        password.setText(value.password);
        password.setEchoChar('*');

        // This is all terribly ugly, because there are about a dozen combinations
        // of applet tag parameters, each of which determines a different main
        // panel...
        focus = new Focus(username);
        if (value.member) {
            focus.add(password);
            if (value.memberEditableProfile) {
                focus.add(profile);
            }
        } else {
            focus.add(profile);
        }
        focus.add(enter);
        add(blank, 0, 0, 3, true, true);
        String s = value.member ? value.textMemberName : value.textMainUsername;
        add(new Label(s), 0, 1, 1, false, false);
        add(username, 1, 1, 1, true, false);
        add(enter, 2, 1, 1, false, false);
        int y = 3;
        if (value.member) {
            add(new Label(value.textMemberPassword), 0, 2, 1, false, false);
            add(password, 1, 2, 2, true, false);
            if (value.memberEditableProfile) {
                add(new Label(value.textMainProfile), 0, 3, 1, false, false);
                add(profile, 1, 3, 2, true, false);
                y = 4;
            }
        } else {
            add(new Label(value.textMainProfile), 0, 2, 1, false, false);
            add(profile, 1, 2, 2, true, false);
        }
        add(status, 0, y, 3, true, false);
        add(focus, 0, y + 1, 3, true, true);

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
     *  Triggered if the user hits enter on a text field, or presses
     *  the "Enter Room" button; in either case, we want to propagate the
     *  event to the parent container only if the username and, if necessary,
     *  password field have been filled in.
     *
     *  @param Event The event object
     *  @param what  Ignored.
     */
    public boolean action(Event ev, Object what) {
        if (username.getText().replace(Build.NON_BREAKING_SPACE, ' ').trim().equals(BLANK)) {
            skipFocusMessage = true;
            username.requestFocus();
            if (value.member) {
                status.setText(value.textStatusEntermembername);
            } else {
                status.setText(Message.format(value.textStatusEntername, value.group));
            }
            return true;
        }
        if (value.member && password.getText().equals(BLANK)) {
            skipFocusMessage = true;
            password.requestFocus();
            status.setText(value.textStatusEntermemberpassword);
            return true;
        }
        // Impose our limits.
        truncate(username, value.lengthUsername);
        truncate(profile, value.lengthProfile);
        return false;
    }


    /**
     *  Truncates the contents of the given text field.
     *
     *  @param tf The textfield to truncate.
     *  @param max The maximum number of characters.
     */
    private void truncate(TextField tf, int max) {
        if (tf.getText().length() > max) {
            tf.setText(WebVolanoChat.truncate(tf.getText(), max));
        }
    }


    /**
     *  Invoked when the keyboard focus transfers to a new component.
     *
     *  @param ev  The event object
     *  @param what  Ignored.
     */
    public boolean gotFocus(Event ev, Object what) {
        if (skipFocusMessage) {
            skipFocusMessage = false;
        } else if (ev.target == username) {
            if (value.member) {
                status.setText(value.textStatusFocusMembername);
            } else {
                status.setText(value.textStatusFocusUsername);
            }
        } else if (ev.target == password) {
            status.setText(value.textStatusFocusMemberpassword);
        } else if (ev.target == profile) {
            status.setText(value.textStatusFocusProfile);
        } else if (ev.target == enter) {
            status.setText(value.textStatusFocusEnter);
        }
        focus.setCurrent((Component)ev.target);
        return true;
    }


    /**
     *  Invoked when a key is pressed.
     *
     *  @param ev  The event object
     *  @param what  Ignored.
     */
    public boolean keyDown(Event event, int key) {
        boolean handled = false;
        if (key == Build.TAB) {
            if (event.shiftDown()) {
                focus.previous();
            } else {
                focus.next();
            }
            handled = true;
        } else if (key == Build.NEW_LINE || key == Build.RETURN) {
            postEvent(new Event(enter, Event.ACTION_EVENT, ""));
            handled = true;
        }
        return handled;
    }


    /**
     *  Sets the status message.
     *
     *  @param s  The new status message.
     */
    public void setStatus(String s) {
        skipFocusMessage = true;
        status.setText(s);
    }


    /**
     *  Returns the contents of the username textfield.
     *
     *  @return The contents of the username textfield.
     */
    public String getUsername() {
        return username.getText().replace(Build.NON_BREAKING_SPACE, ' ').trim();
    }


    /**
     *  Returns the contents of the profile textfield.
     *
     *  @return the contents of the profile textfield.
     */
    public String getProfile() {
        return profile.getText();
    }


    /**
     *  Returns the contents of the password textfield.
     *
     *  @return the contents of the password textfield.
     */
    public String getPassword() {
        return password.getText();
    }


    /**
     *  Returns the margin around this component.
     *
     *  @return The margin around this component.
     */
    public Insets getInsets() {
        return new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
    }


    /**
     *  Sets whether or not the username field is editable.
     *
     *  @param b  <CODE>true</CODE> if the username field should be editable.
     */
    public void setEditableName(boolean b) {
        username.setEditable(b);
    }


    /**
     *  Sets whether or not the profile field is editable.
     *
     *  @param b  <CODE>true</CODE> if the profile field should be editable.
     */
    public void setEditableProfile(boolean b) {
        profile.setEditable(b);
    }


    /**
     *  Sets whether or not the password field is editable.
     *
     *  @param b  <CODE>true</CODE> if the password field should be editable.
     */
    public void setEditablePassword(boolean b) {
        password.setEditable(b);
    }

}
