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
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import org.apache.catalina.util.Base64;

public class Sign {
    private static final String STD_INPUT = "-";
    private static final String DELIMETER = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String HOST_PORT_SEPARATOR = ":";
    private static final String URL_PREFIX = "http://";
    private static final String KEY_ALGORITHM = "DSA";
    private static final String SIG_ALGORITHM = "DSA";
    private static final String ENCODING = "UTF-8";
    private static final String VOLANO_CHAT = "VolanoChat";
    private static final String VOLANO_CHAT_PRO = "VolanoChatPro";
    private static final String VOLANO_CHAT_SP = "VolanoChatSP";
    private static final String NULL_HOST = "0.0.0.0:0";
    private static final String NULL_DATE = "null";

    private static final String HEADER = "License file for VOLANO 2.5+"
                                         + ". Save as plain text to \"conf/key.txt\".";

    private static final String HELP = "Type \"java Sign -h\" for help.";
    private static final String USAGE =
        "Usage: java Sign [options]\n"
        + "where options include:\n"
        + "  -s <string> sign license string\n"
        + "  -f <file>   sign file containing one license per line\n"
        + "  -h          print this help text\n"
        + "  -v          print version\n"
        + "A license string is in the format:\n"
        + "  host:port=product,limit,expiration\n"
        + "A file of \"-\" means read from standard input.\n"
        + "The license key is written to standard output.\n"
        + "Examples:\n"
        + "  java Sign -s 0.0.0.0:0=VolanoChatPro,5,12/31/2002\n"
        + "  java Sign -s www.volano.com:8000=VolanoChatPro,4000,null\n"
        + "  java Sign -s 192.168.0.2:8000=VolanoChat,0,null\n"
        + "  java Sign -f input.txt > key.txt\n"
        + "  java Sign -f - < input.txt > key.txt";

    private static String string = null;
    private static String filename = null;

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec    spec    = new DSAPrivateKeySpec(new BigInteger(Constants.X), new BigInteger(Constants.P), new BigInteger(Constants.Q), new BigInteger(Constants.G));
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        return factory.generatePrivate(spec);
    }

    // Return the key in "key=value".
    private static final String getKey(String string) {
        return string.substring(0, string.indexOf(KEY_VALUE_SEPARATOR));
    }

    // Return the value in "key=value".
    private static final String getValue(String string) {
        return string.substring(string.indexOf(KEY_VALUE_SEPARATOR) + 1);
    }

    // Convert the host name to its numeric IP address.
    // www.volano.com:8000 -> 209.61.182.173:8000
    private static final String getIPAddress(String host)
    throws MalformedURLException, UnknownHostException {
        URL url = new URL(URL_PREFIX + host);
        InetAddress address = InetAddress.getByName(url.getHost());
        return address.getHostAddress() + HOST_PORT_SEPARATOR + url.getPort();
    }

    // Sign the byte array and return the signature as a printable string.
    private static final String sign(PrivateKey privateKey, byte[] data)
    throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature dsa = Signature.getInstance(SIG_ALGORITHM);
        dsa.initSign(privateKey);
        dsa.update(data);
        return new String(Base64.encode(dsa.sign()), ENCODING);
    }

    // Sign each line and return a properties file with each entry signed.
    // Each line is of the format:
    //    host:port=product,limit,expiration
    // Each entry in the returned properties file is of the format:
    //    host:port=product,limit,expiration,signature
    private static final Properties sign(PrivateKey privateKey, BufferedReader reader) throws Exception {
        Properties properties = new Properties();
        String line = reader.readLine();
        while (line != null) {
            String string = parse(line);
            String signature = sign(privateKey, string.getBytes(ENCODING));
            String key = getKey(string);
            String value = getValue(string) + DELIMETER + signature;
            properties.setProperty(key, value);
            line = reader.readLine();
        }
        return properties;
    }

    // Check the validity of each key and value where the key is in the format
    // "host:port" and the value is in the format "product,limit,expiration".
    private static final String parse(String string) throws Exception {
        String address = null;
        String product = null;
        String limit = null;
        String expiration = null;
        try {
            String key = getKey(string);
            String value = getValue(string);
            StringTokenizer tokenizer = new StringTokenizer(value, DELIMETER);
            product = tokenizer.nextToken();
            limit = tokenizer.nextToken();
            expiration = tokenizer.nextToken();
            address = key.equals(NULL_HOST) ? NULL_HOST : getIPAddress(key);
            if (!product.equals(VOLANO_CHAT) && !product.equals(VOLANO_CHAT_PRO) && !product.equals(VOLANO_CHAT_SP)) {
                throw new Exception("Error parsing product in " + value);
            }
            Integer integer = Integer.valueOf(limit);
            if (!expiration.equals(NULL_DATE)) {
                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
                Date date = format.parse(expiration);
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println("Unrecognized license format in \"" + string + "\".");
            throw e;
        } catch (NoSuchElementException e) {
            System.err.println("Missing fields in \"" + string + "\".");
            throw e;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host in \"" + string + "\".");
            throw e;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing limit in \"" + string + "\".");
            throw e;
        } catch (ParseException e) {
            System.err.println("Error parsing expiration date in \"" + string + "\".");
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
        return address + KEY_VALUE_SEPARATOR + product + DELIMETER + limit + DELIMETER + expiration;
    }

    // Set each command-line option with error checking.
    private static final void setOptions(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(USAGE);
            throw new Exception();
        }

        int index = 0;
        while (index < args.length) {
            String option = args[index];
            try {
                if (option.equals("-s")) {
                    string = args[++index];
                } else if (option.equals("-f")) {
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

        if (string != null && filename != null) {
            throw new Exception("Must specify string or file but not both.");
        }
    }

    public static final void main(String[] args) throws Exception {
        try {
            setOptions(args);

            // Decode our internal private key.
            PrivateKey privateKey = getPrivateKey();

            BufferedReader reader = null;
            if (string != null) {
                reader = new BufferedReader(new StringReader(string));
            } else if (filename.equals(STD_INPUT)) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else if (filename != null) {
                reader = new BufferedReader(new FileReader(filename));
            }
            Properties properties = sign(privateKey, reader);
            properties.store(System.out, HEADER);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null) {
                System.err.println(message);
                System.err.println(HELP);
            }
        }
    }
}
