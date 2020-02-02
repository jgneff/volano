/*
 * ProxyServlet.java - a servlet for proxying HTTP requests from applets.
 * Copyright (C) 1996-2003 John Neffenger
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
 *
 */

package COM.volano;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import COM.volano.chat.Build;

/**
 * This servlet functions as a proxy server acting on behalf of Java applets
 * requesting images or Web pages.  The parameters correspond to the attributes
 * of a linked image, such as:
 * <pre>
 * &lt;a href="link.html"&gt;&lt;img src="image.gif"&gt;&lt;/a&gt;
 * </pre>
 * Note that this is not a transparent proxy and rewrites the request and
 * response headers it receives.
 *
 * <p>The servlet parameters are:
 *
 * <dl>
 * <dt><code>src</code>
 * <dd>specifies the source location of an image retrieved by an applet using
 * the <code>Applet.getImage</code> method.
 *
 * <dt><code>href</code>
 * <dd>specifies the anchor location of an HTML document retrieved by an applet
 * using the <code>AppletContext.showDocument</code> method.
 *
 * <dt><code>trace</code>
 * <dd><code>true</code> if the proxy servlet should trace the headers of each
 * HTTP request and response; otherwise <code>false</code>.  The default is
 * <code>false</code>.
 * </dl>
 *
 * <p>If both <code>src</code> and <code>href</code> are specified, the
 * <code>href</code> parameter is ignored.
 *
 * @version 2.5.1
 * @author  John Neffenger
 */

