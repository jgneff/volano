/*
 * Connection.java - an observable connection for streamable objects
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

package COM.volano.net;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class manages a socket connection that can be used to send and receive
 * <code>Streamable</code> objects.  For example, a server could accept new
 * connections with a method like the following:
 * <pre>
 * public void listen() throws IOException {
 *   ServerSocket serverSocket = new ServerSocket(8000);
 *   while (true) {
 *     Socket     socket     = serverSocket.accept();
 *     Connection connection = new Connection(socket);
 *     connection.addObserver(this);
 *     connection.startSending(Thread.NORM_PRIORITY);
 *     connection.startReceiving(Thread.NORM_PRIORITY);
 *   }
 * }
 * </pre>
 * Once the <code>startReceiving</code> method is called, all observers of the
 * connection are invoked through their <code>update</code> method with any
 * objects received on the connection.  For example, here is an
 * <code>update</code> method that prints the received object and echoes it back
 * to the client:
 * <pre>
 * public void update(Observable observable, Object object) throws IOException {
 *   Connection connection = (Connection) observable;
 *   if (object instanceof TalkPacket) {
 *     TalkPacket packet = (TalkPacket) object;
 *     System.out.println("Packet says:  " + packet.say());
 *     connection.send(packet);
 *   }
 *   else if (object instanceof StreamableError) {
 *     StreamableError e = (StreamableError) object;
 *     System.err.println("Closing connection (" + e.getText() + ").");
 *     connection.close();
 *   }
 *   else if (object instanceof InterruptedIOException) {
 *     InterruptedIOException e = (InterruptedIOException) object;
 *     System.err.println("Closing connection (" + e + ").");
 *     connection.close();
 *   }
 *   else if (object == null) {
 *     System.out.println("Connection is closed.");
 *     connection.deleteObserver(this);
 *   }
 * }
 * </pre>
 * where <code>TalkPacket</code> implements the <code>Streamable</code>
 * interface.  For example:
 * <pre>
 * import java.io.*;
 * public class TalkPacket implements Streamable {
 *   private String text;
 *   public TalkPacket() {
 *     this.text = "";
 *   }
 *   public TalkPacket(String text) {
 *     this.text = text;
 *   }
 *   public String say() {
 *     return text;
 *   }
 *   public void writeTo(DataOutputStream output) throws IOException {
 *     output.writeUTF(text);
 *   }
 *   public void readFrom(DataInputStream input) throws IOException {
 *     text = input.readUTF();
 *   }
 * }
 * </pre>
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @since   20 May 1996
 * @see     Packet
 * @see     Streamable
 * @see     StreamableError
 */

public class Connection extends Observable implements Runnable {
    // 3 month sample of access log with 651,546 VolanoChat (HTTP/2.1.0)
    // entries running 2.1.9.0 on JDK 1.2.2 on Linux gave:
    //
    //   200 OK                                                 537,546   83%
    //   400 Bad Request (protocol violation, new to 2.1.10)
    //   401 Unauthorized (bad password)                            172  < 1%
    //   403 Forbidden (host or document denied)                 17,411    3%
    //   408 Request Time-out (dead session timeout)             29,119    4%
    //   409 Conflict (multiple sign on of member-monitor)          229  < 1%
    //   413 Request Entity Too Large (string limits exceeded)       15  < 1%
    //   500 Internal Server Error (socket error)                66,993   10%
    //   503 Service Unavailable (server shutdown)                   61  < 1%
    //   505 HTTP Version not supported (wrong applet type)           0    0%
    //                                                          -------
    //                                                          651,546  100%

    // HTTP status codes recorded by this class.
    static final int HTTP_OK             = 200;
    static final int HTTP_BAD_REQUEST    = 400;
    static final int HTTP_INTERNAL_ERROR = 500;
    static final int HTTP_UNAVAILABLE    = 503;

