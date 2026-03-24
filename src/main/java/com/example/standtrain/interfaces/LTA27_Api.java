package com.example.standtrain.interfaces;

import com.example.standtrain.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

public interface LTA27_Api extends Library {
    LTA27_Api INSTANCE = Native.load("com/example/standtrain/lta27_api.dll", LTA27_Api.class);
    int LTA27_GetLibraryVersion();
    int LTA27_Init(PointerByReference dev_hnd, Pointer con_hnd, int lta_inflags_t, Pointer outflags);
    int LTA27_DeInit(Pointer lta27_hnd, int flags);

    int LTA27_AsyncRead(Pointer dev_hnd, int channel, IntByReference rawData);

    int LTA27_ProcessData(Pointer dev_hnd, int channel, double rawValue, DoubleByReference processedValue, IntByReference unitType);

    Pointer LTA27_GetErrorString(int errCode, int lang);
    int LTA27_SetConfig(Pointer dev_hnd, LTAConfigStruct config, int flags);

}
