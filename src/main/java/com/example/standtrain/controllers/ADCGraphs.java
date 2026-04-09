package com.example.standtrain.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import java.util.concurrent.ArrayBlockingQueue;

import java.io.IOException;

import static com.example.standtrain.util.Globals.buf1;
import static com.example.standtrain.util.Globals.buf2;
import static com.example.standtrain.util.Globals.buf3;
import static com.example.standtrain.util.Globals.buf4;

/**
 Класс, реализующий переключение графиков вывода данных с АЦП
 */
public class ADCGraphs {

    @FXML private ComboBox<String> comboBox;
    @FXML private AnchorPane graphContainer;

    @FXML
    public void initialize() {
        comboBox.setItems(FXCollections.observableArrayList(
                "Вывод графиков с 4-ех каналов",
                "Канал 1",
                "Канал 2",
                "Канал 3",
                "Канал 4",
                "Общий график"
        ));

        loadGraph("/com/example/standtrain/views/AllGraphs.fxml", null, 0);
    }

    /* Метод перезагрузки view для переключения графиков */
    private void loadGraph(String fxmlPath, ArrayBlockingQueue<Double> buffer, int channelNum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SingleGraphController singleController) {
                singleController.setDataQueue(buffer, channelNum);
            }

            graphContainer.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML

    /* Метод переключения графиков */
    private void changeGraph() {
        String selected = comboBox.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Общий график" ->
                    loadGraph("/com/example/standtrain/views/GraphMain.fxml", null, -1);
            case "Канал 1" ->
                    loadGraph("/com/example/standtrain/views/SingleGraph.fxml", buf4, 1);
            case "Канал 2" ->
                    loadGraph("/com/example/standtrain/views/SingleGraph.fxml", buf3, 2);
            case "Канал 3" ->
                    loadGraph("/com/example/standtrain/views/SingleGraph.fxml", buf2, 3);
            case "Канал 4" ->
                    loadGraph("/com/example/standtrain/views/SingleGraph.fxml", buf1, 4);
            case "Вывод графиков с 4-ех каналов" ->
                    loadGraph("/com/example/standtrain/views/AllGraphs.fxml", null, -1);
        }
    }
}