// Final HTTP response to client with Apache Tomcat 5.0 and Java 1.4.2
// -------------------------------------------------------------------
// GET /servlet/proxy?src=http://leader.linkexchange.com/2/X540954/showle HTTP/1.0
//
// HTTP/1.1 200 OK
// Set-Cookie: XLINK2=540954:191221; Path=/servlet/proxy
// Content-Type: image/gif
// Date: Fri, 25 Jul 2003 15:27:30 GMT
// Server: Apache-Coyote/1.1
// Connection: close
//
// GET /servlet/proxy?src=http://leader.linkexchange.com/2/X540954/showle HTTP/1.1
//
// HTTP/1.1 200 OK
// Set-Cookie: XLINK2=540954:436580; Path=/servlet/proxy
// Content-Type: image/gif
// Transfer-Encoding: chunked
// Date: Fri, 25 Jul 2003 15:27:58 GMT
// Server: Apache-Coyote/1.1
//
// GET /servlet/proxy?href=http://leader.linkexchange.com/2/X540954/clickle HTTP/1.0
//
// HTTP/1.1 302 Moved Temporarily
// Location: http://www.onchat.com/first-time.html
// Content-Length: 0
// Date: Fri, 25 Jul 2003 15:31:02 GMT
// Server: Apache-Coyote/1.1
// Connection: Keep-Alive
//
// GET /servlet/proxy?href=http://leader.linkexchange.com/2/X540954/clickle HTTP/1.1
//
// HTTP/1.1 302 Moved Temporarily
// Location: http://www.onchat.com/first-time.html
// Content-Length: 0
// Date: Fri, 25 Jul 2003 15:33:37 GMT
// Server: Apache-Coyote/1.1
//
// Internet Explorer 6.0 with Microsoft Java 1.1.4
// -----------------------------------------------
// -> GET /servlet/proxy?trace=true&src=http://leader.linkexchange.com/2/X540954/showle HTTP/1.1
// -> accept-language: en-US
// -> accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
// -> user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Win32)
// -> host: localhost:8080
// -> connection: Keep-Alive
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:15:47 GMT
//   <- Connection: close
//   <- P3P: CP="NOI OUR BUS IND CUR UNI COM NAV INT"
//   <- Set-Cookie: XLINK2=540954:436580; path=/
//   <- Location: http://image.linkexchange.com/00/43/65/80/banner468x60.gif
//   <-
//
//   <- HTTP/1.1 200 OK
//   <- Date: Fri, 25 Jul 2003 15:15:47 GMT
//   <- Server: Apache/1.3.6 (Unix)
//   <- Last-Modified: Tue, 21 Mar 2000 16:46:47 GMT
//   <- ETag: "252c28-b82-38d7a777"
//   <- Accept-Ranges: bytes
//   <- Content-Length: 2946
//   <- Connection: close
//   <- Content-Type: image/gif
//   <-
//
// -> GET /servlet/proxy?trace=true&href=http://leader.linkexchange.com/2/X540954/clickle HTTP/1.1
// -> accept: */*
// -> accept-language: en-us,en;q=0.8,it;q=0.5,fr;q=0.3
// -> accept-encoding: gzip, deflate
// -> user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Q312461; .NET CLR 1.0.3705; .NET CLR 1.1.4322)
// -> host: localhost:8080
// -> connection: Keep-Alive
// -> cookie: XLINK2=540954:436580
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:15:50 GMT
//   <- Connection: close
//   <- Location: http://www.onchat.com/first-time.html
//   <-
//
// -> GET /servlet/proxy?trace=true&src=http://leader.linkexchange.com/4/X540954/showle HTTP/1.1
// -> accept-language: en-US
// -> accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
// -> user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Win32)
// -> host: localhost:8080
// -> connection: Keep-Alive
// -> cookie: XLINK2=540954:436580
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:16:10 GMT
//   <- Connection: close
//   <- P3P: CP="NOI OUR BUS IND CUR UNI COM NAV INT"
//   <- Set-Cookie: XLINK4=540954:1708556; path=/
//   <- Location: http://image.linkexchange.com/01/70/85/56/banner468x60.gif
//   <-
//
//   <- HTTP/1.1 200 OK
//   <- Date: Fri, 25 Jul 2003 07:25:26 GMT
//   <- Server: Apache/1.3.6 (Unix)
//   <- Last-Modified: Sat, 05 Jul 2003 22:03:40 GMT
//   <- ETag: "57aaa0-2d38-3f074b3c"
//   <- Accept-Ranges: bytes
//   <- Content-Length: 11576
//   <- Connection: close
//   <- Content-Type: image/gif
//   <-
//
// -> GET /servlet/proxy?trace=true&href=http://leader.linkexchange.com/4/X540954/clickle HTTP/1.1
// -> accept: */*
// -> accept-language: en-us,en;q=0.8,it;q=0.5,fr;q=0.3
// -> accept-encoding: gzip, deflate
// -> user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Q312461; .NET CLR 1.0.3705; .NET CLR 1.1.4322)
// -> host: localhost:8080
// -> connection: Keep-Alive
// -> cookie: XLINK2=540954:436580; XLINK4=540954:1708556
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:16:14 GMT
//   <- Connection: close
//   <- Location: http://www.wellnessrecipes.com
//   <-
//
// Mozilla 1.4 with Sun Java 1.4.2
// -------------------------------
// -> GET /servlet/proxy?trace=true&src=http://leader.linkexchange.com/2/X540954/showle HTTP/1.1
// -> user-agent: Mozilla/4.0 (Windows 2000 5.0) Java/1.4.2
// -> host: localhost:8080
// -> accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
// -> connection: keep-alive
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:20:40 GMT
//   <- Connection: close
//   <- P3P: CP="NOI OUR BUS IND CUR UNI COM NAV INT"
//   <- Set-Cookie: XLINK2=540954:191221; path=/
//   <- Location: http://image.linkexchange.com/00/19/12/21/banner468x60.gif
//   <-
//
//   <- HTTP/1.1 200 OK
//   <- Date: Fri, 25 Jul 2003 15:32:00 GMT
//   <- Server: Apache/1.3.6 (Unix)
//   <- Last-Modified: Sun, 11 May 2003 20:39:06 GMT
//   <- ETag: "70e323-257a-3ebeb4ea"
//   <- Accept-Ranges: bytes
//   <- Content-Length: 9594
//   <- Connection: close
//   <- Content-Type: image/gif
//   <-
//
// -> GET /servlet/proxy?trace=true&href=http://leader.linkexchange.com/2/X540954/clickle HTTP/1.1
// -> host: localhost:8080
// -> user-agent: Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.4) Gecko/20030624
// -> accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,video/x-mng,image/png,image/jpeg,image/gif;q=0.2,*/*;q=0.1
// -> accept-language: en-us,en;q=0.8,it;q=0.5,fr;q=0.3
// -> accept-encoding: gzip,deflate
// -> accept-charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
// -> keep-alive: 300
// -> connection: keep-alive
// -> referer: http://localhost:8080/servlet/vcclient/
// -> cookie: XLINK2=540954:191221
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:20:44 GMT
//   <- Connection: close
//   <- Location: http://games.swirve.com/utopia
//   <-
//
// -> GET /servlet/proxy?trace=true&src=http://leader.linkexchange.com/4/X540954/showle HTTP/1.1
// -> cookie: XLINK2=540954:191221
// -> user-agent: Mozilla/4.0 (Windows 2000 5.0) Java/1.4.2
// -> host: localhost:8080
// -> accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
// -> connection: keep-alive
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:21:00 GMT
//   <- Connection: close
//   <- P3P: CP="NOI OUR BUS IND CUR UNI COM NAV INT"
//   <- Set-Cookie: XLINK4=540954:436580; path=/
//   <- Location: http://image.linkexchange.com/00/43/65/80/banner468x60.gif
//   <-
//
//   <- HTTP/1.1 200 OK
//   <- Date: Fri, 25 Jul 2003 15:13:17 GMT
//   <- Server: Apache/1.3.6 (Unix)
//   <- Last-Modified: Tue, 21 Mar 2000 16:46:47 GMT
//   <- ETag: "252c28-b82-38d7a777"
//   <- Accept-Ranges: bytes
//   <- Content-Length: 2946
//   <- Connection: close
//   <- Content-Type: image/gif
//   <-
//
// -> GET /servlet/proxy?trace=true&href=http://leader.linkexchange.com/4/X540954/clickle HTTP/1.1
// -> host: localhost:8080
// -> user-agent: Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.4) Gecko/20030624
// -> accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,video/x-mng,image/png,image/jpeg,image/gif;q=0.2,*/*;q=0.1
// -> accept-language: en-us,en;q=0.8,it;q=0.5,fr;q=0.3
// -> accept-encoding: gzip,deflate
// -> accept-charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
// -> keep-alive: 300
// -> connection: keep-alive
// -> referer: http://localhost:8080/servlet/vcclient/
// -> cookie: XLINK2=540954:191221; XLINK4=540954:436580
// ->
//
//   <- HTTP/1.1 302 Found
//   <- Server: Microsoft-IIS/5.0
//   <- Date: Fri, 25 Jul 2003 15:21:03 GMT
//   <- Connection: close
//   <- Location: http://www.onchat.com/first-time.html
//   <-

