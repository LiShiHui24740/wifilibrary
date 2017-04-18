package com.ebanswers.wifilibrary.p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.View;

import com.ebanswers.wifilibrary.NetUtils;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiConfig;
import com.ebanswers.wifilibrary.WifiReceiver;
import com.ebanswers.wifilibrary.dialog.DialogUtils;
import com.ebanswers.wifilibrary.m.ModelControllerImpl;
import com.ebanswers.wifilibrary.v.IViewController;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Created by lishihui on 2017/4/10.
 */

public class PresenterImpl implements IPresenter {
    private IViewController viewController;
    private Context mContext;
    private WifiReceiver wifiReceiver;
    private ModelControllerImpl modelController;
    private Executor threadPool = Executors.newCachedThreadPool();
    private String current_ssid, current_password;

    public PresenterImpl(Context context, IViewController controller) {
        mContext = context;
        this.viewController = controller;
        modelController = new ModelControllerImpl(context, this);
        WifiReceiver.bindWifiState(modelController);
        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        mContext.registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    public void init(final List<ScanResult> list) {
        if (WifiAdmin.getInstance(mContext).isWifiEnable()) {
            viewController.openToggle();
            openWifiAndScan(list);
        } else {
            viewController.closeToggle();
            viewController.showCloseTip();
        }
    }

    @Override
    public void openWifiAndScan(final List<ScanResult> list) {
        modelController.setScanResultlist(list);
        viewController.showOpenTip();
        scan();
    }

    private void scan() {
        WifiAdmin.getInstance(mContext).OpenWifi();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                WifiAdmin.getInstance(mContext).startScan();
            }
        });
    }

    @Override
    public void closeSystemWifi() {
        viewController.showCloseTip();
        WifiAdmin.getInstance(mContext).closeWifi();
    }

    @Override
    public void connect(final ScanResult scanResult) {
        Log.d("isSuccess1", "scanResult");
        //判断点击是否是当前已经连接的
        if (scanResult.BSSID.equals(WifiAdmin.getInstance(mContext).getBSSID()) && NetUtils.isWifi(mContext)) {
            viewController.showDisconnectDialog(scanResult, new DialogUtils.DialogCallBack() {
                @Override
                public void callBack(View view, ScanResult scanResult, String str) {
                    viewController.closeDisconnectDialog();
                    WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                    WifiAdmin.getInstance(mContext).removeConfiguredWifi(WifiConfig.getInstance(mContext).getSaveWifiId(scanResult.BSSID),true);
                    WifiConfig.getInstance(mContext).setBssid("");
                    Log.d("disconnect", "scanResult.SSID:" + scanResult.SSID);
                    Log.d("disconnect", "WifiConfig.getInstance(mContext).getSaveWifiId():" + WifiConfig.getInstance(mContext).getSaveWifiId(scanResult.SSID));
                }

                @Override
                public void ignore() {

                }

                @Override
                public void cancel() {
                    viewController.closeDisconnectDialog();
                }
            });
        } else {
            final String security = scanResult.capabilities.toLowerCase();
            if (!security.contains("wpa") && !security.contains("wep")) {
                WifiAdmin.getInstance(mContext).removeConfiguredWifi(WifiConfig.getInstance(mContext).getSaveWifiId(WifiConfig.getInstance(mContext).getBssid()),true);
                WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(scanResult.SSID, "", 1));
                WifiConfig.getInstance(mContext).setBssid(scanResult.BSSID);
                viewController.showLoadDialog();
            } else {
                viewController.showInputPasswordDialog(scanResult, new DialogUtils.DialogCallBack() {
                    @Override
                    public void callBack(View view, ScanResult scanResult, String passward) {
                        viewController.closeInputPasswordDialog();
                        viewController.showLoadDialog();
                        current_ssid = scanResult.SSID;
                        current_password = passward;
                        Log.d("isSuccess1", "scanResult.SSID:" + scanResult.SSID);
                        Log.d("isSuccess1", "passward:" + passward);
                        //断开现有连接
                        WifiAdmin.getInstance(mContext).removeConfiguredWifi(WifiConfig.getInstance(mContext).getSaveWifiId(WifiConfig.getInstance(mContext).getBssid()),true);
                        WifiConfig.getInstance(mContext).setBssid(scanResult.BSSID);
                        if (security.contains("wpa")) {
                            Log.d("isSuccess1", "wpa:");
                            WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(scanResult.SSID, passward, 3));
                        } else if (security.contains("wep")) {
                            Log.d("isSuccess1", "wep:");
                            WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(scanResult.SSID, passward, 2));
                        }
                    }

                    @Override
                    public void ignore() {
                        viewController.closeInputPasswordDialog();
                        WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                        WifiAdmin.getInstance(mContext).removeConfiguredWifi(WifiConfig.getInstance(mContext).getSaveWifiId(scanResult.BSSID),false);
                    }

                    @Override
                    public void cancel() {
                        viewController.closeInputPasswordDialog();
                    }
                });

            }

        }

    }

    public void updateData() {
        WifiAdmin.getInstance(mContext).updateConfigure();
        viewController.refreshList();
    }

    @Override
    public void addWifi() {
        viewController.showAddWifiDialog(new DialogUtils.DialogAddWifiCallBack() {
            @Override
            public void callBack(String ssid, String password, int type) {
                viewController.closeAddWifiDialog();
                current_ssid = ssid;
                current_password = password;
                Log.d("isSuccess1", "scanResult.SSID:" + ssid);
                Log.d("isSuccess1", "passward:" + password);
                //断开现有连接
                WifiAdmin.getInstance(mContext).removeConfiguredWifi(WifiConfig.getInstance(mContext).getSaveWifiId(ssid),true);
                WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, type));
                viewController.showLoadDialog();
            }

            @Override
            public void cancel() {
                viewController.closeAddWifiDialog();
            }

        });
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void savePassword(String ssid) {
        if (ssid.equals(current_ssid))
        WifiConfig.getInstance(mContext).savePasswd(current_ssid, current_password);
    }

    @Override
    public void removePassword() {
        WifiConfig.getInstance(mContext).removePasswd(current_ssid);
    }


    @Override
    public void destory() {
        mContext.unregisterReceiver(wifiReceiver);
        WifiConfig.destory();
        mContext = null;
    }

    @Override
    public IViewController getViewController() {
        return viewController;
    }


}
