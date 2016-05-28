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
import sim.util.Interval;
import sim.util.gui.LabelledList;
import sim.util.gui.PropertyField;

import java.awt.Color;

/**
 * Contains a collection of user interface widgets that may be used to
 * configure a single {@link Group}.
 */
public class GroupOptions {

    /** Allows the user to select a color for visualizing "happy" agents. */
    private final ChooseColorButton happyColorChooser;

    /**
     * Allows the user to select the name of a group; this name is used for
     * charting.
     */
    private final PropertyField     nameField;

    /**
     * Allows the user to select the percentage of available cells that
     * should be occupied by a group.
     */
    private final PropertyField     popField;

    /**
     * Allows the user to select the number of same-group neighbors that must
     * be present for an agent to be "happy" with her current neighborhood.
     */
    private final PropertyField     toleranceField;

    /** Allows the user to select a color for visualizing "unhappy" agents. */
    private final ChooseColorButton unhappyColorChooser;

    /**
     * Constructor.
     *
     * @param group
     *        The group to use.
     * @throws NullPointerException
     *         If {@code group} is {@code null}.
     */
    public GroupOptions(final Group group) {
        if(group == null) {
            throw new NullPointerException();
        }

        this.happyColorChooser = new ChooseColorButton(group.getHappyColor());
        this.nameField = new PropertyField(group.getName());
        this.popField =
                new PropertyField(null,
                                  Double.toString(group.getPopulationPercent()),
                                  true, new Interval(0.0d, 1.0d),
                                  PropertyField.SHOW_SLIDER);
        this.toleranceField =
                new PropertyField(null,
                                  Double.toString(group.getTolerance()), true,
                                  new Interval(0.0d, 1.0d),
                                  PropertyField.SHOW_SLIDER);
        this.unhappyColorChooser =
                new ChooseColorButton(group.getUnhappyColor());
    }

    /**
     * Creates a {@link LabelledList} that contains all available widgets
     * with appropriate labels.
     *
     * @return A {@link LabelledList} of widgets for display.
     */
    public LabelledList createWidgetList()  {
        final LabelledList list = UiUtils.createLabelledList();

        list.addLabelled("Name", this.nameField);
        list.addLabelled("Population", this.popField);
        list.addLabelled("Tolerance", this.toleranceField);
        list.addLabelled("Happy Color", this.happyColorChooser);
        list.addLabelled("Unhappy Color", this.unhappyColorChooser);

        return list;
    }

    /**
     * Returns the color to use when visualizing a "happy" agent in the
     * simulation.
     *
     * @return The color of a "happy" agent.
     */
    public Color getHappyColor() {
        return this.happyColorChooser.getCurrentColor();
    }

    /**
     * Returns the name of the group as specified by the user.
     *
     * @return The group name.
     */
    public String getName() {
        return this.nameField.getValue();
    }

    /**
     * Returns the percentage of available cells that should be occupied by a
     * certain group during a simulation.
     *
     * @return The percentage of available cells for a group.
     */
    public double getPopulationPercent() {
        return Double.parseDouble(this.popField.getValue());
    }

    /**
     * Returns the percentage of same-group neighbors that must be present in
     * order for an agent to be "happy" with her current neighborhood,
     * otherwise she will be "unhappy" and attempt to find a new residence.
     *
     * @return The percentage of same-group neighbors required for "happiness".
     */
    public double getTolerance() {
        return Double.parseDouble(this.toleranceField.getValue());
    }

    /**
     * Returns the color to use when visualizing an "unhappy" agent in the
     * simulation.
     *
     * @return The color of an "unhappy" agent.
     */
    public Color getUnhappyColor() {
        return this.unhappyColorChooser.getCurrentColor();
    }
}
