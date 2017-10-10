# wifilibrary
## 不多说先上部分截图
![github](https://github.com/LiShiHui24740/wifilibrary/blob/master/WifiModel/img/wifi_1.jpg)  
![github](https://github.com/LiShiHui24740/wifilibrary/blob/master/WifiModel/img/wifi_2.jpg)  
![github](https://github.com/LiShiHui24740/wifilibrary/blob/master/WifiModel/img/wifi_3.jpg) 
![github](https://github.com/LiShiHui24740/wifilibrary/blob/master/WifiModel/img/wifi_4.jpg) 
## wifi库,实现了常用的wifi连接.使用在app的build.gradle中添加以下依赖：

```
compile 'com.ebanswers:wifilibrary:1.4.4'

```
## 使用wifi模块：

```
 WifiFragment.getInstance(StyleConfig.TYPE1_2)；
 
```

## 例如在MainActicity中使用：
```
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
                .setLayoutType(StyleConfig.TYPE1_2)
                .setBackGroundColor(Color.parseColor("#161B21"))
                .setItemTextColor(Color.parseColor("#767C78"))
                .build();
        fragmentManager.beginTransaction().replace(R.id.id_fl_container, WifiFragment.getInstance(styleConfig)).commitAllowingStateLoss();
    }
}

```
## 权限添加
```
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```    
