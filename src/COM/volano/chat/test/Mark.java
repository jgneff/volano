/*
 * Mark.java - the VolanoMark benchmark.
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
import  COM.volano.net.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

public class Mark extends Base {
    public  static final String  COPYRIGHT    = Build.COPYRIGHT;
    private static final String  LOCALHOST    = "localhost";
    private static final boolean PACKET_COUNT = false;

    private static String file  = Build.MARK_LOG; // Output file name
    private static String host  = LOCALHOST;      // Host name
    private static int    port  = 8000;           // Port number
    private static int    start =    1;           // Starting room number
    private static int    rooms =   50;           // Number of rooms
    private static int    users =   20;           // Number of users per room
    private static int    count =  100;           // Number of messages per user (0 = no limit)
    private static int    pause =    0;           // Pause between chat messages (0 = paced chat)

    private static final String USAGE = "Usage: java COM.volano.Mark [options]" + LINE_SEPARATOR +
                                        LINE_SEPARATOR +
                                        "where options include (default in parenthesis):" + LINE_SEPARATOR +
                                        "  -run              run the benchmark" + LINE_SEPARATOR +
                                        "  -help             print this message" + LINE_SEPARATOR +
                                        "  -version          print version information" + LINE_SEPARATOR +
                                        "  -jvm              print Java Virtual Machine environment" + LINE_SEPARATOR +
                                        "  -file  <string>   output file name (" + file + ")" + LINE_SEPARATOR +
                                        "  -host  <string>   server host name (" + host + ")" + LINE_SEPARATOR +
                                        "  -port  <integer>  server port number (" + port + ")" + LINE_SEPARATOR +
                                        "  -start <integer>  starting room number (" + start + ")" + LINE_SEPARATOR +
                                        "  -rooms <integer>  number of rooms (" + rooms + ")" + LINE_SEPARATOR +
                                        "  -users <integer>  number of users per room (" + users + ")" + LINE_SEPARATOR +
                                        "  -count <integer>  messages per user or 0 for no limit (" + count + ")" + LINE_SEPARATOR +
                                        "  -pause <integer>  message pause in seconds or 0 for pacing (" + pause + ")";

    private static long  millis;
    private static long  sent;
    private static long  received;
    private static long  total;
    private static float seconds;
    private static long  average;

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
                    System.out.println(Build.MARK_CLIENT_TITLE);
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
                    if (Build.MARK_LIMITED) {
                        if (! host.equals(LOCALHOST)) {
                            throw new Exception(option + ": must be localhost");
                        }
                    }
                } else if (option.equals("-port")) {
                    port = Integer.parseInt(args[++index]);
                } else if (option.equals("-start")) {
                    start = Integer.parseInt(args[++index]);
                } else if (option.equals("-rooms")) {
                    rooms = Integer.parseInt(args[++index]);
                } else if (option.equals("-users")) {
                    users = Integer.parseInt(args[++index]);
                    if (users < 2) {
                        throw new Exception(option + ": must be at least 2");
                    }
                } else if (option.equals("-count")) {
                    count = Integer.parseInt(args[++index]);
                } else if (option.equals("-pause")) {
                    pause = Integer.parseInt(args[++index]);
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

    private static void createUsers() throws InterruptedException {
        for (int i = start; i < start + rooms; i++) {
            System.out.println("Creating room number " + i + " ...");
            MarkUser[] user = new MarkUser[users];
            for (int j = 0; j < users; j++) {
                try {
                    Connection connection = new Connection(host, port);
                    connection.getSocket().setTcpNoDelay(true);   // Disable Nagle algorithm
                    user[j] = new MarkUser(connection, i, j + 1, users, count, pause);
                    user[j].waitUser();
                } catch (IOException e) {
                    user[j] = null;
                    System.err.println("Error connecting user " + (j + 1) + " in room " + i + " (" + e + ").");
                }
            }
            System.out.println(Connection.getCount() +  " connections so far.");
        }
    }

    private static void getResults(long begin, long end) {
        millis   = end - begin;
        sent     = MarkUser.getSent();
        received = MarkUser.getReceived();
        total    = sent + received;
        seconds  = (float) millis / 1000.0f;
        average  = Math.round((float) total / seconds);
    }

    private static void printResults(PrintStream output) {
        output.println("VolanoMark version = " + Build.VERSION);
        output.println("Messages sent      = " + sent);
        output.println("Messages received  = " + received);
        output.println("Total messages     = " + total);
        if (seconds < 1) {
            output.println("Elapsed time       < 1 second");
        } else {
            output.println("Elapsed time       = " + seconds + (seconds > 1 ? " seconds" : " second"));
            output.println("Average throughput = " + average + " messages per second");
        }

        if (PACKET_COUNT) {
            output.println();
            output.println("Packets sent       = " + Connection.getSent());
            output.println("Packets received   = " + Connection.getReceived());
        }
    }

    public static void main (String[] args) throws IOException, InterruptedException {
        if (! Build.MARK_LIMITED) {
            try {
                setOptions(args);
            } catch (Exception e) {
                String message = e.getMessage();
                if (message != null) {
                    System.err.println(e.getMessage());
                    System.err.println(USAGE);
                }
                System.exit(1);
            }
        }

        System.out.println(Build.MARK_CLIENT_TITLE);
        System.out.println(Build.COPYRIGHT);
        Connection.setPacketFactory(new ChatPacketFactory());
        if (Build.MARK_LIMITED) {
            host = InetAddress.getLocalHost().getHostName();
            if (host == null) {
                throw new IOException("Local host name is undefined");
            }
        }

        if (PACKET_COUNT) {
            Connection.setCounting(true);
        }

        createUsers();
        if (Connection.getCount() != rooms * users) {
            System.err.println("Test failed.");
            System.exit(1);
        }

        fullGC(Runtime.getRuntime());
        System.out.println("Running the test ...");
        long begin = System.currentTimeMillis();
        MarkUser.notifyStart();             // Start the test
        boolean okay = MarkUser.waitEnd();  // Wait for all users to complete
        long end = System.currentTimeMillis();

        if (! okay) {
            System.err.println("Test failed.");
            System.exit(1);
        }

        System.out.println("Test complete.");
        getResults(begin, end);
        System.out.println();
        printResults(System.out);

        PrintStream output = new PrintStream(new FileOutputStream(new File(file)), true);
        printJVM(output);
        output.println();
        printResults(output);
    }
}
