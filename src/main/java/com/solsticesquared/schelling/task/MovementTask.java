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

import com.solsticesquared.schelling.Ruleset;
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.move.MovementMethod;
import com.solsticesquared.schelling.update.AgentUpdater;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Represents a task, implemented as a {@link Steppable}, that moves lists of
 * agents around a simulation space by utilizing the movement logic selected
 * by the user.
 */
public class MovementTask implements Steppable {

    @Override
    public void step(SimState simState) {
        final SchellingExplorer model = (SchellingExplorer)simState;
        final Ruleset ruleset = model.getRuleset();

        // Collect the pieces necessary to move and update the locations of
        // all necessary agents.
        final AgentUpdater agentUpdater = ruleset.getAgentUpdater();
        final MovementMethod movementMethod = ruleset.getMovementMethod();

        // Do so.
        agentUpdater.update(movementMethod, model);
    }
}
