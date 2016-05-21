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

import sim.util.IntBag;

/**
 * Represents a collection of reusable objects that are intended to be shared
 * across a single simulation thread.
 */
public class ComputeCache {

    /**
     * A collection of default values for a {@code ComputeCache}.
     */
    public static final class Defaults {

        /**
         * The default initial capacity to use.
         *
         * <p>
         *     This corresponds to a typical "nearest neighbor" grid search with
         *     a Moore distance of one without including the search origin.
         * </p>
         */
        public static final int InitialCapacity = 8;

        /**
         * Constructor (private).
         */
        private Defaults() {
        }
    }

    /**
     * The x-axis values that correspond - in order - to the grid cells returned
     * from a nearest-neighbor search query.
     */
    public final IntBag xLocations;

    /**
     * The x-axis values that correspond - in order - to the grid cells returned
     * from a nearest-neighbor search query.
     */
    public final IntBag yLocations;

    /**
     * Constructor.
     */
    public ComputeCache() {
        this(Defaults.InitialCapacity);
    }

    /**
     * Constructor.
     *
     * @param initialCapacity
     *        The number of elements to reserve.
     * @throws IllegalArgumentException
     *         If {@code initialCapacity} is less than zero.
     */
    public ComputeCache(final int initialCapacity) {
        if(initialCapacity < 0) {
            throw new IllegalArgumentException("initial capacity must be " +
                                               "positive!");
        }

        this.xLocations = new IntBag(initialCapacity);
        this.yLocations = new IntBag(initialCapacity);
    }
}
