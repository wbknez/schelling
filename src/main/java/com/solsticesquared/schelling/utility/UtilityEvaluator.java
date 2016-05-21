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

package com.solsticesquared.schelling.utility;

import com.solsticesquared.schelling.Agent;
import com.solsticesquared.schelling.SchellingExplorer;

/**
 * Represents a mechanism for computing the "happiness" of an arbitrary agent
 * with her current neighborhood.
 */
public interface UtilityEvaluator {

    /**
     * Computes the "happiness" of the specified agent with her current
     * neighborhood according to some arbitrary logic.
     *
     * @param agent
     *        The agent whose neighborhood needs to be evaluated.
     * @param model
     *        The model to use.
     * @return The percentage of same-group neighbors (relative to an agent)
     * in a given neighborhood.
     * @throws NullPointerException
     *         If either {@code agent} or {@code model} is {@code null}.
     */
    double evaluate(final Agent agent, final SchellingExplorer model);
}