public class ProxyServlet extends HttpServlet {
    // Parameter names are from <a href="link.html"><img src="image.gif"></a>.
    private static final String PARAM_SRC = "src";
    private static final String PARAM_HREF = "href";
    private static final String PARAM_TRACE = "trace";

    // HTTP request headers managed by this servlet.
    //   X-Forwarded-For is client IP address for ValueClick.
    //   Client-IP is client IP address for DoubleClick.
    private static final String X_FORWARDED_FOR_KEY = "X-Forwarded-For";
    private static final String CLIENT_IP_KEY = "Client-IP";
    private static final String COOKIE_KEY = "Cookie";

    // HTTP response headers managed by this servlet.
    private static final String LOCATION_KEY = "Location";
    private static final String SET_COOKIE_KEY = "Set-Cookie";

    private static final String HEADER_SEPARATOR = ": ";
    private static final String REQUEST_PREFIX = "-> ";
    private static final String RESPONSE_PREFIX = "  <- ";
    private static final int BUFFER_SIZE = 256;

    // 1 year * 365 days/year * 24 hours/day * 60 minutes/hour * 60 seconds/minute
    // = 31,536,000 seconds
    private static final int COOKIE_AGE = 1 * 365 * 24 * 60 * 60;

    /**
     * Returns a string containing information about the servlet, such as its
     * author, version, and copyright.
     *
     * @return the servlet information string.
     */

    public String getServletInfo() {
        return getClass().getName()
               + " "
               + Build.VERSION
               + " "
               + Build.VOLANO_URL
               + Character.LINE_SEPARATOR
               + Build.COPYRIGHT;
    }

    /**
     * Called when an HTTP GET request is received for this servlet.
     *
     * If the location is specified by <code>src</code>, this servlet follows redirects
     * on behalf of the applet since <code>Applet.getImage</code> does not, and would
     * likely trigger a network security exception if it did.  We also can't simply
     * let the <code>HttpURLConnection</code> follow the redirects, since we need to
     * accumulate any cookies that are set in the intermediate redirect responses
     * and add them to our final response to the client.
     *
     * If the location is specified by <code>href</code>, this servlet passes a
     * redirect back to the client since they were invoked by the applet with
     * <code>AppletContext.showDocument</code>.  In fact, we must pass the redirect
     * back to the client since we don't want any secondary requests (with relative
     * links for inline images) coming back to us.
     *
     * @param request   encapsulates the request to the servlet.
     * @param response  encapsulates the response from the servlet.
     * @exception ServletException  if a servlet error occurs.
     * @exception IOException       if an I/O error occurs.
     */

