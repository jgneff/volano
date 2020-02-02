/*
 * CountServlet.java - a Java servlet to get the current connection count.
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
import  COM.volano.chat.Build;        // For getServletInfo
import  COM.volano.net.Connection;
import  java.io.*;
import  java.text.*;
import  javax.servlet.*;
import  javax.servlet.http.*;

/**
 * This servlet prints an HTML page giving the current connection count.
 *
 * @version 24 February 1998
 * @author  John Neffenger
 */

public class CountServlet extends HttpServlet {
    public MessageFormat formatter;

    /**
     * Returns a string containing information about the servlet, such as its
     * author, version, and copyright.
     *
     * @return the servlet information string.
     */

    public String getServletInfo() {
        return getClass().getName() + " " + Build.VERSION + " " + Build.VOLANO_URL
               + Character.LINE_SEPARATOR + Build.COPYRIGHT;
    }

    /**
     * Called once by the network service each time it loads this servlet.  This
     * method is guaranteed to finish before any service requests are accepted.
     *
     * @param config the servlet configuration information.
     * @exception javax.servlet.ServletException if a servlet error occurs.
     * @exception java.io.IOException if an I/O error occurs.
     */

    public void init(ServletConfig config) throws ServletException {
        super.init(config);                 // To store servlet configuration
        String file = getInitParameter("template");
        if (file != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringWriter   string = new StringWriter();
                PrintWriter    writer = new PrintWriter(string);
                String line = reader.readLine();
                while (line != null) {
                    writer.println(line);
                    line = reader.readLine();
                }
                reader.close();
                formatter = new MessageFormat(string.toString());
            } catch (IOException e) {
                throw new UnavailableException(e.toString());
            }
        }
    }

    /**
     * Called when an HTTP GET request is received for this servlet.
     *
     * @param req encapsulates the request to the servlet.
     * @param res encapsulates the response from the servlet.
     * @exception javax.servlet.ServletException if a servlet error occurs.
     * @exception java.io.IOException if an I/O error occurs.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServletOutputStream out = res.getOutputStream();
        int count = Connection.getCount();
        if (formatter == null) {
            res.setContentType("text/plain");
            out.print(count);
        } else {
            res.setContentType("text/html");
            Object[] args = {new Integer(count)};
            out.print(formatter.format(args));
        }
        // Do not flush the stream since it prevents a keep-alive connection.
        out.close();
    }
}
