package com.ebanswers.wifilibrary.m;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiReceiver;
import com.ebanswers.wifilibrary.p.IPresenter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Created by lishihui on 2017/4/11.
 */

public class ModelControllerImpl implements WifiReceiver.WifiStateChange {
    private List<ScanResult> mlist;
    private IPresenter mPresenter;
    private Context mContext;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler();
    private Toast toast;

    public ModelControllerImpl(Context context, IPresenter presenter, List<ScanResult> mlist) {
        mContext = context;
        mPresenter = presenter;
        this.mlist = mlist;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void openWifi() {

    }

    @Override
    public void closeWifi() {

    }

    /**
     * wifi正在连接中
     */
    @Override
    public void connectingWifi() {
        Log.d("loadDialog", "openingWifi");
        if (mPresenter != null && mPresenter.getViewController() != null) {
            mPresenter.getViewController().showLoadDialog();
            mPresenter.startConnectOutTime();
            if (mPresenter.getViewController().getOnConnectCallBack() != null) {
                if (mlist != null && mlist.size() > 0)
                    mPresenter.getViewController().getOnConnectCallBack().connectResult(mlist.get(0));
            }
        }

    }

    /**
     * 连接成功
     */
    @Override
    public void connectWifi() {
        Log.d("loadDialog", "connectWifi:closeLoadDialog");
        if (mPresenter != null && mPresenter.getViewController() != null) {
            mPresenter.getViewController().closeLoadDialog();
            mPresenter.cancelConnectOutTime();
        }

    }

    @Override
    public void disconnectWifi() {

    }

    @Override
    public void updateWifiList() {
        if (executor != null && !executor.isShutdown()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final List<ScanResult> scanResults = WifiAdmin.getInstance(mContext).getWifiListWithFilting();
                    if (mlist != null) {
                        mlist.clear();
                        mlist.addAll(scanResults);
                    }
                    if (scanResults != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mPresenter != null)
                                    mPresenter.updateData();
                            }
                        });
                    }
                }
            });
        }

    }

    @Override
    public void failWifi(String failMsg) {
        Log.d("loadDialog", "failWifi:closeLoadDialog");
        if (mPresenter != null && mPresenter.getViewController() != null) {
            mPresenter.getViewController().closeLoadDialog();
            mPresenter.removePassword();
            mPresenter.cancelConnectOutTime();
        }
        toast.setText(failMsg);
        toast.show();
    }

    public void destory() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
