# wifilibrary
## wifi库,实现了常用的wifi连接.使用在app的build.gradle中添加以下依赖：

```
compile 'com.ebanswers:wifilibrary:1.1.5'

```
## 使用wifi模块：

```
 WifiFragment.getInstance(StyleConfig.TYPE1_2)；
 
```
## 获取到wififragment实例，参数为样式；

# 例如在MainActicity中使用：
```
public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        fragmentManager = getSupportFragmentManager();
        
        StyleConfig styleConfig = new StyleConfig.Builder().setItemTextColor(Color.parseColor("#ffff00"))
                .setTopBackGroundColor(Color.parseColor("#3F51B5"))
                .setLayoutType(StyleConfig.TYPE1_2)
                .setTopTitleColor(Color.parseColor("#FF4081")).build();
        
        fragmentManager.beginTransaction().replace(R.id.id_fl_container, WifiFragment.getInstance(styleConfig)).commitAllowingStateLoss();
    }
}

```

