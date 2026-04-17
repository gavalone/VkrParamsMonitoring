package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.example.standtrain.util.structs.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

import java.nio.charset.*;

import static com.example.standtrain.util.Globals.handleLTADevice;

public class InitializationLTA {
    public static int openLTAByIp() {
        PointerByReference handleRef = new PointerByReference();
        handleRef.setValue(Pointer.createConstant(0));
        int status = LTA27_Api.INSTANCE.LTA_Open(
                handleRef,
                iniLtaDevRecStruct(),
                1, //api version
                1000,                //const for lta timeout
                0 //in_flags
        );

        if (status == 0) {
            handleLTADevice = handleRef.getValue();
        }
        return status;
    }

    public static LTADevRecStruct iniLtaDevRecStruct(){
        LTADevRecStruct ltaDevRecStruct = new LTADevRecStruct();

        byte[] src0 = Config.LTA_name.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(src0, 0, ltaDevRecStruct.devname, 0, Math.min(src0.length, ltaDevRecStruct.devname.length));

        byte[] src1 = Config.LTA_serial.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(src1, 0, ltaDevRecStruct.serial_dev, 0, Math.min(src1.length, ltaDevRecStruct.serial_dev.length));

        byte[] src2 = Config.LTA_serial.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(src2, 0, ltaDevRecStruct.serial_crate, 0, Math.min(src2.length, ltaDevRecStruct.serial_crate.length));


        ltaDevRecStruct.slot = Config.LTA_slot;
        ltaDevRecStruct.ip_addr = UtilMethods.createIp(Config.ipLTA_1, Config.ipLTA_2, Config.ipLTA_3, Config.ipLTA_4);
        ltaDevRecStruct.cmd_port = Config.LTA_cmdPort;
        return ltaDevRecStruct;
    }

    public static int initLTA(){
        return LTA27_Api.INSTANCE.LTA27_Init(handleLTADevice, 0, null);
    }

    public static int enableChannels() {
        LTAConfigStruct config = new LTAConfigStruct();
        config.a27_adc_dr = UtilMethods.convertDrToEnum(32);

        for (int i = 0; i < Consts.LTA27_CH_QTY; i++){
            config.dir[i] = 0;
        }

        for (int i = 0; i < Consts.LTA27_CH_QTY; i++){
            config.enable[i] = 0;
        }

        config.enable[0] = 1;
        config.enable[2] = 1;
        config.enable[4] = 1;

        return LTA27_Api.INSTANCE.LTA27_SetConfig(handleLTADevice, config, 0);
    }
}
