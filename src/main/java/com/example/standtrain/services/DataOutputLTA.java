package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.sun.jna.ptr.*;

import java.util.concurrent.*;

import static com.example.standtrain.util.Globals.threadLTArunning;
import static com.example.standtrain.util.Globals.handleLTADevice;
import static com.example.standtrain.util.Globals.voltageBuf;
import static com.example.standtrain.util.Globals.amperageBuf;
import static com.example.standtrain.util.Globals.resistanceBuf;

/**
 * Класс сервиса, описывающий метод асинхронного сбора данных с устройства LTA
 */
public class DataOutputLTA {
    public static Thread startAsynchroAcquisitionLTA() {
        Thread t = new Thread(() -> {
            DoubleByReference dst_data = new DoubleByReference();
            int[] channels = {0, 2, 4};
            int in_flags = 0x03; // флаг говорит о том, что мы берем физические величины
            int status;

            try {
                while (threadLTArunning) {
                    Thread.sleep(100);
                    for (int channel : channels) {
                        status = LTA27_Api.INSTANCE.LTA27_AsyncInAdc(handleLTADevice, channel, dst_data, in_flags);
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
