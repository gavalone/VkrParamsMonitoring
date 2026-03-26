package com.example.standtrain.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static com.example.standtrain.util.Globals.buf1;
import static com.example.standtrain.util.Globals.buf2;
import static com.example.standtrain.util.Globals.buf3;
import static com.example.standtrain.util.Globals.buf4;

public class FourGraphsController extends BaseGraphController {

    @FXML private LineChart<Number, Number> chart1;
    @FXML private LineChart<Number, Number> chart2;
    @FXML private LineChart<Number, Number> chart3;
    @FXML private LineChart<Number, Number> chart4;

    @FXML private NumberAxis xAxis1;
    @FXML private NumberAxis xAxis2;
    @FXML private NumberAxis xAxis3;
    @FXML private NumberAxis xAxis4;

    protected List<LineChart<Number, Number>> getCharts() {
        return List.of(chart1, chart2, chart3, chart4);
    }

    protected List<NumberAxis> getXAxes() {
        return List.of(xAxis1, xAxis2, xAxis3, xAxis4);
    }

    protected List<ArrayBlockingQueue<Double>> getDataQueues() {
        return List.of(buf1, buf2, buf3, buf4);
    }
}
