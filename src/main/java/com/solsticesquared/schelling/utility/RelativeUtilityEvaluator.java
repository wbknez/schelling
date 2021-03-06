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
import com.solsticesquared.schelling.ComputeCache;
import com.solsticesquared.schelling.Group;
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.SchellingExplorer.Constants;
import sim.field.grid.IntGrid2D;

/**
 * Represents an implementation of a {@link UtilityEvaluator} that
 * computes the "happiness" of an arbitrary agent with her current
 * neighborhood based on the number of available surrounding neighbors.
 *
 * <p>
 *     The key difference between this and {@link AbsoluteUtilityEvaluator}
 *     is that number of similar agents is divided by the total number of
 *     available agents, instead of the total number of possible agents.  In
 *     brief, this algorithm discards empty cells and therefore agents in
 *     isolated areas are not penalized for poor local population density.
 * </p>
 *
 * <p>
 *     A brief example of this: consider an agent {@code A} whose tolerance
 *     is {@code 0.5} and whose neighborhood consists of just one neighbor,
 *     {@code B}.  If {@code B} is a member of agent {@code A}'s group then,
 *     using this particular algorithm, agent {@code A} will be "happy" with
 *     her current location and not move despite only one neighbor being
 *     present.  Thus, this particular algorithm emphasizes the idea of a
 *     relative majority in place of spatial dominance.
 * </p>
 *
 * <p>
 *     Please note that when using a swap-based model, absolute and relative
 *     utility evaluators perform identically.
 * </p>
 */
public class RelativeUtilityEvaluator implements UtilityEvaluator {

    @Override
    public double evaluate(final Agent agent, final int x, final int y,
                           final SchellingExplorer model) {
        if(agent == null) {
            throw new NullPointerException();
        }

        if(model == null) {
            throw new NullPointerException();
        }

        // Obtain a compute cache first.
        final ComputeCache cache = model.getComputeCache();
        final Group group = agent.getGroup();
        final IntGrid2D simulSpace = model.getSimulationSpace();

        // Obtain the current neighborhood.
        simulSpace.getMooreLocations(x,
                                     y,
                                     model.getRuleset().getSearchRadius(),
                                     model.getRuleset().getBoundsType(),
                                     false,
                                     cache.xLocations, cache.yLocations);

        // The number of non-empty cells to evaluate.
        int totalAgents = cache.xLocations.size();
        // The number of same-group agents.
        int similarAgents = 0;

        for(int i = 0; i < cache.xLocations.size(); i++) {
            // The current x-axis and y-axis locations, respectively.
            final int currentX = cache.xLocations.get(i);
            final int currentY = cache.yLocations.get(i);

            // The current neighbor to evaluate.
            final int currentNeighbor = simulSpace.get(currentX, currentY);

            // Determine if this is an empty cell.
            if(currentNeighbor == Constants.EmptyCell) {
                totalAgents--;
                continue;
            }

            // Determine if the current neighbor is an in-group or out-group
            // member.
            if(group.isMember(currentNeighbor)) {
                similarAgents += 1;
            }
        }

        return (double)similarAgents / totalAgents;
    }
}
