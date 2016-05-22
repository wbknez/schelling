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

package com.solsticesquared.schelling.update;

import com.solsticesquared.schelling.Agent;
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.move.MovementMethod;

import java.util.ArrayList;

/**
 * Represents an implementation of an {@link AgentUpdater} that updates the
 * location(s) of a single agent, or group of agents, at a time.
 */
public class SingleAgentUpdater implements AgentUpdater {

    @Override
    public void update(final MovementMethod moveMethod,
                       final SchellingExplorer model) {
        if(moveMethod == null) {
            throw new NullPointerException();
        }

        if(model == null) {
            throw new NullPointerException();
        }

        // Obtain the list of agents to move.
        final ArrayList<Agent> agents = model.getMovementList();

        // Ensure that the number of agents that the movement logic requires
        // to be present exist.
        if(agents.size() >= moveMethod.getMinimumNumberOfAgentsRequired()) {
            moveMethod.move(agents, model);
        }

        // Clear the list.
        agents.clear();
    }
}
