/*
 * ConfigServlet.java - Web based configuration for the applet properties.
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

package COM.volano;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import COM.volano.util.Base64;

/**
 * This servlet allows for Web based configuration of the VolanoChat applet
 * properties.
 *
 * 07 Oct 1998 - John Neffenger (jgn)
 *   Deny access if administrator password is defined but blank.
 *
 * @author  Paul Jack
 * @version 07 Oct 1998
 */

public class ConfigServlet extends HttpServlet {
    final static String HEADING     = "VolanoChat Applet Properties - do not move or modify this line.";
    final static String BASIC_REALM = "Basic realm=\"VOLANO Chat Server\"";

    final static String ADMIN_PASSWORD_KEY        = "admin.password";
    final static String CODEBASE_KEY              = "applet.codebase";
    final static String INSTALL_ROOT_KEY          = "install.root";
    final static String DEFAULT_CLIENT_DIR        = "vcclient";
    final static String DEFAULT_PASSWORD          = null;
    final static String TEMPLATE_KEY              = "template";
    final static String PROPERTIES_KEY            = "properties";
    final static String SAVE_FILENAME_KEY         = "properties";
    final static String defaultPropertiesFilename = "english.txt";
    final static String defaultTemplateFilename   = "template.html";

    String adminPassword;   // password to authenticate
    String templatePath;    // path from which to read the templates
    String propertiesPath;  // path from which to read the properties
    // -- this is effectively the applet's codebase

    /**
     * Initializes this servlet.
     *
     * @param config Contains the startup parameters
     * @throws ServletException if super.init(ServletConfig) throws it
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // For NT users running VolanoChat as a service, see if they've
        // specified the install root.
        String workingDirectory = System.getProperty(INSTALL_ROOT_KEY);
        if (workingDirectory == null) {
            workingDirectory = System.getProperty("user.dir");
        }
        // first try servlet parameter
        propertiesPath =
            getInitParameter(config, CODEBASE_KEY, null);
        // next try System property.
        if (propertiesPath == null) {
            propertiesPath = System.getProperty(CODEBASE_KEY);
        }
        // latch ditch effort
        if (propertiesPath == null) {
            propertiesPath = workingDirectory + File.separator + DEFAULT_CLIENT_DIR;
        }
        templatePath = propertiesPath;
        adminPassword =
            getInitParameter(config, ADMIN_PASSWORD_KEY, DEFAULT_PASSWORD);
    }


    /**
     * Handles HTTP GET requests.
     * Returns a template with substituted parameters.  Doesn't update the
     * properties file.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doWork(request, response, false);
    }


    /**
     * Handles HTTP POST requests.
     * Returns a template with substituted parameters.  Updates the properties
     * file.
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doWork(request, response, true);
    }


    /**
     * Reads a template file, optionally saves new properties into a file,
     * and substitutes the properties from that file in the template.
     *
     * @param request The request object, perhaps containg POST key/value pairs
     * @param response The response object
     * @param save Whether or not to save new properties (true for POST, false for GET)
     */
    void doWork(HttpServletRequest request, HttpServletResponse response, boolean save) throws IOException {
        // Check that the administrator password is defined.
        String test = getAdminPassword();
        if (test == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ADMIN_PASSWORD_KEY + " is undefined.");
            return;
        }

        // before we do anything else, ensure authenticatation.
        if (!authenticate(request, response)) {
            return;
        }
        // get the template filename, if specified.  Otherwise use the default.
        String templateFilename =
            getQueryParameter(request, TEMPLATE_KEY, defaultTemplateFilename);
        // get the properties filename, if specified.  Otherwise use the default.
        String propertiesFilename =
            getQueryParameter(request, PROPERTIES_KEY, defaultPropertiesFilename);

