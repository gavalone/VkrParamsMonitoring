package com.example.standtrain.services;

import com.example.standtrain.interfaces.*;
import com.example.standtrain.util.*;
import com.sun.jna.*;
import com.sun.jna.ptr.*;

/**
 * Класс сервиса, описывающий объявленные методы создания указателя, инициализации каналов и подготовки к
 * началу работы для модуля Е16
 */
public class InitializationE16 {
    public static Pointer createHandle(){
        return X502Api.INSTANCE.X502_Create();
    }

    // Открытие модуля по адресу
    public static int openE16(Pointer handle){
        return E502Api.INSTANCE.E16_OpenByIpAddr(handle, UtilMethods.createIp(Config.ipE16_1, Config.ipE16_2, Config.ipE16_3, Config.ipE16_4), 0, 200);
    }

    // Устанавливает количество логических каналов в логической таблице АЦП
    public static int setE16ChannelsCount(Pointer handle, int count){
        return X502Api.INSTANCE.X502_SetLChannelCount(handle, count);
    }

    // Инициализация количества логических каналов в логической таблице АЦП
    public static int iniE16Channels(Pointer handle, int count){
        for (int i = 0; i < count; i++) {
            int err = X502Api.INSTANCE.X502_SetLChannel(handle, i, i, Consts.X502_LCH_MODE_DIFF, Consts.E16_ADC_RANGE_156, 0);
            if (err != 0 ) return err;
        }
        return 0;
    }

    // Передача настроек в модуль
    public static int put16Data(Pointer handle){
        return X502Api.INSTANCE.X502_Configure(handle, 0); // согласно апи флаг=0
    }

    // Включение подтягивающих резисторов на цифровых входах (установка напряжения при отсутствии подключенных датчиков)
    public static int iniRelay(Pointer handle){
        return X502Api.INSTANCE.X502_SetDigInPullup(handle, Consts.E16_MODE_RELAY_ON);
    }

    // Установка частоты сбора АЦП
    public static int X502_SetAdcFreq(Pointer handle){
        DoubleByReference f_acq = new DoubleByReference(Consts.E16_ADC_FREQ_MINI); // желаемая частота
        int res = X502Api.INSTANCE.X502_SetAdcFreq(handle, f_acq, null);
        return res;
    }

}
