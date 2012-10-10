package com.sungeo.smhouse.util;

import android.os.Handler;

public class MsgHandler extends Handler{
    public final static int MSG_TYPE_STR = 0x3001;
    public final static int MSG_TYPE_CONNECT_SUCESS = 0x3002;
    public final static int MSG_TYPE_CONNECT_FAIL = 0x3003;
    public final static int MSG_TYPE_CONNECT_LOST = 0x3006;
    public final static int MSG_TYPE_START_FIND = 0x3007;
    public final static int MSG_TYPE_START_CONNECT = 0x3008;
}
