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

package com.solsticesquared.schelling;

import com.solsticesquared.schelling.Parameters.Defaults;
import com.solsticesquared.schelling.dynamics.PhysicalSimulationDynamics;
import com.solsticesquared.schelling.dynamics.SimulationDynamics;
import com.solsticesquared.schelling.dynamics.SwapSimulationDynamics;
import com.solsticesquared.schelling.move.MovementMethod;
import com.solsticesquared.schelling.move.PhysicalMovementMethod;
import com.solsticesquared.schelling.update.AgentUpdater;
import com.solsticesquared.schelling.update.BatchAgentUpdater;
import com.solsticesquared.schelling.update.SingleAgentUpdater;
import com.solsticesquared.schelling.utility.AbsoluteUtilityEvaluator;
import com.solsticesquared.schelling.utility.RelativeUtilityEvaluator;
import com.solsticesquared.schelling.utility.UtilityEvaluator;
import sim.field.grid.Grid2D;

/**
 * Represents a collection of "rules" that define a simulation's behavior.
 *
 * <p>
 *     This is meant to both mirror and be a refinement of a
 *     {@link Parameters} object.  In particular, this object is meant to
 *     serve as an immutable resource while a simulation is running, and
 *     therefore shield both the simulation and the user from race-conditions
 *     or other problems that may occur when a simulation's parameters are
 *     updated while it is running (accidentally or otherwise).
 * </p>
 */
public class Ruleset {

    /** The method of moving agents - either one at a time or all at once. */
    private AgentUpdater        agentUpdater;

    /** The type of boundary conditions to use - bounded or toroidal. */
    private int                 boundsType;

    /**
     * The chance that a "happy" agent will attempt to relocate to another
     * location of the same utility as her current neighborhood.
     */
    private double              movementChance;

    /**
     * The algorithm an single agent (or pair of agents) may use to find a
     * new location in which to live.
     */
    private MovementMethod      movementMethod;

    /** The maximum number of empty cells to search for a better location. */
    private int                 searchLimit;

    /** The radius of cells to use to define a neighborhood. */
    private int                 searchRadius;

    /** The type of dynamics to use for a simulation. */
    private SimulationDynamics  simulationDynamics;

    /**
     * The algorithm to use to determine an agent's "happiness" with her
     * current neighborhood.
     */
    private UtilityEvaluator    utilityEvaluator;

    /**
     * Constructor.
     *
     * <p>
     *     The default initialization used here mirrors the defaults chosen
     *     for a {@link Parameters} object.
     * </p>
     */
    public Ruleset() {
        this.agentUpdater = new SingleAgentUpdater();
        this.boundsType = Grid2D.TOROIDAL;
        this.movementChance = Defaults.MoveChance;
        this.movementMethod = new PhysicalMovementMethod();
        this.searchLimit = Defaults.SearchLimit;
        this.searchRadius = Defaults.SearchRadius;
        this.simulationDynamics = new PhysicalSimulationDynamics(true);
        this.utilityEvaluator = new AbsoluteUtilityEvaluator();
    }

    /**
     * Returns the method used to update the locations of "unhappy" agents.
     *
     * @return The agent update mechanism.
     */
    public AgentUpdater getAgentUpdater() {
        return this.agentUpdater;
    }

    /**
     * Returns the boundary conditions used to determine the overall geometry
     * of a simulation space.
     *
     * @return The boundary conditions.
     */
    public int getBoundsType() {
        return this.boundsType;
    }

    /**
     * Returns the probability that a "happy" agent will attempt to move to
     * another location of the same utility.
     *
     * <p>
     *     This only applies to simulations that use "liquid" dynamics.
     * </p>
     *
     * @return The "happy" agent move probability.
     */
    public double getMovementChance() {
        return this.movementChance;
    }

    /**
     * Returns the algorithm used to search for and select a new location an
     * agent may move to.
     *
     * @return The algorithm to move agents.
     */
    public MovementMethod getMovementMethod() {
        return this.movementMethod;
    }

    /**
     * Returns the maximum number of empty cells an agent may search when
     * trying to find a new location that is of equal or greater utility than
     * her current neighborhood.
     *
     * @return The maximum number of empty cells to search.
     */
    public int getSearchLimit() {
        return this.searchLimit;
    }

