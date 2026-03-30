package com.example.standtrain.util;

public class Consts {
    public static final int X502_LCH_MODE_DIFF = 1;
    public static final int X502_ADC_RANGE_02 = 5;
    public static final int E16_ADC_RANGE_156 = 3;
    public static final int X502_PROC_FLAGS_VOLT = 0x00000001;
    public static final int E16_MODE_RELAY_ON = 0x40;
    public static final int X502_DAC_CH1 = 0;
    public static final int X502_DAC_FLAGS_VOLT = 0x0001;
    public static final int X502_STREAM_ADC = 0x01;
    public static final int FPGA_REG_SM_MASK = 0x2000;
    public static final int FPGA_REG_ADDR_CH0_OUT = FPGA_REG_SM_MASK | 0x0040;
    public static final int FPGA_REG_ADDR_CH2_OUT = FPGA_REG_SM_MASK | 0x0042;
    public static final int FPGA_REG_ADDR_CH4_OUT = FPGA_REG_SM_MASK | 0x0044;
    public static final int LTA27_MEZ_QTY = 8;
    public static final int LTA27_CH_QTY  = 16;
    public static final int LTA27_MEZ_SER_A = 1;   // series A
    public static final int LTA27_A27_DR_32  = 0x0F;
    public static final int LTA27_A27_DR_64  = 0x1F;
    public static final int LTA27_A27_DR_128 = 0x3F;
    public static final int LTA27_A27_DR_256 = 0x7F;
    public static final int LTA27_A27_DR_512 = (byte)0xFF;
    public static final int LTA27_PD_FLAGS_VALUE = 0x02;
    public static final int LTA27_PD_FLAGS_CALIBR = 0x01;
    public static final int E16_ADC_FREQ_DEFAULT = 500000;

    public static final int E16_BUFFER_CAPACITY = 5000;
    public static final int E16_ADC_FREQ_MINI = 50000;
    public static final int READ_BLOCK_SIZE = 5000;
    public static final int READ_TIMEOUT = 3000;
    public static final int MAX_POINTS = 1000;
    public static final int REFRESH_MS = 100;
    public static final int SKIP_ADC_DATA = 100;
    public static final double chartBond = 0.0001;

    public static final int chNum = 4;
    public static final float msPerPoint = ((1f / ((float)E16_ADC_FREQ_MINI / (float)SKIP_ADC_DATA) / (float)MAX_POINTS) * 1000f) * chNum;



    public static final int LTAChartsSize = 25;
}
