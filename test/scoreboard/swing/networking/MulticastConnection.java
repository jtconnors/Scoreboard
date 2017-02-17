

package scoreboard.swing.networking;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import static scoreboard.common.Constants.MAX_DATAGRAM_MSG_SIZE;
import static scoreboard.common.Constants.DEFAULT_PORT;
import static scoreboard.common.Constants.DEFAULT_SESSION_ADDR;
import static scoreboard.common.Constants.DEBUG_NONE;
import static scoreboard.common.Constants.DEBUG_STATUS;
import static scoreboard.common.Constants.DEBUG_SEND;
import static scoreboard.common.Constants.DEBUG_RECV;
import static scoreboard.common.Constants.DEBUG_EXCEPTIONS;

public class MulticastConnection implements SocketListener {

    private MulticastSocket multicastSocket = null;
    private ReaderThread readerThread;
    private SocketListener fxListener;
    private int portNum;
    private String addr;
    private InetAddress inetAddress;
    private int debugFlags = 0x0;

    public MulticastConnection(SocketListener fxListener) {
        this(fxListener, DEFAULT_SESSION_ADDR, DEFAULT_PORT, DEBUG_NONE);
    }

    public MulticastConnection(SocketListener fxListener, int portNum) {
        this(fxListener, DEFAULT_SESSION_ADDR, portNum, DEBUG_NONE);
    }

    public MulticastConnection(SocketListener fxListener,
            String addr, int portNum, int debugFlags) {
        this.fxListener = fxListener;
        this.addr = addr;
        this.portNum = portNum;
        this.debugFlags = debugFlags;
        connect();
    }

    /**
     * Set up a connection.  This method returns no status,
     * however the onClosedStatus(boolean) method will be called when the
     * status of the socket changes, either opened or closed (for whatever
     * reason).
     */
    private void connect() {
        try {
            /*
             * Create the MulticastSocket instance
             */
            multicastSocket = new MulticastSocket(portNum);
            inetAddress = InetAddress.getByName(addr);
            multicastSocket.joinGroup(inetAddress);
            /*
             * Background thread to continuously read from the input stream.
             */
            readerThread = new ReaderThread();
            readerThread.start();
        } catch (Exception e) {
            if (debugFlagIsSet(DEBUG_EXCEPTIONS)) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close down the MulticastSocket.  As per the Java Socket
     * API, once a Socket has been closed, it is not available for
     * further networking use (i.e. can't be reconnected or rebound).
     * A new Socket needs to be created.
     */
    private void close() {
        try {
            if (multicastSocket != null && !multicastSocket.isClosed()) {
                multicastSocket.close();
            }
            if (debugFlagIsSet(DEBUG_STATUS)) {
                System.out.println("Connection closed");
            }
            /*
             * The onClosedStatus() method has to be implemented by
             * a sublclass.  If used in conjunction with JavaFX 2.0,
             * use Platform.runLater() to force this method to run
             * on the main thread.
             */
            onClosedStatus(true);
        } catch (Exception e) {
            if (debugFlagIsSet(DEBUG_EXCEPTIONS)) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Send a message in String format to the MulticastSocket instance.
     *
     * @param msg The String message to send
     */
    public void sendMessage(String msg) {
        byte[] sendMsgBuf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(sendMsgBuf,
            sendMsgBuf.length, inetAddress, portNum);
        if (debugFlagIsSet(DEBUG_SEND)) {
            System.out.println("send> " + msg);
        }
        try {
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if the socket is connected.
     * @return true if the socket is connected, false if not.
     */
    public boolean isConnected() {
        if (multicastSocket != null) {
            return multicastSocket.isConnected();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the specified debug flag is set.
     * @param flag Debug flag in question
     * @return true if the debug flag 'flag' is set.
     */
    public boolean debugFlagIsSet(int flag) {
        return ((flag & debugFlags) != 0) ? true : false;
    }

    /**
     * Turn on debugging option.
     * @param flag The debugging flag to enable
     */
    public void setDebugFlag(int flag) {
        debugFlags |= flag;
    }

    /**
     * Turn off debugging option.
     * @param flag The debugging flag to disable
     */
    public void clearDebugFlag(int flag) {
        debugFlags &= flag;
    }

    /**
     * Called whenever a message is read from the socket.
     * In JavaFX 2.0, this method must be run on the
     * main thread.  This is accomplished by the Platform.runLater() call.
     * Failure to do so *will* result in strange errors and exceptions.
     * @param msg String message read from the socket.
     */
    @Override
    public void onMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fxListener.onMessage(msg);
            }
        });
    }

    /**
     * Called whenever the open/closed status of the Socket
     * changes.  In JavaFX 2.0, this method must be run on the
     * main thread.  This is accomplished by the Platform.runLater() call.
     * Failure to do so *will* result in strange errors and exceptions.
     * @param isClosed true if the socket is closed
     */
    @Override
    public void onClosedStatus(final boolean isClosed) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fxListener.onClosedStatus(isClosed);
            }
        });
    }

    class ReaderThread extends Thread {
        @Override
        public void run() {
            try {
                /*
                 * Now that the readerThread has started, it's safe to inform
                 * the world that the socket is open, if in fact, it is open.
                 * If used in conjunction with JavaFX2.0, use
                 * Platform.runLater() when implementing this method
                 * to force it to run on the main thread.
                 */
                if (multicastSocket.isBound()) {
                    onClosedStatus(false) ;
                } else {
                    throw new Exception("ReaderThread socket not bound");
                }
                byte[] readBuf = new byte[MAX_DATAGRAM_MSG_SIZE];
                DatagramPacket readPacket = new DatagramPacket(readBuf,
                        readBuf.length);
                /*
                 * Read one DatagramPackate at a time from MulticastSocket
                 * instance, The receive() method below will block until
                 * a datagram is recieved.
                 */
                while (true) {
                    multicastSocket.receive(readPacket);
                    String msg = new String(readPacket.getData(),
                            0, readPacket.getLength());
                    if (debugFlagIsSet(DEBUG_RECV)) {
                        System.out.println("recv> " + msg);
                    }
                    /*
                     * The onMessage() method has to be implemented by
                     * a sublclass.  If used in conjunction with JavaFX2.0,
                     * use Platform.runLater() to force this method to run
                     * on the main thread.
                     */
                    onMessage(msg);
                }
            } catch (Exception e) {
                if (debugFlagIsSet(DEBUG_EXCEPTIONS)) {
                    e.printStackTrace();
                }
            } finally {
                close();
            }
        }
    }
}
