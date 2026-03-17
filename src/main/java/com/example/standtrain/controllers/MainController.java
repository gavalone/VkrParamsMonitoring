package com.example.standtrain.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;

import java.io.*;

public class MainController {
    @FXML
    public StackPane contentArea;
    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(){
        loadView("/com/example/standtrain/views/MenuConfig.fxml");
    }
    public void showADCGraphs()  {
        loadView("/com/example/standtrain/views/ADCGraphs.fxml");
    }
    public void showTrainControl()  {
        loadView("/com/example/standtrain/views/TrainControl.fxml");
    }
    public void showMenuConfig() {
        loadView("/com/example/standtrain/views/MenuConfig.fxml");
    }
}
