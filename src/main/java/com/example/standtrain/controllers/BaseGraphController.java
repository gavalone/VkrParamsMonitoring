package com.example.standtrain.controllers;

import com.example.standtrain.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class BaseGraphController {

    private Timeline refresher;

    @FXML private VBox graphVBox;

    protected abstract List<LineChart<Number, Number>> getCharts();
    protected abstract List<NumberAxis> getXAxes();
    protected abstract List<ArrayBlockingQueue<Double>> getDataQueues();

    @FXML
    public void initialize() {
        List<LineChart<Number, Number>> charts = getCharts();
        List<NumberAxis> xAxes = getXAxes();
        for (int i = 0; i < charts.size(); i++) {
            setupChart(charts.get(i), xAxes.get(i));
        }

        graphVBox.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                stopRefresher();
            } else {
                startRefresher();
            }
        });

        if (graphVBox.getParent() != null) {
            startRefresher();
        }
    }

    private void setupChart(LineChart<Number, Number> chart, NumberAxis xAxis) {
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().clear();
        chart.getData().add(new XYChart.Series<>());

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Consts.MAX_POINTS);
        xAxis.setTickUnit(Math.max(1, Consts.MAX_POINTS / 10));
    }

    private void startRefresher() {
        refresher = new Timeline(new KeyFrame(Duration.millis(Consts.REFRESH_MS), e -> refreshCharts()));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    private void stopRefresher() {
        if (refresher != null) {
            refresher.stop();
            refresher = null;
        }
        for (LineChart<Number, Number> chart : getCharts()) {
            chart.getData().forEach(series -> series.getData().clear());
        }
    }

    private void refreshCharts() {
        List<LineChart<Number, Number>> charts = getCharts();
        List<NumberAxis> xAxes = getXAxes();
        List<ArrayBlockingQueue<Double>> queues = getDataQueues();

        for (int i = 0; i < charts.size(); i++) {
            updateChartFromQueue(queues.get(i), charts.get(i), xAxes.get(i));
        }
    }

    private void updateChartFromQueue(ArrayBlockingQueue<Double> queue, LineChart<Number, Number> chart, NumberAxis xAxis) {
        Double[] snapshot = queue.toArray(new Double[0]);
        int n = snapshot.length;
        if (n == 0) {
            chart.getData().get(0).getData().clear();
            return;
        }

        int start = Math.max(0, n - Consts.MAX_POINTS);
        ObservableList<XYChart.Data<Number, Number>> points = FXCollections.observableArrayList();
        for (int i = start; i < n; i++) {
            points.add(new XYChart.Data<>(i - start, snapshot[i] != null ? snapshot[i] : 0.0));
        }

        chart.getData().get(0).getData().setAll(points);
        xAxis.setUpperBound(points.size());
    }
}