    private static final String RECEIVE_NAME    = "Receiver-";
    private static final String SEND_NAME       = "Sender-";
    private static final int    ATTRIBUTE_SIZE  =   11;
    private static final int    JOIN_TIMEOUT    = 3000;   // 3 seconds

    // When set to false, we do not place the send threads into a thread group so
    // as to avoid Apple Mac Runtime for Java bug 2232076.  Using thread groups on
    // the Mac can result in intermittent java.lang.IllegalThreadStateExceptions.
    public static boolean useThreadGroups = true; // Group the send threads
    public static boolean verbose;                // Verbose error messages

    private static PacketFactory factory      = new PacketFactory();
    private static ThreadGroup   sendGroup    = new ThreadGroup("Senders");
    private static ThreadGroup   receiveGroup = new ThreadGroup("Receivers");
    private static Hashtable     connections  = new Hashtable();

    // Use static object locks instead of "static synchronized" methods to avoid
    // Java bugs 4056233 and 4041699 (fixed in Java 1.2).

    private static Object  idCounterLock = new Object();  // Synchronization lock
    private static int     idCounter;                     // Connection id counter
    private static Object  countLock = new Object();      // Synchronization lock
    private static int     count;                         // Number of active connections
    private static Object  turnstile = new Object();      // Used for turnstile synchronization
    private static int     turnstileCount;                // Count of connections through turnstile

    private static boolean counting;                      // Counting packets sent and received
    private static Object  sentLock = new Object();       // Synchronization lock
    private static long    sent;                          // Number of packets sent
    private static Object  receivedLock = new Object();   // Synchronization lock
    private static long    received;                      // Number of packets received

    private Socket                socket;
    private boolean               useTurnstile;
    private DataInputStream       input;
    private DataOutputStream      output;
    private InetAddress           inetAddress;    // The other side's IP address
    private SendQueue             queue;
    private boolean               isServer;

    private boolean   open       = true;
    private int       id         = nextId();
    private long      startTime  = System.currentTimeMillis();
    private long      endTime;
    private Thread    receiver;
    private Thread    sender;
    private int       timeouts;
    private int       status     = HTTP_OK;       // Reason connection was closed
    private String    kicker     = "";            // Host name of closing client
    private Hashtable attributes = new Hashtable(ATTRIBUTE_SIZE);

    /**
     * Returns the next integer identifier for this connection.  This identifier
     * is unique among all connection objects in the virtual machine.
     *
     * @return the next integer identifier for a connection.
     */

    private static int nextId() {
        synchronized (idCounterLock) {
            return ++idCounter;               // Return value after increment
        }
    }

    /**
     * Adds a connection to the list of all connections, increments the connection
     * counter and returns its incremented value.
     *
     * @param connection  the connection to add.
     * @return the number of connections, including this one.
     */

    private static int add(Connection connection) {
        String host = connection.getHostAddress();
        synchronized (countLock) {
            Vector list = (Vector) connections.get(host);
            if (list == null) {
                list = new Vector(1);
            }
            list.addElement(connection);
            connections.put(host, list);
            return ++count;                   // Return value after increment
        }
    }

    /**
     * Removes a connection from the list of all connections, decrements the
     * connection counter and returns its decremented value.
     *
     * @param connection  the connection to remove.
     * @return the number of connections, excluding this one.
     */

    private static int remove(Connection connection) {
        String host = connection.getHostAddress();
        synchronized (countLock) {
            Vector list = (Vector) connections.get(host);
            if (list != null) {
                list.removeElement(connection);
                if (list.isEmpty()) {
                    connections.remove(host);
                }
            }
            return --count;                   // Return value after decrement
        }
    }

    /**
     * Gets the list of connections with this host name.
     *
     * @param host  the IP address of this connection.
     * @return  an array of all connections with this host name.
     */

    public static Connection[] get(String host) {
        Connection[] array = new Connection[0];
        synchronized (countLock) {
            Vector list = (Vector) connections.get(host);
            if (list != null) {
                array = new Connection[list.size()];
                list.copyInto(array);
            }
        }
        return array;
    }

