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

package com.solsticesquared.schelling.ui;

import sim.engine.SimState;

/**
 * Represents a mechanism for updating a {@link DisplayChart} with new values
 * computed a (running) simulation.
 */
@FunctionalInterface
public interface ChartUpdater {

    /**
     * Modifies the specified chart by adding new data computed or derived
     * from the specified simulation.
     *
     * @param simState
     *        The simulation to use.
     * @param chart
     *        The chart to update.
     * @throws NullPointerException
     *         If either {@code simState} or {@code chart} are {@code null}.
     */
    void updateChart(final SimState simState, final DisplayChart chart);
}
