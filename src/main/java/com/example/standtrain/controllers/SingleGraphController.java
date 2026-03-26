package com.example.standtrain.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class SingleGraphController extends BaseGraphController {

    @FXML private LineChart<Number, Number> chart;
    @FXML private NumberAxis xAxis;

    private ArrayBlockingQueue<Double> dataQueue;

    public void setDataQueue(ArrayBlockingQueue<Double> queue) {
        dataQueue = queue;
    }

    protected List<LineChart<Number, Number>> getCharts() {
        return List.of(chart);
    }

    protected List<NumberAxis> getXAxes() {
        return List.of(xAxis);
    }

    protected List<ArrayBlockingQueue<Double>> getDataQueues() {
        return List.of(dataQueue);
    }
}
