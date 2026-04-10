package com.example.standtrain.interfaces;

import com.sun.jna.*;

/**
 * Интерфейс для объяления методов для устройства Е16 (соединение)
 */
public interface E502Api extends Library {
    E502Api INSTANCE = Native.load("e502api.dll", E502Api.class);
    int E16_OpenByIpAddr(Pointer hnd, int ipAddr, int flags, int timeoutMs);
}