    // Do not disconnect the HttpURLConnection.  See:
    //   Bug ID 4147525, "bugs in HttpUrlConnection with KeepAlive"
    //   http://developer.java.sun.com/developer/bugParade/bugs/4147525.html

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String location = null;
        String src = request.getParameter(PARAM_SRC);
        String href = request.getParameter(PARAM_HREF);
        String trace = request.getParameter(PARAM_TRACE);
        if (src != null && src.length() > 0) {
            location = src;
        } else if (href != null && href.length() > 0) {
            location = href;
        }
        if (location == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no src or href parameter");
            return;
        }

        boolean followingRedirects = location == src;
        boolean traceHeaders = Boolean.valueOf(trace).booleanValue();
        if (traceHeaders) {
            traceRequest(REQUEST_PREFIX, request);
        }

        // A full URLEncoder.encode messes up the URL, so do a mini-encode.
        location = location.replace(' ', '+');
        URL url = null;

        try {
            Vector cookies = new Vector();
            HttpURLConnection connection = null;
            while (location != null) {
                url = new URL(location);
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                copyHeaders(request, connection);
                connection.connect();
                saveCookies(request, connection, cookies);
                if (traceHeaders) {
                    traceResponse(RESPONSE_PREFIX, connection);
                }
                location = followingRedirects ? nextLocation(connection) : null;
            }
            copyHeaders(response, connection);
            addCookies(response, cookies);
            copyContent(response, connection);
        } catch (MalformedURLException e) {
            log("Unable to get URL (" + e + ")");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        } catch (IOException e) {
            log("Error getting URL <" + url + "> (" + e + ")");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
    }

    /**
     * Copies select client request headers over to our request to the server.
     *
     * This method copies over the first Cookie request header and sets the
     * X-Forwarded-For and Client-IP headers to identify the IP address of
     * the actual client.  The method does not copy other headers, such as
     * the User-Agent header, because remote ad servers can get fancy
     * with their responses (such as sending iframes instead of images)
     * based on assumptions about the client.
     *
     * @param request     the HTTP request from the client.
     * @param connection  the URL connection to the server.
     */

    private void copyHeaders(HttpServletRequest request, URLConnection connection) {
        String address = request.getRemoteAddr();
        connection.setRequestProperty(X_FORWARDED_FOR_KEY, address);
        connection.setRequestProperty(CLIENT_IP_KEY, address);
        // Change to getHeaders/addRequestProperty in Java 1.4 so we
        // can process multiple Cookie request headers.
        String cookie = request.getHeader(COOKIE_KEY);
        if (cookie != null && cookie.length() > 0) {
            connection.setRequestProperty(COOKIE_KEY, cookie);
        }
    }

    /**
     * Saves any cookies that were sent back in the HTTP response
     * by one or more Set-Cookie headers.
     *
     * @param request     the HTTP request from the client.
     * @param connection  the URL connection to the server.
     * @param cookies     the list in which to accumulate the cookies.
     * @exception IOException  if an I/O error occurs.
     */

    // 5 Apr 2005
    // Let the cookies last one year (rather than until the browser is closed)
    // so that ad servers which send back their cookie on the first
    // click-through will work immediately on subsequent browser sessions.

