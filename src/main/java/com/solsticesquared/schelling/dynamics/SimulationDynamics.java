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

package com.solsticesquared.schelling.dynamics;

import com.solsticesquared.schelling.move.MovementMethod;

/**
 * Represents a mechanism for defining the types of dynamics (both allowed
 * and to use) for an arbitrary simulation.
 */
public interface SimulationDynamics {

    /**
     * Returns whether or not the dynamics of a simulation allow empty cells
     * in the simulation space.
     *
     * @return Whether or not empty cells are allowed.
     */
    boolean allowsEmptyCells();

    /**
     * Returns whether or not the dynamics of a simulation allow a "happy"
     * agent to search for a new location whose utility is the same as her
     * current neighborhood.
     *
     * @return Whether or not "happy" agents may relocate.
     */
    boolean allowsHappyAgentRelocation();

    /**
     * Returns the {@link MovementMethod} that represents how agents may move
     * around a simulation.
     *
     * @return How agents may move in a simulation space.
     */
    MovementMethod getMovementMethod();
}
