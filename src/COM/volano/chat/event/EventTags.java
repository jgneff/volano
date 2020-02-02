/*
 * EventTags.java - defines the XML tags for event notifications.
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
 * This interface defines the XML tags for event notifications.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

interface EventTags {
    String CREATE_START          = "<create>";
    String CREATE_END            = "</create>";
    String ENTER_MEMBER_START    = "<enter type=\"member\">";
    String ENTER_NONMEMBER_START = "<enter type=\"non-member\">";
    String ENTER_END             = "</enter>";
    String CHAT_START            = "<chat>";
    String CHAT_END              = "</chat>";
    String WHISPER_START         = "<whisper>";
    String WHISPER_END           = "</whisper>";
    String RING_START            = "<ring>";
    String RING_END              = "</ring>";
    String KICK_REMOVE_START     = "<kick type=\"remove\">";
    String KICK_DISCONNECT_START = "<kick type=\"disconnect\">";
    String KICK_BAN_START        = "<kick type=\"ban\">";
    String KICK_END              = "</kick>";
    String EXIT_START            = "<exit>";
    String EXIT_END              = "</exit>";
    String DELETE_START          = "<delete>";
    String DELETE_END            = "</delete>";

    String ROOM_START            = "<room>";
    String ROOM_END              = "</room>";
    String USER_START            = "<user>";
    String USER_END              = "</user>";
    String FROM_START            = "<from>";
    String FROM_END              = "</from>";
    String TO_START              = "<to>";
    String TO_END                = "</to>";
    String HOST_START            = "<host>";
    String HOST_END              = "</host>";
    String PROFILE_START         = "<profile>";
    String PROFILE_END           = "</profile>";
    String MESSAGE_START         = "<message>";
    String MESSAGE_END           = "</message>";
}
