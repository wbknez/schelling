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
import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.SchellingExplorer.Constants;
import com.solsticesquared.schelling.SchellingExplorerUtils.Colors;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.util.gui.PropertyField;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Color;

/**
 * Represents a user interface for an agent-based Schelling segregation
 * simulation, providing the ability for a user to control and tweak various
 * parameters.
 */
public class SchellingExplorerWithUi extends GUIState {

    /**
     * The mechanism used to render each individual model grid cell to the user
     * interface.
     *
     * <p>
     *     In this case, each cell is rendered as a scaled rectangle with a
     *     color specified by the user interface's color map (dictated by the
     *     groups in the model).
     * </p>
     */
    private final FastValueGridPortrayal2D agentPortrayal;

    /** The user interface window that contains the statistics charts. */
    private final JFrame                    chartFrame;

    /**
     * The {@link JTabbedPane} that contains all the statistics charts, each
     * w ith its own tab.
     */
    private final JTabbedPane               chartPane;

    /** The rendering surface of the simulation space (grid). */
    private final Display2D                 display2d;

    /** Allows the user to select a color for visualizing empty cells. */
    private final ChooseColorButton         emptyColor;

    /** The collection of user interface widgets for the first group. */
    private final GroupOptions              firstGroup;

    /** The statistics chart that shows the interface density over time. */
    private final ChartDisplay              intensityChart;

    /** The collection of user interface widgets for the second group. */
    private final GroupOptions              secondGroup;

    /**
     * Allows the user to determine whether or not "unhappy" agents should
     * be shown in a different per-group color than "happy" ones.
     */
    private final PropertyField             showUnhappy;

    /** The map that associates model grid cell values with colors for rendering. */
    private final StateColorMap             stateColorMap;

    /** The user interface window that contains the simulation display. */
    private final JFrame                    simulFrame;

    /**
     * Allows the user to determine whether or not statistics for a
     * simulation should be tracked.
     */
    private final PropertyField             trackStatistics;

    /**
     * The statistics chart that shows the percentage of unhappy agents over
     * time.
     */
    private final ChartDisplay              unhappyChart;

    /**
     * Constructor.
     *
     * @param simState
     *        The simulation to provide user-interface access to.
     */
    public SchellingExplorerWithUi(final SimState simState) {
        super(simState);

        this.agentPortrayal = new FastValueGridPortrayal2D();
        this.chartFrame = new JFrame();
        this.chartPane = new JTabbedPane();
        this.display2d = new Display2D(500, 500, this);
        this.emptyColor = new ChooseColorButton(Colors.EmptySpace);
        this.firstGroup =
                new GroupOptions(((SchellingExplorer)simState).getGroup(0));
        this.intensityChart = new ChartDisplay();
        this.secondGroup =
                new GroupOptions(((SchellingExplorer)simState).getGroup(1));
        this.showUnhappy = new PropertyField(null, "true", true, null,
                                             PropertyField.SHOW_CHECKBOX);
        this.stateColorMap = new StateColorMap();
        this.simulFrame = this.display2d.createFrame();
        this.unhappyChart = new ChartDisplay();

        // Fix tooltips.
        this.emptyColor.setToolTipText("Choose a color for the empty cells.");
        this.showUnhappy.setToolTipText("Determine whether or not to show " +
                                        "\"unhappy\" agents as a different " +
                                        "color.");
        this.trackStatistics = new PropertyField(null, "true", true, null,
                                                 PropertyField.SHOW_CHECKBOX);
        this.trackStatistics.setToolTipText("Determine whether or not to " +
                                            "track statistics for a " +
                                            "simulation.  Uncheck this to " +
                                            "improve performance.");
    }

    /**
     * Returns the widget that allows the user to choose a color to visualize
     * empty cells, or cells that are not occupied by an agent.
     *
     * @return The widget that controls the color of empty cells.
     */
    public ChooseColorButton getEmptyColor() {
        return this.emptyColor;
    }

