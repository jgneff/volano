/*
 * Access.java - a packet for accessing the chat server.
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
import  COM.volano.net.Packet;
import  COM.volano.chat.Build;
import  java.io.*;

/**
 * This class encapsulates a server access request and confirmation.  Note that
 * this class must be kept backward compatible with VolanoChat 1.2 in order for
 * servers based on VolanoChat 2.0 and later servers to correctly interpret and
 * deny access to earlier, incompatible applets.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Access extends Packet {
    // Access request applet version strings.
    public static final String PUBLIC_VERSION   = Build.VERSION;
    public static final String PERSONAL_VERSION = Build.VERSION + "-p";

    // Access confirmation result codes.  For backward compatibility, the 1.2
    // result codes must remain unchanged.
    public static final int OKAY             = 1;
    public static final int HOST_DENIED      = 2;
    public static final int DOCUMENT_DENIED  = 3;
    public static final int VERSION_DENIED   = 4;
    public static final int BAD_PASSWORD     = 5;  // New to 2.0
    public static final int BAD_JAVA_VERSION = 6;  // New to 2.1
    public static final int HOST_DUPLICATE   = 7;  // New to 2.5

    private static final String OLD_NAME        = "COM.volano.Access";
    static final int    OLD_NAME_LENGTH = 17;
    static final int    ID              =  1;

    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    // When true, we received a version 2.1 COM.volano.Access packet so we need
    // to read and write the 17-byte "COM.volano.Access" class name before
    // handling the actual instance variables.
    //     Hex: 0011 434F4D2D ...
    //   Value:   17  C O M . v o l a n o . A c c e s s" ...
    private boolean oldClient;

    // Request fields.
    private String defaultRoom      = "";
    private String appletVersion    = "";         // See constants above
    private String documentBase     = "";
    private String codeBase         = "";
    private String javaVendor       = "";
    private String javaVendorUrl    = "";
    private String javaVersion      = "";
    private String javaClassVersion = "";
    private String osName           = "";
    private String osVersion        = "";
    private String osArch           = "";

    // Confirmation fields.
    private int      result;                      // See constants above
    private String[] rooms = new String[0];
    private byte[]   bytes = new byte[0];

    /*
      private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
                                            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

      private static String hexEncode(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
          byte b = bytes[i];
          buffer.append(DIGITS[(b & 0xF0) >> 4]);
          buffer.append(DIGITS[b & 0x0F]);
        }
        return buffer.toString();
      }
    */

    /**
     * Sets the read pause for this class of objects.
     *
     * @param pause the pause, in milliseconds, after reading an object of this
     *              class.
     */

    public static void setReadPause(long pause) {
        readPause = pause;
    }

    /**
     * Sets the write pause for this class of objects.
     *
     * @param pause the pause, in milliseconds, after writing an object of this
     *              class.
     */

    public static void setWritePause(long pause) {
        writePause = pause;
    }

    /**
     * Gets the read pause for this object.
     *
     * @return the pause, in milliseconds, after reading this object.
     */

    public long getReadPause() {
        return readPause;
    }

    /**
     * Gets the write pause for this object.
     *
     * @return the pause, in milliseconds, after writing this object.
     */

    public long getWritePause() {
        return writePause;
    }

    /**
     * The no-arg constructor required for deserialization.
     */

    public Access() {
        this.oldClient = false;
    }

    /**
     * The constructor required for deserialization of a packet from a version
     * 2.1 client.
     *
     * @param oldClient when true, we are communicating with a version 2.1
     *                  client.  Otherwise false.
     */

    public Access(boolean oldClient) {
        this.oldClient = oldClient;
    }

    /**
     * Creates an access request packet containing information about the client
     * applet's version, referring Web page, origin, Java environment, and
     * operating system platform.  Also included is the room name or document
     * base to identify the room dynamically created when the server is accessed
     * through the applet's Web page.
     *
     * @param defaultRoom       the name of the room identified on the referring
     *                          Web page by the <i>group</i> applet parameter, or
     *                          an empty string if the parameter is undefined.
     *                          If the client is the MyVolanoChat applet
     *                          requesting access to a personal room, the applet's
     *                          document base is passed as the default room.
     * @param appletVersion     the version string identifying the type and
     *                          version of the requesting applet, defined as
     *                          static fields in this class.
     * @param documentBase      the client applet document base.
     * @param codeBase          the client applet code base.
     * @param javaVendor        the <code>java.vendor</code> property.
     * @param javaVendorUrl     the <code>java.vendor.url</code> property.
     * @param javaVersion       the <code>java.version</code> property.
     * @param javaClassVersion  the <code>java.class.version</code> property.
     * @param osName            the <code>os.name</code> property.
     * @param osVersion         the <code>os.version</code> property.
     * @param osArch            the <code>os.arch</code> property.
     */

    public Access(String defaultRoom, String appletVersion, String documentBase, String codeBase,
                  String javaVendor, String javaVendorUrl, String javaVersion, String javaClassVersion,
                  String osName, String osVersion, String osArch) {
        this.defaultRoom      = defaultRoom;
        this.appletVersion    = appletVersion;
        this.documentBase     = documentBase;
        this.codeBase         = codeBase;
        this.javaVendor       = javaVendor;
        this.javaVendorUrl    = javaVendorUrl;
        this.javaVersion      = javaVersion;
        this.javaClassVersion = javaClassVersion;
        this.osName           = osName;
        this.osVersion        = osVersion;
        this.osArch           = osArch;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return oldClient ? ChatPacketId.PACKET_ACCESS_OLD : ChatPacketId.PACKET_ACCESS;
    }

    /**
     * Changes this packet into a negative access confirmation.
     *
     * @param result  the result code indicating the reason the client was refused
     *                access to the server.
     */

    public void confirm(int result) {
        setType(CONFIRM);
        this.result = result;
    }

    /**
     * Changes this packet into a positive confirmation, sent when client
     * authentication is enabled.  The room list is sent on the authenticate
     * confirmation.
     *
     * @param result  the result code indicating that the client was granted
     *                access to the server, defined as static fields in this
     *                class.
     * @param bytes   random bytes for the client to sign.
     */

    public void confirm(int result, byte[] bytes) {
        confirm(result);
        this.bytes = bytes;
    }

    /**
     * Changes this packet into a positive confirmation, sent when client
     * authentication is disabled.  The room list is sent with this access
     * confirmation since no authenticate confirmation will be sent.  For
     * MyVolanoChat, the room list is a zero-length array.
     *
     * @param result  the result code indicating that the client was granted
     *                access to the server, defined as static fields in this
     *                class.
     * @param rooms   an array listing the names of all the public rooms in the
     *                server.
     */

    public void confirm(int result, String[] rooms) {
        confirm(result, bytes);
        this.rooms = rooms;
    }

    /**
     * Gets the name of the public room to create automatically and keep available
     * for as long as this client is connected.  For connections to the
     * MyVolanoChat applet, this method gets the document base to uniquely
     * identify the personal room created from the referring Web page.
     *
     * @return the name of the public room for connections to the VolanoChat
     *         applet, or the document base of the applet for connections to the
     *         MyVolanoChat applet.
     */

    public String getDefaultRoom() {
        return defaultRoom;
    }

    /**
     * Gets the applet type and version of the client applet.
     *
     * @return the type and version string of the applet.
     */

    public String getAppletVersion() {
        return appletVersion;
    }

    /**
     * Gets the document base of the client applet.
     *
     * @return the URL of the Web page containing the client applet.
     */

    public String getDocumentBase() {
        return documentBase;
    }

    /**
     * Gets the code base of the client applet.
     *
     * @return the URL identifying the location from which the applet was loaded.
     */

    public String getCodeBase() {
        return codeBase;
    }

    /**
     * Gets the name of the vendor of the Java virtual machine running the client
     * applet.
     *
     * @return the <code>java.vendor</code> system property of the applet.
     */

    public String getJavaVendor() {
        return javaVendor;
    }

    /**
     * Gets the Web address of the vendor of the Java virtual machine running the
     * client applet.
     *
     * @return the <code>java.vendor.url</code> system property of the applet.
     */

    public String getJavaVendorUrl() {
        return javaVendorUrl;
    }

    /**
     * Gets the version string of the Java virtual machine running the client
     * applet.
     *
     * @return the <code>java.version</code> system property of the applet.
     */

    public String getJavaVersion() {
        return javaVersion;
    }

    /**
     * Gets the API version string of the Java virtual machine running the client
     * applet.
     *
     * @return the <code>java.class.version</code> system property of the applet.
     */

    public String getJavaClassVersion() {
        return javaClassVersion;
    }

    /**
     * Gets the name of the operating system running the client applet.
     *
     * @return the <code>os.name</code> system property of the applet.
     */

    public String getOsName() {
        return osName;
    }

    /**
     * Gets the version of the operating system running the client applet.
     *
     * @return the <code>os.version</code> system property of the applet.
     */

    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Gets the name of the processor architecture running the client applet.
     *
     * @return the <code>os.arch</code> system property of the applet.
     */

    public String getOsArch() {
        return osArch;
    }

    /**
     * Gets the access confirmation result code.
     *
     * @return the confirmation result code.
     */

    public int getResult() {
        return result;
    }

    /**
     * Gets the names of the rooms in the server.
     *
     * @return the list of room names.
     */

    public String[] getRooms() {
        return rooms;
    }

    /**
     * Gets the random bytes for the client to sign.
     *
     * @return the random bytes to sign.
     */

    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        if (oldClient) {
            output.writeBytes(OLD_NAME);
        }
        super.writeTo(output);
        switch (getType()) {
        case REQUEST:
            output.writeUTF(defaultRoom);
            output.writeUTF(appletVersion);
            output.writeUTF(documentBase);
            output.writeUTF(codeBase);
            output.writeUTF(javaVendor);
            output.writeUTF(javaVendorUrl);
            output.writeUTF(javaVersion);
            output.writeUTF(javaClassVersion);
            output.writeUTF(osName);
            output.writeUTF(osVersion);
            output.writeUTF(osArch);
            break;
        case CONFIRM:
            output.writeInt(result);
            output.writeInt(rooms.length);  // Keep this writeInt for version 2.1
            for (int i = 0; i < rooms.length; i++) {
                output.writeUTF(rooms[i]);
            }
            // Put this last for backward compatibility with 2.1 applets getting the
            // VERSION_DENIED result code.
            if (! oldClient) {
                output.writeByte(bytes.length);  // Less than 255 random bytes
                if (bytes.length > 0) {          // Otherwise client side hangs if zero
                    output.write(bytes);
                }
            }
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
        if (oldClient) {
            input.readFully(new byte[OLD_NAME_LENGTH]);
        }
        super.readFrom(input);
        switch (getType()) {
        case REQUEST:
            defaultRoom      = input.readUTF().trim();  // 2.1.10
            appletVersion    = input.readUTF();
            documentBase     = input.readUTF();
            codeBase         = input.readUTF();
            javaVendor       = input.readUTF();
            javaVendorUrl    = input.readUTF();
            javaVersion      = input.readUTF();
            javaClassVersion = input.readUTF();
            osName           = input.readUTF();
            osVersion        = input.readUTF();
            osArch           = input.readUTF();
            break;
        case CONFIRM:
            result = input.readInt();
            int count = input.readInt();    // Keep this readInt for version 2.1
            rooms = new String[count];
            for (int i = 0; i < count; i++) {
                rooms[i] = input.readUTF();
            }
            // Put this last for backward compatibility with 2.1 applets getting the
            // VERSION_DENIED result code.
            if (! oldClient) {
                int size = input.readUnsignedByte();
                bytes = new byte[size];
                if (size > 0) {               // Otherwise client side hangs if zero
                    input.read(bytes);
                }
                // System.out.println("Challenge = " + hexEncode(bytes));
            }
            break;
        }
    }
}
