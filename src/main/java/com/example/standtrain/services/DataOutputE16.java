package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import static com.example.standtrain.util.Globals.threadE16running;
import static com.example.standtrain.util.Globals.buf1;
import static com.example.standtrain.util.Globals.buf2;
import static com.example.standtrain.util.Globals.buf3;
import static com.example.standtrain.util.Globals.buf4;

import java.util.*;
import java.util.concurrent.*;

public class DataOutputE16 {
    public static double[] asyncGetAdcFrame(Pointer handle, int flags, int timeoutMs, int lchCount) {
        long bytes = (long) lchCount * Native.getNativeSize(Double.TYPE); // 8 * count, allocate native buffer for lchCount doubles
        Memory buf = new Memory(bytes);

        int err = X502Api.INSTANCE.X502_AsyncGetAdcFrame(handle, flags, timeoutMs, buf);
        System.out.println("X502_AsyncGetAdcFrame: " + err);

        double[] out = new double[lchCount];
        for (int i = 0; i < lchCount; i++) {
            out[i] = buf.getDouble((long) i * Native.getNativeSize(Double.TYPE));
        }
        return out;
    }

    public static int streamsEnable(Pointer hnd){
        return X502Api.INSTANCE.X502_StreamsEnable(hnd, Consts.X502_STREAM_ADC);
    }

    public static int streamsDisable(Pointer hnd){
        return X502Api.INSTANCE.X502_StreamsDisable(hnd, Consts.X502_STREAM_ADC);
    }

    public static int streamsStart(Pointer hnd){
        return X502Api.INSTANCE.X502_StreamsStart(hnd);
    }
    public static int streamsStop(Pointer hnd){
        return X502Api.INSTANCE.X502_StreamsStop(hnd);
    }

    public static Thread startSynchroAcquisition(Pointer handle, int lchCount) {
        long recvBytes = (long) Consts.READ_BLOCK_SIZE * Native.getNativeSize(Integer.TYPE);
        final Memory recvBuf = new Memory(recvBytes);

        long adcBytes = (long) Consts.READ_BLOCK_SIZE * Native.getNativeSize(Double.TYPE);
        final Memory adcBuf = new Memory(adcBytes); // double[]
        long dinBytes = (long) Consts.READ_BLOCK_SIZE * Native.getNativeSize(Integer.TYPE);
        final Memory dinBuf = new Memory(dinBytes); // uint32_t[]

        final IntByReference adcSizeRef = new IntByReference(Consts.READ_BLOCK_SIZE);
        final IntByReference dinSizeRef = new IntByReference(Consts.READ_BLOCK_SIZE);
        final IntByReference firstLchRef = new IntByReference();
        final int[] sampleCounter = new int[lchCount];

        //everything inside lambda launches at t.start()
        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < lchCount; i++) {
                    sampleCounter[i] = 0;
                }

                while (threadE16running) {
                    int received = X502Api.INSTANCE.X502_Recv(handle, recvBuf, Consts.READ_BLOCK_SIZE, Consts.READ_TIMEOUT);
                    if (received < 0) {
                        int err = received;
                        System.err.println("Error at data receive : " + err);
                        break;
                    }
                    if (received == 0) {
                        break;
                    }

                    adcSizeRef.setValue(Consts.READ_BLOCK_SIZE);
                    dinSizeRef.setValue(Consts.READ_BLOCK_SIZE);

                    int err = X502Api.INSTANCE.X502_ProcessData(handle,
                            recvBuf,
                            received,
                            Consts.X502_PROC_FLAGS_VOLT,
                            adcBuf,
                            adcSizeRef,
                            dinBuf,
                            dinSizeRef);

                    if (err != 0) {
                        System.err.println("Error at data process : " + err);
                        continue;
                    }

                    int adcCount = adcSizeRef.getValue();

                    if (X502Api.INSTANCE.X502_GetNextExpectedLchNum(handle, firstLchRef) == 0) {
                    } else {
                        break;
                    }
                    int firstLch = firstLchRef.getValue();

                    //copy data from memory to java array (easier to work with but resource intensive)
                    double[] adc = new double[adcCount];
                    for (int i = 0; i < adcCount; i++) {
                        adc[i] = adcBuf.getDouble((long) i * Native.getNativeSize(Double.TYPE));
                    }

                   //block for division of data per channel from singular array
                    int capacityPerChannel = (adcCount + lchCount - 1) / lchCount;
                    double[][] channels = new double[lchCount][capacityPerChannel];
                    int[] idx = new int[lchCount];
                    for (int i = 1; i < adcCount; i++) {
                        int chZeroBased = ((firstLch - 1) + i) % lchCount;
                        channels[chZeroBased][idx[chZeroBased]++] = adc[i];
                    }

                     //cut to have actual data per channels (some math division problems cause it)
                    double[][] finalChannels = new double[lchCount][];
                    for (int ch = 0; ch < lchCount; ch++) {
                        finalChannels[ch] = Arrays.copyOf(channels[ch], idx[ch]);
                    }

                    for (int ch = 0; ch < lchCount && ch < 4; ch++) {
                        ArrayBlockingQueue<Double> target;
                        switch (ch) {
                            case 0 -> target = buf1;
                            case 1 -> target = buf2;
                            case 2 -> target = buf3;
                            case 3 -> target = buf4;
                            default -> target = null;
                        }

                        for (int j = 0; j < idx[ch]; j++) {
                            if ((sampleCounter[ch]++ % Consts.SKIP_ADC_DATA) != 0) continue;
                            if (!target.offer(channels[ch][j])) {
                                target.poll();
                                target.offer(channels[ch][j]);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true); //background-only thread
        t.start(); //start the thread
        return t;
    }
}
