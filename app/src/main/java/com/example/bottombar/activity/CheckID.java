package com.example.bottombar.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.example.bottombar.activity.MainActivity.user;

public class CheckID extends AppCompatActivity {
    private static final String TAG = "CheckID";

    Handler handler = new Handler(new Handler.Callback() {//打印日志标识
        @Override
        public boolean handleMessage(Message message) {//handler对象对线程传出信息进行处理
            if(message.what == 0) {
                Toast.makeText(CheckID.this,"认证失败", Toast.LENGTH_SHORT).show();
            }
            else{//认证成功跳转到首页
                Toast.makeText(CheckID.this,"认证成功", Toast.LENGTH_SHORT).show();
                MainActivity.instance.finish();//销毁之前的主活动
                Intent intent = new Intent(CheckID.this,MainActivity.class);
                startActivity(intent);
                finish(); //销毁当前活动
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_id);

        //获取文本框对象
        final EditText id_editText = (EditText)findViewById(R.id.editText_id);
        final EditText name_editText = (EditText)findViewById(R.id.editText_name);

        //认证按钮
        (findViewById(R.id.button_check)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id=id_editText.getText().toString().trim();//身份证号
                final String name=name_editText.getText().toString().trim();//用户姓名
                Log.e(TAG, id);
                //创建线程调用身份证实名认证接口
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String check="{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\""+name+"\",\"idcard\":\""+id+"\",\"isok\":true}}";
                        Message msg = new Message();
                        OkHttpClient client = new OkHttpClient();//使用OkHttp连接接口
                        Request request = new Request.Builder()
                            .url("http://apis.haoservice.com/idcard/VerifyIdcard?cardNo="+id+"&realName="+name+"&key=XXXXXX")
                            .get()
                            .build();

                        try {
                            Response response = client.newCall(request).execute();
                            String re=response.body().string();
                            if(check.equals(re)) {//认证成功则更新用户对象以及数据库
                                msg.what = 1;
                                user.setID(id);
                                user.setName(name);
                                user.setState(1);
                                String phone=user.getPhone();
                                DBUtils.UpdateUserState(phone,id,name);
                            }
                            else
                                msg.what = 0;
                        } catch (IOException e) {
                             e.printStackTrace();
                        }
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }
}
