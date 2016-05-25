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
import com.solsticesquared.schelling.move.PhysicalMovementMethod;

/**
 * Represents an implementation of {@link SimulationDynamics} that describes
 * a simulation where "unhappy" agents search for new locations to move from
 * a list of empty cells and, potentially, where "happy" agents may also
 * relocate to neighborhoods that are of equal utility to their current one.
 *
 * <p>
 *     As per Vinković and Kirman (2006), a simulation that allows "happy"
 *     agents to relocate as well as "unhappy" ones yields "liquid" dynamics and
 *     is continuously in motion.  In contrast, a "solid" simulation is one
 *     where only "unhappy" are allowed to move and, in general, usually
 *     grinds to a halt after a short number of iterations (assuming
 *     there is no stochastic influence in, for example, the utility
 *     function).  This "solid" phenomenon is just as pronounced in a
 *     swap-based simulation, as given by {@link SwapSimulationDynamics}.
 * </p>
 */
public class PhysicalSimulationDynamics implements SimulationDynamics {

    /**
     * Whether or not the simulation dynamics are "liquid"; that is, whether
     * or not a simulation allows agents who are "happy" to relocate.
     */
    private final boolean isLiquid;

    /**
     * Constructor.
     *
     * @param isLiquid
     *        Whether or not to allow liquid dynamics.
     */
    public PhysicalSimulationDynamics(final boolean isLiquid) {
        this.isLiquid = isLiquid;
    }

    @Override
    public boolean allowsEmptyCells() {
        return true;
    }

    @Override
    public boolean allowsHappyAgentRelocation() {
        return this.isLiquid;
    }

    @Override
    public MovementMethod getMovementMethod() {
        return new PhysicalMovementMethod();
    }
}