    /**
     * Returns a collection of user interface widgets used to configure a
     * group (the first one).
     *
     * @return A collection of user interface widgets.
     */
    public GroupOptions getFirstGroup() {
        return this.firstGroup;
    }

    /**
     * Returns a collection of user interface widgets used to configure a
     * group (the second one).
     *
     * @return A collection of user interface widgets.
     */
    public GroupOptions getSecondGroup() {
        return this.secondGroup;
    }

    /**
     * Returns the widget that allows the user to select whether or not
     * an "unhappy" agent should be visualized with a per-group color that is
     * different from a "happy" one.
     *
     * @return The widget that controls whether or not "unhappy" agents are
     * visualized differently.
     */
    public PropertyField getShowUnhappy() {
        return this.showUnhappy;
    }

    /**
     * Returns the {@link JFrame} that contains the visualization of a/the
     * simulation.
     *
     * @return The simulation frame.
     */
    public JFrame getSimulationFrame() {
        return this.simulFrame;
    }

    /**
     * Returns the widget that allows the user to determine whether or not
     * statistics (e.g. interface density, percentage of agents that are
     * "unhappy") should be tracked.
     *
     * <p>
     *     Disabling this will improve performance.
     * </p>
     *
     * @return The widget that controls whether or not to track statistics.
     */
    public PropertyField getTrackStatistics() {
        return this.trackStatistics;
    }

    @Override
    public Object getSimulationInspectedObject() {
        final SchellingExplorer model = (SchellingExplorer)this.state;
        return model.getParameters();
    }

    @Override
    public void init(final Controller controller) {
        if(controller == null) {
            throw new NullPointerException();
        }

        super.init(controller);

        // Set the title properly.
        this.simulFrame.setTitle("Schelling Explorer - Simulation");
        this.chartFrame.setTitle("Schelling Explorer - Statistics");

        // Bind the charts.
        this.intensityChart.init(this.chartPane, "Interface Density");
        this.unhappyChart.init(this.chartPane, "Unhappy Agents");

        // Bind the chart pane(s).
        this.chartFrame.add(this.chartPane);
        this.chartFrame.pack();

        // Set the metadata in-case the user views the charts before any data
        // is available.
        this.intensityChart.setMetaData("Interface Density over Time",
                                        "Time (steps)", "Interface Density");
        this.unhappyChart.setMetaData("Percent of Unhappy Agents over Time",
                                      "Time (steps)", " Percent (%) of " +
                                                      "Unhappy Agents");

        // Bind the controller.
        controller.registerFrame(this.simulFrame);
        controller.registerFrame(this.chartFrame);

        // Bind the portrayal.
        this.display2d.attach(this.agentPortrayal, "Grid Portrayal");

        // Make the user interface visible.
        this.simulFrame.setVisible(true);
    }

    @Override
    public void load(final SimState simState) {
        super.load(simState);
        this.setUpCharts();
        this.setUpColorMap();
        this.setUpPortrayal();
    }

    @Override
    public void quit() {
        super.quit();

        // Clean-up all user interface resources.
        //
        // Since this is the UI portion of the MASON library, we assume that
        // this method is called on the Event Dispatch Thread.
        if(this.simulFrame != null) {
            this.simulFrame.dispose();
        }

        if(this.chartFrame != null) {
            this.chartFrame.dispose();
        }
    }

