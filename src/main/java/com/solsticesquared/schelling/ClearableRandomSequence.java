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

import sim.engine.RandomSequence;
import sim.engine.Steppable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an implementation of a {@link RandomSequence} whose collection of
 * agents may be removed, or cleared, and therefore a single instance is able to
 * be reused in an arbitrary number of simulation runs.
 */
public class ClearableRandomSequence extends RandomSequence {

    /**
     * Constructor.
     */
    public ClearableRandomSequence() {
        super(new ArrayList<>(0), false);
    }

    /**
     * Constructor.
     *
     * @param agents
     *        The initial array of agents to use.
     */
    public ClearableRandomSequence(final Steppable[] agents) {
        super(agents, false);
        this.setUsesSets(true);
    }

    /**
     * Constructor.
     *
     * @param collection
     *        The initial collection of agents to use.
     */
    public ClearableRandomSequence(final Collection<Steppable> collection) {
        super(collection, false);
        this.setUsesSets(true);
    }

    /**
     * Constructor.
     *
     * @param agents
     *        The initial array of agents to use.
     * @param shouldSynchronize
     *        Whether or not to synchronize on the agent array.
     */
    public ClearableRandomSequence(final Steppable[] agents,
                                   final boolean shouldSynchronize) {
        super(agents, shouldSynchronize);
        this.setUsesSets(true);
    }

    /**
     * Constructor.
     *
     * @param collection
     *        The initial collection of agents to use.
     * @param shouldSynchronize
     *        Whether or not to synchronize on the agent collection.
     */
    public ClearableRandomSequence(final Collection<Steppable> collection,
                                   final boolean shouldSynchronize) {
        super(collection, shouldSynchronize);
        this.setUsesSets(true);
    }

    /**
     * Queues all agents in this random sequence for removal and forces the
     * underlying {@code Steppable} collection to do so.
     */
    public void clear() {
        // Check to ensure there are agents to remove.
        if(this.size != 0) {
            // Add the current list of Steppables to the removal queue.
            for(int i = 0; i < this.size; i++) {
                this.removeSteppable(this.steps[i]);
            }

            // Force the underlying collection to remove all agents immediately.
            this.loadSteps();
        }
    }

    /**
     * Returns the number of {@code Steppable} objects currently in this
     * sequence.
     *
     * @return The active {@code Steppable} count.
     */
    public int getSize() {
        return this.size;
    }
}
