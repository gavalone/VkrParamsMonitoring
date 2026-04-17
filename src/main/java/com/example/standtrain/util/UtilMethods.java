package com.example.standtrain.util;

import java.io.*;
import java.util.*;

public class UtilMethods {
    public static int createIp(int o1, int o2, int o3, int o4) {
        return (o1 << 24) | (o2 << 16) | (o3 << 8) | o4;
    }
    public static byte convertDrToEnum(int drValue) {
        return switch (drValue) {
            case 32 -> Consts.LTA27_A27_DR_32;
            case 64 -> Consts.LTA27_A27_DR_64;
            case 128 -> Consts.LTA27_A27_DR_128;
            case 256 -> Consts.LTA27_A27_DR_256;
            default -> throw new IllegalArgumentException("Unsupported decimation rate: " + drValue);
        };
    }
    public static void loadConfig() {
        Properties properties = new Properties();
        File file = new File("config.properties");

        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();

                Config.ipE16_1 = Integer.parseInt(properties.getProperty("ipE16_1"));
                Config.ipE16_2 = Integer.parseInt(properties.getProperty("ipE16_2"));
                Config.ipE16_3 = Integer.parseInt(properties.getProperty("ipE16_3"));
                Config.ipE16_4 = Integer.parseInt(properties.getProperty("ipE16_4"));
                Config.LTA_name = properties.getProperty("LTA_name");
                Config.LTA_serial = properties.getProperty("LTA_serial");
                Config.LTA_slot =(byte)Short.parseShort(properties.getProperty("LTA_slot"));
                Config.LTA_cmdPort = Short.parseShort(properties.getProperty("LTA_cmdPort"));
                Config.ipLTA_1 = Integer.parseInt(properties.getProperty("ipLTA_1"));
                Config.ipLTA_2 = Integer.parseInt(properties.getProperty("ipLTA_2"));
                Config.ipLTA_3 = Integer.parseInt(properties.getProperty("ipLTA_3"));
                Config.ipLTA_4 = Integer.parseInt(properties.getProperty("ipLTA_4"));
                Config.chart_bond = Double.parseDouble(properties.getProperty("chart_bond"));
                Config.amperage_bound = Double.parseDouble(properties.getProperty("amperage_bound"));
                Config.voltage_bound = Double.parseDouble(properties.getProperty("voltage_bound"));

            } else {
                //file doesn't exist, create it with default values
                properties.setProperty("ipE16_1", String.valueOf(192));
                properties.setProperty("ipE16_2", String.valueOf(168));
                properties.setProperty("ipE16_3", String.valueOf(1));
                properties.setProperty("ipE16_4", String.valueOf(10));
                properties.setProperty("LTA_name", "LTA27");
                properties.setProperty("LTA_serial", "");
                properties.setProperty("LTA_slot", String.valueOf(255));
                properties.setProperty("LTA_cmdPort", String.valueOf(11114));
                properties.setProperty("ipLTA_1", String.valueOf(192));
                properties.setProperty("ipLTA_2", String.valueOf(168));
                properties.setProperty("ipLTA_3", String.valueOf(1));
                properties.setProperty("ipLTA_4", String.valueOf(20));
                properties.setProperty("chart_bond", String.valueOf(0.0001));
                properties.setProperty("amperage_bound", String.valueOf(15.0));
                properties.setProperty("voltage_bound", String.valueOf(10.0));

                FileOutputStream fos = new FileOutputStream(file);
                properties.store(fos, "Configuration");
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final static double[] tempList = new double[]{
            -200,-190,-180,-170,-160,-150,-140,-130,-120,-110,
            -100,-90,-80,-70,-60,-50,-40,-30,-20,-10,
            0,10,20,30,40,50,60,70,80,90,
            100,110,120,130,140,150,160,170,180,190,200
    };

    final static double[] resistanceList = new double[]{
            6.085, 8.14, 10.29, 12.57, 14.84, 17.105, 19.355, 21.595, 23.83, 26.05,
            28.265, 30.505, 32.695, 34.875, 37.055, 39.225, 41.39, 43.55, 45.705, 47.855,
            50.0, 52.14, 54.28, 56.415, 58.555, 60.695, 62.835, 64.97, 67.11, 69.25,
            71.39, 73.525, 75.665, 77.805, 79.945, 82.08, 84.22, 86.36, 88.5, 90.635, 92.775
    };

    public static double resistanceToTemperature(double resistance) {
        if (resistance <= resistanceList[0]) {
            return tempList[0];
        }
        if (resistance >= resistanceList[resistanceList.length - 1]) {
            return tempList[tempList.length - 1];
        }

        for (int i = 0; i < resistanceList.length - 1; i++) {
            if (resistance >= resistanceList[i] && resistance <= resistanceList[i + 1]) {
                double t1 = tempList[i];
                double t2 = tempList[i + 1];
                double r1 = resistanceList[i]+3.5;
                double r2 = resistanceList[i + 1]+3.5;
                return t1 + (resistance - r1) * (t2 - t1) / (r2 - r1);
            }
        }
        return 0;
    }
}
