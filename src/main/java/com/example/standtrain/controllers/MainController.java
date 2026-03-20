package com.example.standtrain.controllers;

import com.example.standtrain.services.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.adcThread;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.logs;


public class MenuConfig {
    @FXML TextArea logBox;
    int status;

    public void addTextToScroll(String text) {
        try {
            Platform.runLater(() -> {
                logs.add(text);
                logBox.appendText(text + "\n");
            });
        }
        catch (Exception e){System.out.println(e.getMessage());}
    }

    @FXML
    public void initialize() {
        for (String log : logs) {
            logBox.appendText(log + "\n");
        }
    }

    public void e16initialize(){
        if (!handleE16initialized) {
            try {
                handleE16 = Initialization.createHandle();

                status = Initialization.openE16(handleE16);
                addTextToScroll("openE16: " + status);

                status = Initialization.setE16ChannelsCount(handleE16, 4);
                addTextToScroll("setE16ChannelsCount: " + status);

                status = Initialization.iniE16Channels(handleE16, 4);
                addTextToScroll("iniE16Channels: " + status);

                status = Initialization.X502_SetAdcFreq(handleE16);
                addTextToScroll("X502_SetAdcFreq: " + status);

                status = Initialization.put16Data(handleE16);
                addTextToScroll("put16Data: " + status);

                //status = Initialization.iniE16DigReg(handleE16);
                //addTextToScroll("iniE16DigReg: " + status);

                iniVoltageAndRelay();
                if (status==0){
                    handleE16initialized = true;
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void iniVoltageAndRelay(){
        status = DataInput.putV(2.5, handleE16);
        addTextToScroll("putV: " + status);
        status = Initialization.iniRelay(handleE16);
        addTextToScroll("iniRelay: " + status);

    }

    public void launchADCThread() {
        if (adcThread != null && adcThread.isAlive()) return;

        status = DataOutput.streamsEnable(handleE16);
        addTextToScroll("streamsEnable: " + status);
        status = DataOutput.streamsStart(handleE16);
        addTextToScroll("streamsStart: " + status);

        threadE16running = true;
        adcThread = DataOutput.startSynchroAcquisition(handleE16, 4);
    }

    public void stopADCThread() {
        if (adcThread == null) return;

        threadE16running = false;
        try {
            adcThread.join(); //waits until thread shutdown caused by threadE16running = false
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        status = DataOutput.streamsStop(handleE16);
        addTextToScroll("streamsStop: " + status);
        status = DataOutput.streamsDisable(handleE16);
        addTextToScroll("streamsDisable: " + status);
    }
}




