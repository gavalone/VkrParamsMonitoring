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

public class ADCGraphs {

    @FXML private ComboBox<String> comboBox;
    @FXML private AnchorPane graphContainer;

    @FXML
    public void initialize() {
        comboBox.setItems(FXCollections.observableArrayList(
                "Главный график",
                "Вывод 1",
                "Вывод 2",
                "Вывод 3",
                "Вывод 4",
                "Вывод четырех графиков"
        ));

        loadGraph("/com/example/standtrain/views/GraphMain.fxml", buf1);
    }

    private void loadGraph(String fxmlPath, ArrayBlockingQueue<Double> buffer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SingleGraphController singleController) {
                singleController.setDataQueue(buffer);
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
    private void changeGraph() {
        String selected = comboBox.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Главный график" ->
                    loadGraph("/com/example/standtrain/views/GraphMain.fxml", buf1);
            case "Вывод 1" ->
                    loadGraph("/com/example/standtrain/views/Graph1.fxml", buf1);
            case "Вывод 2" ->
                    loadGraph("/com/example/standtrain/views/Graph2.fxml", buf2);
            case "Вывод 3" ->
                    loadGraph("/com/example/standtrain/views/Graph3.fxml", buf3);
            case "Вывод 4" ->
                    loadGraph("/com/example/standtrain/views/Graph4.fxml", buf4);
            case "Вывод четырех графиков" ->
                    loadGraph("/com/example/standtrain/views/AllGraphs.fxml", null);
        }
    }
}
