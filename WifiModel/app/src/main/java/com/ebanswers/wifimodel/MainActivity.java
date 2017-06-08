package com.ebanswers.wifimodel;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ebanswers.wifilibrary.StyleConfig;
import com.ebanswers.wifilibrary.WifiFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                .setLayoutType(StyleConfig.TYPE1_NONE).build();
        fragmentManager.beginTransaction().replace(R.id.id_fl_container, WifiFragment.getInstance(styleConfig)).commitAllowingStateLoss();
    }
}
