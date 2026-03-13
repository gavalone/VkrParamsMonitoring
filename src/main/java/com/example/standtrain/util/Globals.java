package com.example.standtrain.util;

import com.sun.jna.*;

import java.util.concurrent.*;

public class Globals {
    public static Pointer handleE16;
    public static boolean handleE16initialized = false;
    public static boolean curDirection = false;
    public static boolean threadE16running = false;
    public static Thread adcThread = null;

    public static double lastVoltage = 2.5;

    public static final int BUFFER_CAPACITY = 1000;
    public static final ArrayBlockingQueue<Double> buf1 = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf2 = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf3 = new ArrayBlockingQueue<>(BUFFER_CAPACITY);
    public static final ArrayBlockingQueue<Double> buf4 = new ArrayBlockingQueue<>(BUFFER_CAPACITY);

}
