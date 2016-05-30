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

import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.SchellingExplorer.TaskOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
    private final XYSeriesCollection        chartData;

    /** The rendering utility for the chart. */
    private final XYLineAndShapeRenderer    chartRenderer;

    /** The name, or id, of the primary series of the chart. */
    private String[]                        seriesIds;

    /**
     * Constructor.
     */
    public ChartDisplay() {

        this.chart = new TimeSeriesChartGenerator();
        this.chartData = new XYSeriesCollection();
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
        this.addDataPoint(0, timeValue, dataValue, true);
    }

    /**
     * Adds the specified data value associated with the specified time value
     * to the specified series shown in this chart and notifies the user
     * interface components after doing so.
     *
     * @param series
     *        The index of the series to modify.
     * @param timeValue
     *        The time value to use.
     * @param dataValue
     *        The data value to use.
     */
    public void addDataPoint(final int series, final double timeValue,
                             final double dataValue) {
        this.addDataPoint(series, timeValue, dataValue, true);
    }

    /**
     * Adds the specified data value associated with the specified time value
     * to the specified series shown in this chart and notifies the user
     * interface components after doing so if specified.
     *
     * @param series
     *        The index of the series to modify.
     * @param timeValue
     *        The time value to use.
     * @param dataValue
     *        The data value to use.
     * @param notify
     *        Whether or not to notify the user interface of chart updates.
     * @throws IllegalStateException
     *         If {@code series} is less than zero or greater than or equal
     *         to the total number of series contained by this chart.
     */
    public void addDataPoint(final int series, final double timeValue,
                             final double dataValue, final boolean notify) {
        if(series < 0 || series >= this.chartData.getSeriesCount()) {
            throw new IndexOutOfBoundsException("series index: " + series + "" +
                                                " - is out of bounds!");
        }

        final XYSeries xySeries = this.chartData.getSeries(series);
        xySeries.add(timeValue, dataValue, notify);
    }

    /**
     * Removes all series associated with this chart and clears all
     * visualization customizations stored in the renderer as well.
     */
    public void clear() {
        this.chartData.removeAllSeries();
        this.chartRenderer.clearSeriesPaints(false);
        this.chartRenderer.clearSeriesStrokes(false);
    }

    /**
     * (Re)Creates the series shown in the specified chart.
     *
     * @param chart
     *        The chart to create the series for.
     * @throws NullPointerException
     *         If {@code chart} is {@code null}.
     */
    private void createSeries(final TimeSeriesChartGenerator chart,
                              final int reserveSize) {
        if(chart == null) {
            throw new NullPointerException();
        }

        if(reserveSize < 0) {
            throw new IllegalArgumentException("the number of elements to " +
                                               "reserve must be positive!");
        }

        // Clear the chart completely.
        chart.removeAllSeries();

        // (Re)Create each requested series using the ids.
        for(int i = 0; i < this.seriesIds.length; i++) {
            final XYSeries xySeries =
                    new FixedSizeXYSeries(this.seriesIds[i], false, true,
                                          reserveSize);
            this.chartData.addSeries(xySeries);

            // Fix in the renderer.
            // Lines only.
            this.chartRenderer.setSeriesLinesVisible(i, true);
            this.chartRenderer.setSeriesShapesVisible(i, false);
            this.chartRenderer.setSeriesVisible(i, true);
        }
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
     * Sets the line color to use for the specified series shown in this chart.
     *
     * @param series
     *        The series to set the stroke for.
     * @param color
     *        The color to use.
     * @throws IllegalArgumentException
     *         If {@code series} is less than zero.
     * @throws NullPointerException
     *         If {@code color} is {@code null}.
     */
    public void setLineColor(final int series, final Color color) {
        if(color == null) {
            throw new NullPointerException();
        }

        if(series < 0) {
            throw new IllegalArgumentException("series id must be positive!");
        }

        this.chartRenderer.setSeriesPaint(series, color);
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
     *     This corresponds to the name shown on the legend.
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

        this.seriesIds = new String[]{seriesId};
    }

    /**
     * Sets the name, or id, of the variable number of series shown with this
     * chart to the specified ids.
     *
     * <p>
     *     This corresponds to the name(s) shown on the legend for each series.
     * </p>
     *
     * @param seriesIds
     *        The variable-length list of ids to use.
     * @throws NullPointerException
     *         If {@code seriesIds} is {@code null}.
     */
    public void setSeriesIds(final String ... seriesIds) {
        if(seriesIds == null) {
            throw new NullPointerException();
        }

        this.seriesIds = seriesIds;
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
     * Sets the stroke to use for the specified series shown in this chart.
     *
     * @param series
     *        The series to set the stroke for.
     * @param stroke
     *        The stroke to use.
     * @throws IllegalArgumentException
     *         If {@code series} is less than zero.
     * @throws NullPointerException
     *         If {@code stroke} is {@code null}.
     */
    public void setSeriesStroke(final int series, final Stroke stroke) {
        if(stroke == null) {
            throw new NullPointerException();
        }

        if(series < 0) {
            throw new IllegalArgumentException("series id must be positive!");
        }

        this.chartRenderer.setSeriesStroke(series, stroke);
    }

    /**
     * Configures the (underlying) chart and renderer.
     */
    private void setUpUi() {
        // Bind the renderer to the chart.
        final XYPlot plot = this.chart.getChart().getXYPlot();
        plot.setDataset(this.chartData);
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

        // Determine how many elements to reserve.
        final int reserveSize =
                ((SchellingExplorer)guiState.state).getParameters()
                                                   .getMaximumSteps();

        // (Re)Create the chart data.
        this.createSeries(this.chart, reserveSize);

        // (Re)Register for updates on the model thread.
        guiState.state.schedule.scheduleRepeating(0.0d, TaskOrder.Charting,
                simState -> chartUpdater.updateChart(simState, this)
        );
    }
}
