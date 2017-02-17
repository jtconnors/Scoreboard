/*
 * Copyright (c) 2013, Jim Connors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of this project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scoreboard.common.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import scoreboard.common.networking.SocketListener;
import static scoreboard.common.Constants.MAX_DATAGRAM_MSG_SIZE;
import static scoreboard.common.Constants.DEBUG_NONE;
import static scoreboard.common.Constants.DEBUG_SEND;
import static scoreboard.common.Constants.DEBUG_RECV;
import static scoreboard.common.Constants.DEBUG_EXCEPTIONS;
import static scoreboard.common.Constants.DEBUG_STATUS;
import static scoreboard.common.Constants.DEFAULT_PORT;
import static scoreboard.common.Constants.DEFAULT_SESSION_ADDR;
import scoreboard.common.networking.SocketBase;


/**
 * This class provides the framework for a multicast socket reader.
 * When an update message is to be posted, it is broadcast over the
 * multicast socket such that all connected clients will receive the message.
 * 
 * This class is abstract and requires implementation of the onMessage()
 * and onClosedStatus() methods.
 */
public abstract class MulticastConnection extends SocketBase implements
        SocketListener, Runnable {
    
    private MulticastSocket multicastSocket = null;
    private int portNum;
    private String addr;
    private InetAddress inetAddress;
    private int debugFlags = DEBUG_NONE;
        
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
                System.out.println("Multicast Connection closed");
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
        
    /*
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
            new ReaderThread().start();
        } catch (Exception e) {
            if (debugFlagIsSet(DEBUG_EXCEPTIONS)) {
                e.printStackTrace();
            }
        }
    }
    
    /*
     * Read from the multicast socket in a separate thread to make this
     * asynchronous.  The main program will be informed of a new received
     * packet when the onMessage() method (to be implemented by a subclass)
     * is called.
     */
    class ReaderThread extends Thread {           
        @Override
        public void run() {
            try {
                if (multicastSocket.isBound()) {
                    /*
                     * onClosedStatus() method to be implemented by a
                     * sublclass.
                     */
                    onClosedStatus(false) ;
                } else {
                    throw new Exception("ReaderThread socket not bound");
                }
                byte[] readBuf = new byte[MAX_DATAGRAM_MSG_SIZE];
                DatagramPacket readPacket = new DatagramPacket(readBuf,
                        readBuf.length);
                /*
                 * Read one DatagramPacket at a time from MulticastSocket
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
                     * onMessage() method to be implemented by a sublclass.
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
     * @param flags The debugging flags to enable
     */
    public void setDebugFlags(int flags) {
        debugFlags = flags;
    }

    /**
     * Turn off debugging option.
     */
    public void clearDebugFlags() {
        debugFlags = DEBUG_NONE;
    }
    
    @Override
    public void run() { 
        connect();   
    }

    public MulticastConnection() {
        this(DEFAULT_SESSION_ADDR, DEFAULT_PORT, DEBUG_NONE);
    }

    public MulticastConnection(int portNum) {
        this(DEFAULT_SESSION_ADDR, portNum, DEBUG_NONE);
    }

    public MulticastConnection(String addr, int portNum, int debugFlags) {
        this.addr = addr;
        this.portNum = portNum;
        this.debugFlags = debugFlags;
    }
}