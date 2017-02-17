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

package test.scoreboard.common.networking.simple;

import scoreboard.common.networking.MulticastConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * WARNING: This class is meant for testing outside the JavaFX framework. In
 * JavaFX we must insure that certain work must be done on the main thread, and
 * this class does no such thing.
 */
public class SimpleMulticastWriter extends MulticastConnection {
    
    @Override
    public void onMessage(String msg) {
        // Should receive message but do noting
    }

    @Override
    public void onClosedStatus(boolean isClosed) {
        String closedStr = isClosed ? "closed" : "open";
        System.out.println("Multicast Writer Socket is " + closedStr);
    }
    
    public static void main(String[] args) {
        SimpleMulticastWriter w = new SimpleMulticastWriter();
        new Thread(w).start();
        
        String line = "";
        int messageNum = 0;
        while (true) {
            System.out.println("type <RETURN> to send message ('q' to exit): ");
            InputStreamReader converter = new InputStreamReader(System.in);
            BufferedReader in = new BufferedReader(converter);
            try {
                line = in.readLine();
            } catch (Exception e) {
            }
            if (line.equals("q")) {
                break;
            } else {
                String msg = "message " + ++messageNum;
                w.sendMessage(msg);
                System.out.println("Multicast Writer Socket sent> " + msg);
            }
        }
    }   
}
