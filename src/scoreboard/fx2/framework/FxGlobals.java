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
package scoreboard.fx2.framework;

import scoreboard.fx2.networking.FxMulticastReader;
import scoreboard.fx2.networking.FxMulticastWriter;
import scoreboard.fx2.networking.FxMultipleSocketWriter;
import scoreboard.fx2.networking.FxSocketReader;

/*
 * Global variables that are JavaFX 2.x specific
 *
 * This class follows the Singleton design pattern and takes advantage of the 
 * properties of the Java Virtual Machine such that initialiazion of the
 * class instance will be done in a thread safe manner.
 */
public class FxGlobals {
    private FxGlobals() {}
    
    private static class LazyHolder {
        private static final FxGlobals INSTANCE = new FxGlobals();
    }
    
    public static FxGlobals instance() {
        return LazyHolder.INSTANCE;
    }
    
    /*
     * Multicast socket to send XML updates over
     */
    public FxMulticastWriter multicastWriter;
    
    /*
     * Multicast socket to receive XML updates over
     */
    public FxMulticastReader multicastReader;
    
    /*
     * IP socket to send XML updates over
     */
    public FxMultipleSocketWriter multipleSocketWriter;
    
    /*
     * IP socket to send XML updates over
     */
    public FxSocketReader socketReader;   
}
