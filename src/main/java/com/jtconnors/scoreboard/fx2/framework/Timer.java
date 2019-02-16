/*
 * Copyright (c) 2019, Jim Connors
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

package com.jtconnors.scoreboard.fx2.framework;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/*
 * Simple Timer class with the following features:
 *   - Multiple handlers (specified by FunctionPtr objects) can be added
 *     or removed dynamically.
 *   - At the end of each clock tick, defined by the duration argument of the
 *     Timer constructor (default 1/10th second), all associated handlers
 *     will be invoked.
 */
public class Timer {

    private Timeline timeline;
    private Duration duration;
    /*
     * All handlers associated with the Timer are placed in this ArrayList
     */
    private ArrayList<FunctionPtr> handlers;

    public Timer() {
        this(FxConstants.instance().ONE_TENTH_SECOND);
    }

    public Timer(Duration duration) {
        this.duration = duration;
        handlers = new ArrayList<FunctionPtr>();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        final KeyFrame kf = new KeyFrame(duration,
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (FunctionPtr handler : handlers) {
                        handler.invoke();
                    }
                }
            }
        );
        timeline.getKeyFrames().add(kf);
    }

    /*
     * Add handler, if it is not already present, to the list of
     * handlers to run.
     */
    public void addHandler(FunctionPtr handler) {
        if (handler != null) {
            for (FunctionPtr h : handlers) {
                if (handler == h) {
                    return;
                }
            }
            handlers.add(handler);
        }
    }

    /*
     * Remove handler if it exists in the handler list.
     */
    public void removeHandler(FunctionPtr handler) {
        boolean somethingToRemove = false;
        for (FunctionPtr h : handlers) {
            if (handler == h) {
                somethingToRemove = true;
            }
        }
        if (somethingToRemove) {
            handlers.remove(handler);
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }
}