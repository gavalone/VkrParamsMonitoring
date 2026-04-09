package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

import java.util.concurrent.*;

import static com.example.standtrain.util.Globals.threadLTArunning;
import static com.example.standtrain.util.Globals.handleLTADevice;
import static com.example.standtrain.util.Globals.voltageBuf;
import static com.example.standtrain.util.Globals.amperageBuf;
import static com.example.standtrain.util.Globals.resistanceBuf;

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

    public static Thread startAsynchroAcquisitionLTA() {
        Thread t = new Thread(() -> {
            IntByReference src_data = new IntByReference();
            DoubleByReference dst_data = new DoubleByReference();
            IntByReference size = new IntByReference(1);
            int[] channels = {0, 2, 4};
            //int in_flags = Consts.LTA27_PD_FLAGS_CALIBR | Consts.LTA27_PD_FLAGS_VALUE; (оно должно быть от структуры поэтому убрал)
            int in_flags = 0x03; //physical value
            int status;

            try {
                while (threadLTArunning) {
                    Thread.sleep(100);
                    for (int channel : channels) {
                        status = LTA27_Api.INSTANCE.LTA27_AsyncRead(handleLTADevice, channel, src_data);
                        if (status != 0) continue;
                        status = LTA27_Api.INSTANCE.LTA27_ProcessData(handleLTADevice, channel, src_data, dst_data, size, in_flags);
                        if (status != 0) continue;

                        ArrayBlockingQueue<Double> target = switch (channel) {
                            case 0 -> voltageBuf;
                            case 2 -> amperageBuf;
                            case 4 -> resistanceBuf;
                            default -> null;
                        };
                        if (!target.offer(dst_data.getValue())) {
                            target.poll();
                            target.offer(dst_data.getValue());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }
}
