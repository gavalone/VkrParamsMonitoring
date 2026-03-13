package com.example.standtrain.controllers;

import com.example.standtrain.services.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.curDirection;
import static com.example.standtrain.util.Globals.lastVoltage;

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
        speedLabel.setText(String.format("Voltage: %.2f V", lastVoltage));

        // Update speed in real-time as slider moves
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double sliderVal = newVal.doubleValue();
            double voltage = 2.5 + sliderVal * 0.025;        // map to 2.5..5V
            speedLabel.setText(String.format("Voltage: %.2f V", voltage));
            changeSpeed(voltage);
            lastVoltage = voltage;                   // save to globals
        });

        // Toggle direction
        directionButton.setOnAction(e -> changeDirection());
    }

    int status;
    public void changeSpeed(double v){
        status = DataInput.putV(v, handleE16);
        System.out.println("putV: " + status);
    }

    public void changeDirection(){
        if (curDirection){
            status = DataInput.putDI0(0x0001, handleE16);
            System.out.println("putDI0: " + status);
        }
        else {
            status = DataInput.putDI0(0x0000, handleE16);
            System.out.println("putDI0: " + status);
        }
        curDirection = !curDirection;
    }
}
