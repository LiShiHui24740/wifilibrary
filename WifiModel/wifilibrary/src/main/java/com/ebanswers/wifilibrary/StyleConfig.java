package com.ebanswers.wifilibrary;

import android.os.Bundle;

import static android.R.attr.type;

/**
 * @author Created by lishihui on 2017/4/18.
 */

public class StyleConfig {
    private Bundle mBundle;
    public static final int TYPE1_1 = 0x01;//listView,添加wifi和手动搜索为垂直排布
    public static final int TYPE1_2 = 0x02;//listView,添加wifi和手动搜索为水平排布
    public static final int TYPE2_1 = 0x03;//grideView,添加wifi和手动搜索为水平排布
    public static final int TYPE2_2 = 0x04;//grideView,添加wifi和手动搜索为水平排布
    public static final int TYPE1_NONE = 0x05;//listview,添加wifi和手动搜索为不可见
    public static final int TYPE2_NONE = 0x06;//grideView,添加wifi和手动搜索为不可见

    private StyleConfig(Bundle bundle) {
        mBundle = bundle;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public static class Builder {
        private Bundle bundle = new Bundle();

        public Builder setLayoutType(int type) {
            bundle.putInt("layout_type", type);
            return this;
        }

        public Builder setTopWifiIsVisible(boolean flg) {
            bundle.putBoolean("top_wifi_visible", flg);
            return this;
        }

        public Builder setTopWifiDrawable(int Id) {
            bundle.putInt("top_wifi", Id);
            return this;
        }

        public Builder setTopBackGroundColor(int color) {
            bundle.putInt("top_bg_color", color);
            return this;
        }

        public Builder setTopBackGroundDrawable(int id) {
            bundle.putInt("top_bg_drawable", id);
            return this;
        }

        public Builder setTopTitleSize(int sp) {
            bundle.putInt("top_title_size", sp);
            return this;
        }

        public Builder setTopTitleColor(int color) {
            bundle.putInt("top_title_color", color);
            return this;
        }

        public Builder setItemTextSize(int sp) {
            bundle.putInt("item_text_size", sp);
            return this;
        }

        public Builder setItemTextColor(int color) {
            bundle.putInt("item_text_color", color);
            return this;
        }

        public Builder setBackGroundColor(int color) {
            bundle.putInt("background_color", color);
            return this;
        }

        public Builder setBackGroundDrawable(int id) {
            bundle.putInt("background_drawable", id);
            return this;
        }

        public Builder setBackIsVisisble(boolean isVisisble) {
            bundle.putBoolean("backIsVisisble", isVisisble);
            return this;
        }

        public Builder setBackDrawableId(int id) {
            bundle.putInt("backDrawableId", id);
            return this;
        }

        public Builder setTipTextColor(int color) {
            bundle.putInt("tipColor", color);
            return this;
        }

        public Builder setTipTextSize(int size) {
            bundle.putInt("tipSize", size);
            return this;
        }

        public StyleConfig build() {
            return new StyleConfig(bundle);
        }
    }

    public interface OnBackClickListener {
        void onClick();
    }

    public interface OnConnectedWifiListener {
        void onConnected();
    }
}
