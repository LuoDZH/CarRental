package com.example.bottombar.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.adapter.WalletDetailAdapter;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.WalletDetail;

import java.util.ArrayList;
import java.util.List;

import static com.example.bottombar.activity.MainActivity.user;

public class CheckWalletDetail extends AppCompatActivity {
    private List<WalletDetail> walletDetailList=new ArrayList<>();

    Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == 0) {
                InitRecycle();
                return true;
            }else{
                Toast.makeText(CheckWalletDetail.this,"暂无信息",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_wallet_detail);

        if (isNetworkConnected(CheckWalletDetail.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    walletDetailList = DBUtils.GetDetailItem(user.getPhone());
                    if(walletDetailList.isEmpty()){
                        msg.what = 1;
                    }
                    else{
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }else{
            Toast.makeText(CheckWalletDetail.this,"当前网络不可用，请连接网络",Toast.LENGTH_SHORT).show();
        }


    }

    public void InitRecycle() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.wallet_detail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        WalletDetailAdapter adapter = new WalletDetailAdapter(walletDetailList);
        recyclerView.setAdapter(adapter);
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
