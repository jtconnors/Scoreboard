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

import static scoreboard.common.Constants.DEBUG_NONE;

/**
 * Base class with debug flags that all Socket based classes can extend.
 */
public abstract class SocketBase {
    
    private int debugFlags;
    
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
     * Get the current set of debug flags.
     * @return the current debug flag bitmask
     */
    public int getDebugFlags() {
        return debugFlags;
    }

    /**
     * Turn off debugging option.
     */
    public void clearDebugFlags() {
        debugFlags = DEBUG_NONE;
    }
    
}
