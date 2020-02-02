/*
 * Test.java - the VolanoTest coverage test driver.
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

package COM.volano.chat.test;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.ChatPacketFactory;
import  COM.volano.chat.server.*;
import  COM.volano.net.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

public class Test extends Base {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String NAME      = new Test().getClass().getName();
    private static final String VERSION   = Build.VERSION;
    private static final String FULL_NAME = NAME + " Version " + VERSION;
    private static final String LOCALHOST = "localhost";

    private static String  file   = Build.TEST_LOG;       // Output file name
    private static String  host   = LOCALHOST;    // Host name
    private static int     port   =  8000;        // Port number
    private static String  prefix = "Test";       // Prefix for user names
    private static int     start  =     1;        // Starting room number
    private static int     rooms  =    10;        // Number of rooms
    private static int     users  =    20;        // Number of users per room
    private static int     level  =     4;        // Access level
    private static int     loops  =    10;        // Number of test loops
    private static boolean recon  = false;        // Reconnect on each loop

    private static final String USAGE = "Usage: java COM.volano.Test [options]" + LINE_SEPARATOR + LINE_SEPARATOR +
                                        "where options include (default in parenthesis):" + LINE_SEPARATOR +
                                        "  -run              run the test" + LINE_SEPARATOR +
                                        "  -help             print this message" + LINE_SEPARATOR +
                                        "  -version          print version information" + LINE_SEPARATOR +
                                        "  -jvm              print Java Virtual Machine environment" + LINE_SEPARATOR +
                                        "  -file   <string>  output file name (" + file + ")" + LINE_SEPARATOR +
                                        "  -host   <string>  server host name (" + host + ")" + LINE_SEPARATOR +
                                        "  -port   <integer> server port number (" + port + ")" + LINE_SEPARATOR +
                                        "  -prefix <string>  user name prefix (" + prefix + ")" + LINE_SEPARATOR +
                                        "  -start  <integer> starting room number (" + start + ")" + LINE_SEPARATOR +
                                        "  -rooms  <integer> number of rooms (" + rooms + ")" + LINE_SEPARATOR +
                                        "  -users  <integer> number of users per room (" + users + ")" + LINE_SEPARATOR +
                                        "  -level  <integer> access level of 1-4 (" + level + ")" + LINE_SEPARATOR +
                                        "  -loops  <integer> number of test loops or 0 for non-stop (" + loops + ")" + LINE_SEPARATOR +
                                        "  -recon            reconnect on loop for levels 2-4 (" + recon + ")";

    private static long millis;
    private static long seconds;

    private static void setOptions(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(USAGE);
            throw new Exception();
        }

        int index = 0;
        while (index < args.length) {
            String option = args[index];
            try {
                if (option.equals("-help")) {
                    System.out.println(USAGE);
                    throw new Exception();
                } else if (option.equals("-version")) {
                    System.out.println(FULL_NAME);
                    throw new Exception();
                } else if (option.equals("-jvm")) {
                    Runtime runtime = Runtime.getRuntime();
                    printUsage(runtime.totalMemory(), runtime.freeMemory());
                    printJVM(System.out);
                    throw new Exception();
                } else if (option.equals("-file")) {
                    file = args[++index];
                } else if (option.equals("-host")) {
                    host = args[++index];
                } else if (option.equals("-port")) {
                    port = Integer.parseInt(args[++index]);
                } else if (option.equals("-prefix")) {
                    prefix = args[++index];
                } else if (option.equals("-start")) {
                    start = Integer.parseInt(args[++index]);
                } else if (option.equals("-rooms")) {
                    rooms = Integer.parseInt(args[++index]);
                } else if (option.equals("-users")) {
                    users = Integer.parseInt(args[++index]);
                } else if (option.equals("-level")) {
                    level = Integer.parseInt(args[++index]);
                    if (level < 1 || level > 4) {
                        throw new Exception(option + ": value must be 1 to 4");
                    }
                } else if (option.equals("-loops")) {
                    loops = Integer.parseInt(args[++index]);
                } else if (option.equals("-recon")) {
                    recon = true;
                } else if (! option.equals("-run")) {
                    throw new Exception(option + ": illegal argument");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new Exception(option + ": missing value");
            } catch (NumberFormatException e) {
                throw new Exception(option + ": value is not an integer (" + e.getMessage() + ")");
            }
            index++;
        }
    }

    private static Vector createUsers() {
        Vector roomList = new Vector(rooms);
        for (int i = start; i < start + rooms; i++) {
            System.out.println("Creating users for room number " + i + " ...");
            TestUser[] userList = new TestUser[users];
            for (int j = 0; j < users; j++) {
                userList[j] = new TestUser(host, port, prefix, i, j + 1, users, level, loops, recon);
            }
            roomList.addElement(userList);
        }
        return roomList;
    }

    private static void waitUsers(Vector roomList) throws InterruptedException {
        Enumeration enumeration = roomList.elements();
        while (enumeration.hasMoreElements()) {
            TestUser[] user = (TestUser[]) enumeration.nextElement();
            for (int j = 0; j < users; j++) {
                if (user[j] != null) {
                    user[j].waitUser();
                }
            }
        }
    }

    private static void getResults(long begin, long end) {
        millis  = end - begin;
        seconds = Math.round((float) millis / 1000.0f);
    }

    private static void printResults(PrintStream output) {
        output.println("VolanoTest version = " + VERSION);
        if (seconds < 1) {
            output.println("Elapsed time       < 1 second");
        } else {
            output.println("Elapsed time       = " + seconds + (seconds > 1 ? " seconds" : " second"));
        }
    }

    public static void main (String args[]) throws IOException, InterruptedException {
        try {
            setOptions(args);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null) {
                System.err.println(message);
                System.err.println(USAGE);
            }
            return;
        }

        System.out.println(FULL_NAME);
        System.out.println(COPYRIGHT);
        Connection.setPacketFactory(new ChatPacketFactory());
        Vector roomList = createUsers();

        fullGC(Runtime.getRuntime());
        System.out.println("Running the test ...");
        long begin = System.currentTimeMillis();
        TestUser.notifyStart();     // Start the test
        waitUsers(roomList);        // Wait for all threads to complete
        long end = System.currentTimeMillis();
        System.out.println("Test complete.");

        getResults(begin, end);
        printResults(System.out);

        PrintStream output = new PrintStream(new FileOutputStream(new File(file)), true);
        printJVM(output);
        output.println();
        printResults(output);
    }
}
