package com.example.standtrain.controllers;

import com.example.standtrain.services.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.curDirection;
import static com.example.standtrain.util.Globals.lastVoltage;
import static com.example.standtrain.util.Globals.logs;
import static com.example.standtrain.util.Globals.handleE16initialized;

public class TrainMovement {
    @FXML
    private Slider speedSlider;
    @FXML private Label speedLabel;
    @FXML private Button directionButton;

    @FXML
    public void initialize() {
        // Initialize slider to last saved voltage
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
}
