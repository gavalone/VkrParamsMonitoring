package com.example.standtrain.util;

import com.sun.jna.*;

import java.util.*;
import java.util.concurrent.*;

public class Globals {
    public static List<String> logs = new ArrayList<>();
    public static Pointer handleE16;
    public static Pointer handleLTACon;
    public static Pointer handleLTADevice;

    public static boolean handleE16initialized = false;
    public static boolean handleLTAinitialized = false;

    public static boolean curDirection = false;
    public static boolean threadE16running = false;
    public static Thread adcThread = null;
    public static Thread ltaThread = null;
    public static boolean threadLTArunning = false;

    public static double lastVoltage = 2.5;

    public static final ArrayBlockingQueue<Double> buf1 = new ArrayBlockingQueue<>(Consts.E16_BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf2 = new ArrayBlockingQueue<>(Consts.E16_BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf3 = new ArrayBlockingQueue<>(Consts.E16_BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf4 = new ArrayBlockingQueue<>(Consts.E16_BUFFER_CAPACITY);


    public static final int LTA_BUFFERS_CAPACITY = 500;
    public static final ArrayBlockingQueue<Double> voltageBuf = new ArrayBlockingQueue<>(LTA_BUFFERS_CAPACITY);
    public static final ArrayBlockingQueue<Double> amperageBuf = new ArrayBlockingQueue<>(LTA_BUFFERS_CAPACITY);
    public static final ArrayBlockingQueue<Double> resistanceBuf = new ArrayBlockingQueue<>(LTA_BUFFERS_CAPACITY);

}
