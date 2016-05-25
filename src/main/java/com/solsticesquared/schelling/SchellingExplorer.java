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
import com.solsticesquared.schelling.task.MovementTask;
import com.solsticesquared.schelling.task.StatisticsTask;
import com.solsticesquared.schelling.task.StopConditionTask;
import com.solsticesquared.schelling.task.UpdateTask;
import sim.engine.RandomSequence;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;
import sim.util.MutableInt2D;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Represents an agent-based Schelling segregation simulation whereby a
 * collection of individuals, each associated with an arbitrary categorical
 * group, move around a simulation space and attempt to maximize their
 * happiness, or comfort, by searching for and occupying neighborhoods
 * that satisfy some arbitrary social criteria.
 *
 * <p>
 *     The above is accomplished by each agent attempting to maximize the number
 *     same-group (in-group) members in an arbitrary radius around her
 *     current location.  If this number falls below the minimum required by
 *     her group, the agent deems the neighborhood unacceptable and moves in
 *     an attempt to find a more suitable location.
 * </p>
 *
 * <p>
 *     This project allows different variations of evaluation behavior,
 *     simulation space geometry, and movement rules.  Each of these
 *     algorithms may affect the resulting simulation in different ways.  In
 *     addition, there are many parameters that also affect each individual
 *     simulation and so all contribute to a rich set of possible outcomes.
 * </p>
 */
