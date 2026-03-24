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

public class DataOutput {
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



    //private static final int READ_BLOCK_SIZE = 0xC6000;
    private static final int READ_BLOCK_SIZE = 500;
    private static final int READ_TIMEOUT = 100;  // ms


    public static Thread startSynchroAcquisition(Pointer handle, int lchCount) {
        long recvBytes = (long) READ_BLOCK_SIZE * Native.getNativeSize(Integer.TYPE);
        final Memory recvBuf = new Memory(recvBytes);

        long adcBytes = (long) READ_BLOCK_SIZE * Native.getNativeSize(Double.TYPE);
        final Memory adcBuf = new Memory(adcBytes); // double[]
        long dinBytes = (long) READ_BLOCK_SIZE * Native.getNativeSize(Integer.TYPE);
        final Memory dinBuf = new Memory(dinBytes); // uint32_t[]

        final IntByReference adcSizeRef = new IntByReference(READ_BLOCK_SIZE);
        final IntByReference dinSizeRef = new IntByReference(READ_BLOCK_SIZE);
        final IntByReference firstLchRef = new IntByReference();

        //everything inside lambda launches at t.start()
        Thread t = new Thread(() -> {
            try {
                while (threadE16running) {
                    Thread.sleep(100);
                    int received = X502Api.INSTANCE.X502_Recv(handle, recvBuf, READ_BLOCK_SIZE, READ_TIMEOUT);
                    if (received < 0) {
                        int err = received;
                        System.err.println("Error at data receive : " + err);
                        break;
                    }
                    if (received == 0) {
                        break;
                    }


                    adcSizeRef.setValue(READ_BLOCK_SIZE);
                    dinSizeRef.setValue(READ_BLOCK_SIZE);

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

                    // In order to find at which channel we are currently starting
                    if (X502Api.INSTANCE.X502_GetNextExpectedLchNum(handle, firstLchRef) == 0) {
                    } else {
                        firstLchRef.setValue(1); // fallback
                    }
                    int firstLch = firstLchRef.getValue();


                    // Copy data from memory to java array (easier to work with but resource intensive)
                    double[] adc = new double[adcCount];
                    for (int i = 0; i < adcCount; i++) {
                        adc[i] = adcBuf.getDouble((long) i * Native.getNativeSize(Double.TYPE));
                    }


                    //This block for division of data per channel from singular array
                    int capacityPerChannel = (adcCount + lchCount - 1) / lchCount;
                    double[][] channels = new double[lchCount][capacityPerChannel];
                    int[] idx = new int[lchCount];
                    for (int i = 1; i < adcCount; i++) {
                        int chZeroBased = ((firstLch - 1) + i) % lchCount;
                        channels[chZeroBased][idx[chZeroBased]++] = adc[i];
                    }

                     //Cut to have actual data per channels (some math division problems cause it)
                    double[][] finalChannels = new double[lchCount][];
                    for (int ch = 0; ch < lchCount; ch++) {
                        finalChannels[ch] = Arrays.copyOf(channels[ch], idx[ch]);
                    }


                    int sampleCounter = 0;
                    for (int ch = 0; ch < lchCount && ch < 4; ch++) {
                        for (int j = 0; j < idx[ch]; j++) {
                            if ((sampleCounter++ % Consts.SKIP_ADC_DATA) != 0) continue; // keep only every SKIP_ADC_DATA-th

                            ArrayBlockingQueue<Double> target;
                            switch (ch) {
                                case 0 -> target = buf1;
                                case 1 -> target = buf2;
                                case 2 -> target = buf3;
                                case 3 -> target = buf4;
                                default -> target = null;
                            }
                            if (!target.offer(channels[ch][j])) {
                                // overwrite oldest if full
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
        return t; //return object in order to call Thread.isAlive() method
    }


}
