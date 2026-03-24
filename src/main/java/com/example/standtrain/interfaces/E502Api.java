package com.example.standtrain.interfaces;

import com.sun.jna.*;

public interface E502Api extends Library {
    E502Api INSTANCE = Native.load("e502api.dll", E502Api.class);
    int E16_OpenByIpAddr(Pointer hnd, int ipAddr, int flags, int timeoutMs);

}
