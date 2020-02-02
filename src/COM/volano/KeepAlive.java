/*
 * KeepAlive.java - a Java application to keep alive a child process.
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

package COM.volano;
import  java.io.*;
import  java.util.*;

/**
 * This class is a Java console application which starts up a secondary
 * application, restarting it if the application ever stops.  This class
 * redirects the output and error streams of the secondary application to the
 * output and error streams of this application.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class KeepAlive implements Runnable {
    private static final String USAGE = "Usage: java COM.volano.KeepAlive server";
    private static final int    PAUSE = 15 * 1000;

    private PrintStream    output;
    private BufferedReader input;

    /**
     * Creates a <code>KeepAlive</code> object to redirect either the standard
     * output or the standard error streams of the child process to the same
     * stream of this process.
     *
     * @param output  the standard output or error stream of this process.
     * @param input   the standard input or error stream of the child process.
     */

    private KeepAlive(PrintStream output, InputStream input) {
        this.output = output;
        this.input  = new BufferedReader(new InputStreamReader(input));
        new Thread(this).start();
    }

    /**
     * Reads each line from the input stream and writes it to the output stream.
     * The input stream is the output or error stream of the child process.  The
     * output stream is the output or error stream of this process.
     */

    public void run() {
        try {
            String line = input.readLine();
            while (line != null) {
                output.println(line);
                line = input.readLine();
            }
        } catch (IOException e) {
            // Usually means server process terminated abruptly.
        } finally {
            try {
                input.close();
            } catch (IOException e) {}
        }
    }

    /**
     * Obtains a timestamp suitable for a log file.
     *
     * @return  a string containing a formatted date and time.
     */

    private static String date() {
        return "[" + new Date() + "]";
    }

    /**
     * Connects an input stream from the child process to an output stream of
     * this process.  The input stream is the standard output or error stream of
     * the child process.
     *
     * @param output  the standard output or error stream of this process.
     * @param input   the standard output or error stream of the child process.
     */

    private static void connect(PrintStream output, InputStream input) {
        new KeepAlive(output, input);
    }

    /**
     * Starts up the specified secondary application, redirects its output streams
     * to those of this program, and waits for it to exit.  This program restarts
     * the application if it stops.
     *
     * @param args  the full command string of the server application to keep
     *              alive.
     */

    public static void main (String args[]) {
        if (args.length == 0) {
            System.err.println(USAGE);
            return;
        }

        System.out.print(date() + " Server is: ");
        for (int i = 0; i < args.length; i++) {
            System.out.print(" " + args[i]);
        }
        System.out.println();

        try {
            while (true) {
                System.out.println(date() + " Starting server ...");
                Process child = Runtime.getRuntime().exec(args);
                connect(System.out, child.getInputStream());
                connect(System.err, child.getErrorStream());
                int exitValue = child.waitFor();
                System.out.println(date() + " Server completed with exit value " + exitValue + ".");
                Thread.currentThread().sleep(PAUSE);
            }
        } catch (IOException e) {
            System.err.println(date() + " Unable to start server (" + e + ").");
        } catch (InterruptedException e) {
            System.err.println(date() + " Interrupted while monitoring server (" + e + ").");
        } finally {
            System.out.println(date() + " Leaving program.");
        }
    }
}
