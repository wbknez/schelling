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
import com.solsticesquared.schelling.Agent.HappinessState;
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.utility.UtilityEvaluator;
import sim.util.MutableInt2D;

import java.util.ArrayList;

/**
 * Represents an implementation of a {@link MovementMethod} that randomly
 * chooses an agent and attempts to find a new location for her that eihter
 * meets or exceeds her "happiness" with her current neighborhood.
 */
public class PhysicalMovementMethod implements MovementMethod {

    /**
     * Computes a list of candidate locations that have maximum utility
     * relative to the specified agent's current neighborhood.
     *
     * <p>
     *     This method performs a "evaluation maximization" for a random
     *     subset of available empty cells, returning a list of those cells
     *     whose evaluations are highest, or maximized.  This results in, for
     *     example, an "unhappy" agent's list of acceptable candidates being
     *     an improvement over her current neighborhood but yet not meeting or
     *     exceeding her group's tolerance expectations.  In contrast, a "happy"
     *     agent (using "liquid" dynamics) will choose randomly from all
     *     locations that meet or exceed those expectations, irrespective of
     *     the number of same-group agents in the vicinity.
     * </p>
     *
     * @param agent
     *        The agent to compute the candidates list for.
     * @param model
     *        The model to use.
     * @return A list of maximum-utility locations to move to.
     * @throws NullPointerException
     *         If either {@code agent} or {@code model} are {@code null}.
     */
    private ArrayList<MutableInt2D> computeCandidates
            (final Agent agent, final SchellingExplorer model) {
        if(agent == null) {
            throw new NullPointerException();
        }

        if(model == null) {
            throw new NullPointerException();
        }

        final ArrayList<MutableInt2D> emptyCells = model.getEmptyCells();
        // Determine how many cells to search.
        final int searchLimit =
                emptyCells.size() < model.getRuleset().getSearchLimit() ?
                emptyCells.size() : model.getRuleset().getSearchLimit();
        final ArrayList<MutableInt2D> candidates =
                new ArrayList<>((int)Math.ceil(searchLimit / 2));
        final UtilityEvaluator evaluator =
                model.getRuleset().getUtilityEvaluator();

        // The "current" evaluation.
        double currentEvaluation =
                agent.getState() == HappinessState.Happy ?
                1.0d : evaluator.evaluate(agent, agent.getXLocation(),
                                          agent.getYLocation(), model);

        // Perform an in-place Knuth shuffle.
        for(int i  = 0; i < searchLimit; i++) {
            final int swapLocation =
                    i + model.random.nextInt(emptyCells.size() - i);
            // Swap the location (in order to remove it from further
            // consideration).
            model.swapLocation(emptyCells.get(i), emptyCells.get(swapLocation));

            // Now, evaluate it.
            final double newEvaluation =
                    this.evaluateNeighborhood(agent, emptyCells.get(i), model);
            final int comparison = Double.compare(newEvaluation,
                                                  currentEvaluation);

            if(comparison >= 0) {
                if(comparison > 0) {
                    candidates.clear();
                    currentEvaluation = newEvaluation;
                }

                candidates.add(emptyCells.get(i));
            }
        }

        return candidates;
    }

    /**
     * Evaluates the neighborhood at the specified location from the
     * specified agent's point of view.
     *
     * <p>
     *     This method uses the evaluation algorithm as explained in Hatna
     *     and Benenson (2012).  In brief, if the evaluation of the
     *     neighborhood at the specified location is less than the specified
     *     agent's group's tolerance expectation, then the evaluation itself
     *     is returned.  Otherwise, this method returns one.  This allows the
     *     evaluation of a neighborhood and subsequent selection of a new
     *     location to move to be independent of the margin of success.  This
     *     treats a group's tolerance expectation as a one-time minimum
     *     requirement for acceptability with no maximum, rendering all
     *     neighborhoods that satisfy that expectation equivalent.
     * </p>
     *
     * @param agent
     *        The agent whose perspective to use.
     * @param location
     *        The location of the neighborhood to investigate.
     * @param model
     *        The model to use.
     * @return The evaluation of a neighborhood from an agent's perspective.
     * @throws NullPointerException
     *         If {@code agent}, {@code location}, or {@code model} are
     *         {@code null}.
     */
    private double evaluateNeighborhood(final Agent agent,
                                        final MutableInt2D location,
                                        final SchellingExplorer model) {
        if(agent == null) {
            throw new NullPointerException();
        }

        if(location == null) {
            throw new NullPointerException();
        }

        if(model == null) {
            throw new NullPointerException();
        }

        final double evaluation =
                model.getRuleset()
                     .getUtilityEvaluator()
                     .evaluate(agent, location.x, location.y, model);

        if(Double.compare(evaluation, agent.getGroup().getTolerance()) >= 0) {
            return 1.0d;
        }

        return evaluation;
    }

    @Override
    public int getMinimumNumberOfAgentsRequired() {
        return 1;
    }

    @Override
    public void move(ArrayList<Agent> agents,
                     SchellingExplorer model) {
        // Obtain a random agent.
        final Agent agent = agents.remove(model.random.nextInt(agents.size()));

        // The collection of valid candidates.
        final ArrayList<MutableInt2D> candidates =
                this.computeCandidates(agent, model);

        if(candidates.size() != 0) {
            // Choose a maximum-utility location at random.
            final MutableInt2D newLocation =
                    candidates.get(model.random.nextInt(candidates.size()));

            // Swap!
            model.swapLocation(agent, newLocation);
        }
    }
}
