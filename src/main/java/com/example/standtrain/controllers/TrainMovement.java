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
import java.util.concurrent.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.curDirection;
import static com.example.standtrain.util.Globals.lastVoltage;
import static com.example.standtrain.util.Globals.logs;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.voltageBuf;
import static com.example.standtrain.util.Globals.amperageBuf;
import static com.example.standtrain.util.Globals.resistanceBuf;

/**
 Класс для страницы управления стендом
 */
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

    @FXML private HBox chartsHBox;

    @FXML Label tempLabel;

    @FXML
    public void initialize() {
        setupChart(chart1, series1, xAxis1);
        setupChart(chart2, series2, xAxis2);
        setupChart(chart3, series3, xAxis3);

        NumberAxis yAxis1 = (NumberAxis) chart1.getYAxis();
        yAxis1.setAutoRanging(false);
        yAxis1.setLowerBound(-Config.voltage_bound);
        yAxis1.setUpperBound(Config.voltage_bound);
        yAxis1.setForceZeroInRange(true);

        NumberAxis yAxis2 = (NumberAxis) chart2.getYAxis();
        yAxis2.setAutoRanging(false);
        yAxis2.setLowerBound(-Config.amperage_bound);
        yAxis2.setUpperBound(Config.amperage_bound);
        yAxis2.setForceZeroInRange(true);

        NumberAxis yAxis3 = (NumberAxis) chart3.getYAxis();
        yAxis3.setAutoRanging(false);
        yAxis3.setLowerBound(0);
        yAxis3.setUpperBound(Config.voltage_bound*Config.amperage_bound*0.5);
        yAxis3.setForceZeroInRange(true);

        // Инициализация "рычага" к последнему сохраненному напряжению
        double sliderValue = (lastVoltage - 2.5) / 0.025; // элемент по образу (пропорция)
        speedSlider.setValue(sliderValue);
        speedLabel.setText(String.format("Напряжение: %.2f V", lastVoltage));

        // Управление скоростью в режиме реального времени
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double sliderVal = newVal.doubleValue();
            double voltage = 2.5 + sliderVal * 0.025;        // конвертация к напряжению от 2.5 до 5В
            speedLabel.setText(String.format("Напряжение: %.2f V", voltage));
            changeSpeed(voltage);
            lastVoltage = voltage;                   // сохранение в глобальные переменные
        });

        // Переключение полярности реле (направление стендового поезда)
        directionButton.setOnAction(e -> changeDirection());

        chartsHBox.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                stopRefresher();
            } else {
                startRefresher();
            }
        });

        if (chartsHBox.getParent() != null) {
            startRefresher();
        }

        chartsHBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null && newScene == null) {
                stopRefresher();
            }
        });
    }

    int status;
    public void changeSpeed(double v){
        if (handleE16initialized) {
            status = DataInputE16.putV(v, handleE16);
            logs.add("putV: " + status);
        }
    }

/* Изменение направления движения стендового поезда */
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

/* Инициализация параметров графиков */
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

        // chart.getYAxis().setAutoRanging(true);
    }

/* Запуск автообновления для динамической перерисовки графиков */
    private void startRefresher() {
        if (refresher != null && refresher.getStatus() == Timeline.Status.RUNNING) return;

        refresher = new Timeline(new KeyFrame(Duration.millis(Consts.REFRESH_MS), e -> refreshAllCharts()));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

/* Остановление автообновления графика */
    private void stopRefresher() {
        if (refresher != null) {
            refresher.stop();
            refresher = null;
        }
        // очистка чартов
        series1.getData().clear();
        series2.getData().clear();
        series3.getData().clear();
    }

    private int tempUpdateCounter = 49;

    /* Перерисовка графиков */
    private void refreshAllCharts() {
        updateVoltageChartFromQueue(voltageBuf, series1, xAxis1);
        updateChartFromQueue(amperageBuf, series2, xAxis2);
        updateMultiplicationChart(voltageBuf, amperageBuf, series3, xAxis3);

        tempUpdateCounter++;
        if (tempUpdateCounter >= 50) {
            tempUpdateCounter = 0;
            try {
                tempLabel.setText(String.valueOf((int)
                        UtilMethods.resistanceToTemperature(resistanceBuf.peek())));
            } catch (Exception e) {}
        }
    }

/* Отображение графика напряжения из очереди */
    private void updateVoltageChartFromQueue(ArrayBlockingQueue<Double> queue, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        // приведение очереди к типу данных, с которыми работает JavaFX + копирование данных, чтобы не блокировать очередь
        Double[] snapshot = queue.toArray(new Double[0]);
        for (int i = 0; i < snapshot.length; i++) {
            double value = snapshot[i];
            if (value>10||value<-10){
                Double[] temp = amperageBuf.toArray(new Double[0]);
                if (temp[temp.length-1] >=0){
                    snapshot[i] = -10.0;
                }
                else {snapshot[i] = 10.0;}
            }
        }

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

        // Обновление всех точек массивом для безопасности
        series.getData().setAll(points);

        // адаптация оси Х под данные
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(1, n - start));
    }

/* Отображение графика силы тока из очереди */
    private void updateChartFromQueue(ArrayBlockingQueue<Double> queue, XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        // приведение очереди к типу данных, с которыми работает JavaFX + копирование данных, чтобы не блокировать очередь
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
            double value = (v == null) ? 0.0 : v;
            points.add(new XYChart.Data<>(i - start, value));
        }

        series.getData().setAll(points);

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(1, n - start));
    }

/* Отображение графика мощности из очереди */
    private void updateMultiplicationChart(ArrayBlockingQueue<Double> q1, ArrayBlockingQueue<Double> q2,
                                           XYChart.Series<Number, Number> series, NumberAxis xAxis) {
        Double[] s1 = q1.toArray(new Double[0]);
        for (int i = 0; i < s1.length; i++) {
            double value = s1[i];
            if (value>10||value<-10){
                s1[i] = 10.0;
            }
        }

        Double[] s2 = q2.toArray(new Double[0]);
        int n = Math.min(s1.length, s2.length);
        if (n == 0) {
            series.getData().clear();
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(25);
            return;
        }

        int start = Math.max(0, n - 25);
        ObservableList<XYChart.Data<Number, Number>> points = FXCollections.observableArrayList();
        for (int i = start; i < n; i++) {
            double v1 = (s1[i] == null) ? 0.0 : s1[i];
            double v2 = (s2[i] == null) ? 0.0 : s2[i];
            points.add(new XYChart.Data<>(i - start, Math.abs(v1 * v2)));
        }

        series.getData().setAll(points);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(1, n - start));
    }
}
