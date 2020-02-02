/*
 * Sample.java - a sample VolanoChat client.
 */

import COM.volano.IClient;
import COM.volano.Server;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * This class implements a VolanoChat client console application.
 *
 * @author John Neffenger
 * @version 12 Apr 1998
 */
public class Sample implements IClient, Runnable {

    private static final String USAGE = "Usage: java Sample hostname port";
    private static final String[] HELP = {
        "  /list                  - list the rooms in the server",
        "  /select <name>         - select a room and list its users",
        "  /enter  <name>         - enter the selected room with the specified user name",
        "  <text>                 - send chat text",
        "  /whisper <name> <text> - send whisper text to the specified user",
        "  /beep   <name>         - send an audio alert to the specified user",
        "  /exit                  - exit the room",
        "  /quit                  - quit this program",
        "  ?                      - list commands"
    };
    private String hostName;             // Name of server host
    private int port;                    // Port number of server
    private Server server;               // Server object
    private BufferedReader reader;       // Console input stream
    private Thread thread;               // Console thread
    private String roomName;             // Current room name or null
    private String userName;             // Current user name or null
    private String profile = "";         // Current user profile

    public Sample(String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;

        System.out.println("Connecting to " + hostName + ":" + port + " ...");
        server = new Server(this, hostName, port);
        access();
        reader = new BufferedReader(new InputStreamReader(System.in));
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            String line = reader.readLine();
            while (line != null && server.isOpen()) {
                line = line.trim();
                if (line.length() > 0) {
                    if (line.startsWith("/")) {
                        if (line.startsWith("/list")) {
                            list();
                        } else if (line.startsWith("/select")) {
                            select(line.substring(7).trim());
                        } else if (line.startsWith("/enter")) {
                            enter(line.substring(6).trim());
                        } else if (line.startsWith("/whisper")) {
                            String temp = line.substring(8).trim();
                            int index = temp.indexOf(" ");
                            String toName = temp.substring(0, index).trim();
                            String message = temp.substring(index).trim();
                            whisper(toName, message);
                        } else if (line.startsWith("/beep")) {
                            beep(line.substring(5).trim());
                        } else if (line.startsWith("/exit")) {
                            exit();
                        } else if (line.startsWith("/quit")) {
                            quit();
                        } else {
                            unknown();
                        }
                    } else if (line.startsWith("?")) {
                        help();
                    } else {
                        sendchat(line);
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            if (server.isOpen()) {
                System.out.println("Error communicating with server. (" + e + ")");
                try {
                    server.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void prompt() {
        if (userName != null) {
            System.out.print(roomName + " (" + userName + ") (?)> ");
        } else if (roomName != null) {
            System.out.print(roomName + " (?)> ");
        } else {
            System.out.print(hostName + " (?)> ");
        }
    }

    private void printUser(String name, String address, String profile, boolean isMember) {
        System.out.println(name + " (" + address + ") " + (isMember ? "is a member." : "is not a member."));
        if (profile.length() > 0) {
            System.out.println("  " + profile);
        }
    }

    private void printUser(String[] user) {
        printUser(user[USER_NAME], user[USER_HOST], user[USER_PROFILE], Boolean.valueOf(user[USER_MEMBER]).booleanValue());
    }

    private void access() throws IOException {
        String javaVendor = System.getProperty("java.vendor", "");
        String javaVendorUrl = System.getProperty("java.vendor.url", "");
        String javaVersion = System.getProperty("java.version", "");
        String javaClassVersion = System.getProperty("java.class.version", "");
        String osName = System.getProperty("os.name", "");
        String osVersion = System.getProperty("os.version", "");
        String osArch = System.getProperty("os.arch", "");
        System.out.println("Requesting access to server ...");
        server.access("", Server.APPLET_PUBLIC,
                "http://www.volano.com/chat.html",
                "http://chat.volano.net/vcclient",
                javaVendor, javaVendorUrl, javaVersion, javaClassVersion,
                osName, osVersion, osArch);
    }

    private void list() throws IOException {
        server.roomList();
    }

    private void select(String roomName) throws IOException {
        if (roomName.length() == 0) {
            prompt();
        } else {
            this.roomName = roomName;
            server.userList(roomName);
        }
    }

    private void enter(String userName) throws IOException {
        if (roomName == null) {
            System.out.println("Select a room to enter.");
            prompt();
        } else if (userName.length() == 0) {
            prompt();
        } else {
            this.userName = userName;
            server.enterRoom(roomName, userName, "");
        }
    }

    private void sendchat(String text) throws IOException {
        if (userName == null) {
            System.out.println("Enter a room to chat.");
        } else {
            server.chat(roomName, userName, text);
        }
        prompt();
    }

    private void whisper(String toName, String message) throws IOException {
        if (userName == null) {
            System.out.println("Enter a room to send whisper messages.");
        } else if (toName.length() > 0 & message.length() > 0) {
            server.whisper(roomName, userName, toName, message);
        }
        if (!toName.equals(userName)) {
            prompt();
        }
    }

    private void beep(String toName) throws IOException {
        if (userName == null) {
            System.out.println("Enter a room to send audio alerts.");
        } else if (toName.length() > 0) {
            server.beep(roomName, userName, toName);
        }
        if (!toName.equals(userName)) {
            prompt();
        }
    }

    private void exit() throws IOException {
        if (userName == null) {
            System.out.println("Enter a room before exiting.");
        } else {
            server.exitRoom(roomName, userName);
            roomName = null;
            userName = null;
        }
        prompt();
    }

    private void quit() throws IOException {
        server.close();
        thread.interrupt();
    }

    private void unknown() {
        System.out.println("Unrecognized command.  Type \"?\" for help.");
        prompt();
    }

    private void help() {
        for (int i = 0; i < HELP.length; i++) {
            System.out.println(HELP[i]);
        }
        prompt();
    }

    public void access(int result, String[] rooms, String profile) {
        if (result == ACCESS_OKAY) {
            for (int i = 0; i < rooms.length; i++) {
                System.out.println(rooms[i]);
            }
        } else {
            System.out.println("Access denied.  Result = " + result + ".");
        }
        prompt();
    }

    public void roomList(String[] rooms) {
        for (int i = 0; i < rooms.length; i++) {
            System.out.println(rooms[i]);
        }
        prompt();
    }

    public void userList(int result, String documentBase, String[][] users) {
        for (int i = 0; i < users.length; i++) {
            printUser(users[i]);
        }
        prompt();
    }

    public void enterRoom(int result, String roomName, int type, int count, String[][] users) {
        if (result == ROOM_OKAY) {
            for (int i = 0; i < users.length; i++) {
                printUser(users[i]);
            }
        } else {
            System.out.println("Unable to enter room.  Result = " + result + ".");
        }
        prompt();
    }

    public void enterRoom(String roomName, String userName, String profile, String address, boolean isMember, boolean showLink) {
        System.out.println();
        printUser(userName, address, profile, isMember);
        prompt();
    }

    public void exitRoom(String roomName, String userName) {
        System.out.println();
        System.out.println(userName + " left room.");
        prompt();
    }

    public void enterPrivate(int roomId, String userName, String profile, String address) {
        // Not implemented in this client.
        prompt();
    }

    public void exitPrivate(int roomId, String userName) {
        // Not implemented in this client.
        prompt();
    }

    public void chat(String roomName, String userName, String text) {
        System.out.println();
        System.out.println("<" + userName + "> " + text);
        prompt();
    }

    public void chat(int roomId, String userName, String text) {
        System.out.println();
        System.out.println("<" + userName + "> " + text);
        prompt();
    }

    public void chat(String text) {
        System.out.println();
        System.out.println("*** " + text);
        prompt();
    }

    public void chatConfirm(String roomName) {
        // Not implemented in this client.
    }

    public void whisper(String roomName, String userName, String message) {
        System.out.println();
        System.out.println("<" + userName + " -> " + this.userName + "> " + message);
        prompt();
    }

    public void beep(String roomName, String userName) {
        System.out.println();
        System.out.println("<" + userName + ">" + '\007');
        prompt();
    }

    public void close() {
        System.out.println();
        System.out.println("Disconnected from " + hostName + ":" + port + ".");
        thread.interrupt();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(USAGE);
            return;
        }
        try {
            String hostName = args[0];
            int port = Integer.parseInt(args[1]);
            new Sample(hostName, port);
        } catch (NumberFormatException e) {
            System.err.println("Port number must be an integer. (" + e + ")");
            System.err.println(USAGE);
        } catch (IOException e) {
            System.err.println("Error connecting to server (" + e + ")");
        }
    }
}

