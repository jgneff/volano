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
import  java.math.BigInteger;
import  java.security.*;
import  java.security.spec.*;

public class AppletKeyPairGenerator {
    private static final String KEY_ALGORITHM = "DSA";

    public AppletKeyPairGenerator() {}

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec    spec    = new DSAPublicKeySpec(new BigInteger(Constants.Y), new BigInteger(Constants.P), new BigInteger(Constants.Q), new BigInteger(Constants.G));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePublic(spec);
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec    spec    = new DSAPrivateKeySpec(new BigInteger(Constants.X), new BigInteger(Constants.P), new BigInteger(Constants.Q), new BigInteger(Constants.G));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePrivate(spec);
    }
}
