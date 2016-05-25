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

import sim.util.Interval;

import javax.swing.JOptionPane;

/**
 * Represents a collection of parameters that influence the behavior of an
 * agent-based Schelling segregation simulation.
 */
public /* strictfp */ class Parameters {

    /**
     * Represents a collection of default initial values for a
     * {@link Parameters} object.
     */
    public static final class Defaults {

        /** The default boundary type, which is toroidal. */
        public static final int     BoundsType      = 1;

        /**
         * The default percentage of a simulation space that start with no
         * agent.
         */
        public static final double  PercentEmpty    = 0.02d;

        /** The default height of the simulation space. */
        public static final int     Height          = 100;

        /** The default width of the simulation space. */
        public static final int     Width           = 100;

        /**
         * The default radius to use when performing a nearest-neighbor search.
         */
        public static final int     SearchRadius    = 1;

        /**
         * The default maximum number of empty cells an agent is allowed to
         * search in order to find a better neighborhood in which to live.
         */
        public static final int     SearchLimit     = 30;

        /**
         * The default chance that a "happy" agent will choose to search for
         * a comparable location.
         *
         * <p>
         *     This only applies to a "liquid" simulation.
         * </p>
         */
        public static final double  MoveChance      = 0.02d;

        /**
         * The default number of maximum steps allowed per simulation.
         */
        public static final int     NumberOfSteps   = 30000;

        /**
         * The default number of Fischer-Yates shuffles to perform on the
         * simulation before it commences.
         */
        public static final int     ShuffleTimes    = 4;

        /**
         * The default of whether or not to stop the simulation upon
         * encountering an equilibrium solution.
         */
        public static final boolean StopOnEquil     = false;

        /** The default simulation dynamics, which is liquid. */
        public static final int     SimDynamics     = 0;

        /** The default update type, which is single. */
        public static final int     UpdateType      = 1;

        /** The default utility type, which is absolute. */
        public static final int     UtilityType     = 0;

        /**
         * Constructor (private).
         */
        private Defaults() {
        }
    }

    /**
     * Represents the visualization of the types of bounds available for the
     * user to choose from.
     */
    private static final String[] BoundsTypes =
            new String[]{"Bounded", "Toroidal"};

    /**
     * Represents the visualization of the types of simulation dynamics
     * available for the user to choose from.
     */
    private static final String[] SimulationDynamicsTypes =
            new String[]{"Liquid", "Solid", "Swap"};

    /**
     * Represents the visualization of the types of agent updating methods
     * available for the user to choose from.
     */
    private static final String[] UpdateTypes =
            new String[]{"Batch", "Single"};

    /**
     * Represents the visualization of the types of utility assessment methods
     * available for the user to choose from.
     */
    private static final String[] UtilityTypes =
            new String[]{"Absolute", "Relative"};

    /** The type of boundary conditions to use. */
    private int     boundsType;

    /** The height of a simulation space in cells. */
    private int     height;

    /**
     * The percent change that a "happy" agent will search for and move to a
     * new location whose utility is the same as her current neighborhood.
     */
    private double  moveChance;

    /** The maximum number of steps to execute. */
    private int     numSteps;

    /**
     * The percentage of total cells in a simulation space that begin with no
     * agent in residence.
     */
    private double  percentEmpty;

    /**
     * The maximum number of empty cells to search when an agent attempts to
     * find an equal or better location relative to her current neighborhood.
     */
    private int     searchLimit;

    /**
     * The radius, in cells, that comprises a neighborhood with an agent at
     * the center.
     */
    private int     searchRadius;

    /** The number of times to shuffle a simulation space before starting. */
    private int     shuffleTimes;

    /** The type of dynamics a simulation should use. */
    private int     simDynamics;

    /**
     * Whether or not to stop a simulation when there are no "unhappy"
     * agents remaining.
     */
    private boolean stopOnEquil;

    /** How to process the list of "unhappy" agents. */
    private int     updateType;

    /**
     * The type of evaluation that should be used when determining an agent's
     * "happiness" with her current neighborhood.
     */
    private int     utilityType;

    /** The width of a simulation space in cells. */
    private int     width;

    /**
     * Constructor.
     */
    public Parameters() {
        this.boundsType = Defaults.BoundsType;
        this.height = Defaults.Height;
        this.moveChance = Defaults.MoveChance;
        this.numSteps = Defaults.NumberOfSteps;
        this.percentEmpty = Defaults.PercentEmpty;
        this.searchLimit = Defaults.SearchLimit;
        this.searchRadius = Defaults.SearchRadius;
        this.shuffleTimes = Defaults.ShuffleTimes;
        this.simDynamics = Defaults.SimDynamics;
        this.stopOnEquil = Defaults.StopOnEquil;
        this.updateType = Defaults.UpdateType;
        this.utilityType = Defaults.UtilityType;
        this.width = Defaults.Width;
    }

    /**
     * Returns the types of boundary conditions that are available.
     *
     * <p>
     *     There are currently only two boundaries: bounded and toroidal.
     * </p>
     *
     * @return The boundary condition types.
     */
    public Object domBoundsType() {
        return BoundsTypes;
    }

    /**
     * Returns the range of probability that a "happy" agent will search for and
     * move to a new location of equal utility to her current neighborhood.
     *
     * <p>
     *     This is from {@code [0, 1]}.
     * </p>
     *
     * @return The range of probability for a "happy" agent to move.
     */
    public Object domMoveChance() {
        return new Interval(0.0d, 1.0d);
    }

    /**
     * Returns the range of empty cells that may be made available.
     *
     * <p>
     *     Technically, this is from {@code [0, 1]} but the simulation
     *     without swapping dynamics will not move if there are no empty cells
     *     allowed.
     * </p>
     *
     * @return The range of the percentage of empty cells.
     */
    public Object domPercentOfEmptyCells() {
        return new Interval(0.0d, 1.0d);
    }

    /**
     * Returns the types of simulation dynamics that are available.
     *
     * <p>
     *     There are currently only three dynamics: liquid, solid, and swap.
     * </p>
     *
     * @return The boundary condition types.
     */
    public Object domSimulationDynamics() {
        return SimulationDynamicsTypes;
    }

    /**
     * Returns the types of agent update methods that are available.
     *
     * <p>
     *     There are currently only two methods: batch and single.
     * </p>
     *
     * @return The boundary condition types.
     */
    public Object domUpdateType() {
        return UpdateTypes;
    }

    /**
     * Returns the types of utility evaluators that are available.
     *
     * <p>
     *     There are currently only two evaluators: absolute and relative.
     * </p>
     *
     * @return The boundary condition types.
     */
    public Object domUtilityEvaluator() {
        return UtilityTypes;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof Parameters)) {
            return false;
        }

        final Parameters params = (Parameters)obj;
        return this.boundsType == params.boundsType
                && this.height == params.height
                && Double.compare(this.moveChance, params.moveChance) == 0
                && this.numSteps == params.numSteps
                && Double.compare(this.percentEmpty, params.percentEmpty) == 0
                && this.searchLimit == params.searchLimit
                && this.searchRadius == params.searchRadius
                && this.shuffleTimes == params.shuffleTimes
                && this.simDynamics == params.simDynamics
                && this.stopOnEquil == params.stopOnEquil
                && this.updateType == params.updateType
                && this.utilityType == params.utilityType
                && this.width == params.width;
    }

    /**
     * Returns the boundary conditions used by a simulation.
     *
     * @return The boundary conditions.
     */
    public int getBoundsType() {
        return this.boundsType;
    }

    /**
     * Returns the height, in cells, of a simulation.
     *
     * @return The simulation height (in cells).
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the probability that a "happy" agent will attempt to move to
     * another location of the same utility.
     *
     * @return The "happy" agent move probability.
     */
    public double getMoveChance() {
        return this.moveChance;
    }

    /**
     * Returns the maximum number of steps a simulation may execute before
     * being forced to quit.
     *
     * @return The maximum number of execution steps allowed.
     */
    public int getMaximumSteps() {
        return this.numSteps;
    }

    /**
     * Returns the percentage of cells of a simulation that are (required to
     * be) empty.
     *
     * @return The empty cell percentage.
     */
    public double getPercentOfEmptyCells() {
        return this.percentEmpty;
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
     * Returns the number of times a simulation will be shuffled using a
     * Fischer-Yates algorithm before it commences to ensure random agent
     * dispersal.
     *
     * @return The number of times a simulation will be shuffled.
     */
    public int getShuffleTimes() {
        return this.shuffleTimes;
    }

    /**
     * Returns the dynamics used by a simulation.
     *
     * @return The simulation dynamics.
     */
    public int getSimulationDynamics() {
        return this.simDynamics;
    }

    /**
     * Returns whether or not a simulation will stop if the number of
     * "unhappy" agents reaches zero.
     *
     * @return Whether or not to stop when the number of "unhappy" agents is
     * zero.
     */
    public boolean getStopOnEquilibrium() {
        return this.stopOnEquil;
    }

    /**
     * Returns the type of agent updating that should be used per step.
     *
     * @return The amount of agents to update.
     */
    public int getUpdateType() {
        return this.updateType;
    }

    /**
     * Returns the type of utility evaluation to use when determining an
     * agent's "happiness" with her current neighborhood.
     *
     * @return The utility evaluator.
     */
    public int getUtilityEvaluator() {
        return this.utilityType;
    }

    /**
     * Returns the width, in cells, of a simulation.
     *
     * @return The simulation width (in cells).
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the conditions a simulation may use to determine agent visibility
     * along a boundary (edge).
     *
     * @param boundsType
     *        The type of boundary to use.
     */
    public void setBoundsType(final int boundsType) {
        this.boundsType = boundsType;
    }

    /**
     * Sets the height, in cells, of a simulation space.
     *
     * @param height
     *        The height to use.
     */
    public void setHeight(final int height) {
        if(height < 1) {
            this.showErrorDialog("Height", "The height of a simulation must " +
                                          "be positive.  Please enter a new " +
                                          "integer value that is greater than" +
                                          " zero.");
            return;
        }

        this.height = height;
    }

    /**
     * Sets the probability that a "happy" agent will choose to search for and
     * move to a new location that is of the same utility as her previous
     * location.
     *
     * @param moveChance
     *        The the new probability to move.
     */
    public void setMoveChance(final double moveChance) {
        if(Double.compare(moveChance, 0.0d) < 0 || Double.compare(moveChance, 1.0d) > 0) {
            this.showErrorDialog("MoveChance", "The movement chance of a " +
                                               "\"happy\" agent should be " +
                                               "between the values of 0 and 1" +
                                               ".  Please enter a new " +
                                               "floating-point value that is " +
                                               "somewhere between zero and " +
                                               "one.");
            return;
        }

        this.moveChance = moveChance;
    }

    /**
     * Sets the maximum number of steps a simulation may execute before being
     * forced to quit.
     *
     * @param maximumSteps
     *        The maximum number of steps to allow.
     */
    public void setMaximumSteps(final int maximumSteps) {
        if(maximumSteps <= 0) {
            this.showErrorDialog("MaximumSteps", "The maximum number of steps" +
                                                 " a " +
                                           "simulation may take must " +
                                           "be positive.  Please enter a new " +
                                           "integer value that is greater than" +
                                           " zero.");
            return;
        }

        this.numSteps = maximumSteps;
    }

    /**
     * Sets the percentage of cells that will be considered empty or unoccupied
     * in a simulation.
     *
     * @param emptyCells
     *        The percentage of empty space to use.
     */
    public void setPercentOfEmptySpaces(final double emptyCells) {
        if(Double.compare(emptyCells, 0.0d) < 0
           || Double.compare(emptyCells, 1.0d) > 0) {
            this.showErrorDialog("PercentOfEmptySpace", "The percentage of " +
                                                      "cells " +
                                               "that may be empty at the " +
                                               "start of a" +
                                               "simulation should be " +
                                               "between the values of 0 and 1" +
                                               ".  Please enter a new " +
                                               "floating-point value that is " +
                                               "somewhere between zero and " +
                                               "one.");
            return;
        }

        this.percentEmpty = emptyCells;
    }

    /**
     * Sets the maximum number of empty cells an agent may search when
     * attempting to find a new location of equal or greater utility compared
     * to her current residence.
     *
     * @param searchLimit
     *        The maximum number of empty cells to use.
     */
    public void setSearchLimit(final int searchLimit) {
        if(searchLimit < 1) {
            this.showErrorDialog("SearchLimit", "The search limit of an agent" +
                                                " must be positive.  Please " +
                                                "enter a new integer value " +
                                                "that is greater than zero.");
            return;
        }

        this.searchLimit = searchLimit;
    }

    /**
     * Sets the radial size of an agent's neighborhood that will be used when
     * performing a nearest-neighbor search.
     *
     * @param searchRadius
     *        The radial neighborhood size to use.
     */
    public void setSearchRadius(final int searchRadius) {
        if(searchRadius < 1) {
            this.showErrorDialog("SearchRadius", "The search radius of an " +
                                           "agent must " +
                                           "be positive.  Please enter a new " +
                                           "integer value that is greater than" +
                                           " zero.");
            return;
        }

        this.searchRadius = searchRadius;
    }

    /**
     * Sets the number of times a simulation will be shuffled using a
     * Fischer-Yates algorithm before it commences to ensure random agent
     * dispersal.
     *
     * @param shuffleTimes
     *        The number of times to shuffle.
     */
    public void setShuffleTimes(final int shuffleTimes) {
        if(shuffleTimes < 1) {
            this.showErrorDialog("ShuffleTimes", "The number of times a " +
                                           "simulation may be shuffled must " +
                                           "be positive.  Please enter a new " +
                                           "integer value that is greater than" +
                                           " zero.");
            return;
        }

        this.shuffleTimes = shuffleTimes;
    }

    /**
     * Sets the type of simulation dynamics a simulation may/will use.
     *
     * @param simDynamics
     *        The simulation dynamics to use.
     */
    public void setSimulationDynamics(final int simDynamics) {
        this.simDynamics = simDynamics;
    }

    /**
     * Sets whether or not a simulation will stop once the number of
     * "unhappy" agents reaches zero.
     *
     * @param stopOnEquil
     *        Whether or not to stop when the number of "unhappy" agents
     *        reaches zero.
     */
    public void setStopOnEquilibrium(final boolean stopOnEquil) {
        this.stopOnEquil = stopOnEquil;
    }

    /**
     * Sets the type of agent updating that should be used per step.
     *
     * @param updateType
     *        The type of updating method to use.
     */
    public void setUpdateType(final int updateType) {
        this.updateType = updateType;
    }

    /**
     * Sets the type of utility evaluation to use when determining an
     * agent's "happiness" with her current neighborhood.
     *
     * @param utilityType
     *        The type of utility evaluator to use.
     */
    public void setUtilityEvaluator(final int utilityType) {
        this.utilityType = utilityType;
    }

    /**
     * Sets the width, in cells, of a simulation space.
     *
     * @param width
     *        The width to use.
     */
    public void setWidth(final int width) {
        if(width < 1) {
            this.showErrorDialog("Width", "The width of a simulation must " +
                                           "be positive.  Please enter a new " +
                                          "integer value that is greater than" +
                                          " zero.");
            return;
        }

        this.width = width;
    }

    /**
     * Shows a small dialog detailing an input error (e.g. out of range,
     * invalid characters, etc.).
     *
     * <p>
     *     This is intended to replace throwing
     *     {@link IllegalArgumentException} objects since there is no way for
     *     MASON to respond to them correctly.
     * </p>
     *
     * <p>
     *     It is expected that when an input error is detected (and this
     *     dialog shown), the value of the specified variable will not be
     *     altered.  This preserves the integrity of both a simulation and
     *     this project.
     * </p>
     *
     * @param variable
     *        The name of the variable whose value was input incorrectly.
     * @param message
     *        The message describing what, exactly, the user did wrong and
     *        how they should fix it.
     */
    private void showErrorDialog(final String variable, final String message) {
        // Construct the title.
        final String title = "Invalid Input: " + variable;

        // Show the dialog.
        JOptionPane.showMessageDialog(null, message, title,
                                      JOptionPane.ERROR_MESSAGE);
    }
}
