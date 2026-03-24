package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import static com.example.standtrain.util.Globals.handleLTADevice;


public class DataOutputLTA {
    public static int readChannel(Pointer handle, int regAddr) {
        IntByReference val = new IntByReference();
        int status = LTA_Api.INSTANCE.LTA_ReadReg(handle, regAddr, val);
        if (status == 0) {
            int value = val.getValue();
            System.out.printf(String.valueOf(value));
        } else {
            System.err.println(status);
        }
        return status;
    }

    public static void readLTA(int channel) {
        IntByReference raw = new IntByReference();
        int status = LTA27_Api.INSTANCE.LTA27_AsyncRead(handleLTADevice, channel, raw);

        System.out.println("read status: " + status);

//        double rawVal = raw.getValue();
//
//        DoubleByReference processed = new DoubleByReference();
//        IntByReference unit = new IntByReference();
//
//        status = LTA27_Api.INSTANCE.LTA27_ProcessData(handleLTADevice, channel, rawVal, processed, unit);
//
//        System.out.println(status);
//
//        if (status == 0) {
//            double value = processed.getValue();
//            int unitCode = unit.getValue();
//            System.out.printf("Channel %d: %.6f (%d)\n", channel, value, unitCode);
//        }


    }


}
