/*
 * AccessControl.java - a class for host and referrer based access control.
 * Copyright (C) 1996-2002 John Neffenger
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

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class performs the host and referrer access control along with the host
 * banning operations.
 *
 * @author  John Neffenger
 * @version 2.5
 * @since   1.0
 * @see     COM.volano.net.DNSBlacklist
 */

class AccessControl {
    private static final int    IPV4_ADDRESS_BUFFER    = 16;
    private static final String IPV4_ADDRESS_SEPARATOR = ".";
    private static final String URL_PATH_SEPARATOR     = "/";
    private static final String URL_PROTOCOL_SUFFIX    = ":";
    private static final String URL_AUTHORITY_PREFIX   = "//";

    private static Properties hostsAllowed     = new Properties();
    private static Properties hostsDenied      = new Properties();
    private static Properties referrersAllowed = new Properties();
    private static Properties referrersDenied  = new Properties();
    private static Hashtable  hostsBanned      = new Hashtable();

    /**
     * Gets the string representation of an IPv4 address.
     *
     * @param bytes the IPv4 address in network byte order (highest order byte
     *        of the address is in <code>bytes[0]</code>).
     * @return the textual representation of the IPv4 address in the format
     *         <code>d.d.d.d</code>.
     */

    private static String getByAddress(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(IPV4_ADDRESS_BUFFER);
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(bytes[i] & 0xff);
            if (i < bytes.length - 1) {
                buffer.append(IPV4_ADDRESS_SEPARATOR);
            }
        }
        return buffer.toString();
    }

    /**
     * Gets the string representation of the network address block by combining
     * the specified address and network mask with a bitwise <code>AND</code>
     * operation.  For example, if the address is <code>172.178.214.202</code>
     * and the network mask is <code>255.0.0.0</code>, this method returns the
     * string <code>172.0.0.0</code>.
     *
     * @param address the address whose network address block is to be returned.
     * @param netmask the network mask that defines the number of bits in the
     *        network address block.
     * @return the text representation of the network address block.
     */

    static String getNetblock(String address, String netmask) {
        String netblock = address;
        try {
            byte[] addr = InetAddress.getByName(address).getAddress();
            byte[] mask = InetAddress.getByName(netmask).getAddress();
            byte[] block = new byte[addr.length];
            for (int i = 0; i < addr.length; i++) {
                block[i] = i < mask.length ? (byte) (addr[i] & mask[i]) : 0;
            }
            netblock = getByAddress(block);
        } catch (UnknownHostException e) {}
        return netblock;
    }

    /**
     * Gets the score of the host address in the specified table.  The score is
     * equal to the number of component parts of the network address that are
     * found in the table.  For example, if the address to check is
     * <code>172.178.214.202</code>, an entry found in the table of:
     * <li>
     * <ul><code>172.178.214.202</code> scores 4,</ul>
     * <ul><code>172.178.214.0</code> scores 3,</ul>
     * <ul><code>172.178.0.0</code> scores 2,</ul>
     * <ul><code>172.0.0.0</code> scores 1, and</ul>
     * <ul><code>0.0.0.0</code> scores 0.</ul>.
     * </li>
     * <p>A score of -1 is returned when no entry is found.
     *
     * @param table the table whose keys contain a list of host addresses or
     *        network blocks.
     * @param host the base address to use for constructing lookup addresses.
     * @return the score of the address based on how many components were found
     *         as a key entry in the table, or -1 if no lookup address was found.
     */

    private static int getScore(Hashtable table, InetAddress host) {
        byte[] address = host.getAddress();
        int score = -1;
        boolean found = false;
        for (int i = address.length; i >= 0 && !found; i--) {   // i = 4    i = 3    i = 2    i = 1    i = 0
            if (i < address.length) {
                address[i] = 0;    // a.b.c.d  a.b.c.0  a.b.0.0  a.0.0.0  0.0.0.0
            }
            found = table.containsKey(getByAddress(address));
            if (found) {
                score = i;    // s = 4    s = 3    s = 2    s = 1    s = 0
            }
        }
        return score;
    }

    /**
     * Gets the score of the referring URL in the specified table.  The score is
     * equal to the length of the longest URL component string that is found in
     * the table.  For example, if the URL to check is
     * <code>http://www.volano.com/vcclient/chat.html</code>, an entry found in
     * the table of:
     * <li>
     * <ul><code>http://www.volano.com/vcclient/chat.html</code> scores 40,</ul>
     * <ul><code>http://www.volano.com/vcclient</code> scores 30, and</ul>
     * <ul><code>http://www.volano.com</code> scores 21.</ul>
     * </li>
     * <p>A score of -1 is returned when no entry is found.
     *
     * <p>A URL can be constructed from the following methods and strings, where
     * the methods shown in brackets can return <code>null</code>.
     * <pre>
     * getProtocol() ":" ["//" getAuthority()] [getFile()] ["#" getRef()]
     * </pre>
     *
     * <p>The following example illustrates all parts of a URL:
     * <pre>
     * http://user@www.example.com:8080/servlet/document?for=one&by=two#section1
     * </pre>
     *
     * @param table the table whose keys contain a list of referring URLs or URL
     *        fragments.
     * @param host the base URL to use for constructing lookup URLs.
     * @return the score of the URL based on the length of the URL's fragment
     *         found as a key entry in the table, or -1 if no fragment was found.
     */

    private static int getScore(Hashtable table, URL referrer) {
        String protocol = referrer.getProtocol();
        String minSpec  = protocol + URL_PROTOCOL_SUFFIX + URL_AUTHORITY_PREFIX;
        int minIndex = minSpec.length() - 1;

        String  spec  = referrer.toString();
        boolean found = table.containsKey(spec) || table.containsKey(spec + URL_PATH_SEPARATOR);
        int     index = spec.lastIndexOf(URL_PATH_SEPARATOR);
        while (!found && index > minIndex) {
            spec  = spec.substring(0, index); // Does not include trailing "/"
            found = table.containsKey(spec) || table.containsKey(spec + URL_PATH_SEPARATOR);
            index = spec.lastIndexOf(URL_PATH_SEPARATOR);
        }
        if (!found) {
            spec  = protocol + URL_PROTOCOL_SUFFIX;
            found = table.containsKey(spec) || table.containsKey(spec + URL_AUTHORITY_PREFIX);
        }
        return found ? spec.length() : -1;
    }

    /**
     * Loads the hosts allowed properties from the input stream.
     *
     * @param stream the input stream containing the hosts allowed.
     */

    static void loadHostsAllowed(InputStream stream) throws IOException {
        hostsAllowed.load(stream);
        stream.close();
    }

    /**
     * Loads the hosts denied properties from the input stream.
     *
     * @param stream the input stream containing the hosts denied.
     */

    static void loadHostsDenied(InputStream stream) throws IOException {
        hostsDenied.load(stream);
        stream.close();
    }

    /**
     * Loads the referrers allowed properties from the input stream.
     *
     * @param stream the input stream containing the referrers allowed.
     */

    static void loadReferrersAllowed(InputStream stream) throws IOException {
        referrersAllowed.load(stream);
        stream.close();
    }

    /**
     * Loads the referrers denied properties from the input stream.
     *
     * @param stream the input stream containing the referrers denied.
     */

    static void loadReferrersDenied(InputStream stream) throws IOException {
        referrersDenied.load(stream);
        stream.close();
    }

    /**
     * Sets the banning hash table.
     *
     * @param table the banning hash table to use.
     */

    static void setHostsBanned(Hashtable table) {
        hostsBanned = table;
    }

    /**
     * Gets the list of hosts allowed access.
     *
     * @return the list of hosts allowed access.
     */

    static Properties getHostsAllowed() {
        return hostsAllowed;
    }

    /**
     * Gets the list of hosts denied access.
     *
     * @return the list of hosts denied access.
     */

    static Properties getHostsDenied() {
        return hostsDenied;
    }

    /**
     * Gets the list of referrers allowed access.
     *
     * @return the list of referrers allowed access.
     */

    static Properties getReferrersAllowed() {
        return referrersAllowed;
    }

    /**
     * Gets the list of referrers denied access.
     *
     * @return the list of referrers denied access.
     */

    static Properties getReferrersDenied() {
        return referrersDenied;
    }

    /**
     * Gets the list of hosts banned.
     *
     * @return the list of hosts banned.
     */

    static Hashtable getHostsBanned() {
        return hostsBanned;
    }

    /**
     * Removes any white space from a referring Web address URL.
     *
     * @param url the referring Web address to clean of white space.
     * @return the new referring Web address with white space removed.
     */

    static String cleanReferrer(String url) {
        StringBuffer buffer = new StringBuffer(url.length());
        StringTokenizer tokenizer = new StringTokenizer(url);
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken());
        }
        return buffer.toString();
    }

    /**
     * Checks whether the specified host IP address is allowed access.
     *
     * @param address the IP address of the host to check.
     * @return true if the host is allowed access; otherwise false.
     */

    static boolean isHostAllowed(String address) {
        boolean isAllowed = false;
        try {
            InetAddress host = InetAddress.getByName(address);
            int allowScore = getScore(hostsAllowed, host);
            int denyScore = getScore(hostsDenied, host);
            int banScore = getScore(hostsBanned, host);
            isAllowed = allowScore >= denyScore && allowScore >= banScore;
        } catch (UnknownHostException e) {}
        return isAllowed;
    }

    /**
     * Checks whether the specified referring URL is allowed access.
     *
     * @param url the referring URL to check.
     * @return true if the referring URL is allowed access; otherwise false.
     */

    static boolean isReferrerAllowed(String url) {
        boolean isAllowed = false;
        try {
            // Watch out -- https protocol throws:
            //   java.net.MalformedURLException: unknown protocol: https
            // when using Java 1.3.  Java 1.4 supports https URLs.
            URL referrer = new URL(url.toLowerCase());
            int allowScore = getScore(referrersAllowed, referrer);
            int denyScore = getScore(referrersDenied, referrer);
            isAllowed = allowScore >= denyScore;
        }
        // We should be logging these so we catch errors like "https".
        catch (MalformedURLException e) {}
        return isAllowed;
    }

    /**
     * Bans the specified host IP address.
     *
     * @param address the IP address of the host to ban.
     * @param room    the name of the room in which this host was banned, or the
     *                empty string if not available.
     * @param user    the user name used by this host in the room, or the empty
     *                string if not available.
     * @param monitor the member name of the monitor who banned this host, or the
     *                empty string if not available.
     */

    static void banHost(String address, String room, String user, String monitor) {
        hostsBanned.put(address, new Ban(new Date(), address, room, user, monitor));
    }
}
