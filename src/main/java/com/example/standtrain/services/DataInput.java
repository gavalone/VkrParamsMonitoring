package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;

public class DataInput {
    public static int putV(double v, Pointer handle){
        return X502Api.INSTANCE.X502_AsyncOutDac(handle, Consts.X502_DAC_CH1, v, Consts.X502_DAC_FLAGS_VOLT); //вольты на цап
    }

    public static int putDI0(int value, Pointer handle, int mask){
        return X502Api.INSTANCE.X502_AsyncOutDig(handle, value, mask); //(на движениее, цифровой регистр)
    }
}
