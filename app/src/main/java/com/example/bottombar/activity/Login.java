package com.example.bottombar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;

import static com.example.bottombar.activity.MainActivity.user;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";//打印日志标识
    public static Login instance;//定义一个静态，用于之后在别的活动中调用销毁

    Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == 0)
            {
                int ret = 1;//判断网络是否可用
                Runtime runtime = Runtime.getRuntime();
                try {
                    Process p = runtime.exec("ping -c 3 www.baidu.com");
                    ret = p.waitFor();//ret为0时即表示可用
                    Log.i("Avalible", "Process:"+ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(ret!=0)//为1表示网络不可用
                    Toast.makeText(Login.this,"网络不可用", Toast.LENGTH_SHORT).show();
                else  //为0表示账号已注册
                    Toast.makeText(Login.this,"账号或者密码错误", Toast.LENGTH_SHORT).show();
            }
            else//登陆成功并向主页面传递user_phone信息
            {
                Toast.makeText(Login.this,"登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.putExtra("user_phone", (String)message.obj);
                startActivity(intent);
                finish(); //销毁当前活动
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;//当前Activity传入上面定义的静态

        if(user.getPhone()!="")//如果已登录则跳转主页面
        {
            Intent intent = new Intent(Login.this,MainActivity.class);
            startActivity(intent);
            finish(); //销毁当前活动
        }
        setContentView(R.layout.activity_login);

        //获取文本框对象
        final EditText phone_editText= (EditText)findViewById(R.id.editText_phone);//手机号
        final EditText pwd_editText= (EditText)findViewById(R.id.editText_pwd);//密码

        //注册按钮
        (findViewById(R.id.button_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                //finish(); //销毁当前活动
            }
        });

        //登陆按钮
        (findViewById(R.id.button_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入账号与密码
                final String phone=phone_editText.getText().toString().trim();
                final String pwd=pwd_editText.getText().toString().trim();
                Log.e(TAG, phone);
                Log.e(TAG, pwd);

                //提示不能为空
                if(phone.equals("")|| pwd.equals("")) {
                    Toast.makeText(Login.this,"账号密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String pwd_get= DBUtils.GetUserPwdByPhone(phone);
                            Message msg = new Message();//非UI线程不要试着去操作界面
                            if(pwd_get == null)
                            {
                                msg.what = 0;
                            }
                            else
                            {
                                msg.what = 0;
                                if( pwd_get.equals(pwd))
                                {
                                    msg.what = 1;
                                    msg.obj =phone;
                                }
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
    }
}
