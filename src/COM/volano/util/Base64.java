/*
 * Base64.java - a class for encoding and decoding base64 strings.
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

package COM.volano.util;

import  java.util.*;

/**
 * Used to encode and decode base64 Content-Transfer-Encoded strings. The login
 * userid and password returned in a Authorization http header is encoded in
 * this manner
 *
 * @author Graham Savage
 * @author Paul Jack
 * @version 2.0.0.0
 */
public  final   class   Base64 {
    /**
    Decode a string from Base64 Content-Transfer-Encoding to normality
    <br>The string must be a multiple of four characters in length
    @param encoded A Base64 Content-Transfer-Encoded string
    @return String The decoded string
    @see http://www.fwb.gulf.net/~dwsauder/rfc2045.txt
    @exception IllegalArgumentException Thrown if the string is not a multiple of four characters.
    */
    public  final static  String  decode( String encoded ) throws IllegalArgumentException {
        // Check for bad arguments and shout if bad
        if ( encoded == null ) {
            throw new IllegalArgumentException( "String was null" );
        }

        // Check for bad arguments and shout if bad
        if ( encoded.length() % 4 != 0 ) {
            throw new IllegalArgumentException( "Length of encoded string must be a multiple of 4 chars" );
        }

        String  unpacked = "";
        // Build the decoded String using four encoded characters at a time
        for ( int i = 0; i < encoded.length(); i += 4 ) {
            unpacked += unpackFour( encoded, i );
        }

        // Encoded strings are padded with '=' to make them multiples of 4 characters
        // so if this was the case, strip off the crap at the end after we decoded
        if ( encoded.endsWith("==") ) {
            return unpacked.substring( 0, unpacked.length()-2 );
        } else if ( encoded.endsWith("=") ) {
            return unpacked.substring( 0, unpacked.length()-1 );
        } else {
            return unpacked;
        }
    }

    /**
    Convert four encoded characters into three decoded characters:
    <ul>
        <li>Lookup the number of the 4 encoded characters in the base64 table
        <li>Each of the four numbers is 6-bits; pack the four 6-bits numbers
            side-by-side => 24-bits
        <li>From the packed 24-bits pull out three 8-bit bytes (lower 3 bytes)
        <li>Treat each of the 8-bit bytes as one normal character
        <li>Presto! We have three unpacked charaters
    </ul>
    Note: '=' is used for padding during encoding to make the encoded string a
    multiple of 4 chraacters. So when decoding, the '=' character should not be
    included in the result.
    @param encoded The base64 encoded string
    @param offset The current offset of the first character of four into the
    encded string
    @return String The three-character string resulting from unpacking four
    encoded characters
    */
    private final static String  unpackFour( String encoded, int offset ) {
        int c1 = lookup( encoded.charAt( offset ) );
        int c2 = lookup( encoded.charAt( offset + 1 ) );
        int c3 = lookup( encoded.charAt( offset + 2 ) );
        int c4 = lookup( encoded.charAt( offset + 3 ) );
        int x = c1 << 18 | c2 << 12 | c3 << 6 | c4;
        byte[] triplet = new byte[] {
            (byte)( ( x & 0x00ff0000 ) >> 16 ),
            (byte)( ( x & 0x0000ff00 ) >> 8 ),
            (byte)(   x & 0x000000ff )
        };
        return new String( triplet );
    }

    /**
    Lookup table that maps one base64 character into a number (6-bits)
    @param letter A single base64 character extracted from an encoded string
    @return int The 6-bit number
    */
    private final static int    lookup( char letter ) {
        if ( letter >= 'A' && letter <= 'Z' ) {
            return letter - 'A';
        } else if ( letter >= 'a' && letter <= 'z' ) {
            return letter - 'a' + 26;
        } else if ( letter >= '0' && letter <= '9' ) {
            return letter - '0' + 52;
        } else if ( letter == '+' ) {
            return 62;
        } else if ( letter == '/' ) {
            return 63;
        } else {
            return 0;
        }
    }
}
