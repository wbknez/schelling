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

import org.jfree.data.xy.XYSeries;

import java.util.ArrayList;

/**
 * Represents an implementation of an {@link XYSeries} that pre-allocates the
 * underlying array in order to help improve performance.
 */
public class FixedSizeXYSeries extends XYSeries {

    /**
     * Constructor.
     *
     * @param key
     *        The unique name, or id, for the series.
     * @param autoSort
     *        Whether or not to automatically sort incoming data values.
     * @param allowDuplicateXValues
     *        Whether or not to allow duplicate data values.
     * @param reserveElements
     *        The amount of memory to reserve in advance.
     * @throws IllegalArgumentException
     *         If {@code reserveElements} is less than zero.
     */
    public FixedSizeXYSeries(final Comparable<?> key,
                             final boolean autoSort,
                             final boolean allowDuplicateXValues,
                             final int reserveElements) {
        super(key, autoSort, allowDuplicateXValues);

        if(reserveElements < 0) {
            throw new IllegalArgumentException("number of elements to reserve" +
                                               " must be positive!");
        }

        // Reserve the appropriate amount of memory.
        ((ArrayList<?>)this.data).ensureCapacity(reserveElements);
    }
}
