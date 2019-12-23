package com.example.bottombar.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.OnMultiClickListener;
import com.example.bottombar.entity.SendCode;

import java.util.Random;

public class Register extends AppCompatActivity {
    private static final String TAG = "register";//打印日志标识

    private Button timeBtn;
    private CountDownTime mTime;

    Handler handler = new Handler(new Handler.Callback() //handler对象对线程传出信息进行处理
    {
        @Override
        public boolean handleMessage(Message message)//库函数，不是命名问题
        {
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
                    Toast.makeText(Register.this,"网络不可用", Toast.LENGTH_SHORT).show();
                else    //为0表示账号已注册
                    Toast.makeText(Register.this,"该账号已注册", Toast.LENGTH_SHORT).show();
            }
            else    //注册成功并跳转登录页面
            {
                Toast.makeText(Register.this,"注册成功", Toast.LENGTH_SHORT).show();
                Login.instance.finish();
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
                finish(); //销毁当前活动
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //判断AndroidSDK版本9以上则使用StrictMode捕捉异常并提示，用于短信接口
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //获取文本框对象 Ps：谷歌提供的xml的变量名就是editText
        final EditText phone_editText= (EditText)findViewById(R.id.phone_editText);//手机号
        final EditText pwd_editText= (EditText)findViewById(R.id.pwd_editText);//密码
        final EditText repwd_editText= (EditText)findViewById(R.id.repwd_editText);//确认密码
        final EditText check_editText= (EditText)findViewById(R.id.check_editText);//用户输入验证码
        final String[] code = {""};
        final String[] phone_check = {""};


        timeBtn = (Button) findViewById(R.id.check_button);
        //timeBtn.setOnClickListener(this);
        mTime = new CountDownTime(60000, 1000);//初始化对象

        //获取验证码按钮
        findViewById(R.id.check_button).setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                String phone=phone_editText.getText().toString();
                code[0] =GetCode();//获取系统生成的验证码
                phone_check[0] =phone;
                Log.e(TAG, code[0]);//日志打印
                Log.e(TAG,phone_check[0] );
                Log.e(TAG,phone);
                if(phone.length()!=11)
                {
                    Toast.makeText(Register.this,"手机号必须为11位", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /*int ret = 1;//判断网络是否可用
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        Process p = runtime.exec("ping -c 3 www.baidu.com");
                        ret = p.waitFor();//ret为0时即表示可用
                        Log.i("Avalible", "Process:"+ret);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    if(!isNetworkConnected(Register.this))//为1表示网络不可用
                        Toast.makeText(Register.this,"网络不可用", Toast.LENGTH_SHORT).show();
                    else{
                        try {
                            SendCode.SendSms(phone, code[0]);
                            Toast.makeText(Register.this,"获取成功", Toast.LENGTH_SHORT).show();
                            switch (view.getId()){
                                case R.id.check_button:
                                    mTime.start(); //开始计时
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(Register.this,"获取失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //确认注册按钮
        (findViewById(R.id.register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone=phone_editText.getText().toString().trim();
                final String pwd=pwd_editText.getText().toString().trim();
                String repwd=repwd_editText.getText().toString();
                String check=check_editText.getText().toString();
                if(phone.length()==11){
                    if(pwd.equals(repwd))//判断密码与确认密码是否一致
                    {
                        if(CheckPwd(pwd))//判断密码是否符合规则，验证码是否正确
                        {
                            if(code[0].equals(check)&&phone_check[0].equals(phone)&&!code[0].isEmpty())//判断验证码正确与否
                            {
                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        int res= DBUtils.InsertUserInfo(phone,pwd);
                                        Message msg = new Message();
                                        if(res==0)
                                            msg.what = 0;
                                        else
                                            msg.what = 1;
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                            }
                            else
                                Toast.makeText(Register.this,"验证码错误", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(Register.this,"密码不符合规范", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(Register.this,"密码与确认密码不一致", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Register.this,"手机号必须为11位", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String GetCode()//获取四位随机验证码
    {
        Random random = new Random();
        String code= random.nextInt(10000) + "";
        int len = code.length();
        if(len<4)
        {
            for(int i=1; i<=4-len; i++)
                code = "0" + code;
        }
        return code;
    }

    public boolean CheckPwd(String pwd)//密码检查，必须6-10位，包含大小写字母和数字
    {
        int len=pwd.length();
        int format_num_a = 0;
        int format_num_A = 0;
        int format_num_0 = 0;
        if(len>=6&&len<=10)
        {
            for(int i = 0; i < len; i++)
            {
                if(pwd.charAt(i) >= 'a' && pwd.charAt(i) <= 'z')
                {
                    format_num_a = 1;
                    continue;
                }
                else if(pwd.charAt(i) >= 'A' && pwd.charAt(i) <= 'Z')
                {
                    format_num_A = 1;
                    continue;
                }
                else if(pwd.charAt(i) >= '0' && pwd.charAt(i) <= '9')
                {
                    format_num_0 = 1;
                    continue;
                }
            }
        }
        int total = format_num_0 + format_num_a + format_num_A;
        return total >= 3 ? true : false;
    }

    /**
     * 第一种方法 使用android封装好的 CountDownTimer
     * 创建一个类继承 CountDownTimer
     */
    class CountDownTime extends CountDownTimer {

        //构造函数  第一个参数代表总的计时时长  第二个参数代表计时间隔  单位都是毫秒
        public CountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) { //每计时一次回调一次该方法
            timeBtn.setClickable(false);
            timeBtn.setText(l/1000 + "秒");
        }

        @Override
        public void onFinish() { //计时结束回调该方法
            timeBtn.setClickable(true);
            timeBtn.setText("获取验证码");
        }
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