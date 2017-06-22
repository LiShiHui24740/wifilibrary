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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Created by lishihui on 2017/4/11.
 */

public class ModelControllerImpl implements WifiReceiver.WifiStateChange {
    private List<ScanResult> mlist;
    private IPresenter mPresenter;
    private Context mContext;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler();

    public ModelControllerImpl(Context context, IPresenter presenter, List<ScanResult> mlist) {
        mContext = context;
        mPresenter = presenter;
        this.mlist = mlist;
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
        mPresenter.getViewController().showLoadDialog();
        if (mPresenter.getViewController().getOnConnectCallBack() != null) {
            mPresenter.getViewController().getOnConnectCallBack().connectResult(mlist.get(0));
        }
    }

    /**
     * 连接成功
     */
    @Override
    public void connectWifi() {
        Log.d("loadDialog", "connectWifi:closeLoadDialog");
        mPresenter.getViewController().closeLoadDialog();
    }

    @Override
    public void disconnectWifi() {

    }

    @Override
    public void updateWifiList() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<ScanResult> scanResults = WifiAdmin.getInstance(mContext).getWifiListWithFilting();
                if (scanResults != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mlist != null) {
                                mlist.clear();
                                mlist.addAll(scanResults);
                                mPresenter.updateData();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    public void failWifi(String failMsg) {
        Log.d("loadDialog", "failWifi:closeLoadDialog");
        mPresenter.getViewController().closeLoadDialog();
        mPresenter.removePassword();
        Toast.makeText(mContext, failMsg, Toast.LENGTH_SHORT).show();
    }
}
