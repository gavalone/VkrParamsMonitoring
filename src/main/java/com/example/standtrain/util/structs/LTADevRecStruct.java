package com.example.standtrain.util.structs;

import com.sun.jna.*;

import java.util.*;

//lta_dev_rec_t
public class LTADevRecStruct extends Structure {
    public byte[] devname = new byte[32];
    public byte[] serial_dev = new byte[32];
    public byte[] serial_crate = new byte[32];
    public byte slot;
    public int ip_addr;
    public short cmd_port;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("devname", "serial_dev", "serial_crate", "slot", "ip_addr", "cmd_port");
    }
}
