package com.example.standtrain.controllers;

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

import static com.example.standtrain.util.Globals.buf1;
import static com.example.standtrain.util.Globals.buf2;
import static com.example.standtrain.util.Globals.buf3;
import static com.example.standtrain.util.Globals.buf4;



import java.util.concurrent.ArrayBlockingQueue;

public class ADCGraphs {

    @FXML private VBox graphVBox;

    @FXML private LineChart<Number, Number> chart1;
    @FXML private LineChart<Number, Number> chart2;
    @FXML private LineChart<Number, Number> chart3;
    @FXML private LineChart<Number, Number> chart4;

    @FXML private NumberAxis xAxis1;
    @FXML private NumberAxis xAxis2;
    @FXML private NumberAxis xAxis3;
    @FXML private NumberAxis xAxis4;

    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series4 = new XYChart.Series<>();

    private Timeline refresher;

    private static final int MAX_POINTS = 500;       // how many points to display
    private static final int REFRESH_MS = 100;       // UI refresh interval (ms)

    @FXML
    public void initialize() {
        // Chart setup (performance & appearance)
        setupChart(chart1, series1, xAxis1);
        setupChart(chart2, series2, xAxis2);
        setupChart(chart3, series3, xAxis3);
        setupChart(chart4, series4, xAxis4);

        // Start the refresher when the view is attached, stop when detached.
        // When MainController.loadView() replaces content, the node's parent becomes null -> we stop.
        graphVBox.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                stopRefresher();
            } else {
                startRefresher();
            }
        });

        // If already attached at initialize time, start immediately:
        if (graphVBox.getParent() != null) startRefresher();
    }

    private void setupChart(LineChart<Number, Number> chart, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().clear();
        chart.getData().add(series);

        // x axis we manage manually (0..N-1 window)
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(MAX_POINTS);
        xAxis.setTickUnit(Math.max(1, MAX_POINTS / 10));
        // y-axis auto-ranging is fine to keep
        // chart.getYAxis().setAutoRanging(true);
    }

    private void startRefresher() {
        if (refresher != null && refresher.getStatus() == Timeline.Status.RUNNING) return;

        refresher = new Timeline(new KeyFrame(Duration.millis(REFRESH_MS), e -> refreshAllCharts()));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    private void stopRefresher() {
        if (refresher != null) {
            refresher.stop();
            refresher = null;
        }
        // optionally clear charts:
        series1.getData().clear();
        series2.getData().clear();
        series3.getData().clear();
        series4.getData().clear();
    }

    private void refreshAllCharts() {
        updateChartFromQueue(buf1, series1, xAxis1);
        updateChartFromQueue(buf2, series2, xAxis2);
        updateChartFromQueue(buf3, series3, xAxis3);
        updateChartFromQueue(buf4, series4, xAxis4);
    }

    private void updateChartFromQueue(ArrayBlockingQueue<Double> queue, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        // snapshot the queue contents - non-destructive
        Double[] snapshot = queue.toArray(new Double[0]);
        int n = snapshot.length;
        if (n == 0) {
            series.getData().clear();
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(MAX_POINTS);
            return;
        }

        int start = Math.max(0, n - MAX_POINTS);
        ObservableList<XYChart.Data<Number, Number>> points = FXCollections.observableArrayList();
        for (int i = start; i < n; i++) {
            Double v = snapshot[i];
            double value = (v == null) ? 0.0 : v; // defensive
            points.add(new XYChart.Data<>(i - start, value));
        }

        // Replace all points atomically (safer than incremental updates for simplicity)
        series.getData().setAll(points);

        // update X axis range to match displayed window
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(1, n - start));
    }
}
