package com.example.standtrain.services;

import com.example.standtrain.*;
import com.example.standtrain.interfaces.*;
import com.sun.jna.*;

public class LcardTest {
    public static void test() {
        int status;
        Pointer handle = Initialization.createHandle();

        try {
            status = Initialization.openE16(handle);
            System.out.println("openE16: " + status);

            status = Initialization.setE16ChannelsCount(handle, 4);
            System.out.println("setE16ChannelsCount: " + status);

            status = Initialization.iniE16Channels(handle, 4);
            System.out.println("iniE16Channels: " + status);
        }

        catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            X502Api.INSTANCE.X502_Close(handle);
            X502Api.INSTANCE.X502_Free(handle);
        }

    }
}