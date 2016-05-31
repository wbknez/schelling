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

import com.solsticesquared.schelling.Agent;
import com.solsticesquared.schelling.Agent.HappinessState;
import com.solsticesquared.schelling.ComputeCache;
import com.solsticesquared.schelling.Group;
import com.solsticesquared.schelling.Ruleset;
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.SchellingExplorer.Constants;
import sim.field.grid.IntGrid2D;

import java.util.ArrayList;

/**
 * A collection of utility methods for computing different statistics.
 */
public final class StatisticsUtils {

    /**
     * Determines whether the compressed values of the two specified agents
     * indicates that they are from two separate groups.
     *
     * @param agent0
     *        The compressed value of an agent.
     * @param agent1
     *        The compressed value of another agent.
     * @return Whether or not two agents are from the same group.
     */
    public static boolean areFromDifferentGroups(final int agent0,
                                                 final int agent1) {
        // Strip the group from the first agent.
        final short group0 = (short)((agent0 >> 16) & 0x0ff);
        final short group1 = (short)((agent1 >> 16) & 0x0ff);

        return group0 != group1;
    }

    public static double[] computeUnhappyAgents(final SchellingExplorer model) {
        final double results[] = new double[2];
        computeUnhappyAgents(model, results);
        return results;
    }

    /**
     * Computes the percentage of unhappy agents currently in the specified
     * model.
     *
     * @param model
     *        The model to use.
     * @throws NullPointerException
     *         If either {@code model} is {@code null}.
     */
    public static void computeUnhappyAgents(final SchellingExplorer model,
                                            final double[] results) {
        if(model == null) {
            throw new NullPointerException();
        }

        if(results.length != 2) {
            throw new IllegalArgumentException("results array must have a " +
                                               "size of 2!");
        }

        // The queue of unhappy agents.
        final ArrayList<Agent> unhappyAgents =
                model.getMovementList();
        // The group to decide divisions.
        final Group firstGroup = model.getGroup(0);

        results[0] = 0.0d;
        results[1]  = 0.0d;

        for(final Agent agent : unhappyAgents) {
            // Screen for "happy" agents.
            if(agent.getState() != HappinessState.Happy) {
                if(agent.getGroup() == firstGroup) {
                    results[0] += 1;
                }
                else {
                    results[1] += 1;
                }
            }
        }

        // Compute how many total agents there are.
        final int[] totalAgents = model.getTotalAgents();

        results[0] = (results[0] / (double)(totalAgents[0])) * 100.0d;
        results[1] = (results[1] / (double)(totalAgents[1])) * 100.0d;
    }

    /**
     * Computes the interface density for the specified model.
     *
     * <p>
     *     The interface density is defined as the number of connections
     *     between agents of different groups divided by the total number of
     *     possible connections.  For this project, the total number of
     *     connections per agent is {@code 8}, as per the Moore neighborhood
     *     of one, and therefore the total number of possible connections is
     *     given as {@code 8n^2}.
     * </p>
     *
     * @param model
     *        The model to use.
     * @return The interface density.
     * @throws NullPointerException
     *         If {@code model} is {@code null}.
     */
    public static double computeInterfaceDensity(
            final SchellingExplorer model) {
        if(model == null) {
            throw new NullPointerException();
        }

        // The temporary cache.
        final ComputeCache cache = model.getComputeCache();
        // The ruleset.
        final Ruleset ruleset = model.getRuleset();
        // The total computed interface density.
        double interfaceDensity = 0.0d;
        // The simulation space.
        final IntGrid2D simSpace = model.getSimulationSpace();

        // Model parameters.
        final int boundsType = ruleset.getBoundsType();
        final int searchRadius = ruleset.getSearchRadius();

        // So, for each agent, obtain the local neighborhood and determine
        // how many non-alike neighbors are nearby.
        for(int x = 0; x < simSpace.getWidth(); x++) {
            for(int y = 0; y < simSpace.getHeight(); y++) {
                simSpace.getMooreLocations(x, y, searchRadius, boundsType,
                                           false, cache.xLocations,
                                           cache.yLocations);

                // The total number of agents to evaluate.
                final int totalAgents = cache.xLocations.size();

                // The current agent the evaluation is centered around.
                final int currentAgent = simSpace.field[x][y];

                for(int i = 0; i < totalAgents; i++) {
                    // The neighbor's coordinates.
                    final int neighborX = cache.xLocations.get(i);
                    final int neighborY = cache.yLocations.get(i);

                    // Grab the neighboring agent.
                    final int neighborAgent =
                            simSpace.field[neighborX][neighborY];

                    // Screen for empty cells.
                    if( neighborAgent == Constants.EmptyCell) {
                        continue;
                    }

                    if(areFromDifferentGroups(currentAgent, neighborAgent)) {
                        interfaceDensity += 1.0d;
                    }
                }
            }
        }

        // Divide all of this by the possible number of edges, which is the
        // total number of agents times eight.
        final int numberOfAgents = (model.getSimulationSpace().getWidth() *
                                   model.getSimulationSpace().getHeight()) -
                                    model.getEmptyCells().size();
        return interfaceDensity /
               (numberOfAgents * 8.0d);
    }

    /**
     * Constructor (private).
     */
    private StatisticsUtils() {
    }
}
