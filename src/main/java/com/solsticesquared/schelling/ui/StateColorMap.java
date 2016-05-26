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

import com.solsticesquared.schelling.Group;
import sim.util.gui.AbstractColorMap;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents an implementation of a {@link sim.util.gui.ColorMap} that
 * allows its internal color table to be modified on demand.
 *
 * <p>
 *     This class is intended to be used with value-based {@code Grid2D}s and
 *     is intended to reflect the integer state masks created by
 *     {@link com.solsticesquared.schelling.Group} objects.
 * </p>
 */
public class StateColorMap extends AbstractColorMap {

    /**
     * The collection of mappings from from a model cell to visual color.
     */
    private final Map<Double, Color> mappings;

    /**
     * Constructor.
     */
    public StateColorMap() {
        super();
        this.mappings = new HashMap<>();
    }

    /**
     * Associates the specified mode cell value with the specified color in
     * this color map.
     *
     * @param value
     *         The model cell value to use.
     * @param mapping
     *         The color to associate with the model grid cell value.
     * @throws IllegalStateException
     *         If {@code value} is already associated with a color; this map
     *         does not allow duplicates due to how the colors are encoded.
     * @throws NullPointerException
     *         If {@code mapping} is {@code null}.
     */
    public void addMapping(final double value, final Color mapping) {
        if(mapping == null) {
            throw new NullPointerException();
        }

        // Enforce no duplicates.
        if(this.mappings.containsKey(value)) {
            throw new IllegalStateException("Map already contains " + value
                                            + " mapped to "
                                            + this.mappings.get(value)
                                            + " instead of " + mapping + "!");
        }

        this.mappings.put(value, mapping);
    }

    /**
     * Associates the state masks of the specified group with the specified
     * group's colors for both "happy" and "unhappy" states.
     *
     * @param group
     *        The group whose state masks will be mapped to their appropriate
     *        colors for rendering.
     * @throws IllegalStateException
     *         If the state masks of {@code group} have either already been
     *         added to this color map or are (somehow) bound to other color
     *         values.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public void addMapping(final Group group) {
        if(group == null) {
            throw new NullPointerException();
        }

        // Bind both the happy and unhappy states.
        this.addMapping(group.getHappyStateMask(), group.getHappyColor());
        this.addMapping(group.getUnhappyStateMask(), group.getUnhappyColor());
    }

    /**
     * Associates the state masks of the specified group with the specified
     * group's colors for both "happy" and "unhappy" states or only the
     * "happy" state if "unhappy" agents should not be differentiated.
     *
     * @param group
     *        The group whose state masks will be mapped to their appropriate
     *        colors for rendering.
     * @param showUnhappyAgents
     *        Whether or not "unhappy" agents should be shown using a
     *        different color (per group).
     * @throws IllegalStateException
     *         If the state masks of {@code group} have either already been
     *         added to this color map or are (somehow) bound to other color
     *         values.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public void addMapping(final Group group, final boolean showUnhappyAgents) {
        if(group == null) {
            throw new NullPointerException();
        }

        // Bind only the happy color but both happy and unhappy states.
        this.addMapping(group.getHappyStateMask(), group.getHappyColor());
        this.addMapping(group.getUnhappyStateMask(), group.getHappyColor());
    }

    /**
     * Clears all current value-color mappings in this color map.
     */
    public void clear() {
        this.mappings.clear();
    }

    @Override
    public double defaultValue() {
        return -1.0d;
    }

    @Override
    public Color getColor(final double value) {
        return this.mappings.get(value);
    }

    /**
     * Removes the color associated with the specified model cell value in
     * this color map.
     * <p>
     * <p>
     * In most cases, {@link #clear()} should be preferred over this method.
     * </p>
     *
     * @param value
     *         The model cell value to remove.
     * @throws NoSuchElementException
     *         If {@code value} does not have a (currently) associated color in
     *         the map.
     */
    public void removeMapping(final double value) {
        if(!this.mappings.containsKey(value)) {
            throw new NoSuchElementException("No mapping found for " + value
                                             + "!");
        }

        this.mappings.remove(value);
    }
}
