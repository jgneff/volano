/*
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
package COM.volano.chat.security;
import  java.security.*;

public class AppletSecureRandom extends SecureRandom {
    // 2.13.0
    public  static final byte[] BYTES = {-89, -46, 2, -119, -118, -88, -94, -103, 123, -35, -108, -108, -67, 89, -57, 37, -59, -88, 20, 108, -124, 97, 73, 94, -83, 12, 116, -50, -104, 7, -50, 59, -30, 69, -109, -35, 77, 56, 127, 78, 42, 110, 40, 73, 2, -54, -97, 3, 4, -54, 1, 95, 107, -115, 126, 96, -11, -48, 59, -15, 60, -31, 22, 105, 114, -78, 25, -7, -15, -56, -35, 94, -116, -17, -64, 62, -55, 63, -48, -104, -65, 1, 72, 93, 53, -4, 124, -8, -108, 1, -71, -90, 65, -27, 115, 4, 6, 100, -122, 77};
    private static final int    JUMP  = 1; // Rotate from 1 to 5

    private static int index = 0;

    private static void getBytes(byte[] result) {
        for (int i = 0; i < result.length; i++) {
            result[i] = BYTES[index % BYTES.length];
            index += JUMP;
        }
    }

    public static byte[] getSeed(int numBytes) {
        byte[] result = new byte[numBytes];
        getBytes(result);
        return result;
    }

    public AppletSecureRandom() {
        // Don't let the compiler generate a call to the no-arg super
        // constructor, since that generates a secure random number that we
        // don't need (and takes too long to do so).
        super(getSeed(20));
    }

    public AppletSecureRandom(byte[] seed) {}

    public void setSeed(byte[] seed) {}

    public void setSeed(long seed) {}

    public synchronized void nextBytes(byte[] result) {
        getBytes(result);
    }
}
