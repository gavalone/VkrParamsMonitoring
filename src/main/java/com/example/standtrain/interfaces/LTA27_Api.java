package com.example.standtrain.interfaces;

import com.example.standtrain.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 * Интерфейс для объяления методов для устройства LTA27 (логика работы)
 */
public interface LTA27_Api extends Library {
    LTA27_Api INSTANCE = Native.load("lta27_api.dll", LTA27_Api.class);
    int LTA27_Init(PointerByReference dev_hnd, Pointer con_hnd, int lta_inflags_t, Pointer outflags);
    int LTA27_DeInit(Pointer lta27_hnd, int flags);

    int LTA27_AsyncRead(Pointer dev_hnd, int channel, IntByReference rawData);

    int LTA27_ProcessData(Pointer dev_hnd, int ch_num, IntByReference src_data, DoubleByReference dst_data, IntByReference size, int in_flags);
    int LTA27_SetConfig(Pointer dev_hnd, LTAConfigStruct lta27_config_t , int flags);
}
