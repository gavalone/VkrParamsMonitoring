package com.example.standtrain.controllers;

import com.example.standtrain.services.*;

import static com.example.standtrain.util.Globals.handleE16;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.adcThread;
import static com.example.standtrain.util.Globals.handleE16initialized;


public class MenuConfig {
    int status;

    public void e16initialize(){
        if (!handleE16initialized) {
            try {
                handleE16 = Initialization.createHandle();

                status = Initialization.openE16(handleE16);
                System.out.println("openE16: " + status);

                status = Initialization.setE16ChannelsCount(handleE16, 4);
                System.out.println("setE16ChannelsCount: " + status);

                status = Initialization.iniE16Channels(handleE16, 4);
                System.out.println("iniE16Channels: " + status);

                status = Initialization.X502_SetAdcFreq(handleE16);
                System.out.println("X502_SetAdcFreq: " + status);

                status = Initialization.put16Data(handleE16);
                System.out.println("put16Data: " + status);

                //iniVoltageAndRelay();
                if (status==0)handleE16initialized = true;

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void iniVoltageAndRelay(){
        status = DataInput.putV(2.5, handleE16);
        System.out.println("putV: " + status);
        status = Initialization.iniRelay(handleE16);
        System.out.println("iniRelay: " + status);
    }

    public void launchADCThread() throws InterruptedException {
        if (adcThread != null && adcThread.isAlive()) return;

        int st = DataOutput.streamsEnable(handleE16);
        System.out.println("streamsEnable: " + st);
        st = DataOutput.streamsStart(handleE16);
        System.out.println("streamsStart: " + st);

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
        System.out.println("streamsStop: " + status);
        status = DataOutput.streamsDisable(handleE16);
        System.out.println("streamsDisable: " + status);
    }
}




