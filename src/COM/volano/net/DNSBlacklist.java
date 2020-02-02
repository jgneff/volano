/*
 * DNSBlacklist.java - a class for doing ip4r DNSBL-style IP address checks.
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

package COM.volano.net;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class performs an ip4r DNSBL-style IP address check, checking the
 * address <code>127.0.0.2</code>, for example, by looking up the host name
 * <code>2.0.0.127.relays.osirusoft.com</code> in the DNS server.  If the host
 * is found, the check is successful.  Otherwise, the check fails.  The meaning
 * of the result depends on the type of DNS blacklist against which the address
 * was checked.  This class defines two categories of blacklists -- one set for
 * denying access to certain IP addresses and another set for determining
 * whether an IP address is dynamically assigned (as with dial-up accounts or
 * PPPoE cable and DSL accounts).
 *
 * <p>To deny access from open WinGate, HTTP, or SOCKS proxies, check the
 * address against the following DNS blacklists:
 * <pre>
 * opm.blitzed.org
 *     Non-authoritative answer:
 *     Name:    2.0.0.127.opm.blitzed.org
 *     Address:  127.1.0.7
 *
 *     127.0.0.1 - an open WinGate
 *     127.0.0.2 - an open SOCKS4 or SOCKS5
 *     127.0.0.3 - an open WinGate and SOCKS4/5
 *     127.0.0.4 - an open HTTP/connect
 *     127.0.0.5 - an open WinGate and HTTP/connect
 *     127.0.0.6 - an open SOCKS4/5 and HTTP/connect
 *     127.0.0.7 - an open WinGate and SOCKS4/5 and HTTP/connect
 *
 * relays.osirusoft.com:127.0.0.9
 *     Non-authoritative answer:
 *     Name:    2.0.0.127.relays.osirusoft.com
 *     Addresses:  127.0.0.4, 127.0.0.2, 127.0.0.6, 127.0.0.9
 * </pre>
 *
 * <p>To find out whether the IP address is dynamic, check the address against
 * the following DNS blacklists:
 * <pre>
 * dynablock.wirehub.net:127.0.0.2
 *     Name:    2.0.0.127.dynablock.wirehub.net
 *     Address:  127.0.0.2
 *
 *     Non-authoritative answer:
 *     Name:    dialup.ip.dynablock.wirehub.net
 *     Address:  127.0.0.2
 *     Aliases:  196.98.137.172.dynablock.wirehub.net
 *
 * blackholes.five-ten-sg.com:127.0.0.3
 *     can't find 2.0.0.127.blackholes.five-ten-sg.com: Non-existent domain
 *
 *     Name:    aol.com.dialup.blackholes.five-ten-sg.com
 *     Address:  127.0.0.3
 *     Aliases:  196.98.137.172.blackholes.five-ten-sg.com
 *
 * no-more-funn.moensted.dk:127.0.0.3
 *     Non-authoritative answer:
 *     Name:    test.no-more-funn.moensted.dk
 *     Address:  127.0.0.2
 *     Aliases:  2.0.0.127.no-more-funn.moensted.dk
 *
 * dnsbl.njabl.org:127.0.0.3
 *     Non-authoritative answer:
 *     Name:    2.0.0.127.dnsbl.njabl.org
 *     Address:  127.0.0.2
 *
 *     Name:    196.98.137.172.dnsbl.njabl.org
 *     Address:  127.0.0.3
 *
 * spamguard.leadmon.net:127.0.0.2
 *     Non-authoritative answer:
 *     Name:    test.dialup.spamguard.leadmon.net
 *     Address:  127.0.0.2
 *     Aliases:  2.0.0.127.spamguard.leadmon.net
 *
 * relays.osirusoft.com:127.0.0.3
 *     Non-authoritative answer:
 *     Name:    2.0.0.127.relays.osirusoft.com
 *     Addresses:  127.0.0.9, 127.0.0.4, 127.0.0.2, 127.0.0.6
 *
 *     Name:    196.98.137.172.relays.osirusoft.com
 *     Address:  127.0.0.3
 * </pre>
 *
 * <p>When checked against the 3,370 hosts connecting to the VolanoChat demo
 * server on February 20, 2002, the following results were returned.  For the
 * open proxy check, only 2 addresses were detected among both DNS blacklists:
 * <pre>
 * opm.blitzed.org
 *   207.35.102.29
 *   65.92.168.44
 * relays.osirusoft.com:127.0.0.9
 *   207.35.102.29
 * </pre>
 *
 * <p>For the dynamic IP address check, a total of 53 percent were detected as
 * dynamic when all 6 lists were checked:
 * <pre>
 * DNS Blacklist                         Detected (out of 3370)
 * ------------------------------------  ----------------------
 * dynablock.wirehub.net:127.0.0.2       1199  36%
 * blackholes.five-ten-sg.com:127.0.0.3  1047  31%
 * no-more-funn.moensted.dk:127.0.0.3     703  21%
 * dnsbl.njabl.org:127.0.0.3              634  19%
 * spamguard.leadmon.net:127.0.0.2        448  13%
 * relays.osirusoft.com:127.0.0.3         422  13%
 *
 * Top list (wirehub.net)                1199  36%
 * Top 2 lists                           1647  49% (+ 13%)
 * Top 3 lists                           1719  51% (+  2%)
 * Top 4 lists                           1742  52% (+  1%)
 * Top 5 lists                           1752  52% (+  0%)
 * All 6 lists                           1770  53% (+  1%)
 * </pre>
 *
 * @author  John Neffenger
 * @version 2.5
 * @since   2.5
 */

