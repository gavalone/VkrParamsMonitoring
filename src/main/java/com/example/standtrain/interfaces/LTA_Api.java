package com.example.standtrain.interfaces;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 * Интерфейс для объяления методов для устройства LTA27 (соединение)
 */
public interface LTA_Api extends Library {
    LTA_Api INSTANCE = Native.load("lta_api.dll", LTA_Api.class);
    int LTA_Open(PointerByReference hnd, Pointer devRecs, int devCnt, String name, String serial, byte slot, int ipAddr, short ipPort, int timeout);
    int LTA_Close(Pointer hnd);
}
