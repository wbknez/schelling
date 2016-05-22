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

package com.solsticesquared.schelling.move;

import com.solsticesquared.schelling.Agent;
import com.solsticesquared.schelling.SchellingExplorer;

import java.util.ArrayList;

/**
 * Represents an implementation of a {@link MovementMethod} that directly
 * swaps a pair of unhappy agents with each other.
 */
public class SwapMovementMethod implements MovementMethod {

    @Override
    public int getMinimumNumberOfAgentsRequired() {
        return 2;
    }

    @Override
    public void move(final ArrayList<Agent> agents,
                     final SchellingExplorer model) {
        // Obtain two random agents to operate on.
        final Agent agent0 = agents.remove(model.random.nextInt(agents.size()));
        final Agent agent1 = agents.remove(model.random.nextInt(agents.size()));

        // Swap!
        model.swapLocation(agent0, agent1);
    }
}
