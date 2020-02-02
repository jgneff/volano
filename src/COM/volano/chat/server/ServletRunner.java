/*
 * ServletRunner.java - creates the embedded Tomcat servlet runner.
 * Copyright (C) 2001 John Neffenger
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

package COM.volano.chat.server;

import  COM.volano.chat.Build;
import  COM.volano.util.Message;

import  java.net.InetAddress;
import  java.net.UnknownHostException;

import  org.apache.catalina.Connector;
import  org.apache.catalina.Context;
import  org.apache.catalina.Host;
import  org.apache.catalina.LifecycleException;

import  org.apache.coyote.tomcat4.CoyoteConnector;
import  org.apache.catalina.core.StandardEngine;
import  org.apache.catalina.logger.FileLogger;
import  org.apache.catalina.startup.Embedded;
import  org.apache.catalina.valves.AccessLogValve;

/**
 * This class represents the embedded servlet runner.
 *
 * @author  John Neffenger
 * @version 22 May 2001
 */

public class ServletRunner {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private Embedded embedded;
    private StandardEngine engine;

    ServletRunner(Value value) {
        FileLogger logger = new FileLogger();
        logger.setDirectory(value.logDirectory);
        logger.setPrefix(value.logServletPrefix);
        logger.setSuffix(value.logServletSuffix);
        logger.setTimestamp(true);
        embedded = new Embedded(logger, null);

        engine = (StandardEngine) embedded.createEngine();
        engine.setName("Standard Engine");
        engine.setDefaultHost("localhost");

        Host host = embedded.createHost("localhost", "webapps");
        engine.addChild(host);

        Context context = embedded.createContext("", "ROOT");
        host.addChild(context);

        // Defaults in "server.xml" file
        // -----------------------------
        // <Connector className="org.apache.coyote.tomcat4.CoyoteConnector"
        //     port="8080" minProcessors="5" maxProcessors="75"
        //     enableLookups="true" redirectPort="8443"
        //     acceptCount="100" debug="0" connectionTimeout="20000"
        //     useURIValidationHack="false" disableUploadTimeout="true" />
        //
        // Defaults in code
        // ----------------
        // org.apache.catalina.Connector attributes:
        //   className                "org.apache.coyote.tomcat4.CoyoteConnector"
        //   enableLookups            false
        //   redirectPort             443
        //   scheme                   "http"
        //   secure                   false
        //
        // org.apache.coyote.tomcat4.CoyoteConnector attributes:
        //   acceptCount              10
        //   address                  null (all IP addresses)
        //   bufferSize               2048
        //   compression              "off"
        //   connectionLinger         -1 (disabled)
        //   connectionTimeout        60000 (1 minute)
        //   debug                    0
        //   disableUploadTimeout     false
        //   maxProcessors            20
        //   minProcessors            5
        //   port                     8080
        //   proxyName                null
        //   proxyPort                0
        //   tcpNoDelay               true
        //
        // Undocumented attributes (but still publicly settable):
        //   connectionUploadTimeout  300000 (5 minutes)
        //   maxKeepAliveRequests     100
        //   serverSocketTimeout      0
        //   tomcatAuthentication     true
        //   useURIValidationHack     true

        // Embedded.createConnector sets address, port, and secure attributes.
        //
        // Tomcat converts the InetAddress to a string and then creates a new
        // InetAddress from the string with:
        //   try{
        //       params[0]= InetAddress.getByName(value);
        //   }catch(UnknownHostException exc) {
        //       d("Unable to resolve host name:" + value);
        //       ok=false;
        //   }
        // So make sure the InetAddress is not resolved.  Otherwise, we get:
        //   IntrospectionUtils: Unable to resolve host name:t42.bc.volano.com/192.168.11.7

        // Can't fix until JDK 5.0 ...
        // JDK 5.0 - InetAddress address = InetAddress.getByAddress(value.license.getInetAddress().getAddress());
        // JDK 5.0 - CoyoteConnector connector = (CoyoteConnector) embedded.createConnector(address, value.servletPort, false);
        CoyoteConnector connector = (CoyoteConnector) embedded.createConnector(value.license.getInetAddress(), value.servletPort, false);
        connector.setMinProcessors(value.servletMinprocessors);
        connector.setMaxProcessors(value.servletMaxprocessors);
        connector.setTcpNoDelay(false);

        if (value.logHttpPrefix.length() > 0) {
            AccessLogValve valve = new AccessLogValve();
            valve.setPrefix(value.logHttpPrefix);
            valve.setSuffix(value.logHttpSuffix);
            valve.setPattern("combined");
            engine.addValve(valve);
        }

        embedded.addEngine(engine);
        embedded.addConnector(connector);
    }

    void start() {
        try {
            embedded.start();
        } catch (LifecycleException e) {
            System.err.println(Message.format(Msg.ERROR_STARTING, e.toString()));
        }
    }

    void stop() {
        try {
            embedded.removeEngine(engine);
            embedded.stop();
        } catch (LifecycleException e) {
            System.err.println(Message.format(Msg.ERROR_STOPPING, e.toString()));
        }
    }
}
