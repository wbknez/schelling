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

import com.solsticesquared.schelling.Agent.HappinessState;

import java.awt.Color;
import java.util.Arrays;

/**
 * Represents a categorical association that agents may belong to in a
 * Schelling-based segregation simulation.
 */
public strictfp class Group {

    /**
     * The color that an agent of this group is drawn as when it is happy, or
     * satisfied, with the neighborhood it currently occupies.
     */
    private Color           happyColor;

    /**
     * The numeric identifier for this group.
     */
    private final short     id;

    /**
     * The (full) name of this group.
     */
    private String          name;

    /**
     * The percentage of remaining "non-empty" cells that should be occupied
     * by agents of this group.
     */
    private double          popPercent;

    /**
     * The preference agents of this group have for one another in close
     * proximity.
     *
     * <p>
     *     As of this writing, this value is held constant across a population.
     * </p>
     */
    private double          tolerance;

    /**
     * Compressed group-specific state identifiers used exclusively for
     * rendering.
     */
    private final int[]     stateMasks;

    /**
     * The color that an agent of this group is drawn as when it is unhappy, or
     * unsatisfied, with the neighborhood it currently occupies.
     */
    private Color           unhappyColor;

    /**
     * Constructor.
     *
     * @param name
     *        The name to use.
     * @param id
     *        The (unique) id to use.
     * @throws IllegalArgumentException
     *         If {@code id} is less than zero.
     * @throws NullPointerException
     *         If {@code name} is {@code null}.
     */
    public Group(final String name, final short id) {
        if(name == null) {
            throw new NullPointerException();
        }

        if(id < 0) {
            throw new IllegalArgumentException("id must be positive!");
        }

        this.happyColor = Color.black;
        this.id = id;
        this.name = name;
        this.stateMasks = new int[2];
        this.unhappyColor = Color.gray;

        this.createStateMasks();
    }

    /**
     * Uses this group's identifier to create a state mask for an agent that is
     * "happy" with her current neighborhood.
     *
     * @return A "happy" state mask.
     */
    private int createHappyStateMask() {
        return this.id << 16;
    }

    /**
     * Uses this group's identifier to create a state mask for an agent that is
     * "unhappy" with her current neighborhood.
     *
     * @return An "unhappy" state mask.
     */
    private int createUnhappyStateMask() {
        return (1 << 31) | (this.id << 16);
    }

    /**
     * Uses this group's identifier to create state masks for both an agent that
     * is "happy" with her neighborhood and an agent who is "unhappy".
     *
     * <p>
     *     State masks are intended to be unique across groups and are used by
     *     the rendering framework to draw agents by group.
     * </p>
     */
    private void createStateMasks() {
        this.createStateMasks(this.stateMasks);
    }

    /**
     * Uses this group's identifier to create state masks for both an agent that
     * is "happy" with her neighborhood and an agent who is "unhappy" and stores
     * the result in the specified array.
     *
     * <p>
     *     State masks are intended to be unique across groups and are used by
     *     the rendering framework to draw agents by group.
     * </p>
     *
     * @param stateMasks
     *        The array to store the result(s) in.
     * @throws IllegalArgumentException
     *         If {@code stateMask} has a length that is not equal to two.
     * @throws NullPointerException
     *         If {@code stateMasks} is {@code null}.
     */
    public void createStateMasks(final int[] stateMasks) {
        if(stateMasks == null) {
            throw new NullPointerException();
        }

        if(stateMasks.length != 2) {
            throw new IllegalArgumentException("State array size must be 2!");
        }

        stateMasks[HappinessState.Happy.ordinal()] =
                this.createHappyStateMask();
        stateMasks[HappinessState.Unhappy.ordinal()] =
                this.createUnhappyStateMask();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof Group)) {
            return false;
        }

        final Group group = (Group)obj;
        return this.happyColor.equals(group.happyColor)
               && this.id == group.id
               && this.name.equals(group.name)
               && Double.compare(this.popPercent, group.popPercent) == 0
               && Arrays.equals(this.stateMasks, group.stateMasks)
               && Double.compare(this.tolerance, group.tolerance) == 0
               && this.unhappyColor.equals(group.unhappyColor);
    }

    /**
     * Returns the color of a "happy" agent of this group.
     *
     * @return The color of a "happy" agent.
     */
    public Color getHappyColor() {
        return this.happyColor;
    }

    /**
     * Returns the compressed state mask for an arbitrary agent who is "happy"
     * with their current neighborhood.
     *
     * @return The "happy" state mask.
     */
    public int getHappyStateMask() {
        return this.stateMasks[HappinessState.Happy.ordinal()];
    }

    /**
     * Returns the numeric identifier of this group.
     *
     * @return The numeric identifier.
     */
    public short getId() {
        return this.id;
    }

    /**
     * Returns the (full) name of this group.
     *
     * @return The group name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the percentage of non-empty cells that should be initially
     * allocated to agents that belong to this group.
     *
     * @return Empty cell allocation percentage.
     */
    public double getPopulationPercent() {
        return this.popPercent;
    }

    /**
     * Returns the compressed state mask for an arbitrary agent with the
     * specified happiness.
     *
     * <p>
     *     This is used to determine the color of a cell for rendering purposes.
     * </p>
     *
     * @param happinessState
     *        Whether or not the agent is happy or unhappy; acts as an index
     *        into the state mask array.
     * @return The mask for the specified state.
     * @throws NullPointerException
     *         If {@code happinessState} is {@code null}.
     */
    public int getStateMask(final HappinessState happinessState) {
        if(happinessState == null) {
            throw new NullPointerException();
        }

        return this.stateMasks[happinessState.ordinal()];
    }

    /**
     * Returns the preference for same-group members to be in close proximity to
     * the specified value.
     *
     * @return The same-group preference.
     */
    public double getTolerance() {
        return this.tolerance;
    }

    /**
     * Returns the color of an "unhappy" agent of this group.
     *
     * @return The color of an "unhappy" agent.
     */
    public Color getUnhappyColor() {
        return this.unhappyColor;
    }

    /**
     * Returns the compressed state mask for an arbitrary agent who is "unhappy"
     * with their current neighborhood.
     *
     * @return The "unhappy" state mask.
     */
    public int getUnhappyStateMask() {
        return this.stateMasks[HappinessState.Unhappy.ordinal()];
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = (37 * result) + this.happyColor.hashCode();
        result = (37 * result) + Short.hashCode(this.id);
        result = (37 * result) + this.name.hashCode();
        result = (37 * result) + Double.hashCode(this.popPercent);
        result = (37 * result) + Arrays.hashCode(this.stateMasks);
        result = (37 + result) + Double.hashCode(this.tolerance);
        result = (37 * result) + this.unhappyColor.hashCode();

        return result;
    }

    /**
     * Returns whether or not the compressed state, which represents an agent
     * in the simulation at an arbitrary location, belongs to this group.
     *
     * @param compressedState
     *        The compressed state of an agent to check.
     * @return Whether or not an agent is a member of the group.
     */
    public boolean isMember(final int compressedState) {
        // Obtain the group identifier from the state.
        final short otherGroupId = (short)((compressedState >> 16) & 0x0ff);
        return this.id == otherGroupId;
    }

    /**
     * Sets the color of a "happy" agent of this group.
     *
     * @param happyColor
     *        The "happy" color to use.
     * @throws NullPointerException
     *         If {@code happyColor} is {@code null}.
     */
    public void setHappyColor(final Color happyColor) {
        if(happyColor == null) {
            throw new NullPointerException();
        }

        this.happyColor = happyColor;
    }

    /**
     * Sets the (full) name of this group.
     *
     * @param name
     *        The full name to use.
     * @throws NullPointerException
     *         If {@code name} is {@code null}.
     */
    public void setName(final String name) {
        if(name == null) {
            throw new NullPointerException();
        }

        this.name = name;
    }

    /**
     * Sets the percentage of non-empty cells that should be initially allocated
     * to agents that belong to this group to the specified percentage.
     *
     * @param popPercent
     *        The percentage to use.
     * @throws IllegalArgumentException
     *         If {@code popPercent} is less than zero or greater than one.
     */
    public void setPopulationPercentage(final double popPercent) {
        if(Double.compare(popPercent, 0.0d) < 0
           || Double.compare(popPercent, 1.0d) > 0) {
            throw new IllegalArgumentException("Tolerance must be on [0, 1]!");
        }

        this.popPercent = popPercent;
    }

    /**
     * Sets the preference for same-group members to be in close proximity to
     * the specified value.
     *
     * @param tolerance
     *        The preference value to use.
     * @throws IllegalArgumentException
     *         If {@code tolerance} is less than zero or greater than one.
     */
    public void setTolerance(final double tolerance) {
        if(Double.compare(tolerance, 0.0d) < 0
           || Double.compare(tolerance, 1.0d) > 0) {
            throw new IllegalArgumentException("Tolerance must be on [0, 1]!");
        }

        this.tolerance = tolerance;
    }

    /**
     * Sets the color of an "unhappy" agent of this group.
     *
     * @param unhappyColor
     *        The "unhappy" color to use.
     * @throws NullPointerException
     *         If {@code unhappyColor} is {@code null}.
     */
    public void setUnhappyColor(final Color unhappyColor) {
        if(unhappyColor == null) {
            throw new NullPointerException();
        }

        this.unhappyColor = unhappyColor;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