public /* strictfp */ class SchellingExplorer extends SimState {

    /**
     * Represents a collection of constant values for a
     * {@link SchellingExplorer} object.
     */
    public static final class Constants {

        /** The value for an unoccupied cell in a simulation space. */
        public static final int EmptyCell       = -1;

        /**
         * Constructor (private).
         */
        private Constants() {
        }
    }

    /**
     * Represents a collection of values that define the ordering for the
     * tasks that this simulation is broken into.
     */
    public static final class TaskOrder {

        /** Denotes the ordering for a {@link MovementTask}. */
        public static final int Movement        = 1;

        /** Denotes the order for an {@link UpdateTask}. */
        public static final int Update          = 2;

        /** Denotes the order for a {@link StatisticsTask}. */
        public static final int Statistics      = 3;

        /** Denotes the order for a {@link StopConditionTask}. */
        public static final int StopCondition   = 4;

        /**
         * Constructor (private).
         */
        private TaskOrder() {
        }
    }

    /**
     * The collection of agents in the model.
     *
     * <p>
     *     A {@link sim.engine.RandomSequence} is used to randomize the order
     *     of iteration of agents per step.
     * </p>
     */
    private final ClearableRandomSequence   agents;

    /**
     * A thread-safe repository of {@link ComputeCache} objects.
     *
     * <p>
     *     For the current version of this simulation using a
     *     {@code ThreadLocal} is not necessary. However, I would like to
     *     investigate parallelization using
     *     {@link sim.engine.ParallelSequence} and in that case having the
     *     cache be thread-friendly is worthwhile.
     * </p>
     */
    private final ThreadLocal<ComputeCache> computeCache =
            new ThreadLocal<ComputeCache>() {

                @Override
                public ComputeCache initialValue() {
                    return new ComputeCache();
                }
            };

    /**
     * The list of empty cells that may be searched by agents for a new
     * residence.
     */
    private final ArrayList<MutableInt2D>   emptyCells;

    /** The list of all available groups that an agent may belong to. */
    private final ArrayList<Group>          groups;

    /**
     * The list/map of all agents that need to either be swapped with
     * others of the same group or those of a different one.
     */
    private final ArrayList<Agent>          moveList;

    /** The collection of user-modifiable simulation parameters. */
    private final Parameters                parameters;

    /** The utility for creating agent populations. */
    private final PopulationDispenser       popDispenser;

    /**
     * The "rules" of the currently running simulation, updated just before a
     * new simulation is begun.
     */
    private final Ruleset                   ruleset;

    /**
     * The simulation space, represented as a two-dimensional array of integers.
     *
     * <p>
     *     Each integer value represents an agent, where the agent's value is
     *     a compressed integer containing both her happiness state and
     *     also her group's identifier.
     * </p>
     */
    private IntGrid2D                       simulSpace;

    /**
     * Constructor.
     *
     * @param seed
     *        The random generator seed to use.
     */
    public SchellingExplorer(final long seed) {
        this(seed, Defaults.Width, Defaults.Height);
    }

    /**
     * Constructor.
     *
     * @param seed
     *        The random generator seed to use.
     * @param initialWidth
     *        The initial width of the simulation space to use.
     * @param initialHeight
     *        The initial height of the simulation space to use.
     * @throws IllegalArgumentException
     *         If either {@code initialWidth} or {@code initialHeight} are less
     *         than one.
     */
    public SchellingExplorer(final long seed, final int initialWidth,
                             final int initialHeight) {
        super(seed);

        if(initialHeight < 1) {
            throw new IllegalArgumentException("Height must be positive!");
        }

        if(initialWidth < 1) {
            throw new IllegalArgumentException("Width must be positive!");
        }

        this.agents = new ClearableRandomSequence();
        this.emptyCells = new ArrayList<>();
        this.groups = new ArrayList<>(2);
        this.moveList = new ArrayList<>();
        this.parameters = new Parameters();
        this.popDispenser = new PopulationDispenser();
        this.ruleset = new Ruleset();
        this.simulSpace = new IntGrid2D(initialWidth, initialHeight, -1);
    }

    /**
     * Adds the specified group to this simulation's list of available groups
     * for agents to belong to.
     *
     * @param group
     *        The group to add.
     * @return Whether or not the addition operation succeeded.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public boolean addGroup(final Group group) {
        if(group == null) {
            throw new NullPointerException();
        }

        return this.groups.add(group);
    }

    /**
     * Creates and binds all tasks for this simulation.
     *
     * <p>
     *     There are three tasks that perform the following functions:
     *     <ol>
     *         <li>Move between unhappy agents of differing groups and happy
     *         agents of similar groups.</li>
     *         <li>Evaluate a/the stopping condition for premature exit.
     *         .</li>
     *         <li>Update each agent's evaluation of her current
     *         neighborhood and, based on the result, slate for movement as
     *         above.</li>
     *     </ol>
     *     These tasks are also executed in the above order to ensure that
     *     the user interface, if used, is up-to-date with the current state
     *     of the simulation.
     * </p>
     */
    private void attachTasks() {
        this.schedule.scheduleRepeating(this.schedule.getTime() + 1.0,
                                        TaskOrder.Movement,
                                        new MovementTask(),
                                        1.0);
        this.schedule.scheduleRepeating(this.schedule.getTime() + 1.0,
                                        TaskOrder.Update,
                                        new UpdateTask(),
                                        1.0);
        this.schedule.scheduleRepeating(this.schedule.getTime() + 1.0,
                                        TaskOrder.Statistics,
                                        new StatisticsTask(),
                                        1.0);
        this.schedule.scheduleRepeating(this.schedule.getTime() + 1.0,
                                        TaskOrder.StopCondition,
                                        new StopConditionTask(),
                                        1.0);
    }

    /**
     * Iterates over the simulation space and does the following:
     *  <ul>
     *      <li>If an empty cell is encountered, it is added to the empty cell
     *      list, or</li>
     *      <li>Otherwise, the group index is used (from the cell) to create a
     *      new agent of that group and the cell value is replaced by a
     *      "happy" state to indicate an agent's place (all agents start
     *      "happy").</li>
     *  </ul>
     */
    private void collectAgentsAndEmptyCells() {
        for(int i = 0; i < this.parameters.getWidth(); i++) {
            for(int j = 0; j < this.parameters.getHeight(); j++) {
                final int currentValue = this.simulSpace.field[i][j];

                if(currentValue == Constants.EmptyCell) {
                    final MutableInt2D emptyCell = new MutableInt2D(i, j);
                    this.emptyCells.add(emptyCell);
                }
                else {
                    // Obtain the group the value refers to.
                    final Group group = this.groups.get(currentValue);
                    // Create a new agent based on that group.
                    final Agent agent = new Agent(group, i, j);

                    // Add the agent to the simulation queue.
                    this.agents.addSteppable(agent);
                    // Set the grid value to the "real" group indicator.
                    // Use "unhappy" because the simulation is uncertain.
                    this.simulSpace.field[i][j] = group.getHappyStateMask();
                }
            }
        }
    }

    /**
     * Returns the randomized collection of agents currently in this simulation.
     *
     * @return The randomized collection of agents.
     */
    public RandomSequence getAgents() {
        return this.agents;
    }

    /**
     * Returns a thread-local copy of a {@link ComputeCache} for agents to use
     * when evaluating their neighborhoods utilizing nearest-neighbor searches.
     *
     * @return A thread-local {@link ComputeCache} object.
     */
    public ComputeCache getComputeCache() {
        return this.computeCache.get();
    }

    public ArrayList<MutableInt2D> getEmptyCells() {
        return this.emptyCells;
    }

    /**
     * Searches for and returns the group with the specified name.
     *
     * @param groupName
     *        The name of the group to search for.
     * @return The group with the required name.
     * @throws NullPointerException
     *         If {@code groupName} is {@code null}.
     */
    public Group getGroupByName(final String groupName) {
        if(groupName == null) {
            throw new NullPointerException();
        }

        for(final Group group : this.groups) {
            if(groupName.equals(group.getName())) {
                return group;
            }
        }

        throw new NoSuchElementException("Could not find group: " + groupName);
    }

    /**
     * Returns the collection of categorical groups to which an agent may
     * belong.
     *
     * @return The collection of groups.
     */
    public ArrayList<Group> getGroupList() {
        return this.groups;
    }

    /**
     * Returns the list or mapping of all agents that either need to be
     * swapped with others of the same group ("happy") or switched with others
     * whose group is different ("unhappy").
     *
     * @return The list or mapping of agents that need to move to new locations.
     */
    public ArrayList<Agent> getMovementList() {
        return this.moveList;
    }

    /**
     * Returns the collection of user-modifiable simulation parameters.
     *
     * @return The collection of parameters.
     */
    public Parameters getParameters() {
        return this.parameters;
    }

    /**
     * Returns the "rules" that are being used by the currently running
     * simulation.
     *
     * <p>
     *     These "rules" are updated before the start of each new simulation.
     * </p>
     *
     * @return The simulation rules.
     */
    public Ruleset getRuleset() {
        return this.ruleset;
    }

    /**
     * Returns the object used to denote the simulation space, which is given
     * as a grid of integers whose values represent individual agents.
     *
     * @return The grid object used as the simulation space.
     */
    public IntGrid2D getSimulationSpace() {
        return this.simulSpace;
    }

    /**
     * Populates the simulation space with both empty cells and group indices
     * from all available groups according to their relative population
     * percentages.
     *
     * <p>
     *     Please note that this method does <b>not</b> create new agents nor
     *     does it place valid state masks in the simulation space for
     *     rendering.  The resulting simulation space after this method is
     *     called should never be displayed.
     * </p>
     */
    private void populateGridSpace() {
        // First, compute the total population of the grid as a whole.
        final int totalCells = this.parameters.getWidth()
                               * this.parameters.getHeight();
        // Then compute the number of cells that are required to be empty.
        final int emptyCells =
                this.ruleset.getSimulationDynamics().allowsEmptyCells() ?
                (int) Math.floor(totalCells
                                 * this.parameters.getPercentOfEmptyCells())
                : 0;

        // Initialize the population dispenser.
        this.popDispenser.initialize(this.groups, totalCells - emptyCells,
                                     false);

        // Force the empty cell list to have the requisite amount of space.
        this.emptyCells.trimToSize();
        this.emptyCells.ensureCapacity(emptyCells);

        // For each cell, populate with a random indice value.
        for(int i = 0; i < this.parameters.getWidth(); i++) {
            for(int j = 0; j < this.parameters.getHeight(); j++) {
                this.simulSpace.field[i][j] =
                        this.popDispenser.nextAgent(this.random);

                if(!this.popDispenser.hasMore()) {
                    return;
                }
            }
        }
    }

    /**
     * Removes the specified group from this simulation's list of available
     * groups for agents to belong to.
     *
     * @param group
     *        The group to remove.
     * @return Whether or not the removal operation succeeded.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public boolean removeGroup(final Group group) {
        if(group == null) {
            throw new NullPointerException();
        }

        return this.groups.remove(group);
    }

    /**
     * Clears and resets all reusable simulation-related objects.
     */
    private void resetSimulation() {
        this.agents.clear();
        this.emptyCells.clear();
        this.moveList.clear();
        this.popDispenser.clear();
        this.simulSpace.setTo(Constants.EmptyCell);
    }

    /**
     * Shuffles the entire simulation space using Fischer-Yates for the
     * specified number of times.
     *
     * @param numberOfTimes
     *        The number of times to shuffle the entire simulation space.
     * @throws IllegalArgumentException
     *         If {@code numberOfTimes} is less than one.
     */
    private void shuffleGridSpace(final int numberOfTimes) {
        if(numberOfTimes < 1) {
            throw new IllegalArgumentException("Number of shuffles must be"
                                               + " positive!");
        }

        // Use Fischer-Yates to shuffle the simulation space
        // the specified number of times.
        for(int i = 0; i < numberOfTimes; i++) {
            this.useFischerYatesShuffle();
        }
    }

    @Override
    public void start() {
        super.start();

        // Reset any necessary state for the simulation.
        this.resetSimulation();

        // (Re)Update the "rules".
        this.ruleset.updateRules(this.parameters);

        // (Re)Populate the simulation space as necessary.
        this.populateGridSpace();

        // (Re)Shuffle the simulation space.
        this.shuffleGridSpace(this.parameters.getShuffleTimes());

        // (Re)Collect all agents and empty cells.
        this.collectAgentsAndEmptyCells();

        // (Re)Attach all tasks.
        this.attachTasks();
    }

    /**
     * Swaps the locations of the specified agents and updates the specified
     * simulation space as needed.
     *
     * @param agent0
     *        An agent to swap.
     * @param agent1
     *        Another agent to swap.
     * @throws NullPointerException
     *         If either {@code agent0} or {@code agent1} are {@code null}.
     */
    public void swapLocation(final Agent agent0, final Agent agent1) {
        if(agent0 == null) {
            throw new NullPointerException();
        }

        if(agent1 == null) {
            throw new NullPointerException();
        }

        // Grab the x and y values of the first agent.
        final int tempX = agent0.getXLocation();
        final int tempY = agent0.getYLocation();

        // Copy agent1 to agent0 first, then use the temporaries.
        agent0.setLocation(agent1.getXLocation(), agent1.getYLocation());
        agent1.setLocation(tempX, tempY);

        // Update the simulation space of each agent.
        agent0.updateSimulationSpace(this.simulSpace);
        agent1.updateSimulationSpace(this.simulSpace);
    }

    /**
     * Swaps the locations of the specified agent and the specified empty cell
     * and updates the specified simulation space as needed.
     *
     * @param agent
     *        The agent to swap.
     * @param emptyCell
     *        The empty cell to swap.
     * @throws NullPointerException
     *         If either {@code agent} or {@code emptyCell} are {@code null}.
     */
    public void swapLocation(final Agent agent, final MutableInt2D emptyCell) {
        if(agent == null) {
            throw new NullPointerException();
        }

        if(emptyCell == null) {
            throw new NullPointerException();
        }

        // Grab the x, y, and field values of the first agent.
        final int tempX = agent.getXLocation();
        final int tempY = agent.getYLocation();

        // Update the agent's location with the empty cell, first.
        agent.setLocation(emptyCell.x, emptyCell.y);
        agent.updateSimulationSpace(this.simulSpace);

        // Update the empty cell's location with the agent's previous location.
        emptyCell.setTo(tempX, tempY);
        this.simulSpace.field[tempX][tempY] = Constants.EmptyCell;
    }

    /**
     * Swamps the locations of the specified empty cells with each other.
     *
     * <p>
     *     Note that since they are empty there is no need to update the
     *     simulation space.
     * </p>
     *
     * @param emptyCell0
     *        An empty cell to swap.
     * @param emptyCell1
     *        Another empty cell to swap.
     * @throws NullPointerException
     *         If either {@code emptyCell0} or {@code emptyCell1} are {@code
     *         null}.
     */
    public void swapLocation(final MutableInt2D emptyCell0,
                             final MutableInt2D emptyCell1) {
        if(emptyCell0 == null) {
            throw new NullPointerException();
        }

        if(emptyCell1 == null) {
            throw new NullPointerException();
        }

        // Save the old coordinates, first.
        final int tempX = emptyCell0.x;
        final int tempY = emptyCell0.y;

        // Swap!
        emptyCell0.setTo(emptyCell1.x, emptyCell1.y);
        emptyCell1.setTo(tempX, tempY);
    }

    /**
     * Swaps the two specified simulation space cells with each other.
     *
     * @param aX
     *        The first cell's x-axis coordinate.
     * @param aY
     *        The first cell's y-axis coordinate.
     * @param bX
     *        The second cell's x-axis coordinate.
     * @param bY
     *        The second cell's y-axis coordinate.
     */
    private void swapLocation(final int aX, final int aY,
                              final int bX, final int bY) {
        final int temp = this.simulSpace.field[aX][aY];

        this.simulSpace.field[aX][aY] = this.simulSpace.field[bX][bY];
        this.simulSpace.field[bX][bY] = temp;
    }

    /**
     * Applies a modified two-dimensional Fischer-Yates shuffle to randomly
     * shuffle the entire simulation space.
     */
    private void useFischerYatesShuffle() {
        final int shuffleWidth = this.parameters.getWidth() - 1;
        final int shuffleHeight = this.parameters.getHeight() - 1;

        for(int i = shuffleWidth; i > 0; i--) {
            for(int j = shuffleHeight; j > 0; j--) {
                final int m = this.random.nextInt(i + 1);
                final int n = this.random.nextInt(j + 1);

                this.swapLocation(i, j, m, n);
            }
        }
    }
}
