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

package scoreboard.common.networking.simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import scoreboard.common.networking.MultipleSocketWriter;

public class SimpleMultipleSokcketWriter extends MultipleSocketWriter {
    
    @Override
    public void onMessage(String msg) {
        // For this version we read nothing
    }

    @Override
    public void onClosedStatus(boolean isClosed) {
        int numConnections = updateListeners.size();
        if (numConnections != 1) {
            System.out.println(numConnections + " connections");
        } else {
            System.out.println("1 connection");
        }
    }
    
    public static void main(String[] args) {
        SimpleMultipleSokcketWriter m = new SimpleMultipleSokcketWriter();
        new Thread(m).start() ;
        
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
                m.postUpdate("message " + ++messageNum);
            }
        }
        m.shutdown();
        System.exit(0);
    }
    
}