    /**
     * Checks whether the list of connections contains one with this host address.
     *
     * @param host the IP address to check.
     * @return true if there is already a connection with this address.
     */

    public static boolean isDuplicate(Connection connection) {
        boolean duplicate = false;
        Connection[] array = get(connection.getHostAddress());
        for (int i = 0; i < array.length && ! duplicate; i++) {
            if (connection != array[i] && connection.getStartTime() >= array[i].getStartTime()) {
                duplicate = true;
            }
        }
        return duplicate;
    }

    /**
     * Increments the sent packet count by one.
     */

    private static void incrementSent() {
        synchronized (sentLock) {
            sent++;
        }
    }

    /**
     * Decrements the received packet count by one.
     */

    private static void incrementReceived() {
        synchronized (receivedLock) {
            received++;
        }
    }

    /**
     * Prints a timestamped error message followed immediately by a stack trace.
     *
     * @param message  a descriptive error message.
     * @param t        the error or exception which caused the problem.
     */

    static void printError(String message, Throwable t) {
        if (verbose) {
            synchronized (System.err) {
                System.err.println("[" + new Date() + "] " + message);
                t.printStackTrace(System.err);
            }
        } else {
            System.err.println("[" + new Date() + "] " + message + " (" + t + ")");
        }
    }

    /**
     * Sets the global packet factory for all connections.
     *
     * @param factory  the packet factory for creating all packets on all
     *                 connections.
     */

    public static void setPacketFactory(PacketFactory factory) {
        Connection.factory = factory;
    }

    /**
     * Enters through a <i>turnstile</i> to request permission to create another
     * connection.  If the current number of connections is less than the
     * specified limit, this method increments the count and returns immediately.
     * Otherwise, this method waits until the count is decremented by a closing
     * connection exiting through turnstile.
     *
     * @param  limit  the maximum number of concurrent connections allowed.
     * @exception java.lang.InterruptedException
     *           if interrupted while waiting for the connect count to be
     *           decremented.
     */

    public static void inTurnstile(int limit) throws InterruptedException {
        synchronized (turnstile) {
            while ((turnstileCount >= limit) && (limit != 0)) {
                turnstile.wait();
            }
            turnstileCount++;
        }
    }

    /**
     * Exits through a <i>turnstile</i> to give permission for another connection
     * by decrementing the connection count and notifying the waiting thread.
     */

    public static void outTurnstile() {
        synchronized (turnstile) {
            turnstileCount--;
            turnstile.notify();
        }
    }

    /**
     * Enables or disables packet counting by all connections for throughput
     * analysis, where a packet is a streamable object.  With this option enabled,
     * all connections in the virtual machine use static methods to increment the
     * sent count each time a streamable object is sent and to increment the
     * received count each time a streamable object is received.
     *
     * @param value  <code>true</code> to enable packet counting; otherwise
     *               <code>false</code>.
     */

    public static void setCounting(boolean value) {
        counting = value;
    }

    /**
     * Gets the number of packets sent by all connections in this virtual machine.
     *
     * @returns the number of packets sent.
     */

    public static long getSent() {
        return sent;
    }

    /**
     * Gets the number of packets received by all connections in this virtual
     * machine.
     *
     * @returns the number of packets received.
     */

    public static long getReceived() {
        return received;
    }

    /**
     * Returns the connection counter value.
     *
     * @return the number of connections.
     */

    public static int getCount() {
        return count;
    }

    /**
     * Returns the number of unique IP addresses connected to the server.
     *
     * @return the number of connections with a unique IP address.
     */

    public static int getUniqueCount() {
        return connections.size();
    }

    /**
     * Shuts down all connections on the server side by stopping all the send
     * threads, which in turn stop the receive threads.
     */

