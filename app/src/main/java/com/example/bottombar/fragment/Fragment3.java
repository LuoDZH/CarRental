package com.example.bottombar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bottombar.activity.CheckID;
import com.example.bottombar.R;
import com.example.bottombar.activity.SetUp;
import com.example.bottombar.activity.Wallet;
import static com.example.bottombar.activity.MainActivity.user;

public class Fragment3 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment3, container, false);//为碎片获取对应布局

        TextView tv_name=view.findViewById(R.id.textView_username);//设置用户名
        tv_name.setText(user.getName());

        TextView tv_state=view.findViewById(R.id.textView_state);
        if(user.getState()==0)//根据用户认证状态设置是否认证标识和认证按钮
        {
            tv_state.setText("未认证");
            (view.findViewById(R.id.button_check)).setVisibility(View.VISIBLE);
        }
        else
        {
            tv_state.setText("已认证");
            (view.findViewById(R.id.button_check)).setVisibility(View.INVISIBLE);
        }

        //设置钱包相关信息显示
        TextView tv_balance=view.findViewById(R.id.textView_balance);
        tv_balance.setText(user.getBalance()+"元");
        TextView tv_deposit=view.findViewById(R.id.textView_deposit);
        tv_deposit.setText(user.getDeposit()+"元");
        TextView tv_score=view.findViewById(R.id.textView_score);
        tv_score.setText(user.getScore()+"分");

        //认证按钮，跳转认证页面
        (view.findViewById(R.id.button_check)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CheckID.class);
                startActivity(intent);
            }
        });

        //认证按钮，跳转认证页面
        (view.findViewById(R.id.button_withdraw_deposit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SetUp.class);
                startActivity(intent);
            }
        });

        //打开钱包
        (view.findViewById(R.id.Open_wallet)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Wallet.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onResume(){//更新碎片信息，更新充值、提现操作后的钱包信息显示
        super.onResume();
        TextView tv_balance=getView().findViewById(R.id.textView_balance);
        tv_balance.setText(user.getBalance()+"元");
        TextView tv_deposit=getView().findViewById(R.id.textView_deposit);
        tv_deposit.setText(user.getDeposit()+"元");
        TextView tv_score=getView().findViewById(R.id.textView_score);
        tv_score.setText(user.getScore()+"分");
    }
}
