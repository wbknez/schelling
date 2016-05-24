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
import com.solsticesquared.schelling.move.SwapMovementMethod;

/**
 * Represents an implementation of {@link SimulationDynamics} that describes
 * a simulation where "unhappy" agents swap locations directly.
 *
 * <p>
 *     This dynamics of this variation of the traditional Schelling model have
 *     the following rules:
 *     <ol>
 *         <li>There are no empty cells.</li>
 *         <li>There is no relocation of "happy" agents (it does not make
 *         sense).</li>
 *         <li>"Unhappy" agents exchange places with each other (at random)
 *         .</li>
 *     </ol>
 * </p>
 */
public class SwapSimulationDynamics implements SimulationDynamics {

    @Override
    public boolean allowsEmptyCells() {
        return false;
    }

    @Override
    public boolean allowsHappyAgentRelocation() {
        return false;
    }

    @Override
    public MovementMethod getMovementMethod() {
        return new SwapMovementMethod();
    }
}
