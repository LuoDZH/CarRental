package com.example.bottombar.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.Order;
import com.example.bottombar.adapter.OrderAdapter;
import com.example.bottombar.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.bottombar.activity.MainActivity.user;

/**
 * 创建于 2018/12/3 14:32
 * 此类用于显示界面“我的”，通过连接数据得到数据
 * 并将重要数据显示在界面上
 */

public class Fragment2 extends Fragment {
    //用于存储要传入recycleview的数据
    private List<Order> orderList = new ArrayList<>();
    View view;
    //0代表此时显示未完成订单，1代表此时显示已完成订单。
    private static int finish_flag;
    public static boolean isRefresh = false;

    //通过handler获取数据
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                init_recycle();
                return true;
            }
            return false;
        }
    });


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        //为碎片获取对应布局
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment2, container, false);

        if(isNetworkConnected(getContext())){
            init_unfinished();
        }else {
            Toast.makeText(getActivity(), "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
        }
        //连接界面的完成按钮和未完成按钮
        Button unfinished_button = view.findViewById(R.id.unfinished_order);
        Button finish_button = view.findViewById(R.id.finished_order);
        //设置监听，如果点击，那么通过改变列表种存储的值改变界面显示的订单。
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finish_flag != 1){
                    if(isNetworkConnected(getContext())){
                        init_finished();
                        init_recycle();
                    }else {
                        Toast.makeText(getActivity(), "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
                    }
                    finish_flag = 1;
                }

            }
        });

        unfinished_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finish_flag != 0){
                    if(isNetworkConnected(getContext())){
                        init_unfinished();
                        init_recycle();
                    }else {
                        Toast.makeText(getActivity(), "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
                    }
                    finish_flag = 0;
                }
            }
        });
        return view;
    }

    /**
     * 此函数用于更新碎片信息，更新订单信息变化之后的显示
     */
   public void onResume(){
        super.onResume();
        if (isRefresh ){
            if(isNetworkConnected(getContext())){
                if (finish_flag == 0){
                    init_unfinished();
                    init_recycle();
                }else if(finish_flag == 1){
                    init_finished();
                    init_recycle();
                }
            }else {
                Toast.makeText(getActivity(), "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
            }
            isRefresh = false;
        }
    }

    /**
     *  此函数用于初始化recycleview的数据填充
     *  以及recycleview 的显示格式
     */
    public void init_recycle() {
        RecyclerView recycle_view = (RecyclerView) view.findViewById(R.id.recycle_view);
        recycle_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        OrderAdapter orderAdapter = new OrderAdapter(getActivity(), orderList);
        recycle_view.setHasFixedSize(true);
        recycle_view.setAdapter(orderAdapter);
    }

    /**
     * 此函数用于初始化订单状态为已完成的订单数组
     * 遍历所有订单，将订单状态是“已完成”的存入数组
     */
    private void init_finished() {
        orderList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                orderList = DBUtils.GetFinishedOrder(user.getPhone());
                if(orderList.isEmpty()){
                    msg.what = 1;
                }else{
                    msg.what = 0;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 此函数用于初始化订单状态为“未完成”的订单数组
     * 遍历所有订单，将订单状态是“未完成”的存入数组
     */
    private void init_unfinished() {
        orderList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                orderList = DBUtils.GetUnfinishedOrder(user.getPhone());
                if(orderList.isEmpty()){
                    msg.what = 1;
                }else{
                    msg.what = 0;
                }
                handler.sendMessage(msg);
            }
        }).start();
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