public class DNSBlacklist {
    private static final String ZONE_RESPONSE_SEPARATOR = ":";
    private static final String HOST_NAME_SEPARATOR = ".";
    private static final int INET4_ADDRESS_BYTES = 4;
    private static final int INET6_ADDRESS_BYTES = 16;
    private static final int IP4R_PREFIX_BUFFER = 16;

    private static Vector denied = new Vector();
    private static Vector dynamic = new Vector();

    private String zone = "";
    private String response = "";

    /**
     * Gets the list of DNS blacklists for denying access.
     *
     * @return the list of DNS blacklists containing addresses to be denied
     *         access.
     */

    public static Vector getDenied() {
        return denied;
    }

    /**
     * Gets the list of DNS blacklists for dynamic IP addresses.
     *
     * @return the list of DNS blacklists containing dial-up addresses and other
     *         dynamically assigned IP addresses.
     */

    public static Vector getDynamic() {
        return dynamic;
    }

    /**
     * Adds a sequence of DNS blacklists to the given list.  The DNS blacklists
     * are separated by white space and in the format <code>zone:response</code>
     * where <code>zone</code> is required and <code>:response</code> is
     * optional.  If no response is specified for a blacklist, any response from
     * the DNS query indicates that the address is on the list.  Otherwise, only
     * the specific response specified indicates the address is on the list.
     *
     * @param vector the list where the DNS blacklists are to be added.
     * @param list the sequence of DNS blacklists to add.
     */

    public static void parseList(Vector vector, String list) {
        StringTokenizer listTokenizer = new StringTokenizer(list);
        while (listTokenizer.hasMoreTokens()) {
            StringTokenizer tokenizer = new StringTokenizer(listTokenizer.nextToken(), ZONE_RESPONSE_SEPARATOR);
            String zone = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
            String response = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
            if (zone.length() > 0) {
                vector.addElement(new DNSBlacklist(zone, response));
            }
        }
    }

    /**
     * Checks whether the address is found in the specified list of DNS
     * blacklists.  The lists are checked in the order given.
     *
     * @param vector the list of DNS blacklists to check.
     * @param address the address to check in each list until found.
     * @return true if the address is found on one of the lists; otherwise false.
     */

