package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;

/**
 * Класс сервиса, описывающий объявленные методы вывода данных из устройства Е16
 */
public class DataInputE16 {
    // асинхронный вывод значения на один из каналов ЦАП
    public static int putV(double v, Pointer handle){
        return X502Api.INSTANCE.X502_AsyncOutDac(handle, Consts.X502_DAC_CH1, v, Consts.X502_DAC_FLAGS_VOLT); // вольты на цап
    }
    // асинхронный вывод значений на цифровые линии
    public static int putDI0(int value, Pointer handle, int mask){
        return X502Api.INSTANCE.X502_AsyncOutDig(handle, value, mask); // на движение, цифровой регистр
    }
}