        Properties props;
        try { // load the properties
            props = loadProperties(propertiesPath, propertiesFilename);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "IO Error occurred while reading properties: " + e);
            return;
        }
        // the "save" filename is where we're writing the properties.  It
        // defaults to the same file we read the properties from but can be
        // overriden by setting a "properties" POST value.
        String saveFilename = propertiesFilename;
        if (save) {
            // Could they have made this any more difficult?  Iterate over the
            // the POST key/value pairs, updating the properties object with these
            // values.
            for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
                String s = (String)e.nextElement();
                String value = request.getParameterValues(s)[0];
                if (s.equals(SAVE_FILENAME_KEY)) {
                    saveFilename = value;
                } else {
                    props.put(s, value);
                }
            }
            // now save them.
            try {
                saveProperties(props, propertiesPath, saveFilename);
            } catch (IOException e) {
                response.sendError(response.SC_BAD_REQUEST, "IO Error occured while saving properties: " + e);
                return;
            }
        }
        props.put(SAVE_FILENAME_KEY, saveFilename);
        // Properties are ready, now perform the translation with a Formatter
        // object.
        response.setContentType("text/html");
        try {
            FileReader fr = openReader(templatePath, templateFilename);
            new Formatter(fr, response.getWriter(), props).translate();
            fr.close();
        } catch (IOException e) {
            response.sendError(response.SC_BAD_REQUEST, "IO Error occurred while reading template: " + e);
            return;
        }
    }


    /**
     * Get a parameter from a request object.
     *
     * @param request The request object
     * @param p       The name of the parameter
     * @param d       The default value of the parameter
     * @return        The value of the property
     */
    String getQueryParameter(HttpServletRequest request, String p, String d) {
        String query = request.getQueryString();
        if (query == null) {
            return d;
        } else {
            Hashtable params = HttpUtils.parseQueryString(query);
            String[] annoying = (String[])params.get(p);
            if (annoying == null) {
                return d;
            } else {
                return annoying[0];
            }
        }
    }


    /**
     * Get a parameter from a config object.
     *
     * @param config The config object
     * @param p      The name of the parameter
     * @param d      The default value of the parameter
     * @return       The value of the parameter
     */
    String getInitParameter(ServletConfig config, String p, String d) {
        String result = config.getInitParameter(p);
        return (result == null) ? d : result;
    }

    /**
     * Cause a .htaccess style login dialog to display at the user's browser
     * so we can verify the user.
     *
     * @param req Request block from the doGet()
     * @param resp Response block from the doGet()
     */
    void displayLoginDialog(HttpServletRequest req, HttpServletResponse resp) {
        // Make their browser popup a login dialog
        resp.setStatus( HttpServletResponse.SC_UNAUTHORIZED);
        // Set the product name displayed in the login dialog
        resp.setHeader("WWW-Authenticate", BASIC_REALM);
    }


    /**
     * Verifies authentication.
     *
     * @param req Request block from the doGet()
     * @param resp Response block from the doGet()
     * @return <CODE>true</CODE> if the user is authenticated; <CODE>false</CODE> otherwise.
     */
    boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
        // gets the most recent admin password
        String adminPassword = getAdminPassword();
        // If the admin password is undefined or blank, deny access. (jgn)
        if (adminPassword == null) {
            return false;
        } else {                                    // jgn
            adminPassword = adminPassword.trim();     // jgn
            if (adminPassword.length() == 0) {        // jgn
                return false;                           // jgn
            }                                         // jgn
        }                                           // jgn

        // See if we have an Authorization header, if not then the user must
        // first login before anything else. Once a user has logged in, the
        // Authorization header will be returned with all requests
        // The Authorization header contains 'Basic userid:password'
        String auth = req.getHeader("Authorization");

        // If there is no Authorization header, then we haven't made the
        // user login yet, so do so
        if (auth == null) {
            displayLoginDialog(req, resp);
            return false;
        } else {
            // Need to always validate the userid/password
            String userid = null;
            if ((userid = authorizeAndReturnUserid(resp, auth, adminPassword)) != null) {
                return true;
            } else {
                // Password authorization failed so force login again
                displayLoginDialog(req, resp);
                return false;
            }
        }
    }


    /**
     * Decode the autorization string and check that the userid and password
     * are authorized.
     *
     * @param auth the decoded Authorization header value ("Basic userid:password")
     * @return boolean true If the userid/password is valid
     */
    String authorizeAndReturnUserid(HttpServletResponse resp, String auth, String adminPassword) {
        try {
            StringTokenizer st = new StringTokenizer(auth);
            // Discard the 'Basic' before the userid:password
            st.nextToken();
            // Get userid:password
            String base64UseridPassword = st.nextToken();

            // Decode the base64
            String decoded = Base64.decode(base64UseridPassword);

            // Split the decoded string at the ':'
            st = new StringTokenizer(decoded, ":");
            String userid = st.nextToken();
            String password = st.nextToken();
            String adminUserid = userid; // Don't bother validating userid for the moment - Dec 2nd 97
            if (userid.equals(adminUserid) && password.equals(adminPassword)) {
                return userid;
            } else {
                return null;
            }
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    /**
     * Returns the admin password.
     * First we check for a System property, then we return the value
     * specified in the servlet configuration.
     *
     * @return the admin password.
     */
    String getAdminPassword() {
        String s = adminPassword;
        return (s == null) ? System.getProperty(ADMIN_PASSWORD_KEY) : s;
    }


    /**
     * Convenience method for opening a file stream.
     *
     * @param path  The file's path
     * @param name  The file's name
     * @return    The <CODE>FileInputStream</CODE>.
     * @throws IOException if an I/O error occurs
     */
    FileInputStream openStream(String path, String name) throws IOException {
        return new FileInputStream(new File(path, name));
    }


    /**
     * Convenience method for opening a file reader.
     *
     * @param path  The file's path
     * @param name  The file's name
     * @return    The <CODE>FileReader</CODE>.
     * @throws IOException if an I/O error occurs
     */
    FileReader openReader(String path, String name) throws IOException {
        return new FileReader(new File(path, name));
    }


    /**
     * Convenience method for reading properties from a file.
     *
     * @param path  The file's path
     * @param name  The file's name
     * @return    The properties.
     * @throws IOException if an I/O error occurs
     */
    Properties loadProperties(String path, String name) throws IOException {
        Properties result = new Properties();
        InputStream inp = openStream(path, name);
        result.load(inp);
        inp.close();
        return result;
    }


    /**
     * Convenience method for writing properties to a file.
     * Synchronized so the file doesn't become corrupted during concurrent
     * accesses.
     *
     * @param path  The file's path
     * @param name  The file's name
     * @throws IOException if an I/O error occurs
     */
    synchronized void saveProperties(Properties p, String path, String name) throws IOException {
        FileOutputStream fout = new FileOutputStream(new File(path, name));
        p.store(fout, HEADING);
        fout.close();
    }

}