    public static void shutdown() {
        Thread[] receivers = new Thread[receiveGroup.activeCount()];
        receiveGroup.enumerate(receivers);
        sendGroup.stop();                   // Stop all connection threads
        for (int i = 0; i < receivers.length; i++) {
            if (receivers[i] != null) {
                try {
                    receivers[i].join(JOIN_TIMEOUT);
                } catch (InterruptedException e) {}
            }
        }
    }

    /**
     * Creates a connection from an existing socket, optionally entering through
     * a <i>turnstile</i> limiting the total number of concurrent connections.
     * This constructor is called from the server side when creating connections
     * for which there is a limit on the total number of connections.
     *
     * @param socket        the socket on which the connection is built.
     * @param useTurnstile  indicates that this connection came in through a
     *                      turnstile so it should exit through one as well.
     * @exception java.io.IOException when an I/O error occurs.
     */

    public Connection(Socket socket, boolean useTurnstile) throws IOException {
        this.socket       = socket;
        this.useTurnstile = useTurnstile;
        this.input        = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.output       = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.inetAddress  = socket.getInetAddress();
        this.queue        = new SendQueue(this);
        this.isServer     = true;
        add(this);
    }

    /**
     * Creates a connection from an existing socket without regard to any limit on
     * the total number of connections.  This constructor is called from the
     * server side.
     *
     * @param socket  the socket on which the connection is built.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public Connection(Socket socket) throws IOException {
        this(socket, false);                // Open without using turnstile
    }

    /**
     * Creates a connection given a host name and port number, allowing the choice
     * of a buffered or non-buffered input stream.  This constructor is called
     * from the client side.
     *
     * @param hostname  the name of the host running the server.
     * @param port      the port number on which the server accepts connections.
     * @param buffered  <code>true</code> for a buffered input stream; otherwise
     *                  <code>false</code>.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public Connection(String hostname, int port, boolean buffered) throws IOException {
        this.socket = new Socket(hostname, port);

        // Netscape Navigator 3.0 on the Mac cannot handle a BufferedInputStream,
        // so don't use one at all on the client side.
        if (buffered) {
            this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } else {
            this.input = new DataInputStream(socket.getInputStream());
        }
        this.output      = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.inetAddress = socket.getInetAddress();
        this.queue       = new SendQueue(this);
        add(this);
    }

    /**
     * Creates a connection given a host name and port number, using a buffered
     * input stream.  This constructor is called from the client side.
     *
     * @param hostname  the name of the host running the server.
     * @param port      the port number on which the server accepts connections.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public Connection(String hostname, int port) throws IOException {
        this(hostname, port, true);         // Open using buffered input streams
    }

    /**
     * Gets the underlying socket for this connection.
     *
     * @return the underlying stream socket.
     */

    public Socket getSocket() {
        return socket;
    }

    /**
     * Gets the IP address, in text format, of the other side of the connnection.
     *
     * @return a string containing the numeric IP address of the host to which
     *         this connection was made.
     */

    public String getHostAddress() {
        return inetAddress.getHostAddress();
    }

    /**
     * Gets the integer identifier of this connection.
     *
     * @return an integer value uniquely identifying this connection among all the
     *         connections in this virtual machine.
     */

    public int getId() {
        return id;
    }

    /**
     * Gets the time this connection was started.
     *
     * @return the time in milliseconds when the connection was started.
     */

    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the time this connection was closed.
     *
     * @return the time in milliseconds when the connection was closed, or zero if
     *         the connection is still open.
     */

    public long getEndTime() {
        return endTime;
    }

    /**
     * Gets the number of bytes sent on this connection.
     *
     * @return the total number of bytes sent on this connection.
     */

    public long getBytesSent() {
        return output.size();
    }

    /**
     * Gets the number of consecutive timeouts that have occurred on this
     * connection.  The timeout count is incremented when a timeout occurs on the
     * connection and is reset to zero each time an object is received on the
     * connection.
     *
     * @return the number of consecutive timeouts.
     */

