package com.example.standtrain.controllers;

import com.example.standtrain.util.*;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static com.example.standtrain.util.Globals.buf1;
import static com.example.standtrain.util.Globals.buf2;
import static com.example.standtrain.util.Globals.buf3;
import static com.example.standtrain.util.Globals.buf4;
import static com.example.standtrain.util.Consts.REFRESH_MS;

public class CombinedGraphController extends BaseGraphController {

    @FXML private LineChart<Number, Number> chart;
    @FXML private NumberAxis xAxis;

    private final List<ArrayBlockingQueue<Double>> queues = List.of(buf1, buf2, buf3, buf4);

    protected List<LineChart<Number, Number>> getCharts() {
        return List.of(chart);
    }

    protected List<NumberAxis> getXAxes() {
        return List.of(xAxis);
    }

    protected List<ArrayBlockingQueue<Double>> getDataQueues() {
        return List.of(buf1);
    }

    @FXML
    public void initialize() {
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().clear();

        XYChart.Series<Number, Number> s1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> s2 = new XYChart.Series<>();
        XYChart.Series<Number, Number> s3 = new XYChart.Series<>();
        XYChart.Series<Number, Number> s4 = new XYChart.Series<>();

        chart.getData().addAll(s1, s2, s3, s4);

        s1.getNode().setStyle("-fx-stroke: #2F4F4F");
        s2.getNode().setStyle("-fx-stroke: #8FBC8F");
        s3.getNode().setStyle("-fx-stroke: #8B0000");
        s4.getNode().setStyle("-fx-stroke: #FF8C00");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Consts.MAX_POINTS);
        xAxis.setTickUnit(50);


        startRefresher();
    }

    private javafx.animation.Timeline refresher;


    private void startRefresher() {
        refresher = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(REFRESH_MS), e -> refreshAll())
        );
        refresher.setCycleCount(javafx.animation.Animation.INDEFINITE);
        refresher.play();
    }

    private void refreshAll() {
        @SuppressWarnings("unchecked")
        List<XYChart.Series<Number, Number>> seriesList = (List<XYChart.Series<Number, Number>>) (List<?>) chart.getData();

        for (int i = 0; i < queues.size(); i++) {
            ArrayBlockingQueue<Double> q = queues.get(i);
            Double[] snapshot = q.toArray(new Double[0]);
            int n = snapshot.length;
            int start = Math.max(0, n - Consts.MAX_POINTS);

            ObservableList<XYChart.Data<Number, Number>> points = FXCollections.observableArrayList();
            for (int j = start; j < n; j++) {
                Double val = snapshot[j];
                points.add(new XYChart.Data<>(j - start, val != null ? val : 0.0));
            }
            seriesList.get(i).getData().setAll(points);
        }
    }
}
