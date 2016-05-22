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
 * Represents a mechanism for performing a single "movement" step in a
 * simulation, whereby one agent (or agents) try and move to a new location.
 */
public interface MovementMethod {

    /**
     * Returns the minimum number of agents that must be in an agent list in
     * order for this movement method to work.
     *
     * @return The minimum number of agents required.
     */
    int getMinimumNumberOfAgentsRequired();

    /**
     * Performs a single "movement" operation on the specified list of agents
     * using some arbitrary logic.
     *
     * <p>
     *     The specifics of the algorithm used are irrelevant, however there
     *     are two important requirements that all implementations must meet:
     *     <ol>
     *         <li>The agent, or agents, that are moved must removed from
     *         the specified agent list.</li>
     *         <li>The agent, or agents, that are moved must have their
     *         positions updated before this method completes; this also
     *         applies to the empty cell list (that is why it consists of
     *         mutable coordinate objects).</li>
     *     </ol>
     * </p>
     *
     * <p>
     *     However, it should be noted that this project guarantees that by
     *     the time this method is called, all appropriate pre-requisites
     *     have been met.  In particular, the number of agents required for a
     *     single, successful movement operation (as defined by
     *     {@link #getMinimumNumberOfAgentsRequired()}) have been verified;
     *     there is no need for implementations to check.
     * </p>
     *
     * @param agents
     *        The list of agents to use.
     * @param model
     *        The model to use.
     * @throws NullPointerException
     *         If either {@code agents} or {@code model} are {@code null}.
     */
    void move(final ArrayList<Agent> agents,
              final SchellingExplorer model);
}