    public int getTimeouts() {
        return timeouts;
    }

    /**
     * Sets the connection status code which specifies the reason this connection
     * was closed.  Status codes are taken from the HTTP server response codes
     * returned by Web servers.
     *
     * @param code  the status code value.
     */

    public void setStatus(int code) {
        status = code;
    }

    /**
     * Gets the connection status code, indicating why this connection was closed.
     *
     * @return the connection status code.
     */

    public int getStatus() {
        return status;
    }

    /**
     * Gets the name of the client host which closed this connection, or an empty
     * string if this connection was not closed by another client.
     *
     * @return the name of the host which closed this connection.
     */

    public String getKicker() {
        return kicker;
    }

    /**
     * Gets the value of the specified attribute.
     *
     * @param  name  the name of the requested attribute.
     * @return the attribute value, or <code>null</code> if not found.
     */

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets the value of the specified attribute.
     *
     * @param  name   the name of the attribute to set.
     * @param  value  the value of the attribute to set.
     * @return the previous value of the attribute, or <code>null</code> if it did
     *         not have one.
     */

    public Object setAttribute(String name, Object value) {
        return attributes.put(name, value);
    }

    /**
     * Gets the Boolean value of the specified attribute.
     *
     * @param  name  the name of the requested attribute.
     * @return the attribute's Boolean value, or <code>false</code> if not found.
     */

    public boolean getBoolean(String name) {
        Boolean value = (Boolean) attributes.get(name);
        return value == null ? false : value.booleanValue();
    }

    /**
     * Sets the Boolean value of the specified attribute to <code>true</code>.
     *
     * @param  name   the name of the attribute to set.
     * @return the previous value of the attribute, or <code>false</code> if it
     *         did not have one.
     */

    public boolean setBoolean(String name) {
        Boolean value = (Boolean) attributes.put(name, new Boolean(true));
        return value == null ? false : value.booleanValue();
    }

    /**
     * Starts the asynchronous send thread.  Objects may be sent synchronously
     * with the <code>send</code> method.
     *
     * @param priority  the priority of the send thread.
     * @see #write
     * @see #startReceiving
     */

    public synchronized void startSending(int priority) {
        if (sender == null) {
            if (isServer && useThreadGroups) {
                sender = new Thread(sendGroup, queue, SEND_NAME + id);
            } else {
                sender = new Thread(queue, SEND_NAME + id);
            }
            sender.setPriority(priority);
            sender.start();
        }
    }

    /**
     * Starts the asynchronous receive thread.  This thread notifies all observers
     * of any received objects, notifying with the <code>null</code> object when
     * the connection is closed.  Objects may be received synchronously with the
     * <code>read</code> method.
     *
     * @param priority  the priority of the receiving thread.
     * @see #read
     * @see #startSending
     */

    public synchronized void startReceiving(int priority) {
        if (receiver == null) {
            if (isServer && useThreadGroups) {
                receiver = new Thread(receiveGroup, this, RECEIVE_NAME + id);
            } else {
                receiver = new Thread(this, RECEIVE_NAME + id);
            }
            receiver.setPriority(priority);
            receiver.start();
        }
    }

    /**
     * Queues a streamable object on the send queue for the send thread.
     * If an error besides an <code>IOException</code> occurs while writing the
     * object, a <code>StreamableError</code> is sent on the connection instead.
     *
     * @param object  the streamable object to be sent on the connection.
     * @exception java.io.IOException  if the connection is closed.
     */

    public void send(Streamable object) throws IOException {
        int count = queue.putElement((Object) object);
        if (count == 0) {
            throw new IOException("connection is closed");
        }
    }

