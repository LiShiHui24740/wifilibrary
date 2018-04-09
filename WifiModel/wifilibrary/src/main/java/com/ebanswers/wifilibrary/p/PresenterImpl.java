package com.ebanswers.wifilibrary.p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanswers.wifilibrary.ConnetParamsConfig;
import com.ebanswers.wifilibrary.NetUtils;
import com.ebanswers.wifilibrary.StyleConfig;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiConfig;
import com.ebanswers.wifilibrary.WifiReceiver;
import com.ebanswers.wifilibrary.dialog.DialogUtils;
import com.ebanswers.wifilibrary.m.ModelControllerImpl;
import com.ebanswers.wifilibrary.v.IViewController;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Created by lishihui on 2017/4/10.
 */

public class PresenterImpl implements IPresenter {
    private IViewController viewController;
    private Context mContext;
    private WifiReceiver wifiReceiver;
    private ModelControllerImpl modelController;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private String current_ssid, current_password;
    private CountDownTimer timer;
    private StyleConfig.OnConnectedWifiListener onConnectedWifiListener;
    private int currentId;

    public PresenterImpl(Context context, final IViewController controller, List<ScanResult> list) {
        mContext = context.getApplicationContext();
        this.viewController = controller;
        modelController = new ModelControllerImpl(mContext, this, list);
        WifiReceiver.bindWifiState(modelController);
        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        mContext.registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    public void init(final List<ScanResult> list) {
        if (viewController != null) {
            if (WifiAdmin.getInstance(mContext).isWifiEnable() || viewController.getWifiIsChecked()) {
                viewController.openToggle();
                openWifiAndScan(list);
                if (viewController.getOnConnectCallBack() != null) {
                    viewController.getOnConnectCallBack().isWifiEnable(true);
                }
            } else {
                viewController.closeToggle();
                viewController.showCloseTip();
                if (viewController.getOnConnectCallBack() != null) {
                    viewController.getOnConnectCallBack().isWifiEnable(false);
                }
            }
        }

    }


    @Override
    public void openWifiAndScan(final List<ScanResult> list) {
        if (viewController != null)
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
        if (viewController != null)
            viewController.showCloseTip();
        WifiAdmin.getInstance(mContext).closeWifi();
    }

    @Override
    public void connect(final ScanResult scanResult) {
        //判断点击是否是当前已经连接的
        if (scanResult.BSSID.equals(WifiAdmin.getInstance(mContext).getBSSID()) && NetUtils.isWifi(mContext)) {
            if (viewController != null) {
                viewController.showDisconnectDialog(scanResult, new DialogUtils.DialogCallBack() {
                    @Override
                    public void callBack(View view, ScanResult scanResult, String str) {
                        WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                        if (WifiAdmin.getInstance(mContext).getNetworkId() != -1) {
                            int id = WifiAdmin.getInstance(mContext).getNetworkId();
                            WifiAdmin.getInstance(mContext).disconnectWifi(id);
                            WifiAdmin.getInstance(mContext).removeWifi("\"" + scanResult.SSID + "\"", id);
                            if (modelController != null)
                                modelController.updateWifiList();
                        }
                        if (viewController != null)
                            viewController.closeDisconnectDialog();

                    }

                    @Override
                    public void ignore() {

                    }

                    @Override
                    public void cancel() {
                        if (viewController != null)
                            viewController.closeDisconnectDialog();
                    }
                });
            }

        } else {
            final String security = scanResult.capabilities.toLowerCase();
            if (!security.contains("wpa") && !security.contains("wep")) {
                currentId = connectWifi(scanResult.SSID, "", "");
                if (viewController != null)
                viewController.showLoadDialog();
            } else {
                if (viewController != null) {
                    viewController.showInputPasswordDialog(scanResult, new DialogUtils.DialogCallBack() {
                        @Override
                        public void callBack(View view, ScanResult scanResult, String passward) {
                            if (viewController != null)
                                viewController.closeInputPasswordDialog();
                            current_ssid = scanResult.SSID;
                            current_password = passward;
                            if (security.contains("wpa")) {
                                currentId = connectWifi(scanResult.SSID, passward, "wpa");
                            } else if (security.contains("wep")) {
                                currentId = connectWifi(scanResult.SSID, passward, "wep");
                            }
                            if (viewController != null)
                            viewController.showLoadDialog();
                        }

                        @Override
                        public void ignore() {
                            if (viewController != null)
                                viewController.closeInputPasswordDialog();
                            WifiConfig.getInstance(mContext).removePasswd(scanResult.SSID);
                            WifiAdmin.getInstance(mContext).removeWifi("\"" + scanResult.SSID + "\"");
                        }

                        @Override
                        public void cancel() {
                            if (viewController != null)
                                viewController.closeInputPasswordDialog();
                        }
                    });
                }


            }

        }

    }

    private int connectWifi(String ssid, String password, String secrity) {
        int netId = WifiAdmin.getInstance(mContext).IsConfiguration("\"" + ssid + "\"");
        if (netId != -1) {
            WifiAdmin.getInstance(mContext).ConnectWifi(netId);
        } else {
            if (TextUtils.isEmpty(secrity)) {
                netId = WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, "", 1), true);
            } else if (secrity.equals("wpa")) {
                netId = WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, 3), true);
            } else if (secrity.equals("wep")) {
                netId = WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, 2), true);
            }
        }
        return netId;
    }


    public void updateData() {
        WifiAdmin.getInstance(mContext).updateConfigure();
        if (viewController != null)
            viewController.refreshList();
    }

    @Override
    public void addWifi() {
        if (viewController != null) {
            viewController.showAddWifiDialog(new DialogUtils.DialogAddWifiCallBack() {
                @Override
                public void callBack(String ssid, String password, int type) {
                    if (viewController != null)
                        viewController.closeAddWifiDialog();
                    current_ssid = ssid;
                    current_password = password;
                    int netId = WifiAdmin.getInstance(mContext).IsConfiguration("\"" + ssid + "\"");
                    if (netId != -1) {
                        WifiAdmin.getInstance(mContext).ConnectWifi(netId);
                    } else {
                        WifiAdmin.getInstance(mContext).addNetwork(WifiAdmin.getInstance(mContext).createWifiInfo(ssid, password, type), true);
                    }
                }

                @Override
                public void cancel() {
                    if (viewController != null)
                        viewController.closeAddWifiDialog();
                }

            });
        }

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void savePassword(String ssid) {
        if (ssid.equals(current_ssid) && !TextUtils.isEmpty(current_password))
            WifiConfig.getInstance(mContext).savePasswd(ssid, current_password);
    }

    @Override
    public String getPassWord(String ssid) {
        return WifiConfig.getInstance(mContext).getPasswd(ssid);
    }

    @Override
    public void startConnectOutTime() {
        if (timer == null) {
            timer = new CountDownTimer(ConnetParamsConfig.getConnectOutTime(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    WifiAdmin.getInstance(mContext).disconnectWifi(currentId);
                    if (viewController != null)
                        viewController.closeLoadDialog();
                    cancelConnectOutTime();
                    if (ConnetParamsConfig.getConnectOutTimeCallBack() == null)
                        Toast.makeText(mContext, "连接超时", Toast.LENGTH_SHORT).show();
                    else
                        ConnetParamsConfig.getConnectOutTimeCallBack().onConnectOutTime();
                }
            };
            timer.start();
        }
    }

    @Override
    public void cancelConnectOutTime() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void removePassword() {
        WifiConfig.getInstance(mContext).removePasswd(current_ssid);
        WifiAdmin.getInstance(mContext).removeWifi("\"" + current_ssid + "\"");
    }

    @Override
    public void setOnConnectedWifiListener(StyleConfig.OnConnectedWifiListener onConnectedWifiListener) {
        this.onConnectedWifiListener = onConnectedWifiListener;
    }

    @Override
    public StyleConfig.OnConnectedWifiListener getOnConnectedWifiListener() {
        return onConnectedWifiListener;
    }


    @Override
    public void destory() {
        WifiReceiver.unBindWifiState(modelController);
        mContext.unregisterReceiver(wifiReceiver);
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }
        if (modelController != null) {
            modelController.destory();
        }
        WifiConfig.destory();
        this.viewController = null;
        this.modelController = null;
        this.onConnectedWifiListener = null;
    }

    @Override
    public IViewController getViewController() {
        return viewController;
    }


}
