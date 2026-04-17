package com.example.standtrain.interfaces;

import com.example.standtrain.util.structs.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 * Интерфейс для объяления методов для устройства LTA27 (логика работы)
 */
public interface LTA27_Api extends Library {
    LTA27_Api INSTANCE = Native.load("lta_api.dll", LTA27_Api.class);
    int LTA27_Init(Pointer con_hnd, int lta_inflags_t, Pointer outflags);
    int LTA27_DeInit(Pointer lta27_hnd, int flags);
    int LTA27_SetConfig(Pointer dev_hnd, LTAConfigStruct lta27_config_t , int flags);
    int LTA_Open(PointerByReference hnd, LTADevRecStruct lta_dev_rec_t, int api_version, int timeout, int in_flags);
    int LTA_Close(PointerByReference hnd, int in_flags);
    int LTA27_AsyncInAdc(Pointer hnd, int ch_num, DoubleByReference data, int in_flags);
}
