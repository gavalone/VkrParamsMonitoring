package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.ptr.*;
import static com.example.standtrain.util.Globals.handleLTACon;
import static com.example.standtrain.util.Globals.handleLTADevice;

public class InitializationLTA {
    public static int openLTAByIp() {
        PointerByReference handleRef = new PointerByReference();
        int status = LTA_Api.INSTANCE.LTA_Open(
                handleRef,
                null,        // dev_recs not needed for static IP connection
                0,                  // dev_cnt
                Config.LTA_name,
                Config.LTA_serial,
                Config.LTA_slot,
                UtilMethods.createIp(Config.ipLTA_1, Config.ipLTA_2, Config.ipLTA_3, Config.ipLTA_4),
                Config.LTA_cmdPort,
                1000                //const for lta timeout
        );

        if (status == 0) {
            handleLTACon = handleRef.getValue();
        }
        return status;
    }

    public static int initLTA(){
        PointerByReference devHnd = new PointerByReference();
        int status = LTA27_Api.INSTANCE.LTA27_Init(devHnd, handleLTACon, 0, null);
        if (status == 0) {
            handleLTADevice = devHnd.getValue();
        }
        return status;
    }

    public static int enableChannels() {
        LTAConfigStruct config = new LTAConfigStruct();
        for (int i = 0; i < Consts.LTA27_MEZ_QTY; i++) {
            config.series[i] = Consts.LTA27_MEZ_SER_A;
        }

        for (int i = 0; i < Consts.LTA27_CH_QTY; i++){
            config.filter_dr[i] = UtilMethods.convertDrToEnum(32);
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
