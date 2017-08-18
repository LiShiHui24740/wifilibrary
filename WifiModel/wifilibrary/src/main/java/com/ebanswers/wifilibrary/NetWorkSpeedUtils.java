package com.ebanswers.wifilibrary;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by air on 2017/8/18.
 */

public class NetWorkSpeedUtils {
    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;
    public static final int RSSI_GOOD = 0;
    public static final int RSSI_NORMAL = 1;
    public static final int RSSI_TERRIBLE = 2;
    private static TimerTask timerTask;
    private static Timer timer;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void startShowNetSpeed(final Context context, final onNetSpeedResultlistener onNetSpeedResultlistener) {
        lastTotalRxBytes = getTotalRxBytes(context);
        lastTimeStamp = System.currentTimeMillis();
        if (timerTask != null)
            timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (onNetSpeedResultlistener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int rssi = WifiAdmin.getInstance(context).getRssi();
                            if (rssi > -50) {
                                onNetSpeedResultlistener.speed(RSSI_GOOD, showNetSpeed(context));
                            } else if (rssi > -70) {
                                onNetSpeedResultlistener.speed(RSSI_NORMAL, showNetSpeed(context));
                            } else {
                                onNetSpeedResultlistener.speed(RSSI_TERRIBLE, showNetSpeed(context));
                            }
                        }
                    });
                }
            }
        };
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }

    public static void cancel() {
        if (timerTask != null)
            timerTask.cancel();
        timerTask.cancel();
        if (timer != null)
            timer.cancel();
        timer = null;
        handler.removeCallbacksAndMessages(null);
    }

    private static long getTotalRxBytes(Context context) {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    private static long showNetSpeed(Context context) {
        long nowTotalRxBytes = getTotalRxBytes(context);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }

    public interface onNetSpeedResultlistener {
        void speed(int level, long speed);
    }
}
