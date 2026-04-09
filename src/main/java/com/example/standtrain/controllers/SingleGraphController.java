package com.example.standtrain.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import javafx.scene.control.Label;

/**
 Дочерний класс для отображения отдельных графиков на всю страницу
 */
public class SingleGraphController extends BaseGraphController {

    @FXML private LineChart<Number, Number> chart;
    @FXML private NumberAxis xAxis;
    @FXML private Label titleLabel;

    private ArrayBlockingQueue<Double> dataQueue;

/* В зависимости от выбранного канала выводится соответствующий буфер, название и цвет */
    public void setDataQueue(ArrayBlockingQueue<Double> queue, int channelNum) {
        dataQueue = queue;
        titleLabel.setText("Канал " + channelNum);
        setLineColor(channelNum);
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

    private void setLineColor(int channelNum) {
        switch (channelNum) {
            case 1 -> chart.getData().get(0).getNode().setStyle("-fx-stroke: #FF8C00; -fx-stroke-width: 4px");
            case 2 -> chart.getData().get(0).getNode().setStyle("-fx-stroke: #8B0000; -fx-stroke-width: 4px");
            case 3 -> chart.getData().get(0).getNode().setStyle("-fx-stroke: #8FBC8F; -fx-stroke-width: 4px");
            case 4 -> chart.getData().get(0).getNode().setStyle("-fx-stroke: #2F4F4F; -fx-stroke-width: 4px");
        }

//        Platform.runLater(() -> {
//            if (!chart.getData().isEmpty() && chart.getData().get(0).getNode() != null) {
//                chart.getData().get(0).getNode().setStyle("-fx-stroke: " + color + ";");
//            } else {
//                Platform.runLater(() -> {
//                    if (!chart.getData().isEmpty() && chart.getData().get(0).getNode() != null) {
//                        chart.getData().get(0).getNode().setStyle("-fx-stroke: " + color + ";");
//                    }
//                });
//            }
//        });
    }
}
