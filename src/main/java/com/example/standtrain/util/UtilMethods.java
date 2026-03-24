package com.example.standtrain.util;

import java.io.*;
import java.util.*;

public class UtilMethods {
    public static int createIp(int o1, int o2, int o3, int o4) {
        return (o1 << 24) | (o2 << 16) | (o3 << 8) | o4;
    }

    public static void loadConfig() {
        Properties properties = new Properties();
        File file = new File("config.properties");

        try {
            if (file.exists()) {
                // Load existing config
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

            } else {
                // File doesn't exist, create it with default values
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

                FileOutputStream fos = new FileOutputStream(file);
                properties.store(fos, "Configuration");
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
