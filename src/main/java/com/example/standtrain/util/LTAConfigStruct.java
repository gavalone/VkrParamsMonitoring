package com.example.standtrain.util;

import com.sun.jna.*;

import java.util.*;

public class LTAConfigStruct extends Structure {

    public int ch_enable_mask;   // bitmask: bit0=ch0, bit1=ch1, etc.
    public int reserved1;
    public int reserved2;
    public int reserved3;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("ch_enable_mask", "reserved1", "reserved2", "reserved3");
    }
}
