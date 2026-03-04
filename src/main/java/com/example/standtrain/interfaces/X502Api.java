package com.example.standtrain.interfaces;

import com.example.standtrain.*;
import com.sun.jna.*;


//IntByReference for pointers


public interface X502Api extends Library {
    X502Api INSTANCE = Native.load("C:\\Program Files (x86)\\L-Card\\L502-E502-E16-SDK\\bin\\x64\\x502api.dll", X502Api.class);

    int X502_GetLibraryVersion();
    Pointer X502_Create();
    int X502_Close(Pointer hdl);
    int X502_Free(Pointer hdl);
    int X502_SetLChannelCount(Pointer hdl, int lch_cnt);
    int X502_SetLChannel(Pointer hnd, int lch, int phy_ch, int mode, int range, int avg);
}