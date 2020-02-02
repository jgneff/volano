/*
 * WhisperEvent.java - a whisper event.
 * Copyright (C) 2000 John Neffenger
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
 * This class encapsulates a whisper event.
 *
 * @author  John Neffenger
 * @version 29 Jan 2000
 */

public class WhisperEvent implements EventTags {
    private String roomName = "";
    private String fromUser = "";
    private String toUser   = "";
    private String message  = "";

    /**
     * Creates a new public whisper event.
     *
     * @param roomName  the name of the room.
     * @param fromUser  the name of the user who sent the whisper message.
     * @param toUser    the name of the user who received the whisper message.
     * @param message   the text of the whisper message.
     */

    public WhisperEvent(String roomName, String fromUser, String toUser, String message) {
        this.roomName = roomName;
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
     * Gets the name of the user who sent the whisper message.
     *
     * @return  the from user name.
     */

    public String getFromUser() {
        return fromUser;
    }

    /**
     * Gets the name of the user who received the whisper message.
     *
     * @return  the to user name.
     */

    public String getToUser() {
        return toUser;
    }

    /**
     * Gets the whisper message.
     *
     * @return  the whisper message.
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
        return WHISPER_START +
               ROOM_START    + roomName + ROOM_END +
               FROM_START    + fromUser + FROM_END +
               TO_START      + toUser   + TO_END +
               MESSAGE_START + message  + MESSAGE_END +
               WHISPER_END;
    }
}
