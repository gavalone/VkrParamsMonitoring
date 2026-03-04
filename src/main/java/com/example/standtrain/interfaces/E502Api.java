package com.example.standtrain.interfaces;

import com.example.standtrain.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

public interface E502Api extends Library {
    E502Api INSTANCE = Native.load("C:\\Program Files (x86)\\L-Card\\L502-E502-E16-SDK\\bin\\x64\\e502api.dll", E502Api.class);

    int E16_OpenByIpAddr(Pointer hnd, int ipAddr, int flags, int timeoutMs);

}
