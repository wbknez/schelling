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

import ec.util.MersenneTwisterFast;

import java.util.ArrayList;

/**
 * Represents a simple mechanism for distributing a finite, pre-computed
 * collection of populations over a cell grid in a numerically static manner.
 *
 * <p>
 *     Many implementations of various models use purely probabilistic
 *     algorithms to create their initial populations.  That is, they iterate
 *     over each cell, roll a random number, and determine the cell's content
 *     based upon that roll.  There is nothing sinister about this other than
 *     the algorithm's success being dependent upon the random number generator
 *     used.  In the interest of reproducibility, I decided I wanted a static
 *     mechanism of assigning flat numbers of populations in order to ensure
 *     the ratio of one population to another stays the same (within a
 *     certain epsilon).
 * </p>
 *
 * <p>
 *     Please note that this object is not intended to produce a random
 *     distribution of agents from these populations.  The simulation space
 *     for this project is shuffled repeatedly to achieve that effect.
 * </p>
 */
public /* strictfp */ class PopulationDispenser {

    /** The index of available groups to select from. */
    private final ArrayList<Integer> availableGroups;

    /** The population amounts for each group. */
    private final ArrayList<Integer> popDistribution;

    /**
     * Constructor.
     */
    public PopulationDispenser() {
        this.availableGroups = new ArrayList<>();
        this.popDistribution = new ArrayList<>();
    }

    /**
     * Checks to ensure that the total number of agents to be created in this
     * population dispenser's lists matches <i>exactly</i> the total number
     * of agents required to exist in a/the simulation.
     *
     * <p>
     *     If the above values differ at all, then the remainder is
     *     calculated and equally allocated to each group's population in order.
     * </p>
     *
     * @param requiredPopulation
     *        The number of agents required to exist in a/the simulation.
     */
    private void checkPopulationRemainder(final int requiredPopulation) {
        // Determine how many agents we have slated for creation across all
        // groups.
        final int computedPopulation =
                this.popDistribution.stream()
                                    .mapToInt(Integer::intValue)
                                    .sum();
        // Calculate the remainder.
        final int remainder = requiredPopulation - computedPopulation;

        // Distribute the remainder to all groups in order if necessary.
        if(remainder != 0) {
            int currentIndex = 0;

            for(int i = 0; i < remainder; i++) {
                final int currentGroupPop =
                        this.popDistribution.get(currentIndex);
                this.popDistribution.set(currentIndex, currentGroupPop + 1);

                currentIndex += 1;

                if(currentIndex >= this.popDistribution.size()) {
                    currentIndex = 0;
                }
            }
        }
    }

    /**
     * Clears the available groups and population amounts in advance of being
     * reused.
     */
    public void clear() {
        this.availableGroups.clear();
        this.popDistribution.clear();
    }

    /**
     * Whether or not there are any groups with available populations left to
     * be created or used.
     *
     * @return Whether any group populations remain.
     */
    public boolean hasMore() {
        return !this.popDistribution.isEmpty();
    }

    /**
     * Initializes this population dispenser using the specified group list and
     * the total number of agents that may be spawned.
     *
     * <p>
     *     This method iterates over each of the groups and determines the
     *     fraction of the total population each group may have.
     * </p>
     *
     * <p>
     *     The option to require at least one agent per group is intended to be
     *     used for very small (e.g. {@code 10x10}) simulations only.
     * </p>
     *
     * <p>
     *     The specified group list is used as a mirror here to provide group
     *     indices.  As such, it is expected that between initialization and
     *     {@link #nextAgent(MersenneTwisterFast)} calls this list does not
     *     change.
     * </p>
     *
     * <p>
     *     Finally, because the floor function is used to compute the
     *     population totals any remaining population below the required
     *     amount is distributed equally amongst all groups.  This means that
     *     the desired numeric populations should deviate by no more than one or
     *     two agents at most.
     * </p>
     *
     * @param groupList
     *        The list of groups to use.
     * @param totalPopulation
     *        The total number of agents that may be spawned.
     * @param requireAtLeastOne
     *        Whether or not to require at least one agent as a population per
     *        group.
     * @throws IllegalArgumentException
     *         If either {@code groupList} is empty or {@code totalPopulation}
     *         is less than one.
     * @throws NullPointerException
     *         If {@code groupList} is {@code null}.
     */
    public void initialize(final ArrayList<Group> groupList,
                           final int totalPopulation,
                           final boolean requireAtLeastOne) {
        if(groupList == null) {
            throw new NullPointerException();
        }

        if(groupList.size() == 0) {
            throw new IllegalArgumentException("Must be at least one group!");
        }

        if(totalPopulation < 1) {
            throw new IllegalArgumentException("Total pop must be positive!");
        }

        // Clear the available group list.
        this.availableGroups.clear();

        for(int i = 0; i < groupList.size(); i++) {
            final Group group = groupList.get(i);

            // Obtain the total number of agents to dispense for this group.
            final int groupPop =
                    (int)Math.floor(totalPopulation
                                    * group.getPopulationPercent());

            if(groupPop != 0) {
                this.availableGroups.add(i);
                this.popDistribution.add(groupPop);
            }
            else {
                if(requireAtLeastOne) {
                    this.availableGroups.add(i);
                    this.popDistribution.add(1);
                }
            }
        }

        // Fix any remainder issues.
        this.checkPopulationRemainder(totalPopulation);
    }

    /**
     * Returns the index of the group that the [next] new agent should belong
     * to.
     *
     * @param random
     *        The random number generator to use.
     * @return The next group index that should be used.
     * @throws NullPointerException
     *         If {@code random} is {@code null}.
     */
    public int nextAgent(final MersenneTwisterFast random) {
        if(random == null) {
            throw new NullPointerException();
        }

        // Obtain a random group index to use.
        final int groupSelection = random.nextInt(this.availableGroups.size());

        // The indice of the group in question.
        final int currentGroup = this.availableGroups.get(groupSelection);
        // Obtain the current population of that selection and decrement.
        final int currentPopulation =
                this.popDistribution.get(groupSelection) - 1;

        // Determine if the group's population needs have been exhausted.
        if(currentPopulation <= 0) {
            this.availableGroups.remove(groupSelection);
            this.popDistribution.remove(groupSelection);
        }
        else {
            this.popDistribution.set(groupSelection, currentPopulation);
        }

        return currentGroup;
    }
}
