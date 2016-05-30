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

import sim.display.Console;
import sim.display.GUIState;
import sim.util.gui.LabelledList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * Represents an implementation of a {@link Console} that contains an
 * additional panels that allows the user to configure various parameters for
 * both groups as well as the simulation as a whole.
 */
public class SchellingExplorerConsole extends Console {

    /**
     * Constructor.
     *
     * @param simulation
     *        The simulation to control.
     */
    public SchellingExplorerConsole(final GUIState simulation) {
        super(simulation);

        // Set the title.
        this.setTitle("Schelling Explorer");

        // Create and bind the group pane.
        this.createGroupPane();

        // Fix our positioning.
        this.positionConsole();
    }

    /**
     * Creates and configures the collection of widgets that allow the user
     * to modify both the parameters of each group as well as the color of
     * the empty cells and whether or not "unhappy" agents should be
     * visualized differently than their "happy" counterparts.
     */
    private void createGroupPane() {
        // First, obtain the simulation in question.
        final SchellingExplorerWithUi guiState =
                (SchellingExplorerWithUi)this.getSimulation();

        // The group option lists.
        final LabelledList firstList =
                guiState.getFirstGroup().createWidgetList();
        final LabelledList secondList =
                guiState.getSecondGroup().createWidgetList();

        // The overall panel component.
        final Box container = UiUtils.createBox(BoxLayout.Y_AXIS);

        // The sub-panels for groups.
        final Box firstBox = UiUtils.createBox(BoxLayout.Y_AXIS);
        final Box secondBox = UiUtils.createBox(BoxLayout.Y_AXIS);
        final Box thirdBox = UiUtils.createBox(BoxLayout.Y_AXIS);

        // Group one.
        firstBox.add(new JLabel("Group One", JLabel.LEFT));
        firstBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        firstBox.add(firstList);

        // Group two.
        secondBox.add(new JLabel("Group Two", JLabel.LEFT));
        secondBox.add(new JSeparator(SwingConstants.HORIZONTAL));
        secondBox.add(secondList);

        // Miscellaneous list.
        final LabelledList miscellaneous = UiUtils.createLabelledList();
        miscellaneous.addLabelled("Empty Color", guiState.getEmptyColor());
        miscellaneous.addLabelled("Show Unhappy", guiState.getShowUnhappy());
        miscellaneous.addLabelled("Track Statistics",
                                  guiState.getTrackStatistics());

        // Miscellaneous group.
        thirdBox.add(new JLabel("Miscellaneous", JLabel.LEFT));
        thirdBox.add(new JSeparator(JSeparator.HORIZONTAL));
        thirdBox.add(miscellaneous);

        // (Try to) Fix sizes.
        firstBox.setMaximumSize(firstBox.getPreferredSize());
        secondBox.setMaximumSize(secondBox.getPreferredSize());
        thirdBox.setMaximumSize(thirdBox.getPreferredSize());

        // Bind sub-panels to overall panel.
        container.add(Box.createRigidArea(new Dimension(3, 2)));
        container.add(firstBox);
        container.add(Box.createRigidArea(new Dimension(3, 10)));
        container.add(secondBox);
        container.add(Box.createRigidArea(new Dimension(3, 10)));
        container.add(thirdBox);

        // Finally, bind to the console's tab display.
        final JScrollPane scrollPane = UiUtils.createScrollPanel(container);
        final JTabbedPane tabbedPane = this.getTabPane();

        tabbedPane.add("Groups", scrollPane);
    }

    /**
     * Centers both this console and the simulation display on screen.
     */
    private void positionConsole() {
        // First, obtain the simulation in question.
        final SchellingExplorerWithUi guiState =
                (SchellingExplorerWithUi)this.getSimulation();

        // Next, fix the console's height to match the simulation display.
        this.setSize(this.getWidth(), guiState.getSimulationFrame().getHeight());

        // Next, obtain the bounds of all currently created windows.
        final Rectangle consoleBounds = this.getBounds();
        final Dimension desktopBounds =
                Toolkit.getDefaultToolkit().getScreenSize();
        final Rectangle simBounds = guiState.getSimulationFrame().getBounds();

        // Determine the size of the console and simulation display combined.
        final Dimension totalBounds =
                new Dimension(consoleBounds.width + simBounds.width,
                              consoleBounds.height + simBounds.height);

        // Determine the center point of the desktop.
        final int centerX = desktopBounds.width / 2;
        final int centerY = desktopBounds.height / 2;

        // The resulting location for the simulation display (on the left) is
        // the center of the desktop minus half the total bounds' width and
        // height.
        guiState.getSimulationFrame()
                .setLocation(centerX - (totalBounds.width / 2),
                             centerY - (totalBounds.height / 4));

        // And finally, the console's location is the same height as the
        // simulation display but starting at its width.
        this.setLocation(guiState.getSimulationFrame().getX() + simBounds.width,
                         guiState.getSimulationFrame().getY());
    }
}
