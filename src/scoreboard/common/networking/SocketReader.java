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
 *   * Neither the name of the TimingFramework project nor the names of its
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

import static scoreboard.common.Constants.DEFAULT_HOST;
import static scoreboard.common.Constants.DEFAULT_PORT;
import static scoreboard.common.Constants.DEBUG_NONE;

/**
 * WARNING: This class is meant for testing outside the JavaFX framework. In
 * JavaFX we must insure that certain work must be done on the main thread, and
 * this class does no such thing.
 */
public class SocketReader extends SocketBase {

    private String host;
    private int port;
    private SocketClient socketClient;

    class SocketReaderListener implements SocketListener {

        @Override
        public void onMessage(String msg) {
            System.out.println("onMessage: " + msg);
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            System.out.print("onClosedStatus: socket is ");
            if (isClosed) {
                System.out.println("closed");
            } else {
                System.out.println("open");
            }
            if (isClosed) {
                try {
                    Thread.sleep(3000);
                    connect();
                } catch (Exception e) {
                }
            }
        }
    }

    public void connect() {
        socketClient = new SocketClient(
                new SocketReader.SocketReaderListener(), host, port,
                getDebugFlags());
        socketClient.connect();
    }

    /**
     * Send a message in the form of a String to the socket.
     *
     * @param msg The String message to send
     */
    public void sendMessage(String msg) {
        // Should never write anything
    }
    
    public SocketReader() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEBUG_NONE);
    }

    public SocketReader(String host, int port) {
        this(host, port, DEBUG_NONE);
    }

    public SocketReader(String host, int port,
            int debugFlags) {
        this.host = host;
        this.port = port;
        setDebugFlags(debugFlags);
    }

    public static void main(String[] args) {
        SocketReader socketReader = new SocketReader(DEFAULT_HOST,
                DEFAULT_PORT, DEBUG_NONE);
        socketReader.connect();
    }
}
