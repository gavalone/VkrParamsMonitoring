package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;
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
        int status = LTA27_Api.INSTANCE.LTA27_Init(devHnd, handleLTACon, 0x00000001, null);
        if (status == 0) {
            handleLTADevice = devHnd.getValue();
        }
        return status;
    }


    public static int enableChannels() {
        LTAConfigStruct config = new LTAConfigStruct();
        config.ch_enable_mask = (1 << 1) | (1 << 3) | (1 << 5);
        return LTA27_Api.INSTANCE.LTA27_SetConfig(handleLTADevice, config, 0);
    }



//    public static final int FPGA_REG_SM_MASK = 0x2000;
//    public static final int FPGA_REG_SM_ADDR_CLKEN = FPGA_REG_SM_MASK | 0x0002; // 0x0202
//    public static final int FPGA_REG_SM_ADDR_PWR   = FPGA_REG_SM_MASK | 0x0003; // 0x0203
//    public static final int FPGA_CLK_EN_FCLK_ALL_ON_MASK = 0x0000FF00;
//    public static final int FPGA_PWM_SHUNT_ON_MASK = 0x03;

//    public static void enableFpgaModules() {
//        int rez1 = LTA_Api.INSTANCE.LTA_WriteReg(handleLTACon, FPGA_REG_SM_ADDR_CLKEN, FPGA_CLK_EN_FCLK_ALL_ON_MASK);
//        int rez2 = LTA_Api.INSTANCE.LTA_WriteReg(handleLTACon, FPGA_REG_SM_ADDR_PWR, FPGA_PWM_SHUNT_ON_MASK);
//        System.out.println(rez1);
//        System.out.println(rez2);
//    }

}
