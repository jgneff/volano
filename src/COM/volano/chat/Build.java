/*
 * Build.java - build options for the chat client and server.
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

package COM.volano.chat;

/**
 * This interface provides the common constants for compiling the VolanoChat
 * client applets and server applications, including copyright strings, Web site
 * addresses, expiration dates, connection limitations, and debugging switches.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Build {
    // See <http://en.wikipedia.org/wiki/Software_versioning>.
    // Version numbers are:  major.minor[.maintenance[.build]]
    // Letters (a, b, c) signify a pre-release or early access version.
    // Major changes - once every 12-18 months
    //   Major changes to the code.
    //   Charge a fee to upgrade.
    //   May require a new Java version on the server side.
    //   May change the API interfaces.
    // Minor changes - once every 3-6 months
    //   Feature enhancements to the code, no matter how small.
    //   Free upgrade from the same major version.
    //   No change to the Java version required.
    //   No change to the API interfaces.
    //   May add or remove a few configuration settings.
    // Patch changes - when required
    //   Bug fixes only.
    //   Free maintenance release for the same major version.
    //   No change to the Java version required.
    //   Forward- and backward-compatible with API and configuration.
    // Builds changes - when requested
    //   Differ only in packaging and client authentication.
    //   Created only when multiple builds are requested by the customer.
    //   No change in actual functions or resource files.
    String  VERSION            = "2.13.4";

    // Set to "false" and "0" for normal VolanoChat.
    // Set to "true" and some non-zero number for VolanoMark.
    boolean IS_BENCHMARK       = false; // VolanoChatPro-unlimited license, no expiration
    int     PACKET_ID_OFFSET   = 0;     // Non-zero (1,000 or more) for VolanoMark

    // Special version for Verio.
    // Versions 2.1.7.3, 2.6.3.1
    boolean VERIO              = false;

    // The limited version of VolanoMark requires the benchmark to be run locally
    // with its default options.
    boolean MARK_LIMITED       = false;           // Limited VolanoMark support
    String  MARK_CLIENT_TITLE  = "VOLANO(TM) Chat Server Benchmark Version " + VERSION;

    // Other global constants.
    String  VOLANO_CHAT        = "COM.volano.VolanoChat";
    String  MY_VOLANO_CHAT     = "COM.volano.MyVolanoChat";
    String  VOLANO_URL         = "(www.volano.com)";
    String  CHAT_SERVER_TITLE  = "VOLANO(TM) Chat Server Version " + VERSION;
    String  COPYRIGHT          = "Copyright (C) 1996-2015 John Neffenger";
    String  APPLET_COPYRIGHT   = "Copyright © 1996-2015 John Neffenger";
    String  TARGET             = "_blank";
    String  MARK_LOG           = "volano-mark.log";
    String  TEST_LOG           = "volano-test.log";
    boolean UPDATE_TRACE       = false; // Observer update method trace
    int     TAB                = '\t';
    int     NEW_LINE           = '\n';
    int     RETURN             = '\r';
    char    NON_BREAKING_SPACE = '\u00a0';
}

