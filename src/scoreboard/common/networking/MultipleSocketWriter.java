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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import static scoreboard.common.Constants.DEFAULT_PORT;
import static scoreboard.common.Constants.DEBUG_RECV;
import static scoreboard.common.Constants.DEBUG_SEND;
import static scoreboard.common.Constants.DEBUG_EXCEPTIONS;
import static scoreboard.common.Constants.DEBUG_STATUS;
import static scoreboard.common.Constants.DEBUG_NONE;


/**
 * This class provides the framework for a ServerSocket connection with
 * multiple listeners.  When an update message is to be posted, all listeners
 * will receive the message on their socket connection.
 * 
 * This class is abstract and requires implementation of the onMessage()
 * and onClosedStatus() methods.
 */
public abstract class MultipleSocketWriter extends SocketBase
        implements Runnable {
    
    private int listenerPort;
    private ServerSocket serverSocket;
    protected MultipleSocketWriter multipleSocketWriterRef;
    private int debugFlags = DEBUG_NONE;
    protected List<MultipleSocketWriterListener> updateListeners =
            new ArrayList<MultipleSocketWriterListener>();
    
    abstract public void onMessage(String msg);
        
    abstract public void onClosedStatus(boolean isClosed); 
    
    class MultipleSocketWriterListener implements SocketListener {
        
        private PrintWriter writer;
        private BufferedReader reader;
        private Socket socket;
        
        public void onMessage(String msg) {
            multipleSocketWriterRef.onMessage(msg);        
        }
        
        public void onClosedStatus(boolean isClosed) {
            multipleSocketWriterRef.onClosedStatus(isClosed);    
        } 
             
        /*
         * Close down the Socket infrastructure.  As per the Java Socket
         * API, once a Socket has been closed, it is not available for
         * further networking use (i.e. can't be reconnected or rebound).
         * A new Socket needs to be created.
         */
        private void close() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                if (debugFlagIsSet(DEBUG_STATUS)) {
                    System.out.println("Connection closed");
                }
                removeListener(this);
            } catch (Exception e) {
                if (debugFlagIsSet(DEBUG_EXCEPTIONS)) {
                    e.printStackTrace();
                }
            }
        }
        
        /*
         * Even if we don't read anything from the socket, set up a
         * ReaderThread because it will unable us to detect when a
         * socket connection has been closed.
         */
        class ReaderThread extends Thread {
            @Override
            public void run() {
                /*
                 * Read from input stream one line at a time.  The read
                 * loop will terminate when the socket connection is closed.
                 */
                try {
                    if (reader != null) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (debugFlagIsSet(DEBUG_RECV)) {
                                System.out.println("recv> " + line);
                            }
                            onMessage(line);
                        }
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
        
        public void sendMessage(String line) {
            try {
                if (debugFlagIsSet(DEBUG_SEND)) {
                    System.out.println("send> " + line);
                }
                writer.println(line);
                if (writer.checkError()) {
                    multipleSocketWriterRef.removeListener(this);
                }
            } catch (Exception ex) {
                multipleSocketWriterRef.removeListener(this);
            }
        }
        
        private void setup(Socket socket) throws IOException {
            this.socket = socket;
            /* 
             * Leave check for null here.  At startup, we create a null
             * listener just so that we can call onClosedStatus(true) to
             * print out the connection status line.
             */
            if (socket != null) {
                writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                new ReaderThread().start();
            }
        }
        
        public MultipleSocketWriterListener(Socket socket) throws IOException {
            setup(socket);
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
    
    private void addListener(SocketListener listener) {
        updateListeners.add((MultipleSocketWriterListener) listener);
        listener.onClosedStatus(false);
    }
    
    private void removeListener(SocketListener listener) {
        updateListeners.remove((MultipleSocketWriterListener) listener);
        listener.onClosedStatus(true);
    }
    
    public void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {        
        try {
            new MultipleSocketWriterListener(null).onClosedStatus(true);
            serverSocket = new ServerSocket(listenerPort);
            while(true) {
                Socket acceptSocket = serverSocket.accept();
                addListener(new MultipleSocketWriterListener(acceptSocket));
            }
        } catch(IOException e) {
            shutdown();
        }     
    }
    
    /*
     * Use ExecutorService to post updates.  Each individual socket will
     * get its own thread.  This construct seems to avoid a
     * ConcurrentModificationException, which happens if the updateListener
     * list is modified while iterating over it.
     */
    private ExecutorService executor = new ScheduledThreadPoolExecutor(10);
    
    public void postUpdate(final String line) {
        
        final MultipleSocketWriterListener[] listeners =
                this.updateListeners.toArray(
                new MultipleSocketWriterListener[this.updateListeners.size()]);
        
        for (int i = 0; i < listeners.length; i++) {
            final MultipleSocketWriterListener listener = listeners[i];
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    listener.sendMessage(line);
                }
            });    
        }
    }
    
    private void init() {
        /*
         * Avoid "leaking this in constructor" warning by moving the
         * following assignment outside the constructor into this method.
         */
        multipleSocketWriterRef = this;    
    }
    
    public MultipleSocketWriter () {
        this(DEFAULT_PORT, DEBUG_NONE);
    }
    
    public MultipleSocketWriter (int listenerPort) {
        this(listenerPort, DEBUG_NONE);
    }
    
    public MultipleSocketWriter(int listenerPort, int debugFlags) {        
        this.listenerPort = listenerPort;
        this.debugFlags = debugFlags;
        init();
    } 
}