    private void saveCookies(HttpServletRequest request, URLConnection connection, Vector cookies)
    throws IOException {
        // Process multiple Set-Cookie response headers.
        String key = null;
        for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase(SET_COOKIE_KEY)) {
                String field = connection.getHeaderField(i);
                int index = field.indexOf('=');
                if (index != -1) {
                    String name = field.substring(0, index);
                    int temp = field.indexOf(';');
                    int end = temp == -1 ? field.length() : temp;
                    String value = field.substring(index + 1, end);
                    Cookie cookie = new Cookie(name, value);
                    cookie.setPath(request.getRequestURI());
                    cookie.setMaxAge(COOKIE_AGE);
                    cookies.addElement(cookie);
                }
            }
        }
    }

    /**
     * Obtains the next location if this connection has been redirected.
     * If there are multiple Location headers in the response, this method
     * returns the value of the last.
     *
     * @param connection  the HTTP URL connection to the server.
     * @return the next location if redirected; otherwise <code>null</code>.
     * @exception IOException  if an I/O error occurs.
     */

    // 300 Mutliple Choices   (HTTP/1.1 - RFC 2616)
    // 301 Moved Permanently  (HTTP/1.0 - RFC 1945)
    // 302 Moved Temporarily  (HTTP/1.0 - RFC 1945, sometimes "Found")
    // 303 See Other          (HTTP/1.1 - RFC 2616)
    // 304 Not Modified       (HTTP/1.0 - RFC 1945)
    // 305 Use Proxy          (HTTP/1.1 - RFC 2616)
    // 307 Temporary Redirect (HTTP/1.1 - RFC 2616)

    private String nextLocation(HttpURLConnection connection) throws IOException {
        String location = null;
        // Note that HttpURLConnection.HTTP_NOT_MODIFIED (304) is not included,
        // and HttpURLConnection does not define "307 Temporary Redirect".
        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_MULT_CHOICE
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_SEE_OTHER
                || status == HttpURLConnection.HTTP_USE_PROXY) {
            location = connection.getHeaderField(LOCATION_KEY);
            if (location != null) {
                // In case the new location is not an absolute URL.
                location = new URL(connection.getURL(), location).toString();
            }
        }
        return location;
    }

    /**
     * Copies select server response headers over to our response to the client.
     *
     * This method copies the HTTP status code and the Content-Type and Location
     * header values if present in the response from the server.
     *
     * @param response    the HTTP response to the client.
     * @param connection  the HTTP URL connection to the server.
     * @exception IOException  if an I/O error occurs.
     */

    // Do not copy hop-by-hop HTTP response headers.  See RFC 2616:
    //   Hypertext Transfer Protocol -- HTTP/1.1
    //   13.5.1 End-to-end and Hop-by-hop Headers
    //   http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.5.1

    private void copyHeaders(HttpServletResponse response, HttpURLConnection connection)
    throws IOException {
        int code = connection.getResponseCode();
        if (code != -1) {
            response.setStatus(code);
        }
        String type = connection.getContentType();
        if (type != null && type.length() > 0) {
            response.setContentType(type);
        }
        String location = connection.getHeaderField(LOCATION_KEY);
        if (location != null && location.length() > 0) {
            response.setHeader(LOCATION_KEY, location);
        }
    }

    /**
     * Adds accumulated cookies to the HTTP response.
     *
     * @param response  the HTTP response to the client.
     * @param cookies   the list of cookie values to add.
     */

    private void addCookies(HttpServletResponse response, Vector cookies) {
        // The setHeader method overwrites any previous cookies,
        // so we need to use the addCookie method to add multiple cookies.
        Enumeration enumeration = cookies.elements();
        while (enumeration.hasMoreElements()) {
            response.addCookie((Cookie) enumeration.nextElement());
        }
    }

    /**
     * Copies the URL content from the server response over to our
     * response to the client.  If the request was successful, our
     * reponse contains the requested data.  Otherwise, our response
     * contains the server's original error page, if any.
     *
     * This method has a small internal buffer and flushes the output
     * stream often to prevent excess memory usage.
     *
     * @param response    the HTTP response to the client.
     * @param connection  the HTTP URL connection to the server.
     * @exception IOException  if an I/O error occurs.
     */

    private void copyContent(HttpServletResponse response, HttpURLConnection connection) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = connection.getErrorStream();
            if (input == null) {
                input = connection.getInputStream();
            }
            output = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
                output.flush();
            }
        }
        // Continue with what we got (or nothing) if the copy failed.
        catch (IOException e) {}
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Traces HTTP request headers from the actual client to us.
     *
     * @param prefix   the prefix to use for each traced line.
     * @param request  the request from the client.
     */

    private void traceRequest(String prefix, HttpServletRequest request) {
        synchronized (System.out) {
            System.out.println(
                prefix
                + request.getMethod()
                + " "
                + request.getRequestURI()
                + "?"
                + request.getQueryString()
                + " "
                + request.getProtocol());
            Enumeration enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = request.getHeader(name);
                System.out.println(prefix + name + HEADER_SEPARATOR + value);
            }
            System.out.println(prefix);
            System.out.println();
        }
    }

    /**
     * Traces HTTP response headers from the actual server to us.
     *
     * @param prefix      the prefix to use for each traced line.
     * @param connection  the URL connection to the server.
     */

    private void traceResponse(String prefix, URLConnection connection) {
        synchronized (System.out) {
            System.out.println(prefix + connection.getHeaderField(0));
            String key = null;
            for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                String field = connection.getHeaderField(key);
                System.out.println(prefix + key + HEADER_SEPARATOR + field);
            }
            System.out.println(prefix);
            System.out.println();
        }
    }
}
