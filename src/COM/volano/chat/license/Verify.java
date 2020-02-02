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
package COM.volano.chat.license;

import COM.volano.chat.Build;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.catalina.util.Base64;

public class Verify {
    private static final String STD_INPUT = "-";
    private static final String DELIMETER = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String KEY_ALGORITHM = "DSA";
    private static final String SIG_ALGORITHM = "DSA";
    private static final String ENCODING = "UTF-8";
    private static final String OK = "ok";
    private static final String NOT_OK = "not ok";
    private static final String HELP =
        "Type \"java Verify -h\" for help.";
    private static final String USAGE =
        "Usage: java Verify [options]\n"
        + "where options include:\n"
        + "  -f <file> verify entries in properties file\n"
        + "  -h        print this help text\n"
        + "  -v        print version\n"
        + "A file of \"-\" means read from standard input.\n"
        + "The verification is written to standard output.\n"
        + "Examples:\n"
        + "  java Verify -f key.txt\n"
        + "  java Verify -f - < key.txt > verify.txt";

    private static String filename = null;

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec    spec    = new DSAPublicKeySpec(new BigInteger(Constants.Y), new BigInteger(Constants.P), new BigInteger(Constants.Q), new BigInteger(Constants.G));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePublic(spec);
    }

    private static final String getData(String string) {
        return string.substring(0, string.lastIndexOf(DELIMETER));
    }

    private static final String getSignature(String string) {
        return string.substring(string.lastIndexOf(DELIMETER) + 1);
    }

    private static final boolean verify(PublicKey key, byte[] data, byte[] signature)
    throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance(SIG_ALGORITHM);
        dsa.initVerify(key);
        dsa.update(data);
        return dsa.verify(signature);
    }

    private static final void setOptions(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(USAGE);
            throw new Exception();
        }

        int index = 0;
        while (index < args.length) {
            String option = args[index];
            try {
                if (option.equals("-f")) {
                    filename = args[++index];
                } else if (option.equals("-h")) {
                    System.out.println(USAGE);
                    throw new Exception();
                } else if (option.equals("-v")) {
                    System.out.println(Build.VERSION);
                    throw new Exception();
                } else {
                    throw new Exception(option + ": illegal argument");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new Exception(option + ": missing value");
            }
            index++;
        }
    }

    public static final void main(String[] args) throws Exception {
        try {
            setOptions(args);

            // Decode our internal public key.
            PublicKey publicKey = getPublicKey();

            // Load the properties file.
            InputStream input = null;
            if (filename.equals(STD_INPUT)) {
                input = new BufferedInputStream(System.in);
            } else {
                input = new BufferedInputStream(new FileInputStream(filename));
            }
            Properties properties = new Properties();
            properties.load(input);
            input.close();

            // Verify each element in the properties file.
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                String line = key + KEY_VALUE_SEPARATOR + value;
                byte[] data = getData(line).getBytes(ENCODING);
                byte[] signature = Base64.decode(getSignature(line).getBytes(ENCODING));
                String result = verify(publicKey, data, signature) ? OK : NOT_OK;
                System.out.println(result + '\t' + key);
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null) {
                System.err.println(message);
                System.err.println(HELP);
            }
            System.exit(1);
        }
    }
}
