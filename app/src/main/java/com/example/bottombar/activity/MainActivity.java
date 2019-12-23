package com.example.bottombar.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.BottomBar;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.Order;
import com.example.bottombar.entity.User;
import com.example.bottombar.fragment.Fragment1;
import com.example.bottombar.fragment.Fragment2;
import com.example.bottombar.fragment.Fragment3;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";//打印日志标识
    public static MainActivity instance;//定义一个静态，用于之后在别的活动中调用销毁

    static public User user = new User();//定义一个全局对象user用于全局操作
    static public Order curr_order = new Order();//定义一个当前订单
    static public String user_phone;//定义一个静态变量user_phone用于判读是否从上个活动获取信息

    Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
        @Override
        public boolean handleMessage(Message message) {
            if(message.obj!=null)
            {
                user = (User) message.obj;//将从数据库获取的对象复制给全局对象
                curr_order.setUser_id(user.getPhone());
                return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance=this;//当前Activity传入上面定义的静态
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(user.getPhone()=="")     //判读是否从上个活动获取信息（从登陆活动来则获取，否则不获取
        {
            Intent intent = getIntent();
            user_phone = intent.getStringExtra("user_phone");
        }
        else
        {
            user_phone = user.getPhone();
        }

        //从数据库获取对象信息
        if (isNetworkConnected(MainActivity.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.obj = DBUtils.GetUserInfo(user_phone);
                    handler.sendMessage(msg);
                }
            }).start();
        }else
        {
            Toast.makeText(MainActivity.this, "请选择还车时间!" , Toast.LENGTH_SHORT).show();
        }


        //底部导航栏
        BottomBar bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#ff5d5e")
                .addItem(Fragment1.class,
                        "首页",
                        R.drawable.item1_before,
                        R.drawable.item1_after)
                .addItem(Fragment2.class,
                        "订单",
                        R.drawable.item2_before,
                        R.drawable.item2_after)
                .addItem(Fragment3.class,
                        "我的",
                        R.drawable.item3_before,
                        R.drawable.item3_after)
                .build();

    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