    /**
     * Writes a streamable object synchronously to the socket output stream and
     * increments the number of bytes sent on this connection.  This method will
     * block if no buffers are available in the underlying subsystem.  If an error
     * besides an <code>IOException</code> occurs while writing the object, a
     * <code>StreamableError</code> is written to the socket stream instead.
     * <p>This method is not thread safe and assumes only one send thread.
     *
     * @param object  the streamable object to be written to the output stream.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void write(Streamable object) throws IOException {
        // output.writeUTF(object.getClass().getName());
        output.writeShort(object.getId());
        object.writeTo(output);
        output.flush();
        if (counting) {
            incrementSent();
        }
    }

    /**
     * Sends a <code>StreamableError</code> on the connection and prints an error
     * message.
     *
     * @param t  the error or exception to be wrapped in a
     *           <code>StreamableError</code>.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private void notifyError(Throwable t) throws IOException {
        printError("Error handling packet from " + getHostAddress() + ".", t);
        send(new StreamableError(t));
    }

    /**
     * Pauses for the specified time if the object is streamable.
     *
     * @param object  the object read from the stream.
     * @exception java.io.InterruptedException if this thread is interrupted.
     */

    private void pause(Object object) throws InterruptedException {
        if (object instanceof Streamable) {
            long pause = ((Streamable) object).getReadPause();
            if (pause > 0L) {
                Thread.sleep(pause);
            } else {
                Thread.yield();    // Yield to equal priority threads (for Solaris)
            }
        }
    }

    /**
     * Handles an object received on this connection by notifying all of its
     * observers with the received object.
     *
     * @param object  the object received on the connection.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private void handleObject(Object object) throws IOException {
        try {
            notifyObservers(object);  // This method is synchronized in JDK 1.0.2
        } catch (RuntimeException e) {
            notifyError(e);
        } catch (Error e) {
            notifyError(e);
        }
    }

    /**
     * Reads a streamable object synchronously from the socket input stream and
     * increments the receive count if counting is enabled.  This method will
     * block if a complete object is not immediately available.
     * <p>This method is not thread safe and assumes only one receive thread.
     *
     * @returns  the streamable object read from the input stream, or an
     *           <code>InterruptedIOException</code> if a timeout occurred.
     *           Until the Linux timeout bug is fixed, this method may also return
     *           a <code>SocketException</code> to indicate a timeout when running
     *           the JDK 1.1.3 Java-Linux port.
     * @exception java.lang.ClassNotFoundException
     *              if the class of the received object does not have the no-arg
     *              constructor required in order to create a new instance.
     * @exception java.lang.IllegalAccessException
     *              if the class or its no-arg constructor is not accessible
     *              because it is not <code>public</code>.
     * @exception java.lang.InstantiationException
     *              if the class is <code>abstract</code>, an interface, or the
     *              creation of its instance fails for some other reason.
     * @exception java.io.IOException
     *              if an I/O error occurs while resurrecting the object.
     */

    public Object read() throws ClassNotFoundException, IOException, InterruptedException {
        try {
            input.mark(0);                    // For JavaSoft Bug 4054043
            Streamable object = factory.createPacket(input.readUnsignedShort());
            object.readFrom(input);
            timeouts = 0;
            if (counting) {
                incrementReceived();
            }
            return object;
        } catch (InterruptedIOException e) { // Is an IOException
            input.reset();                    // For JavaSoft Bug 4054043
            timeouts++;
            return e;                         // Bypasses notify if connection closed
        }
    }

    /**
     * The body of the asynchronous receive thread started by
     * <code>startReceiving</code>.  This thread receives objects from the
     * connection and notifies all of its observers.  If the connection is closed
     * by the peer or because of an error while receiving objects, all observers
     * are notified with the <code>null</code> object.
     */

