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

import com.solsticesquared.schelling.ui.SchellingExplorerConsole;
import com.solsticesquared.schelling.ui.SchellingExplorerWithUi;
import sim.display.Console;
import sim.display.GUIState;
import sim.engine.SimState;

import javax.swing.SwingUtilities;

/**
 * The main driver for the Schelling project.
 */
public final class AppEntry {

    /**
     * The application entry point.
     *
     * @param args
     *        The array of command line arguments, if any.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            // The random number generation seed.
            final long seed = System.currentTimeMillis();

            // The underlying Schelling-based model.
            final SimState model =
                    SchellingExplorerUtils.createTwoGroupModel(seed);
            // The user interface.
            final GUIState view = new SchellingExplorerWithUi(model);
            // The user interface controller.
            final Console controller = new SchellingExplorerConsole(view);

            // Run.
            controller.setVisible(true);
        });
    }

    /**
     * Constructor (private).
     */
    private AppEntry() {
    }
}
