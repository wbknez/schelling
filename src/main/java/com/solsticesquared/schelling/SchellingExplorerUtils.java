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

import java.awt.Color;

/**
 * Represents a collection of utility methods for creating
 * {@link SchellingExplorer} simulations for use in my research paper.
 */
public final /* strictfp */ class SchellingExplorerUtils {

    /**
     * The (static) colors to use for rendering model cell values.
     *
     * <p>
     *     This collection of colors merely represent the default
     *     visualization on start; per-group colors may be configured
     *     programmatically or through the user interface.
     * </p>
     */
    public static final class Colors {

        /**
         * The color of a model grid cell that is not occupied by an agent from
         * any group.
         *
         * <p>
         *     This should never happen but is a useful marker for spotting
         *     erroneous population dispersion.
         * </p>
         */
        public static final Color EmptySpace = new Color(255, 255, 255);

        /** The "happy" color for group "A". */
        public static final Color HappyA        = new Color(0, 51, 204);

        /** The "happy" color for group "B". */
        public static final Color HappyB        = new Color(255, 51, 0);

        /** The "unhappy" color for group "A". */
        public static final Color UnhappyA      = new Color(153, 204, 255);

        /** The "unhappy" color for group "B". */
        public static final Color UnhappyB      = new Color(255, 157, 179);

        /**
         * Constructor (private).
         */
        private Colors() {
        }
    }

    /**
     * Creates, configures, and returns a {@link SchellingExplorer}
     * simulation that can be used to generate results for my research paper.
     *
     * @param seed
     *        The random seed to use.
     * @return A new model, configured for two groups.
     */
    public static SchellingExplorer createTwoGroupModel(final long seed) {
        // Create the two groups to use, "A" and "B".
        final Group groupA = new Group("Group A", (short)0);
        final Group groupB = new Group("Group B", (short)1);

        // Assign required colors for display, even if this model is not
        // going to be presented to a/the user.
        groupA.setHappyColor(Colors.HappyA);
        groupA.setUnhappyColor(Colors.UnhappyA);
        groupB.setHappyColor(Colors.HappyB);
        groupB.setUnhappyColor(Colors.UnhappyB);

        // Assign the default populations and tolerance(s).
        groupA.setPopulationPercentage(0.5d);
        groupA.setTolerance(0.5d);
        groupB.setPopulationPercentage(0.5d);
        groupB.setTolerance(0.5d);

        // Create the simulation.
        final SchellingExplorer model = new SchellingExplorer(seed);

        // Add the groups.
        model.addGroup(groupA);
        model.addGroup(groupB);

        // Done.
        return model;
    }

    /**
     * Constructor (private).
     */
    private SchellingExplorerUtils() {
    }
}
