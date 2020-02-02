/*
 * MonitorDialog.java - a dialog for confirming a remove, kick or ban.
 * Copyright (C) 1996-2002 John Neffenger
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
import  COM.volano.chat.packet.Kick;
import  java.awt.*;

/**
 * This class defines a dialog for confirming a remove, kick or ban.
 *
 * @author  John Neffenger
 * @version 1 May 2002
 */

class MonitorDialog extends Dialog {
    private static final int INSET =  2;
    private static final int ROWS  = 10;

    private PublicChat parent;
    private Value      value;
    private User[]     users;
    private int        type;

    private Label      label;
    private List       list;
    private Button     okayButton;
    private Button     cancelButton;

    private User       selected = null;

    /**
     * Creates a new Kick dialog.
     *
     * @param parent the parent frame.
     * @param title  the title of the dialog box.
     * @param modal  whether the dialog box is modal.
     */

    MonitorDialog(PublicChat parent, String title, Value value, User[] users, int type) {
        super(parent, title, true);
        this.parent = parent;
        this.value  = value;
        this.users  = users;
        this.type   = type;

        switch (type) {
        case Kick.REMOVE:
            label = new Label(value.textMonitorLabelRemove);
            break;
        case Kick.KICK:
            label = new Label(value.textMonitorLabelKick);
            break;
        case Kick.BAN:
            label = new Label(value.textMonitorLabelBan);
            break;
        }
        list = new List(ROWS, false);
        for (int i = 0; i < users.length; i++) {
            list.add(users[i].toString());
        }
        okayButton   = new Button(value.textMonitorOkay);
        cancelButton = new Button(value.textMonitorCancel);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, INSET, INSET));
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(INSET, INSET));
        add("North", label);
        add("Center", list);
        add("South", buttonPanel);

        Theme.setTheme(this, parent.getTheme());
    }

    public boolean handleEvent(Event event) {
        if (event.target == list && event.id == Event.LIST_SELECT) {
            selected = users[((Integer) event.arg).intValue()];
            return true;
        }
        if (event.id == Event.WINDOW_DESTROY) {
            doCancel();
            return true;
        }
        return super.handleEvent(event);
    }

    public boolean action(Event event, Object arg) {
        if (event.target == okayButton) {
            doOkay();
            return true;
        }
        if (event.target == cancelButton) {
            doCancel();
            return true;
        }
        return false;
    }

    private void doOkay() {
        if (selected != null) {
            parent.kick(selected.getName(), selected.getAddress(), type);
        }
        setVisible(false);
        dispose();
    }

    private void doCancel() {
        setVisible(false);
        dispose();
    }
}
