package com.ebanswers.wifimodel;

import android.graphics.Color;
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

        StyleConfig styleConfig = new StyleConfig.Builder()
                .setLayoutType(StyleConfig.TYPE1_NONE).build();
        fragmentManager.beginTransaction().replace(R.id.id_fl_container, WifiFragment.getInstance(styleConfig)).commitAllowingStateLoss();
    }
}
