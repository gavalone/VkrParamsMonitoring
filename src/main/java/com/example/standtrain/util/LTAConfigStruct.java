package com.example.standtrain.util;

import com.sun.jna.*;

import java.util.*;

public class LTAConfigStruct extends Structure {

    public int[] series = new int[Consts.LTA27_MEZ_QTY];
    public byte[] enable = new byte[Consts.LTA27_CH_QTY];
    public int[] filter_dr = new int[Consts.LTA27_CH_QTY];

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("series", "enable", "filter_dr");
    }
}

