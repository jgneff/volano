/*
 * ChatEvent.java - a chat event.
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
 * This class encapsulates a chat event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class ChatEvent implements EventTags {
    private int    roomId;
    private String roomName = "";
    private String fromUser = "";
    private String toUser   = "";
    private String message  = "";

    /**
     * Creates a new public chat event.
     *
     * @param roomName  the name of the room.
     * @param fromUser  the name of the user who sent the chat message.
     * @param message   the text of the chat message.
     */

    public ChatEvent(String roomName, String fromUser, String message) {
        this.roomName = roomName;
        this.fromUser = fromUser;
        this.message  = message;
    }

    /**
     * Creates a new private chat event.
     *
     * @param room      the id of the private chat session.
     * @param fromUser  the name of the user who sent the chat message.
     * @param toUser    the name of the user who received the chat message.
     * @param message the text of the chat message.
     */

    public ChatEvent(int roomId, String fromUser, String toUser, String message) {
        this.roomId   = roomId;
        this.fromUser = fromUser;
        this.toUser   = toUser;
        this.message  = message;
    }

    /**
     * Gets the public room name.
     *
     * @return  the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the private room ID.
     *
     * @return  the room name.
     */

    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets the name of the user who sent the chat message.
     *
     * @return  the from user name.
     */

    public String getFromUser() {
        return fromUser;
    }

    /**
     * Gets the name of the user who received the chat message.
     *
     * @return  the to user name.
     */

    public String getToUser() {
        return toUser;
    }

    /**
     * Gets the chat message.
     *
     * @return  the chat message.
     */

    public String getMessage() {
        return message;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        return CHAT_START +
               ROOM_START + roomName + ROOM_END +
               USER_START + fromUser + USER_END +
               MESSAGE_START + message + MESSAGE_END +
               CHAT_END;
    }
}
