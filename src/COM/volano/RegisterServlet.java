/*
 * RegisterServlet.java - a Java servlet to register for event notifications.
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
import  COM.volano.chat.server.Main;
import  java.io.*;
import  java.net.*;
import  javax.servlet.*;
import  javax.servlet.http.*;

/**
 * This servlet registers a callback URL for event notifications.
 *
 * @version 22 Aug 1998
 * @author  John Neffenger
 */

public class RegisterServlet extends HttpServlet {
    private static final String ROOM = "room";
    private static final String HREF = "href";

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
     * Called when an HTTP GET request is received for this servlet.
     *
     * @param req encapsulates the request to the servlet.
     * @param res encapsulates the response from the servlet.
     * @exception java.io.IOException if an I/O error occurs.
     */

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String room = req.getParameter(ROOM);
        String href = req.getParameter(HREF);
        room = room == null ? "" : room.trim();
        href = href == null ? "" : href.trim();
        try {
            if (room.length() == 0) {
                throw new ServletException("no room name");
            }
            if (href.length() == 0) {
                throw new ServletException("no callback URL");
            }
            Main.addRoomObserver(room, new URL(href));
            // We must return some content with a non-zero length in order to enable a
            // keep-alive connection.
            res.setContentType("text/plain");
            res.setContentLength(1);          // Ensures a keep-alive connection
            OutputStream output = res.getOutputStream();
            output.write('\n');
            output.close();
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
    }
}
