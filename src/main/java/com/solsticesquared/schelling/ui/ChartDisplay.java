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

import com.solsticesquared.schelling.SchellingExplorer.TaskOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import sim.display.GUIState;
import sim.util.media.chart.TimeSeriesChartGenerator;

import javax.swing.JTabbedPane;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Represents a utility class to help compress all of the statistics tracking
 * and display into one object.
 */
public class ChartDisplay {

    /** The chart, or graph, itself. */
    private final TimeSeriesChartGenerator  chart;

    /** The data for the single series on the chart. */
    private XYSeries                        chartData;

    /** The rendering utility for the chart. */
    private final XYLineAndShapeRenderer    chartRenderer;

    /** The name, or id, of the primary series of the chart. */
    private String                          seriesId;

    /**
     * Constructor.
     */
    public ChartDisplay() {

        this.chart = new TimeSeriesChartGenerator();
        this.chartRenderer = new XYLineAndShapeRenderer();

        this.setUpUi();
    }

    /**
     * Adds the specified data value associated with the specified time value
     * to this chart and notifies the user interface components after doing so.
     *
     * @param timeValue
     *        The time value to use.
     * @param dataValue
     *        The data value to use.
     */
    public void addDataPoint(final double timeValue, final double dataValue) {
        this.addDataPoint(timeValue, dataValue, true);
    }

    /**
     * Adds the specified data value associated with the specified time value
     * to this chart and notifies the user interface components after doing
     * so if specified.
     *
     * @param timeValue
     *        The time value to use.
     * @param dataValue
     *        The data value to use.
     * @param notify
     *        Whether or not to notify the user interface of chart updates.
     */
    public void addDataPoint(final double timeValue, final double dataValue,
                             final boolean notify) {
        if(this.chartData == null) {
            throw new IllegalStateException("the chart has not been " +
                                            "initialized correctly!");
        }

        this.chartData.add(timeValue, dataValue, notify);
    }

    /**
     * Removes all series associated with this chart.
     */
    public void clear() {
        this.chart.clearAllSeries();
    }

    /**
     * (Re)Creates the series shown in the specified chart.
     *
     * @param chart
     *        The chart to create the series for.
     * @return A new series for a chart.
     * @throws NullPointerException
     *         If {@code chart} is {@code null}.
     */
    private XYSeries createSeries(final TimeSeriesChartGenerator chart) {
        if(chart == null) {
            throw new NullPointerException();
        }

        // Clear the chart completely.
        chart.removeAllSeries();

        // (Re)Create the series.
        // If the series name or identifier does not exist, use the title.
        final String uniqueId = this.seriesId != null ?
                                this.seriesId : chart.getTitle();
        final XYSeries xySeries = new XYSeries(uniqueId, false);

        chart.addSeries(xySeries, null);
        return xySeries;
    }

    /**
     * Initializes the display of this chart by adding it to the specified
     * {@link JTabbedPane}.
     *
     * @param tabbedPane
     *        The pane to add the chart to.
     * @param tabTitle
     *        The title to use for the pane.
     * @throws NullPointerException
     *         If either {@code tabbedPane} or {@code tabTitle} are {@code
     *         null}.
     */
    public void init(final JTabbedPane tabbedPane, final String tabTitle) {
        if(tabbedPane == null) {
            throw new NullPointerException();
        }

        if(tabTitle == null) {
            throw new NullPointerException();
        }

        tabbedPane.add(tabTitle, this.chart.getChartPanel());
    }

    /**
     * Sets the line color to use for the series shown in this chart.
     *
     * @param color
     *        The line color to use.
     * @throws NullPointerException
     *         If {@code color} is {@code null}.
     */
    public void setLineColor(final Color color) {
        if(color == null) {
            throw new NullPointerException();
        }

        this.chartRenderer.setSeriesPaint(0, color);
    }

    /**
     * Sets the metadata of this chart - the title, the x-axis label, and the
     * y-axis label - to the specified values.
     *
     * @param title
     *        The title to use.
     * @param xLabel
     *        The x-axis label to use.
     * @param yLabel
     *        The y-axis label to use.
     * @throws NullPointerException
     *         If {@code title}, {@code xLabel}, or {@code yLabel} are {@code
     *         null}.
     */
    public void setMetaData(final String title, final String xLabel,
                            final String yLabel) {
        if(title == null) {
            throw new NullPointerException();
        }

        if(xLabel == null) {
            throw new NullPointerException();
        }

        if(yLabel == null) {
            throw new NullPointerException();
        }

        this.chart.setTitle(title);
        this.chart.setXAxisLabel(xLabel);
        this.chart.setYAxisLabel(yLabel);
    }

    /**
     * Sets the name, or id, of the series shown with this chart to the
     * specified id.
     *
     * <p>
     *     Since there is no legend to display (only one series per chart,
     *     currently) this is of purely syntactic value only.
     * </p>
     *
     * @param seriesId
     *        The id to use.
     * @throws NullPointerException
     *         If {@code seriesId} is {@code null}.
     */
    public void setSeriesId(final String seriesId) {
        if(seriesId == null) {
            throw new NullPointerException();
        }

        this.seriesId = seriesId;
    }

    /**
     * Sets the stroke to use for the series shown in this chart.
     *
     * @param stroke
     *        The stroke to use.
     * @throws NullPointerException
     *         If {@code stroke} is {@code null}.
     */
    public void setStroke(final Stroke stroke) {
        if(stroke == null) {
            throw new NullPointerException();
        }

        this.chartRenderer.setSeriesStroke(0, stroke);
    }

    /**
     * Configures the (underlying) chart and renderer.
     */
    private void setUpUi() {
        // Lines only.
        this.chartRenderer.setSeriesLinesVisible(0, true);
        this.chartRenderer.setSeriesShapesVisible(0, false);
        this.chartRenderer.setSeriesVisible(0, true);

        // Bind the renderer to the chart.
        final XYPlot plot = this.chart.getChart().getXYPlot();
        plot.setRenderer(this.chartRenderer);
    }

    /**
     * (Re)Creates the series shown in this chart and schedules it for
     * periodic updating using arbitrary logic.
     *
     * @param guiState
     *        The user interface portion of a simulation to use.
     * @param chartUpdater
     *        The chart update logic to use.
     * @throws NullPointerException
     *         If either {@code guiState} or {@code chartUpdater} are {@code
     *         null}.
     */
    public void start(final GUIState guiState, final ChartUpdater chartUpdater) {
        if(guiState == null) {
            throw new NullPointerException();
        }

        if(chartUpdater == null) {
            throw new NullPointerException();
        }

        // (Re)Create the chart data.
        this.chartData = this.createSeries(this.chart);

        // (Re)Register for updates on the model thread.
        guiState.state.schedule.scheduleRepeating(0.0d, TaskOrder.Charting,
                simState -> chartUpdater.updateChart(simState, this)
        );
    }
}
