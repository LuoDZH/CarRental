package com.example.bottombar.entity;

/**
 * Created by hasee on 2018/12/10.
 */

public class WalletDetail {
    private String operation_time;
    private String operation;
    private float amount;

    public WalletDetail(){}
    public WalletDetail(String operation_time,String operation,float amount){
        this.operation_time=operation_time;
        this.operation=operation;
        this.amount=amount;
    }

    public float getAmount() {
        return amount;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperation_time() {
        return operation_time;
    }
}