    /**
     * Returns the "radius" of the grid that a nearest-neighbor search will use
     * to determine the overall size of an agent's neighborhood.
     *
     * @return The radial size of an agent's neighborhood.
     */
    public int getSearchRadius() {
        return this.searchRadius;
    }

    /**
     * Returns the dynamics used by a simulation.
     *
     * @return The simulation dynamics.
     */
    public SimulationDynamics getSimulationDynamics() {
        return this.simulationDynamics;
    }

    /**
     * Returns the algorithm for utility evaluation that is used when
     * determining an agent's "happiness" with her current neighborhood.
     *
     * @return The utility evaluator.
     */
    public UtilityEvaluator getUtilityEvaluator() {
        return this.utilityEvaluator;
    }

    /**
     * Updates this ruleset to use the "rules" defined by the specified
     * parameters, presumably chosen by a/the user.
     *
     * <p>
     *     The primary purpose of this method is to convert the selection
     *     indices (used to denote a/the user's selection in a list or
     *     combobox) into actual objects that a simulation may use.
     * </p>
     *
     * @param params
     *        The model parameters to use.
     * @throws NullPointerException
     *         If {@code params} is {@code null}.
     */
    public void updateRules(final Parameters params) {
        if(params == null) {
            throw new NullPointerException();
        }

        this.updateAgentUpdater(params);
        this.updateBoundsType(params);
        this.updateSimulationDynamics(params);
        this.updateUtilityEvaluator(params);

        // Update primitive parameters.
        this.movementChance = params.getMoveChance();
        this.searchLimit = params.getSearchLimit();
        this.searchRadius = params.getSearchRadius();

        // Update the movement method according to the current simulation
        // dynamics.
        this.movementMethod = this.simulationDynamics.getMovementMethod();
    }

    /**
     * Updates this ruleset's agent update method to those specified by a/the
     * user.
     *
     * @param params
     *        The model parameters to use.
     * @throws NullPointerException
     *         If {@code params} is {@code null}.
     */
    private void updateAgentUpdater(final Parameters params) {
        if(params == null) {
            throw new NullPointerException();
        }

        if(params.getUpdateType() == 0) {
            this.agentUpdater = new BatchAgentUpdater();
        }
        else {
            this.agentUpdater = new SingleAgentUpdater();
        }
    }

    /**
     * Updates this ruleset's boundary conditions to those specified by a/the
     * user and matches them with the appropriate MASON constant(s) for
     * direct use by a simulation.
     *
     * @param params
     *        The model parameters to use.
     * @throws NullPointerException
     *         If {@code params} is {@code null}.
     */
    private void updateBoundsType(final Parameters params) {
        if(params == null) {
            throw new NullPointerException();
        }

        if(params.getBoundsType() == 0) {
            this.boundsType = Grid2D.BOUNDED;
        }
        else {
            this.boundsType = Grid2D.TOROIDAL;
        }
    }

    /**
     * Updates this ruleset's simulation dynamics to those specified by a/the
     * user.
     *
     * @param params
     *        The model parameters to use.
     * @throws NullPointerException
     *         If {@code params} is {@code null}.
     */
    private void updateSimulationDynamics(final Parameters params) {
        if(params == null) {
            throw new NullPointerException();
        }

        switch(params.getSimulationDynamics()) {
            case 0:
                this.simulationDynamics = new PhysicalSimulationDynamics(true);
                break;
            case 1:
                this.simulationDynamics = new PhysicalSimulationDynamics(false);
                break;
            case 2:
                this.simulationDynamics = new SwapSimulationDynamics();
                break;
        }
    }

    /**
     * Updates this ruleset's neighborhood evaluation algorithm to those
     * specified by a/the user.
     *
     * @param params
     *        The model parameters to use.
     * @throws NullPointerException
     *         If {@code params} is {@code null}.
     */
    private void updateUtilityEvaluator(final Parameters params) {
        if(params == null) {
            throw new NullPointerException();
        }

        if(params.getUtilityEvaluator() == 0) {
            this.utilityEvaluator = new AbsoluteUtilityEvaluator();
        }
        else {
            this.utilityEvaluator = new RelativeUtilityEvaluator();
        }
    }
}