    public void run() {
        try {
            do {
                Object object = read();
                setChanged();
                handleObject(object);   // Observers can close this connection
                pause(object);
            } while (open);
        }

        // Connection closed normally by other side.
        //   Windows JDK 1.1.5 - java.io.EOFException
        //           SDK 2.01  - java.io.EOFException
        //   Solaris JDK 1.1.4 - java.io.EOFException
        //           JIT 1.1.3 - java.io.EOFException
        //   Linux   JDK 1.1.3 - java.io.EOFException
        //   OS/2    JDK 1.1.4 - java.io.EOFException
        //
        // Timeout waiting for data from other side.
        //   Windows JDK 1.1.5 - java.io.InterruptedIOException: Read timed out
        //           SDK 2.01  - java.io.InterruptedIOException: Read timed out
        //   Solaris JDK 1.1.4 - java.io.InterruptedIOException: Read timed out
        //           JIT 1.1.3 - java.io.InterruptedIOException: Read timed out
        //   Linux   JDK 1.1.3 - java.net.SocketException: Interrupted system call (usually)
        //                     - java.io.InterruptedIOException: Read timed out (sometimes)
        //   OS/2    JDK 1.1.4 - java.io.InterruptedIOException: Read timed out
        //
        // Receive thread interrupted on pending read.
        //   Windows JDK 1.1.5 - No effect
        //           SDK 2.01  - java.io.InterruptedIOException: Thread interrupted
        //   Solaris JDK 1.1.4 - java.io.InterruptedIOException: operation interrupted
        //           JIT 1.1.3 - java.net.SocketException: Interrupted system call
        //   Linux   JDK 1.1.3 - java.io.InterruptedIOException: operation interrupted
        //   OS/2    JDK 1.1.4 - No effect.
        //
        // Socket input stream closed on pending read.
        //   Windows JDK 1.1.5 - java.net.SocketException: socket was closed
        //           SDK 2.01  - java.net.SocketException: socket was closed
        //   Solaris JDK 1.1.4 - java.net.SocketException: Socket closed
        //           JIT 1.1.3 - java.net.SocketException: Bad file number
        //   Linux   JDK 1.1.3 - java.net.SocketException: Socket closed
        //   OS/2    JDK 1.1.4 - java.net.SocketException: socket was closed
        //
        // Receive thread interrupted and socket input stream closed on pending read.
        //   Windows JDK 1.1.5 - java.net.SocketException: socket was closed
        //           SDK 2.01  - java.net.SocketException: socket was closed
        //   Solaris JDK 1.1.4 - java.io.InterruptedIOException: operation interrupted
        //           JIT 1.1.3 - java.net.SocketException: Interrupted system call
        //   Linux   JDK 1.1.3 - java.io.InterruptedIOException: operation interrupted
        //   OS/2    JDK 1.1.4 - java.net.SocketException: socket was closed

        // 3 month sample of errors with JDK 1.2.2 on Linux gave:
        //   java.net.SocketException          69,711
        //   java.lang.ClassNotFoundException       9
        //   java.io.UTFDataFormatException         4

        // Note that InterruptedIOExceptions are sent to observers.

        catch (EOFException e) {            // Is an IOException
            // Socket closed normally by other side.
        } catch (SocketException e) {       // Is an IOException
            if (open) {
                // Common errors with a sample percentage occurrence among all
                // SocketExceptions (3 month sample with JDK 1.2.2 on Linux):
                //   java.net.SocketException: Connection reset by peer  61,920   89%
                //   java.net.SocketException: No route to host           4,959    7%
                //   java.net.SocketException: Connection timed out       2,671    4%
                //   java.net.SocketException: Broken pipe                  152  < 1%
                //   java.net.SocketException: Connection refused             6  < 1%
                //   java.net.SocketException: Network is unreachable         2  < 1%
                //   java.net.SocketException: Protocol not available         1  < 1%
                printError("Error reading from " + getHostAddress() + ".", e);
                // For version 2.5, leave the status code as HTTP_OK so we don't report
                // all the "connection reset by peer" exceptions as failed requests.
                // status = HTTP_INTERNAL_ERROR;
            }
        }
        // IOException is reported by the VolanoChat CreateRooms packet when it
        // detects a room list or room name too long.
        catch (IOException e) {             // Is an Exception
            printError("Error reading from " + getHostAddress() + ".", e);
            status = HTTP_BAD_REQUEST;        // Set error status code
        } catch (InterruptedException e) {  // Is an Exception
            if (open) {
                printError("Error reading from " + getHostAddress() + ".", e);
            }
        } catch (ClassNotFoundException e) { // Is an Exception
            printError("Error reading from " + getHostAddress() + ".", e);
            status = HTTP_BAD_REQUEST;        // Set error status code
        } catch (ThreadDeath e) {           // Is an Error
            status = HTTP_UNAVAILABLE;        // Shutting down (server) or pruned (applet)
            throw e;                          // Rethrow for cleanup
        } catch (Throwable t) {             // All others
            printError("Error reading from " + getHostAddress() + ".", t);
            status = HTTP_INTERNAL_ERROR;     // Set error status code
        } finally {
            endTime = System.currentTimeMillis();
            remove(this);
            if (useTurnstile) {               // If connection came through turnstile
                outTurnstile();    // Exit through turnstile as well
            }

            setChanged();
            notifyObservers(null);            // This method is synchronized in JDK 1.0.2
            deleteObservers();

            try {
                // Thread.interrupt has no effect in some older Java virtual machines
                // and may throw "netscape.security.AppletSecurityException:
                // security.thread" using Netscape 4.0 on the Mac.
                // It also throws java.lang.NoSuchMethodError using Java 1.021 on
                // Netscape 2.02.
                sender.interrupt();             // Interrupt send thread
            } catch (Throwable t) {}
            queue.close();                    // Force send thread off wait
            try {
                output.close();
            } catch (IOException e) {}

            // Closing the DataInputStream closes the BufferedInputStream which closes
            // (as a synchronized method in JDK 1.2) the SocketInputStream which
            // closes the PlainSocketImpl which closes the socket.
            // Closing the DataOutputStream closes the BufferedOutputStream which
            // closes the SocketOutputStream which closes the PlainSocketImpl which
            // closes the socket.
            // So just close the socket.
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * Closes the connection without a status code, for use when the client closes
     * a connection normally.
     */

    public synchronized void close() {
        if (open) {
            open = false;
            if (Thread.currentThread() != receiver) { // No need to interrupt ourselves
                // 2.6.3
                // Interrupting the receive thread has no effect on the thread,
                // is unnecessary, and even hits this nasty bug in J2SE 1.5 Beta 1:
                //   java.nio.channels.ClosedChannelException reading font file
                //   http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4983039
                // receiver.interrupt();
                try {
                    // Since BufferedInputStream.read and BufferedInputStream.close are
                    // synchronized in J2SE 1.2.2, we must close the underlying socket in
                    // order to unblock the read operation.  Closing the
                    // BufferedInputStream will simply hang if a read operation
                    // is pending.  Closing the socket throws the following to the
                    // receive thread:
                    //   java.net.SocketException: socket closed
                    // input.close(); // This hangs on JDK 1.2.2 if read blocked
                    socket.close(); // Force receive thread off blocked read
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Closes the connection with a status code, for use when the server closes a
     * connection.
     *
     * @param status  the status code giving the reason why this connection was
     *                closed.
     */

    public void close(int status) {
        this.status = status;
        close();
    }

    /**
     * Closes the connection with a status code and a host name, for use when one
     * client closes the connection of another.  For example, a monitor or
     * administrator may want to kick people off the server by closing their
     * connections.
     *
     * @param status  the status code giving the reason why this connection was
     *                closed.
     * @param kicker  the IP address of the host closing this connection.
     */

    public void close(int status, String kicker) {
        this.status = status;
        this.kicker = kicker;
        close();
    }

    /**
     * Finalizes this object by closing the connection.
     *
     * @exception Throwable  if an error occurs finalizing this object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /**
     * Returns a string representation of this object.
     */

    public String toString() {
        return getHostAddress() + "-" + getId();
    }
}
