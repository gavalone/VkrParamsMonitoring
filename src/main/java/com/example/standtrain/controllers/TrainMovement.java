package com.example.standtrain.controllers;

import com.example.standtrain.services.*;
import com.example.standtrain.util.*;
import javafx.animation.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;

import java.util.*;
import java.util.concurrent.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.curDirection;
import static com.example.standtrain.util.Globals.lastVoltage;
import static com.example.standtrain.util.Globals.logs;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.voltageBuf;
import static com.example.standtrain.util.Globals.amperageBuf;
import static com.example.standtrain.util.Globals.resistanceBuf;

public class TrainMovement {
    @FXML
    private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private Button directionButton;

    @FXML private LineChart<Number, Number> chart1;
    @FXML private LineChart<Number, Number> chart2;
    @FXML private LineChart<Number, Number> chart3;


    @FXML private NumberAxis xAxis1;
    @FXML private NumberAxis xAxis2;
    @FXML private NumberAxis xAxis3;

    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();


    @FXML private StackPane chartsPanel;



    @FXML
    public void initialize() {
        setupChart(chart1, series1, xAxis1);
        setupChart(chart2, series2, xAxis2);
        setupChart(chart3, series3, xAxis3);


        double sliderValue = (lastVoltage - 2.5) / 0.025; // inverse mapping
        speedSlider.setValue(sliderValue);
        speedLabel.setText(String.format("Напряжение: %.2f V", lastVoltage));

        // Update speed in real-time as slider moves
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double sliderVal = newVal.doubleValue();
            double voltage = 2.5 + sliderVal * 0.025;        // map to 2.5..5V
            speedLabel.setText(String.format("Напряжение: %.2f V", voltage));
            changeSpeed(voltage);
            lastVoltage = voltage;                   // save to globals
        });

        // Toggle direction
        directionButton.setOnAction(e -> changeDirection());


        chartsPanel.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                stopRefresher();
            } else {
                startRefresher();
            }
        });

        if (chartsPanel.getParent() != null) {
            startRefresher();
        }

    }

    int status;
    public void changeSpeed(double v){
        if (handleE16initialized) {
            status = DataInputE16.putV(v, handleE16);
            logs.add("putV: " + status);
        }
    }

    public void changeDirection() {
        if (handleE16initialized) {
            if (curDirection) {
                status = DataInputE16.putDI0(1, handleE16, 0);
                logs.add("putDI0 up: " + status);
            } else {
                status = DataInputE16.putDI0(0, handleE16, 0);
                logs.add("putDI0 down: " + status);
            }
            curDirection = !curDirection;
        }
    }


    private Timeline refresher;

    private void setupChart(LineChart<Number, Number> chart, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().clear();
        chart.getData().add(series);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Consts.LTAChartsSize);
        xAxis.setTickUnit(Math.max(1, Consts.LTAChartsSize / 10));
        // y-axis auto-ranging is fine to keep
        // chart.getYAxis().setAutoRanging(true);
    }

    private void startRefresher() {
        if (refresher != null && refresher.getStatus() == Timeline.Status.RUNNING) return;

        refresher = new Timeline(new KeyFrame(Duration.millis(Consts.REFRESH_MS), e -> refreshAllCharts()));
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
    }

    private void refreshAllCharts() {
        updateChartFromQueue(voltageBuf, series1, xAxis1);
        updateChartFromQueue(amperageBuf, series2, xAxis2);
        //updateChartFromQueue(, series3, xAxis3);
    }

    private void updateChartFromQueue(ArrayBlockingQueue<Double> queue, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        // snapshot the queue contents - non-destructive
        Double[] snapshot = queue.toArray(new Double[0]);
        int n = snapshot.length;
        if (n == 0) {
            series.getData().clear();
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(25);
            return;
        }

        int start = Math.max(0, n - 25);
        ObservableList<XYChart.Data<Number, Number>> points = FXCollections.observableArrayList();
        for (int i = start; i < n; i++) {
            Double v = snapshot[i];
            double value = (v == null) ? 0.0 : v; // defensive
            points.add(new XYChart.Data<>(i - start, value));
        }

        // replace all points atomically (safer than incremental updates for simplicity)
        series.getData().setAll(points);

        // update x axis range to match displayed window
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(1, n - start));
    }






}
