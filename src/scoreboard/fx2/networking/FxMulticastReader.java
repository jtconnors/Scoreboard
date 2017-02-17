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

import javafx.application.Platform;
import scoreboard.common.networking.MulticastConnection;
import scoreboard.common.Globals;
import static scoreboard.common.Constants.DEFAULT_SESSION_ADDR;
import static scoreboard.common.Constants.DEFAULT_PORT;
import static scoreboard.common.Constants.DEBUG_NONE;

public class FxMulticastReader extends MulticastConnection {
    
    /**
     * Called whenever a message is read from the socket.
     * In JavaFX 2.0, this method must be run on the
     * main thread.  This is accomplished by the Platform.runLater() call.
     * Failure to do so *will* result in strange errors and exceptions.
     * @param line Line of text read from the socket.
     */
    @Override
    public void onMessage(final String line) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Globals.hockeyScoreboardRef.handleUpdate(line);
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Globals.hockeyScoreboardRef.updateStatusRow(isClosed ? 0 : 1);
            }
        });
    }
    
    public FxMulticastReader() {
        super(DEFAULT_SESSION_ADDR, DEFAULT_PORT, DEBUG_NONE);
    }

    public FxMulticastReader(int portNum) {
        super(DEFAULT_SESSION_ADDR, portNum, DEBUG_NONE);
    }

    public FxMulticastReader(String addr, int portNum, int debugFlags) {
        super(addr, portNum, debugFlags);
    }
}
