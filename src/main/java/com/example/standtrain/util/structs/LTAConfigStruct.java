package com.example.standtrain.util.structs;

import com.example.standtrain.util.Consts;
import com.sun.jna.*;

import java.util.*;

public class LTAConfigStruct extends Structure {
    public byte[] enable = new byte[Consts.LTA27_CH_QTY];
    public byte[] dir = new byte[Consts.LTA27_CH_QTY];
    public byte a27_adc_dr;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("enable", "dir", "a27_adc_dr");
    }
}

