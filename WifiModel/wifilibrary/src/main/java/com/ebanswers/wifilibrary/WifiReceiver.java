package com.ebanswers.wifilibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Description  Wifi连接广播接收,使用静态注册，添加以下广播
 * Created by chenqiao on 2015/11/10.
 */
public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            SupplicantState supplicantState = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            Log.d("lishihui_DetailedState", "SupplicantState2:" + supplicantState);
            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(supplicantState);
            Log.d("lishihui_DetailedState", "DetailedState:" + state);
            int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            if (error == WifiManager.ERROR_AUTHENTICATING) {
                wifiStatus(WIFI_FAIL, "身份认证失败");
            }
            switch (supplicantState) {
                case DISCONNECTED:
                    wifiStatus(WIFI_DISCONNECT);
                    break;
            }

        } else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            wifiStatus(WIFI_UPDATE);
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
            wifiStatus(WIFI_UPDATE);
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                System.out.println("wifi网络连接成功");
                wifiStatus(WIFI_CONNECT);
            } else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                System.out.println("wifi正在连接");
                wifiStatus(WIFI_CONNECTTING);
            }
        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// wifi打开与否
            int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifi_state == WifiManager.WIFI_STATE_DISABLED) {
                System.out.println("系统关闭wifi");
                wifiStatus(WIFI_CLOSE);
            } else if (wifi_state == WifiManager.WIFI_STATE_ENABLED) {
                System.out.println("系统开启wifi");
                wifiStatus(WIFI_OPEN);
            }
        } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {// wifi扫描结果可得
            System.out.println("Wifi扫描结果已得");
            wifiStatus(WIFI_UPDATE);
        }
    }

    /**
     * 打开了WIFI
     */
    private static final int WIFI_OPEN = 0x01;
    /**
     * 关闭了WIFI
     */
    private static final int WIFI_CLOSE = 0x02;
    /**
     * wifi正在打开
     */
    private static final int WIFI_CONNECTTING = 0x03;
    /**
     * wifi结果刷新
     */
    private static final int WIFI_UPDATE = 0x04;
    /**
     * wifi连接错误
     */
    private static final int WIFI_FAIL = 0x05;
    /**
     * wifi连接失败
     */
    private static final int WIFI_DISCONNECT = 0x06;
    /**
     * wifi连接成功
     */
    private static final int WIFI_CONNECT = 0x07;

    private static List<WifiStateChange> mWifiStateChanges;

    /**
     * 绑定wifi改变事件
     *
     * @param wifiStateChange
     */
    public static void bindWifiState(WifiStateChange wifiStateChange) {
        if (mWifiStateChanges == null) {
            mWifiStateChanges = new ArrayList<>();
        }
        if (wifiStateChange != null) {
            mWifiStateChanges.add(wifiStateChange);
        }
    }

    /**
     * 解绑wifi改变事件
     *
     * @param wifiStateChange
     */
    public static void unBindWifiState(WifiStateChange wifiStateChange) {
        if (mWifiStateChanges != null) {
            mWifiStateChanges.remove(wifiStateChange);
        }
    }

    /**
     * wifi连接状态改变
     *
     * @param wifiTag
     */
    private void wifiStatus(int wifiTag, String... strings) {
        if (mWifiStateChanges != null) {
            for (WifiStateChange wifiStateChange : mWifiStateChanges) {
                switch (wifiTag) {
                    case WIFI_OPEN:
                        wifiStateChange.openWifi();
                        break;
                    case WIFI_CLOSE:
                        wifiStateChange.closeWifi();
                        break;
                    case WIFI_CONNECTTING:
                        wifiStateChange.connectingWifi();
                        break;
                    case WIFI_UPDATE:
                        wifiStateChange.updateWifiList();
                        break;
                    case WIFI_FAIL:
                        wifiStateChange.failWifi(strings[0]);
                        break;
                    case WIFI_DISCONNECT:
                        wifiStateChange.updateWifiList();
                        wifiStateChange.disconnectWifi();
                        break;
                    case WIFI_CONNECT:
                        wifiStateChange.updateWifiList();
                        wifiStateChange.connectWifi();
                        break;
                }
            }
        }
    }


    public interface WifiStateChange {
        //打开了wifi
        void openWifi();

        //关闭了wifi
        void closeWifi();

        //正在打开wifi
        void connectingWifi();

        //连接成功
        void connectWifi();

        //连接失败
        void disconnectWifi();

        //wifi结果有刷新
        void updateWifiList();

        //异常出错
        void failWifi(String failMsg);
    }
}