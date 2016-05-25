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

import com.solsticesquared.schelling.utility.UtilityEvaluator;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;
import sim.util.MutableInt2D;

import java.util.ArrayList;

/**
 * Represents a single entity in a Schelling-based segregation simulation.
 *
 * <p>
 *     An agent has two important properties associated with her:
 *     <ol>
 *         <li>The group to which she belongs.</li>
 *         <li>Her happiness, or satisfaction, with the neighborhood in which
 *         she currently resides (as given as an {@code nxm} mini-grid
 *         inside the simulation space.</li>
 *     </ol>
 *     Agents attempt to satisfy their own social needs as dictated by the
 *     group to which they belong.  In particular, each agent's happiness
 *     with her current neighborhood is dictated by her group's
 *     ethnolinguistic vitality, calculated as the percentage of
 *     same-group neighbors in the vicinity according to the evaluation rules
 *     of the simulation.
 * </p>
 */
public /* strictfp */ class Agent implements Steppable {

    /**
     * Represents the current state of an agent and her happiness, or
     * satisfaction, with the neighborhood in which she currently resides.
     */
    public enum HappinessState {

        /**
         * Denotes that an agent is happy, or satisfied, with her current
         * neighborhood and will only swap locations with another agent of
         * the same group who is also happy.
         */
        Happy,

        /**
         * Denotes that an agent is unhappy, or unsatisfied, with her current
         * neighborhood and will swap locations with another agent of a
         * different group who is also unhappy.
         */
        Unhappy
    }

    /** The categorical group that this agent belongs to. */
    private final Group         group;

    /**
     * The location of this agent on the simulation grid in Cartesian
     * coordinates.
     */
    private final MutableInt2D location;

    /**
     * The state of happiness, or satisfaction, that this agent has with her
     * current neighborhood.
     */
    private HappinessState      state;

    /**
     * Constructor.
     *
     * @param group
     *        The group to belong to.
     * @param x
     *        The x-axis value to use.
     * @param y
     *        The y-axis value to use.
     * @throws IllegalArgumentException
     *         If either {@code x} or {@code y} are less than zero.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public Agent(final Group group, final int x, final int y) {
        if(group == null) {
            throw new NullPointerException();
        }

        if(x < 0) {
            throw new IllegalArgumentException("x must be positive!");
        }

        if(y < 0) {
            throw new IllegalArgumentException("y must be positive!");
        }

        this.group = group;
        this.location = new MutableInt2D(x, y);
        this.state = HappinessState.Unhappy;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof Agent)) {
            return false;
        }

        final Agent agent = (Agent)obj;
        return this.group.equals(agent.group)
               && this.location.equals(agent.location)
               && this.state == agent.state;
    }

    /**
     * Returns the categorical group to which this agent belongs.
     *
     * @return The categorical group.
     */
    public Group getGroup() {
        return this.group;
    }

    /**
     * Returns this agent's current happiness, or satisfaction, with the
     * neighborhood in which she resides.
     *
     * @return The happiness, or satisfaction, with the current neighborhood.
     */
    public HappinessState getState() {
        return this.state;
    }

    /**
     * Returns the x-axis location of this agent.
     *
     * @return The x-axis location.
     */
    public int getXLocation() {
        return this.location.x;
    }

    /**
     * Returns the y-axis location of this agent.
     *
     * @return The y-axis location.
     */
    public int getYLocation() {
        return this.location.y;
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = (37 * result) + this.group.hashCode();
        result = (37 * result) + this.location.hashCode();
        result = (37 * result) + this.state.ordinal();

        return result;
    }

    /**
     * Sets the location of this agent to the specified x-axis and y-axis
     * coordinates.
     *
     * @param x
     *        The x-axis value to use.
     * @param y
     *        The y-axis value to use.
     * @throws IllegalArgumentException
     *         If either {@code x} or {@code y} are less than zero.
     */
    public void setLocation(final int x, final int y) {
        if(x < 0) {
            throw new IllegalArgumentException("x must be positive!");
        }

        if(y < 0) {
            throw new IllegalArgumentException("y must be positive!");
        }

        this.location.setTo(x, y);
    }

    /**
     * Returns whether or not this agent should attempt to find a new
     * location whose utility is equal to that of her current neighborhood.
     *
     * <p>
     *     This choice is only ever made by "happy" agents and is only ever
     *     allowed by simulations that have "liquid" dynamics.
     * </p>
     *
     * @param model
     *        The model to use.
     * @return Whether or not to relocate even though an agent is "happy".
     * @throws NullPointerException
     *         If {@code model} is {@code null}.
     */
    public boolean shouldMove(final SchellingExplorer model) {
        if(model == null) {
            throw new NullPointerException();
        }

        // The movement chance.
        final double chance = model.getRuleset().getMovementChance();
        // The movement outcome.
        final double outcome = model.random.nextDouble();

        return Double.compare(outcome, chance) <= 0;
    }

    @Override
    public void step(SimState simState) {
        final SchellingExplorer model = (SchellingExplorer)simState;
        final ArrayList<Agent> moveList = model.getMovementList();

        // The logic for an agent is very simple and divided into three steps,
        // the last of which may branch in two directions:
        //
        // 1) Evaluate the current neighborhood.
        // 2) Update current state and simulation space accordingly.
        // 3) i) If "unhappy", add self to "unhappy" movement queue.
        //    ii) If "happy", determine if should move along with other
        //    "unhappy" agents and, if so, add self to "unhappy" movement
        //    queue as well..

        // #1) Evaluate the current neighborhood.
        final UtilityEvaluator evaluator =
                model.getRuleset().getUtilityEvaluator();
        final double currentEvaluation = evaluator.evaluate(this, model);

        // #2) Update current state.
        this.updateState(currentEvaluation);
        this.updateSimulationSpace(model.getSimulationSpace());

        // #3) Determine whether or not to submit ourselves to the movement
        // list.
        if(this.state == HappinessState.Unhappy
           || (model.getRuleset().getSimulationDynamics()
                    .allowsHappyAgentRelocation() && this.shouldMove(model))) {
            moveList.add(this);
        }
    }

    /**
     * Updates the specified simulation space with this agent's group's state
     * mask for visual representation
     *
     * @param simulationSpace
     *        The simulation space to update.
     * @throws NullPointerException
     *         If {@code simulationSpace} is {@code null}.
     */
    public void updateSimulationSpace(final IntGrid2D simulationSpace) {
        if(simulationSpace == null) {
            throw new NullPointerException();
        }

        simulationSpace.field[this.location.x][this.location.y] =
                this.group.getStateMask(this.state);
    }

    /**
     * Updates this agent's happiness, or satisfaction, with her current
     * neighborhood based upon the specified evaluation compared with her
     * group's social requirements.
     *
     * @param evaluation
     *        The neighborhood evaluation to use.
     */
    public void updateState(final double evaluation) {
        if(Double.compare(evaluation, this.group.getTolerance()) >= 0) {
            this.state = HappinessState.Happy;
        }
        else {
            this.state = HappinessState.Unhappy;
        }
    }
}
