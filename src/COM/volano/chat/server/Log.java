/*
 * Log.java - contains static variables and methods for chat server logging.
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

package COM.volano.chat.server;
import  COM.volano.chat.Build;
import  java.io.ByteArrayOutputStream;
import  java.io.IOException;
import  java.util.Date;
import  org.apache.catalina.LifecycleException;
import  org.apache.catalina.Logger;
import  org.apache.catalina.logger.FileLogger;

/**
 * This class contains the print writers for the access, public room, and
 * private room log files, as well as methods for printing error messages.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class Log extends ByteArrayOutputStream {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    static boolean     verbose;   // Verbose error messages
    /*
      static PrintStream error;     // Error log for standard error
      static PrintWriter access;    // Extended Common Log Format access log file
      static PrintWriter pub;       // Public room log file
      static PrintWriter pvt;       // Private room log file
    */
    static Logger error;   // Error log for standard error
    static Logger access;  // Extended Common Log Format access log file
    static Logger pub;     // Public room log file
    static Logger pvt;     // Private room log file
    static Logger ban;     // Ban log file

    private FileLogger logger;

    /**
     * Time stamps and logs an error message.
     *
     * @param message  the message to log.
     */

    static void printError(String message) {
        System.err.println("[" + new Date() + "] " + message);
    }

    /**
     * Time stamps and logs an error message followed by an exception or error
     * stack trace.
     *
     * @param message  the message to log.
     * @param t        the exception or error causing the problem.
     */

    static void printError(String message, Throwable t) {
        if (verbose) {
            synchronized (System.err) {
                printError(message);
                t.printStackTrace(System.err);
            }
        } else {
            printError(message + " (" + t + ")");
        }
    }

    public Log(FileLogger logger) {
        this.logger = logger;
    }

    public void flush() throws IOException {
        if (size() > 0) {
            String string = toString().trim();
            if (string.length() > 0) {
                logger.log(string);
            }
            reset();
        }
    }

    public void close() throws IOException {
        flush();
        try {
            logger.stop();
        } catch (LifecycleException e) {
            throw new IOException(e.getMessage());
        }
    }
}
