package com.example.bottombar.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bottombar.R;
import com.example.bottombar.entity.WalletDetail;

import java.util.List;

/**
 * Created by hasee on 2018/12/10.
 */

public class WalletDetailAdapter extends RecyclerView.Adapter<WalletDetailAdapter.ViewHolder>{
    private Context context;
    WalletDetail walletDetail;
    private List<WalletDetail> mWalletDetailList;

    static class  ViewHolder extends RecyclerView.ViewHolder{
        //设置监听器用
        CardView cardView;
        TextView operation_time,operation ,amount;
        //获取每个条目的图片和文字
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            operation = itemView.findViewById(R.id.textView_operation);
            operation_time = itemView.findViewById(R.id.textView_time);
            amount = itemView.findViewById(R.id.textView_amount);
        }
    }

    public WalletDetailAdapter(List<WalletDetail> walletDetailList){
        mWalletDetailList=walletDetailList;
    }

    @Override
    public WalletDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_detail,parent,false);
        return new WalletDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WalletDetailAdapter.ViewHolder viewHolder, int position) {
        WalletDetail walletDetail = mWalletDetailList.get(position);
        viewHolder.operation.setText(walletDetail.getOperation());
        viewHolder.operation_time.setText(walletDetail.getOperation_time());
        if(walletDetail.getOperation().contains("提现")||walletDetail.getOperation().equals("租车")||walletDetail.getOperation().equals("延长租车"))
            viewHolder.amount.setText("-"+walletDetail.getAmount());
        else
            viewHolder.amount.setText("+"+walletDetail.getAmount());
    }

    @Override
    public int getItemCount() {
        return mWalletDetailList.size();
    }
}
