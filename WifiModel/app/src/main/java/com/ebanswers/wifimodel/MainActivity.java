package com.ebanswers.wifimodel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ebanswers.wifilibrary.NetWorkSpeedUtils;
import com.ebanswers.wifilibrary.StyleConfig;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.id_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetWorkSpeedUtils.startShowNetSpeed(MainActivity.this, new NetWorkSpeedUtils.onNetSpeedResultlistener() {
                    @Override
                    public void speed(int rssi, final long speed) {
                        button.setText(speed + "kb/s");
                    }
                });
                button.setEnabled(false);
            }
        });
        fragmentManager = getSupportFragmentManager();

        /**
         *  TYPE1_1= 0x01;//listView,添加wifi和手动搜索为垂直排布
         *  TYPE1_2= 0x02;//listView,添加wifi和手动搜索为水平排布
         *  TYPE2_1= 0x03;//grideView,添加wifi和手动搜索为水平排布
         *  TYPE2_2= 0x04;//grideView,添加wifi和手动搜索为水平排布
         *  TYPE1_NONE = 0x05;//listview,添加wifi和手动搜索为不可见
         *  TYPE2_NONE = 0x06;//grideView,添加wifi和手动搜索为不可见
         */
        StyleConfig styleConfig = new StyleConfig.Builder()
                .setLayoutType(StyleConfig.TYPE1_2)
                .setBackGroundColor(Color.parseColor("#161B21"))
                .setItemTextColor(Color.parseColor("#767C78"))
                .build();
        WifiFragment wifiFragment = WifiFragment.getInstance(styleConfig);
        wifiFragment.setOnBackClickListener(new StyleConfig.OnBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        wifiFragment.setOnConnectedWifiListener(new StyleConfig.OnConnectedWifiListener() {
            @Override
            public void onConnected() {
                finish();
            }
        });

        fragmentManager.beginTransaction().replace(R.id.id_fl_container,wifiFragment).commitAllowingStateLoss();
    }
}
