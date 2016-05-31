/*
 * Copyright 2016 Will Knez <wbknez.dev@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.solsticesquared.schelling.task;

import com.solsticesquared.schelling.Agent.HappinessState;
import com.solsticesquared.schelling.SchellingExplorer;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Represents a task, implemented as a {@link Steppable}, that determines
 * whether or not a simulation has been allowed to execute for more than an
 * arbitrary number of steps and, if so, forces it to stop.
 *
 * <p>
 *     In addition, this task can also stop a simulation if the number of
 *     "unhappy" agents is zero.  Please note that this is disabled by
 *     default but can be activated via the user-interface options.
 * </p>
 */
public class StopConditionTask implements Steppable {

    /**
     * The current number of executed steps.
     *
     * <p>
     *     This is intentionally kept separate from MASON's internal schedule
     *     counter to avoid potential conflicts.
     * </p>
     */
    private int internalCounter;

    @Override
    public void step(SimState simState) {
        final SchellingExplorer model = (SchellingExplorer)simState;

        // Increment the number of steps we've taken.
        this.internalCounter++;

        // Determine the status of both stopping conditions.
        final boolean mustStop =
                this.internalCounter >= model.getParameters().getMaximumSteps();
        final boolean shouldStop =
                model.getParameters().getStopOnEquilibrium()
                  && model.getMovementList().stream().filter(
                        s -> s.getState() == HappinessState.Unhappy
                ).count() == 0;

        // Compare to the maximum number of steps allowed.
        if(mustStop || shouldStop) {
            // Force the simulation to stop.
            model.finish();
        }
    }
}
