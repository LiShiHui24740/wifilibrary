package com.ebanswers.wifilibrary;

/**
 * Created by air on 2017/7/17.
 */

public class ConnetParamsConfig {

    private static long sConnectOutTime = 10000;
    private static OnConnectOutTimeCallBack sConnectOutTimeCallBack;

    public static void setConnectOutTime(long connectOutTime) {
        sConnectOutTime = connectOutTime;
    }

    public static long getConnectOutTime() {
        return sConnectOutTime;
    }

    public static void setConnectOutTimeCallBack(OnConnectOutTimeCallBack connectOutTimeCallBack) {
        sConnectOutTimeCallBack = connectOutTimeCallBack;
    }

    public static OnConnectOutTimeCallBack getConnectOutTimeCallBack() {
        return sConnectOutTimeCallBack;
    }

    public interface OnConnectOutTimeCallBack {
        void onConnectOutTime();
    }

}
