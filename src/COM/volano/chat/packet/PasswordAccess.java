/*
 * PasswordAccess.java - a packet for gaining password access to the server.
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

package COM.volano.chat.packet;
import  java.io.*;

/**
 * This class encapsulates a packet for gaining monitor or administrator
 * password access to the server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     Access
 */

public class PasswordAccess extends Access {
    // Request fields.
    private boolean member;
    private boolean monitor;
    private boolean admin;
    private String  memberName     = "";
    private String  memberPassword = "";
    private String  password       = "";
    private boolean stage;
    private String  topic          = "";

    // Confirmation fields.
    private String  profile = "";

    /**
     * The no-arg constructor required for deserialization.
     */

    public PasswordAccess() {}

    /**
     * Creates a password access request packet.  The constructor parameters are
     * the same as those for an <code>Access</code> request, with the following
     * additonal parameters.
     *
     * @param member         <code>true</code> if member access is enabled for
     *                       the client; otherwise <code>false</code>.
     * @param monitor        <code>true</code> if monitor access is enabled for
     *                       the client; otherwise <code>false</code>.
     * @param admin          <code>true</code> if administrator access is enabled
     *                       for the client; otherwise <code>false</code>.
     * @param memberName     the member name or an empty string for no name.
     * @param memberPassword the member password or an empty string for no
     *                       password.
     * @param password       the monitor or administrative password, or an empty
     *                       string for no password.
     * @param stage          <code>true</code> if this client entered from the
     *                       event stage; otherwise <code>false</code>.
     * @param topic          the event topic description.
     * @see Access#Access
     */

    public PasswordAccess(String defaultRoom, String appletVersion, String documentBase, String codeBase,
                          String javaVendor, String javaVendorUrl, String javaVersion, String javaClassVersion,
                          String osName, String osVersion, String osArch,
                          boolean member, boolean monitor, boolean admin,
                          String memberName, String memberPassword, String password,
                          boolean stage, String topic) {
        super(defaultRoom, appletVersion, documentBase, codeBase,
              javaVendor, javaVendorUrl, javaVersion, javaClassVersion,
              osName, osVersion, osArch);
        this.member         = member;
        this.monitor        = monitor;
        this.admin          = admin;
        this.memberName     = memberName;
        this.memberPassword = memberPassword;
        this.password       = password;
        this.stage          = stage;
        this.topic          = topic;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_PASSWORD_ACCESS;
    }

    /**
     * Changes this packet into a positive password access confirmation, sent when
     * client authentication is enabled.  The room list is sent on the
     * authenticate confirmation.
     *
     * @param result   the result code indicating that the client was granted
     *                 access to the server, defined as static fields in this
     *                 class.
     * @param bytes    random bytes for the client to sign, or a zero-length array
     *                 if no client authentication is required.
     * @param profile  the member profile if the member access is enabled.
     */

    public void confirm(int result, byte[] bytes, String profile) {
        super.confirm(result, bytes);
        this.profile = profile;
    }

    /**
     * Changes this packet into a positive password access confirmation, sent when
     * client authentication is disabled.  The room list is sent with this access
     * confirmation since no authenticate confirmation will be sent.  For
     * MyVolanoChat, the room list is a zero-length array.
     *
     * @param result   the result code indicating that the client was granted
     *                 access to the server, defined as static fields in this
     *                 class.
     * @param rooms    an array listing the names of all the public rooms in the
     *                 server.
     * @param profile  the member profile if the member access is enabled.
     */

    public void confirm(int result, String[] rooms, String profile) {
        super.confirm(result, rooms);
        this.profile = profile;
    }

    /**
     * Checks whether member access is enabled from the client.
     *
     * @return <code>true</code> if member access is enabled; otherwise
     *         <code>false</code>.
     */

    public boolean isMember() {
        return member;
    }

    /**
     * Checks whether monitor access is enabled from the client.
     *
     * @return <code>true</code> if monitor access is enabled; otherwise
     *         <code>false</code>.
     */

    public boolean isMonitor() {
        return monitor;
    }

    /**
     * Checks whether administrator access is enabled from the client.
     *
     * @return <code>true</code> if administrator access is enabled; otherwise
     *         <code>false</code>.
     */

    public boolean isAdmin() {
        return admin;
    }

    /**
     * Gets the member name.
     *
     * @return the member name.
     */

    public String getMemberName() {
        return memberName;
    }

    /**
     * Gets the member password.
     *
     * @return the member password.
     */

    public String getMemberPassword() {
        return memberPassword;
    }

    /**
     * Gets the monitor or administrator password string.
     *
     * @return the monitor or administrator password.
     */

    public String getPassword() {
        return password;
    }

    /**
     * Gets the member profile.
     *
     * @return the member profile.
     */

    public String getProfile() {
        return profile;
    }

    /**
     * Checks whether this client entered on stage.
     *
     * @return <code>true</code> if this client entered on stage; otherwise
     *         <code>false</code>.
     */

    public boolean isStage() {
        return stage;
    }

    /**
     * Gets the event topic description.  The event topic is set when this client
     * entered on stage.
     *
     * @return the event topic.
     */

    public String getTopic() {
        return topic;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        switch (getType()) {
        case REQUEST:
            output.writeBoolean(member);
            output.writeBoolean(monitor);
            output.writeBoolean(admin);
            output.writeUTF(memberName);
            output.writeUTF(memberPassword);
            output.writeUTF(password);
            output.writeBoolean(stage);
            output.writeUTF(topic);
            break;
        case CONFIRM:
            output.writeUTF(profile);
            break;
        }
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        switch (getType()) {
        case REQUEST:
            member         = input.readBoolean();
            monitor        = input.readBoolean();
            admin          = input.readBoolean();
            memberName     = input.readUTF();
            memberPassword = input.readUTF();
            password       = input.readUTF();
            stage          = input.readBoolean();
            topic          = input.readUTF();
            break;
        case CONFIRM:
            profile        = input.readUTF();
            break;
        }
    }
}
