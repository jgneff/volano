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

import  java.io.*;
import  java.security.*;
import  java.security.interfaces.*;
import  java.util.Random;

public class Keys {
    private static final String KEY_ALGORITHM = "DSA";
    private static final String PROVIDER      = "SUN";
    private static final int    STRENGTH      = 512;
    private static final int    BYTE_SIZE     = 100;
    private static final int    BUILD_SIZE    =   4;
    private static final int    SORT_SIZE     =  16;
    private static final int    SORT_NUM      =  17;

    private static final int    WRAP_COLUMN   = 64;
    private static final String INDENT        = "    ";
    private static final String PREFIX        = "        \"";
    private static final String SUFFIX        = "\" +";
    private static final String END           = "\";";
    private static final String NL            = System.getProperty("line.separator");
    private static final String VERSION       = System.getProperty("java.version");

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
                                          '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
                                         };

    private static String hexEncode(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            buffer.append(DIGITS[(b & 0xF0) >> 4]);
            buffer.append(DIGITS[b & 0x0F]);
        }
        return buffer.toString();
    }

    private static String wrap(String string, int column) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(PREFIX);
        for (int i = 0; i < string.length(); i++) {
            buffer.append(string.charAt(i));
            if ((i + 1) % column == 0) {
                buffer.append(SUFFIX + NL + PREFIX);
            }
        }
        buffer.append(END);
        return buffer.toString();
    }

    private static String encode(Key key) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream    stream = new ObjectOutputStream(output);
        stream.writeObject(key);
        String string = hexEncode(output.toByteArray());
        return wrap(string, WRAP_COLUMN);
    }

    private static String getBytes(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(bytes[i]);
            if (i < bytes.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
        generator.initialize(STRENGTH);
        KeyPair    pair       = generator.generateKeyPair();
        PublicKey  publicKey  = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        DSAPublicKey  dsaPublicKey  = (DSAPublicKey) publicKey;
        DSAPrivateKey dsaPrivateKey = (DSAPrivateKey) privateKey;
        DSAKey        dsaKey = (DSAKey) publicKey;
        DSAParams     params = dsaKey.getParams();
        byte[]        bytes  = new byte[BYTE_SIZE];
        Random        random = new Random();
        random.nextBytes(bytes);

        byte[] build = new byte[BUILD_SIZE];
        random.nextBytes(build);

        if (args.length == 1) {
            System.out.println("package " + args[0] + ";");
            System.out.println();
        }

        System.out.println("public interface Constants {");
        for (int i = 0; i < SORT_NUM; i++) {
            byte[] sort = new byte[SORT_SIZE];
            random.nextBytes(sort);
            System.out.println(INDENT + "// " + hexEncode(sort));
        }
        System.out.println();

        System.out.println(INDENT + "// New random build suffix.");
        System.out.println(INDENT + "String BUILD_SUFFIX = \"" + hexEncode(build) + "\";");
        System.out.println();

        System.out.println(INDENT + "// New random bytes for next time.");
        System.out.println(INDENT + "byte[] NEW_BYTES = " + getBytes(bytes) + ";");
        System.out.println();

        System.out.println(INDENT + "// Random bytes used for keys below.");
        System.out.println(INDENT + "byte[] BYTES = " + getBytes(AppletSecureRandom.BYTES) + ";");
        System.out.println();

        System.out.println(INDENT + "// DSA public and private key parameters.");
        System.out.println(INDENT + "String Y = \"" + dsaPublicKey.getY() + "\";");
        System.out.println(INDENT + "String X = \"" + dsaPrivateKey.getX() + "\";");
        System.out.println(INDENT + "String P = \"" + params.getP() + "\";");
        System.out.println(INDENT + "String Q = \"" + params.getQ() + "\";");
        System.out.println(INDENT + "String G = \"" + params.getG() + "\";");
        System.out.println();

        System.out.println(INDENT + "// Serialized Java version " + VERSION + " " + publicKey.getClass().getName() + ".");
        System.out.println(INDENT + "String PUBLIC_KEY = ");
        System.out.println(encode(publicKey));
        System.out.println();

        System.out.println(INDENT + "// Serialized Java version " + VERSION + " " + privateKey.getClass().getName() + ".");
        System.out.println(INDENT + "String PRIVATE_KEY = ");
        System.out.println(encode(privateKey));
        System.out.println("}");
    }
}