    /**
     * Clears and (re)configures the charts used to display various
     * statistics to the user.
     */
    private void setUpCharts() {
        // First, clear both charts.
        this.intensityChart.clear();
        this.unhappyChart.clear();

        // Create the results cache.
        final double[] unhappyPercentage = new double[2];

        // Next, obtain the group information for labelling purposes.
        final Group firstGroup = ((SchellingExplorer)this.state).getGroup(0);
        final Group secondGroup = ((SchellingExplorer)this.state).getGroup(1);

        // Then, (re)set up the visualization of each chart.
        this.intensityChart.setLineColor(Color.red);
        this.intensityChart.setSeriesId("Interface Density");
        this.unhappyChart.setLineColor(0, firstGroup.getHappyColor());
        this.unhappyChart.setLineColor(1, secondGroup.getHappyColor());
        this.unhappyChart.setSeriesIds(firstGroup.getName(),
                                       secondGroup.getName());

        // Finally, rebind to the appropriate schedules if the user would
        // like these tracked.
        if(Boolean.parseBoolean(this.trackStatistics.getValue())) {
            this.intensityChart.start(this, (simState, chart) -> {
                final SchellingExplorer model = (SchellingExplorer)simState;
                final double interfaceDensity =
                        StatisticsUtils.computeInterfaceDensity(model);

                chart.addDataPoint(model.schedule.getSteps(), interfaceDensity);
            });

            this.unhappyChart.start(this, (simState, chart) -> {
                final SchellingExplorer model = (SchellingExplorer)simState;
                StatisticsUtils.computeUnhappyAgents(model, unhappyPercentage);

                chart.addDataPoint(0, model.schedule.getSteps(),
                                   unhappyPercentage[0]);
                chart.addDataPoint(1, model.schedule.getSteps(),
                                   unhappyPercentage[1]);
            });

            // Refresh as necessary.
            this.scheduleImmediatelyAfter(
                    simState -> {
                        if(this.chartFrame.isVisible()) {
                            this.chartFrame.repaint();
                        }
                    }
            );
        }
    }

    /**
     * Clears and then re-configures the color map to be able to render agents
     * from each group in the underlying model.
     */
    private void setUpColorMap() {
        // First, clear the map of current color associations.
        this.stateColorMap.clear();

        // First, bind the empty space color.
        this.stateColorMap.addMapping(Constants.EmptyCell,
                                      this.emptyColor.getCurrentColor());

        // Grab the model to work off of.
        final SchellingExplorer model = (SchellingExplorer)this.state;
        // Whether or not to show "unhappy" agents or not.
        final boolean showUnhappy =
                Boolean.parseBoolean(this.showUnhappy.getValue());

        // For each group in the model, add each group's happy and unhappy
        // state colors as necessary.
        for(final Group group : model.getGroupList()) {
            this.stateColorMap.addMapping(group, showUnhappy);
        }
    }

    /**
     * Updates the (two) groups of this simulation using user-specified values.
     */
    private void setUpGroups() {
        // Obtain the correct model.
        final SchellingExplorer model = (SchellingExplorer)this.state;

        // Obtain the groups explicitly.
        final Group group0 = model.getGroup(0);
        final Group group1 = model.getGroup(1);

        // Update.
        this.updateGroup(group0, this.firstGroup);
        this.updateGroup(group1, this.secondGroup);
    }

    /**
     * Configures how the user interface will render each individual model
     * cell and updates the display after doing so.
     */
    private void setUpPortrayal() {
        // Bind the color mappings and model grid space to the portrayal.
        this.agentPortrayal.setMap(this.stateColorMap);
        this.agentPortrayal.setField(((SchellingExplorer)this.state)
                                             .getSimulationSpace());

        // Redraw the agent display.
        this.display2d.reset();
        this.display2d.repaint();
    }

    @Override
    public void start() {
        super.start();
        this.setUpGroups();
        this.setUpCharts();
        this.setUpColorMap();
        this.setUpPortrayal();
    }

    /**
     * Updates the specified group's values (such as name, population
     * percent, etc.) with those from the specified user-selected options.
     *
     * @param group
     *        The group to update.
     * @param options
     *        The collection of user-specified values to use.
     * @throws NullPointerException
     *         If either {@code group} or {@code options} are {@code null}.
     */
    private void updateGroup(final Group group, final GroupOptions options) {
        if(group == null) {
            throw new NullPointerException();
        }

        if(options == null) {
            throw new NullPointerException();
        }

        group.setHappyColor(options.getHappyColor());
        group.setName(options.getName());
        group.setPopulationPercentage(options.getPopulationPercent());
        group.setTolerance(options.getTolerance());
        group.setUnhappyColor(options.getUnhappyColor());
    }
}
