/*
 * IClient.java - an interface representing a VolanoChat client.
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
 *
 * 16 Oct 1999
 *   - Added the showLink boolean parameter to the IClient.enterRoom callback
 *     method.
 *   - Added USER_LINK and USER_MAXINFO array indices.
 */

package COM.volano;

/**
 * This class defines a callback interface for a VolanoChat client.  The results
 * of any confirmed requests to the server are returned by asynchronous calls
 * into these methods.
 *
 * @author  John Neffenger
 * @version 2.13
 */

public interface IClient {
    /**
     * Array index for user name.
     */
    int USER_NAME    = 0;
    /**
     * Array index for user profile.
     */
    int USER_PROFILE = 1;
    /**
     * Array index for user host address.
     */
    int USER_HOST    = 2;
    /**
     * Array index for membership indicator.
     */
    int USER_MEMBER  = 3;
    /**
     * Array index for membership indicator.
     */
    int USER_LINK    = 4;
    /**
     * Size of user information array.
     */
    int USER_MAXINFO = 5;

    /**
     * Access result code for successful access.
     */
    int ACCESS_OKAY             = 1;
    /**
     * Access result code for host denied.
     */
    int ACCESS_HOST_DENIED      = 2;
    /**
     * Access result code for referring document denied.
     */
    int ACCESS_DOCUMENT_DENIED  = 3;
    /**
     * Access result code for applet version denied.
     */
    int ACCESS_VERSION_DENIED   = 4;
    /**
     * Access result code for invalid member, monitor, or administrator password.
     */
    int ACCESS_BAD_PASSWORD     = 5;
    /**
     * Access result code for a Java version on the client side that doesn't
     * support the authentication required by the server.
     */
    int ACCESS_BAD_JAVA_VERSION = 6;
    /**
     * Access result code for a connection with an IP address already connected
     * when multiple connections with the same IP address is disabled in the
     * VolanoChat server.
     */
    int ACCESS_HOST_DUPLICATE   = 7;

    /**
     * Room result code for successful result.
     */
    int ROOM_OKAY         = 1;
    /**
     * Room result code for room is full.
     */
    int ROOM_FULL         = 2;
    /**
     * Room result code for user name is taken in room.
     */
    int ROOM_NAME_TAKEN   = 3;
    /**
     * Room result code for user name is taken by a member of the server.
     */
    int ROOM_MEMBER_TAKEN = 4;
    /**
     * Room result code for no such room.
     */
    int ROOM_NOT_FOUND    = 5;

    /**
     * Unknown room type.
     */
    int ROOM_UNKNOWN = 0;
    /**
     * Room type for a normal room.
     */
    int ROOM_NORMAL = 1;
    /**
     * Room type for an event auditorium.
     */
    int ROOM_EVENT = 2;

    /**
     * Called to confirm an access request.
     *
     * @param result   the access result code.
     * @param rooms    the list of rooms in the server.
     * @param profile  the member profile string, if accessing as a member.
     */

    public void access(int result, String[] rooms, String profile);

    /**
     * Called to confirm a room list request.
     *
     * @param rooms  the list of rooms in the server.
     */

    public void roomList(String[] rooms);

    /**
     * Called to confirm a user list request.
     *
     * @param result        the room result code.
     * @param documentBase  the referring Web page which created this room, if not
     *                      a permanent room in the server.
     * @param users         the list of users in the room, with the name, profile,
     *                      host, and member information for each.
     */

    public void userList(int result, String documentBase, String[][] users);

    /**
     * Called to confirm an enter room request.
     *
     * @param result    the room result code.
     * @param roomName  the name of the room entered.
     * @param roomType  the type of the room, either normal or an event
     *                  auditorium.
     * @param count     the total count of people in the room or auditorium.
     * @param users     the list of users in the room, with the name, profile,
     *                  host, and member information for each.
     */

    public void enterRoom(int result, String roomName, int roomType, int count, String[][] users);

    /**
     * Called to indicate a user entered a room.
     *
     * @param roomName  the name of the room entered.
     * @param userName  the name of the user who entered the room.
     * @param profile   the profile of the user who entered the room.
     * @param address   the host address of the user who entered the room.
     * @param isMember  <code>true</code> if the user is a member; otherwise
     *                  <code>false</code>.
     * @param showLink  <code>true</code> if the member profile link is to be
     *                  displayed; otherwise <code>false</code>.
     */

    public void enterRoom(String roomName, String userName, String profile, String address, boolean isMember, boolean showLink);

    /**
     * Called to indicate a user exited a room.
     *
     * @param roomName  the name of the room exited.
     * @param userName  the name of the user who exited the room.
     */

    public void exitRoom(String roomName, String userName);

    /**
     * Called to confirm or indicate an enter private request.
     *
     * @param roomId    the private chat room identifier assigned by the server.
     * @param userName  the name of the other user.
     * @param profile   the profile of the other user.
     * @param address   the host address of the other user.
     */

    public void enterPrivate(int roomId, String userName, String profile, String address);

    /**
     * Called to indicate the other user exited a private chat room.
     *
     * @param roomId    the private chat room identifier.
     * @param userName  the name of the user exiting the private chat room.
     */

    public void exitPrivate(int roomId, String userName);

    /**
     * Called to indicate public or personal room chat text.
     *
     * @param roomName  the name of the room.
     * @param userName  the name of the user sending the chat text.
     * @param text      the chat text.
     */

    public void chat(String roomName, String userName, String text);

    /**
     * Called to indicate private chat text.
     *
     * @param roomId    the private chat room identifier.
     * @param userName  the name of the user sending the private chat text.
     * @param text      the private chat text.
     */

    public void chat(int roomId, String userName, String text);

    /**
     * Called to indicate administrator broadcast chat text.
     *
     * @param text  the broadcast chat text.
     */

    public void chat(String text);

    /**
     * Called to confirm a chat message.
     *
     * @param roomName  the name of the room from which the original message
     *                  was sent.
     */

    public void chatConfirm(String roomName);

    /**
     * Called to indicate a whisper message.
     *
     * @param roomName  the name of the room.
     * @param userName  the name of the user sending the whisper message.
     */

    public void whisper(String roomName, String userName, String message);

    /**
     * Called to indicate an audio alert.
     *
     * @param roomName  the name of the room.
     * @param userName  the name of the user sending the audio alert.
     */

    public void beep(String roomName, String userName);

    /**
     * Called to indicate the connection is closed.
     */

    public void close();
}
