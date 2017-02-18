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

package scoreboard.fx2.networking;

import com.jtconnors.socket.SocketListener;
import com.jtconnors.socket.DebugFlags;
import com.jtconnors.socket.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import scoreboard.fx2.framework.hockey.HockeyScoreboard;

import scoreboard.common.Globals;

public class FxSocketReader {
    
    private static final Logger LOGGER = Logger.getLogger(
            FxSocketReader.class.getName());

    private HockeyScoreboard hockeyScoreboard;
    private FxSocketClient fxSocketClient;
    private String host;
    private int port;
    private int debugFlags;
    

    class FxSocketReaderListener implements SocketListener {

        /*
         * This method is already on the main thread via FxSocketClient's
         * call to onMessage().
         */
        @Override
        public void onMessage(String msg) {
            hockeyScoreboard.handleUpdate(msg);
        }

        /*
         * This method is already on the main thread via FxSocketClient's
         * call to onClosedStatus().
         */
        @Override
        public void onClosedStatus(boolean isClosed) {
            if ((debugFlags & DebugFlags.instance().DEBUG_STATUS) != 0) {
                if (isClosed != Globals.instance().socketClosed) {
                    LOGGER.log(Level.INFO,
                            "Socket status changed: isClosed = {0}", isClosed);  
                }
            }
            Globals.instance().socketClosed = isClosed;
            if (hockeyScoreboard != null) {
                hockeyScoreboard.updateStatusRow(isClosed ? 0 : 1);   
            }
            if (isClosed) {
                try {
                    Thread.sleep(3000);
                    connect();
                } catch (InterruptedException e) {
                }
            }
        }
    }
    

    public void connect() {
        fxSocketClient = new FxSocketClient(
                new FxSocketReaderListener(), host, port, debugFlags);
        fxSocketClient.connect();
    }

    public FxSocketReader(HockeyScoreboard hockeyScoreboard) {
        this(hockeyScoreboard, Constants.instance().DEFAULT_HOST,
                Constants.instance().DEFAULT_PORT,
                DebugFlags.instance().DEBUG_NONE);
    }

    public FxSocketReader(HockeyScoreboard hockeyScoreboard,
            String host, int port) {
        this(hockeyScoreboard, host, port, DebugFlags.instance().DEBUG_NONE);
    }

    public FxSocketReader(HockeyScoreboard hockeyScoreboard,
            String host, int port,
            int debugFlags) {
        this.hockeyScoreboard = hockeyScoreboard;
        this.host = host;
        this.port = port;
        this.debugFlags = debugFlags;
    }

    /**
     * Send a message in the form of a String to the socket.
     *
     * @param msg The String message to send
     */
    public void sendMessage(String msg) {
        // Should never write anything
    }

    /**
     * Get the set of enabled debug flags as defined by the bit masks in
     * scoreboard.common.Constants
     * @return 
     */
    public int getDebugFlags() {
        return debugFlags;
    }

    /**
     * Set the debug flags.
     *
     * @param debugFlags bit mask of debug flags as defined in
     * scoreboard.common.Constants
     */
    public void setDebugFlags(int debugFlags) {
        this.debugFlags = debugFlags;
    }
}
