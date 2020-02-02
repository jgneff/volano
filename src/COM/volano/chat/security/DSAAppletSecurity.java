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

public class DSAAppletSecurity extends AppletSecurity {
    private static final String SIG_ALGORITHM = "DSA";

    private PrivateKey privateKey = null;

    public synchronized void initialize() {
        if (privateKey == null) {
            try {
                AppletKeyPairGenerator generator = new AppletKeyPairGenerator();
                privateKey = generator.getPrivateKey();
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    public byte[] sign(byte[] data) {
        byte[] signature = new byte[0];
        if (privateKey != null) {
            try {
                Signature dsa = Signature.getInstance(SIG_ALGORITHM);
                dsa.initSign(privateKey);
                dsa.update(data);
                signature = dsa.sign();
            }
            // NoSuchAlgorithmException, InvalidKeyException, SignatureException
            catch (Exception e) {
                System.err.println(e);
            }
        }
        return signature;
    }
}
