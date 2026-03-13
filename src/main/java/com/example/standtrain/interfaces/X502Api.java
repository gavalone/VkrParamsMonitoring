package com.example.standtrain.interfaces;

import com.example.standtrain.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;


//IntByReference for pointers


public interface X502Api extends Library {
    X502Api INSTANCE = Native.load("C:\\Program Files (x86)\\L-Card\\L502-E502-E16-SDK\\bin\\x64\\x502api.dll", X502Api.class);

    int X502_GetLibraryVersion();
    Pointer X502_Create();
    int X502_Close(Pointer hdl);
    int X502_Free(Pointer hdl);
    int X502_SetLChannelCount(Pointer hnd, int lch_cnt);
    int X502_SetLChannel(Pointer hnd, int lch, int phy_ch, int mode, int range, int avg);
    int X502_Configure(Pointer hnd, int flags);
    int X502_AsyncGetAdcFrame(Pointer hnd, int flags, int tout, Pointer data);
    int X502_SetDigInPullup(Pointer hnd, int pullups);    //подаем на реле +-15в
    int X502_AsyncOutDac(Pointer hnd, int ch, double data, int flags); //на канал цап
    int X502_AsyncOutDig(Pointer hnd, int val, int mask); //цифровые линии
    int X502_StreamsEnable(Pointer hnd, int streams);
    int X502_StreamsDisable(Pointer hnd, int streams);
    int X502_StreamsStart(Pointer hnd);
    int X502_StreamsStop(Pointer hnd);
    int X502_Recv(Pointer hnd, Pointer buf, int size, int tout);
    int X502_ProcessData(Pointer hnd, Pointer src, int size, int flags, Pointer adc_data, IntByReference adc_data_size, Pointer din_data, IntByReference  din_data_size);
    int X502_GetNextExpectedLchNum(Pointer hnd, IntByReference first_lch);

    int X502_SetAdcFreq(Pointer hnd, DoubleByReference f_acq, DoubleByReference f_frame);
}
