package com.example.standtrain.services;

import com.example.standtrain.*;
import com.example.standtrain.interfaces.*;
import com.sun.jna.*;

public class Initialization {
    static Pointer createHandle(){
        return X502Api.INSTANCE.X502_Create();
    }

    static int openE16(Pointer handle){
        return E502Api.INSTANCE.E16_OpenByIpAddr(handle, createIp(192,168,1,10), 0, 200);
    }

    static int setE16ChannelsCount(Pointer handle, int count){
        return X502Api.INSTANCE.X502_SetLChannelCount(handle, count);
    }

    static int iniE16Channels(Pointer handle, int count){
        for (int i = 0; i < count; i++) {
            int err = X502Api.INSTANCE.X502_SetLChannel(handle, i, i, Consts.X502_LCH_MODE_DIFF, Consts.X502_ADC_RANGE_02, 0);
            if (err != 0 ) return err;
        }
        return 0;
    }

    public static int createIp(int o1, int o2, int o3, int o4) {
        return (o1 << 24) | (o2 << 16) | (o3 << 8) | o4;
    }

}
