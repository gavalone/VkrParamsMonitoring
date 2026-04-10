package com.example.standtrain.controllers;

import com.example.standtrain.services.*;
import com.example.standtrain.util.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.adcThread;
import static com.example.standtrain.util.Globals.handleE16initialized;
import static com.example.standtrain.util.Globals.logs;
import static com.example.standtrain.util.Globals.handleLTAinitialized;
import static com.example.standtrain.util.Globals.ltaThread;
import static com.example.standtrain.util.Globals.threadLTArunning;


/**
 Класс контроллера основной страницы с запуском потоков и инициализацией устройств
 */
public class MenuConfig {
    @FXML TextArea logBox;
    int status;

/* Пополнение списка логов */
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

    /* Инициализация модуля Е16 */
    public void e16initialize(){
        try {
            if (!handleE16initialized) {
                handleE16 = InitializationE16.createHandle();

                status = InitializationE16.openE16(handleE16);
                addTextToScroll("openE16: " + status);

                status = InitializationE16.setE16ChannelsCount(handleE16, 4);
                addTextToScroll("setE16ChannelsCount: " + status);

                status = InitializationE16.iniE16Channels(handleE16, 4);
                addTextToScroll("iniE16Channels: " + status);

                status = InitializationE16.X502_SetAdcFreq(handleE16);
                addTextToScroll("X502_SetAdcFreq: " + status);

                status = InitializationE16.put16Data(handleE16);
                addTextToScroll("put16Data: " + status);

                iniVoltageAndRelay();
                if (status==0){
                    handleE16initialized = true;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* Инициализация устройства LTA */
    public void LTAInititialize(){
        if (!handleLTAinitialized){
            try {
                status = InitializationLTA.openLTAByIp();
                addTextToScroll("openLTA: " + status);

                status = InitializationLTA.initLTA();
                addTextToScroll("iniLTA: " + status);

                status = InitializationLTA.enableChannels();
                addTextToScroll("ini channels: " + status);

                if (status == 0){
                    handleLTAinitialized = true;
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /* Запуск потока обработки данных с LTA */
    public void launchLTAThread(){
        if (handleLTAinitialized) {
            if (ltaThread != null && ltaThread.isAlive()) return;
            addTextToScroll("LTA thread started");
            threadLTArunning = true;
            ltaThread = DataOutputLTA.startAsynchroAcquisitionLTA();
        }
    }

    /* Остановка потока обработки с LTA */
    public void closeLTAThread(){
        if (handleLTAinitialized) {
            if (ltaThread == null) return;
            threadLTArunning = false;
            try {
                ltaThread.join(3000);
                addTextToScroll("LTA thread stopped");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Подача напряжения на рельсы и включение реле */
    public void iniVoltageAndRelay(){
        status = DataInputE16.putV(2.5, handleE16);
        addTextToScroll("putV: " + status);
        status = InitializationE16.iniRelay(handleE16);
        addTextToScroll("iniRelay: " + status);
    }

    /* Запуск потока обработки данных с E16 */
    public void launchADCThread() {
        if (adcThread != null && adcThread.isAlive()) return;
        status = DataOutputE16.streamsEnable(handleE16);
        addTextToScroll("streamsEnable: " + status);
        status = DataOutputE16.streamsStart(handleE16);
        addTextToScroll("streamsStart: " + status);
        threadE16running = true;
        adcThread = DataOutputE16.startSynchroAcquisition(handleE16, Consts.chNum);
    }

    /* Остановка потока обработки с Е16*/
    public void stopADCThread() {
        if (adcThread == null) return;

        threadE16running = false;
        try {
            adcThread.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        status = DataOutputE16.streamsStop(handleE16);
        addTextToScroll("streamsStop: " + status);
        status = DataOutputE16.streamsDisable(handleE16);
        addTextToScroll("streamsDisable: " + status);
    }
}




