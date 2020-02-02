/*
 * EnterEvent.java - an enter room event.
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

package COM.volano.chat.event;

/**
 * This class encapsulates an enter room event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class EnterEvent implements EventTags {
    private String  room     = "";
    private String  user     = "";
    private String  host     = "";
    private String  profile  = "";
    private boolean isMember = false;

    /**
     * Creates a new enter room event.
     *
     * @param room     the name of the room entered.
     * @param user     the name of the user who entered.
     * @param host     the host name or IP address of the user who entered.
     * @param profile  the profile of the user who entered.
     * @param isMember <code>true</code> if the user is a member; otherwise
     *                 <code>false</code>.
     */

    public EnterEvent(String room, String user, String host, String profile, boolean isMember) {
        this.room     = room;
        this.user     = user;
        this.host     = host;
        this.profile  = profile;
        this.isMember = isMember;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        return (isMember ? ENTER_MEMBER_START : ENTER_NONMEMBER_START) +
               ROOM_START + room + ROOM_END +
               USER_START + user + USER_END +
               HOST_START + host + HOST_END +
               PROFILE_START + profile + PROFILE_END +
               ENTER_END;
    }
}
