package com.example.bottombar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.User;

import static com.example.bottombar.activity.MainActivity.user;

public class ModifyPassword extends AppCompatActivity {

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
                    Toast.makeText(ModifyPassword.this,"网络不可用", Toast.LENGTH_SHORT).show();
                else    //为0表示其他情况修改失败
                    Toast.makeText(ModifyPassword.this,"修改失败", Toast.LENGTH_SHORT).show();
            }
            else    //注册成功并跳转登录页面
            {
                Toast.makeText(ModifyPassword.this,"修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                user=new User();
                Intent intent = new Intent(ModifyPassword.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        final EditText pwd_editText = (EditText) findViewById(R.id.editText_pwd);//密码
        final EditText newpwd_editText = (EditText) findViewById(R.id.editText_newpwd);//新密码
        final EditText repwd_editText = (EditText) findViewById(R.id.editText_repwd);//确认密码

        (findViewById(R.id.button_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pwd = pwd_editText.getText().toString().trim();
                final String new_pwd = newpwd_editText.getText().toString().trim();
                String repwd = repwd_editText.getText().toString();

                if (pwd.equals(user.getPwd()))//判断密码是否正确
                {
                    if(!new_pwd.equals(pwd))
                    {
                        if (CheckPwd(new_pwd))//判断新密码是否符合规则
                        {
                            if (repwd.equals(new_pwd))//判断新密码与旧密码是否一致
                            {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int res = DBUtils.UpdateUserPwd(new_pwd,user.getPhone());
                                        Message msg = new Message();
                                        if (res == 0)
                                            msg.what = 0;
                                        else
                                            msg.what = 1;
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                            } else
                                Toast.makeText(ModifyPassword.this, "确认密码错误", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(ModifyPassword.this, "新密码不符合规范", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ModifyPassword.this, "新密码不能与旧密码相同", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(ModifyPassword.this, "密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean CheckPwd(String pwd)//密码检查，必须6-10位，包含字母和数字
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
        return total >= 2 ? true : false;
    }
}