    public static boolean contains(Vector vector, String address) {
        boolean found = false;
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements() && !found) {
            DNSBlacklist dnsbl = (DNSBlacklist) enumeration.nextElement();
            found = dnsbl.contains(address);
        }
        return found;
    }

    /**
     * Converts the array of bytes containing an IP address to an array of
     * corresponding strings.  For IPv4 addresses, each byte is converted to its
     * unsigned decimal representation in an array equal to the byte array size.
     * For IPv6 addresses, each nibble (each 4-bit value) is converted to its
     * hexadecimal representation in an array double the byte array size.
     *
     * @param bytes the byte array to convert.
     * @return an array of strings representing the address byte array.
     */

    private static String[] bytesToStrings(byte[] bytes) {
        String[] strings = new String[0];
        if (bytes.length == INET4_ADDRESS_BYTES) {
            strings = new String[bytes.length];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = Integer.toString(bytes[i] & 0xff);
            }
        } else if (bytes.length == INET6_ADDRESS_BYTES) {
            strings = new String[bytes.length * 2];
            for (int i = 0; i < strings.length; i++) {
                int nibble = i % 2 == 0 ? bytes[i / 2] >> 4 & 0x0f : bytes[i / 2] & 0x0f;
                strings[i] = Integer.toHexString(nibble);
            }
        }
        return strings;
    }

    /**
     * Gets the ip4r prefix for the DNS blacklist zone.  For example, for
     * an IPv4 address, this method converts the address to a reversed string of
     * decimal digits with length of 8 to 16 characters:
     * <pre>
     * 216.221.107.17 -> 17.107.221.216.
     * </pre>
     *
     * <p>For an IPv6 address, this method converts the address to a reversed
     * string of hexadecimal digits with a fixed length of 64 characters:
     * <pre>
     * 3ffe:3700:402::210:a4ff:fe12:fec4
     *   -> 4.c.e.f.2.1.e.f.f.f.4.a.0.1.2.0.0.0.0.0.2.0.4.0.0.0.7.3.e.f.f.3.
     * </pre>
     *
     * @param address the address whose prefix is to be calculated.
     * @return the ip4r prefix of the DNS blacklist zone, including the trailing
     *         <code>"."</code> character.
     */

    private static String getPrefix(InetAddress address) {
        String[] strings = bytesToStrings(address.getAddress());
        StringBuffer buffer = new StringBuffer(IP4R_PREFIX_BUFFER);
        for (int i = strings.length - 1; i >= 0; i--) {
            buffer.append(strings[i]);
            buffer.append(HOST_NAME_SEPARATOR);
        }
        return buffer.toString();
    }

    /**
     * Constructs a new DNS blacklist.
     *
     * @param zone the DNS zone for the blacklist.
     * @param response the required response from the DNS query, or the empty
     *        string if any successful response from this list is acceptable.
     */

    private DNSBlacklist(String zone, String response) {
        this.zone = zone == null ? "" : zone;
        this.response = response == null ? "" : response;
    }

    /**
     * Checks whether the DNS blacklist contains the specified address.
     *
     * @param address the IP address to check.
     * @return true if the address is found in the blacklist; otherwise false.
     */

    private boolean contains(InetAddress address) {
        boolean found = false;
        if (address != null) {
            try {
                InetAddress[] list = InetAddress.getAllByName(getPrefix(address) + zone);
                found = response.length() == 0;
                for (int i = 0; i < list.length && !found; i++) {
                    found = response.equals(list[i].getHostAddress());
                }
            } catch (UnknownHostException e) {}
        }
        return found;
    }

    /**
     * Checks whether the DNS blacklist contains the specified address.
     *
     * @param address the IP address check.
     * @return true if the address is found in the blacklist; otherwise false.
     */

    private boolean contains(String address) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {}
        return contains(inetAddress);
    }

    /**
     * Returns a string representation of the object consisting of the DNS zone,
     * followed by a colon <code>(":")</code>, optionally followed by the DNS
     * lookup response.
     *
     * @return a string representation of the object.
     */

    public String toString() {
        return zone + (response.length() == 0 ? "" : ":" + response);
    }

    private static final String USAGE = "Usage: java DNSBlacklist denied|dynamic file [properties]";
    private static final int DENIED = 1;
    private static final int DYNAMIC = 2;

    /**
     * Tests this class against an input file which contains an IP address as
     * the first token of each line.  The usage is:
     *
     * <pre>
     * java DNSBlacklist type file [properties]
     * </pre>
     *
     * @param type the category of DNS blacklists to check, either the string
     *        <code>denied</code> or <code>dynamic</code>.
     * @param file the name of the input file.
     * @param properties the properties defining the DNS blacklists by the keys
     *        <code>dnslist.denied</code> and <code>dnslist.dynamic</code>.
     */

    public static void main(String[] args) throws IOException {
        int type = 0;
        BufferedReader reader = null;

        if (args.length < 2 || args.length > 3) {
            System.err.println(USAGE);
            return;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("denied")) {
                type = DENIED;
            } else if (args[0].equalsIgnoreCase("dynamic")) {
                type = DYNAMIC;
            } else {
                System.err.println(USAGE);
                return;
            }
        }
        if (args.length > 1) {
            reader = new BufferedReader(new FileReader(args[1]));
        }
        if (args.length > 2) {
            Properties properties = System.getProperties();
            FileInputStream stream = new FileInputStream(args[2]);
            properties.load(stream);
            stream.close();
            System.setProperties(properties);
        }

        Vector vector = null;
        switch (type) {
        case DENIED :
            vector = getDenied();
            parseList(vector, System.getProperty("dnslist.denied", ""));
            break;
        case DYNAMIC :
            vector = getDynamic();
            parseList(vector, System.getProperty("dnslist.dynamic", ""));
            break;
        }
        System.err.println("DNS blacklists = " + vector);

        String line = reader.readLine();
        while (line != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String address = tokenizer.nextToken();
            boolean found = contains(vector, address);
            System.out.print(address);
            if (found) {
                System.out.print("\tfound");
            }
            System.out.println();
            line = reader.readLine();
        }
    }
}
