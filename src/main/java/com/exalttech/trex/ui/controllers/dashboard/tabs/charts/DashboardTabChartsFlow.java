package com.exalttech.trex.ui.controllers.dashboard.tabs.charts;

import javafx.beans.property.IntegerProperty;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exalttech.trex.ui.models.stats.flow.StatsFlowStream;
import com.exalttech.trex.ui.views.statistics.StatsLoader;
import com.exalttech.trex.util.ArrayHistory;


public abstract class DashboardTabChartsFlow extends DashboardTabChartsLine {
    public DashboardTabChartsFlow(IntegerProperty interval) {
        super(interval);
        getYAxis().setLabel(getName());
    }

    public void update(Set<Integer> visiblePorts, Set<String> visibleStreams) {
        getChart().getData().clear();

        if (visibleStreams != null && visibleStreams.isEmpty()) {
            return;
        }

        StatsLoader statsLoader = StatsLoader.getInstance();
        Map<String, ArrayHistory<StatsFlowStream>> streams = statsLoader.getFlowStatsHistoryMap();
        double time = statsLoader.getFlowStatsLastTime();
        List<XYChart.Series<Number, Number>> seriesList = new LinkedList<>();
        streams.forEach((String stream, ArrayHistory<StatsFlowStream> history) -> {
            if (visibleStreams != null && !visibleStreams.contains(stream)) {
                return;
            }

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            history.forEach((StatsFlowStream point) -> {
                series.getData().add(new XYChart.Data<>(point.getTime() - time, calcValue(visiblePorts, point)));
            });

            seriesList.add(series);
        });
        getChart().getData().addAll(seriesList);
    }

    protected abstract String getName();

    protected abstract Number calcValue(Set<Integer> visiblePorts, StatsFlowStream point);
}
