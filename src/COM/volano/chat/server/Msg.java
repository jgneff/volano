/*
 * Msg.java - an interface for defining message text.
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

/**
 * This interface defines the message patterns used to format informational and
 * error messages printed by the server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Msg {
    // General thread messages.
    String INTERRUPTED        = "%0 thread is interrupted.";
    String UNEXPECTED         = "Unexpected error in %0 thread.";
    String STOPPING           = "%0 thread is stopping.";

    // Message formatting errors.
    String BAD_ACCESS_FORMAT  = "Error formatting access log entry.";
    String BAD_PUBLIC_FORMAT  = "Error formatting public log entry.";
    String BAD_PRIVATE_FORMAT = "Error formatting private log entry.";
    String BAD_BAN_FORMAT     = "Error formatting ban log entry.";

    // For Main and AdminServer.
    String BAD_CONNECTION     = "Error starting connection.";
    String LOCAL_ONLY         = "connection must be local";

    // For AccessControl.
    String ACCESS_INCOMPLETE  = "%0:%1: Access directive is incomplete.";
    String ACCESS_UNEXPECTED  = "%0:%1: Unexpected keyword %2.";
    String ACCESS_LINE        = "-> %0";

    // For Main.
    String SERVER_USAGE       = "Usage: java COM.volano.Main [properties]";
    String LOADING_PROPERTIES = "Loading server properties from \"%0\".";
    String EXPIRES            = "This evaluation copy expires on %0.";
    String EXPIRED            = "This evaluation copy expired on %0.";
    String BAD_LOG_FILE       = "Unable to open log file: %0";
    String CANNOT_START       = "Unable to start server: %0";
    String SHUTTING_DOWN      = "Server received shutdown request.";
    String NEW_LICENSE        = "Please obtain a new trial license key at www.volano.com.";

    // For Main, EventSender, and EventLogger.
    String BAD_URL            = "Error invoking %0";
    String WRITE_ERROR        = "Error writing to %0";

    // For Value.
    String NO_THREAD_GROUPS   = "Disabling the use of thread groups...";
    String BAD_PROPERTIES     = "Unable to load property file: %0";
    String PROPERTY_ERROR     = "Invalid value %1 for %0 (%2).";
    String NO_LICENSE         = "Please obtain a trial license key at www.volano.com.";
    // String NO_SERVER_HOST  = "Unable to determine host address. Please define \"server.host\".";

    // For ServletRunner.
    String ERROR_STARTING     = "Error starting servlet runner: %0";
    String ERROR_STOPPING     = "Error stopping servlet runner: %0";
}
