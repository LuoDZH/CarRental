package com.example.bottombar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.bottombar.activity.MainActivity.curr_order;
import static com.example.bottombar.activity.MainActivity.user;
import static com.example.bottombar.activity.OrderDelay.copy_order;
import static com.example.bottombar.activity.OrderDelay.instance_OrderDelay;
import static com.example.bottombar.activity.OrderDelay.isDelay;
import static com.example.bottombar.activity.OrderDelay.order_amo;
import static com.example.bottombar.activity.OrderDetail.instance_OrderDetail;

/**
 * 此类与视图订单支付相连，是订单支付的后台
 * 会根据用户选择的支付类型，做出不同的变化
 */
public class OrderPay extends AppCompatActivity {
    //当前账户余额，从user中读入
    private float temp_balance;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pay);

        InitPrice();

        final RadioButton balance_pay = findViewById(R.id.balance_pay);
        final RadioButton cash_pay = findViewById(R.id.cash_pay);
        final RadioButton alipay_pay = findViewById(R.id.alipay_pay);
        Button pay_button = findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置订单生成时间
                SimpleDateFormat formatter  =  new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date curDate =  new Date(System.currentTimeMillis());
                curr_order.setOrder_time(formatter.format(curDate));
                //设置订单编号
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmss");//设置订单编号
                Date curDate1 =  new Date(System.currentTimeMillis());
                String str = formatter1.format(curDate1)+user.getPhone().substring(7);
                if (!isDelay) curr_order.setOrder_number(str);
                if (isNetworkConnected(OrderPay.this)){
                    InitPay(balance_pay,cash_pay,alipay_pay);
                }else{
                    Toast.makeText(OrderPay.this, "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 根据选择的按钮不同，做出不同的判断（支付或暂不支持）
     * @param balance_pay 按钮：余额支付
     * @param cash_pay 按钮：现金支付
     * @param alipay_pay 按钮：支付宝支付
     */
    private void InitPay(RadioButton balance_pay,RadioButton cash_pay,Button alipay_pay){
        if (balance_pay.isChecked()){
            temp_balance = user.getBalance();
            if(temp_balance > Float.parseFloat(curr_order.getOrder_amount())){
                if(!isDelay){
                    Toast.makeText(OrderPay.this, "支付成功！", Toast.LENGTH_SHORT).show();
                    //如果支付成功，就把当前订单信息传入下一个界面
                    curr_order.setOrder_state("未取车");

                    Intent intent = new Intent(OrderPay.this,OrderDetail.class);
                    intent.putExtra("order",curr_order);

                    //利用线程访问数据库
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBUtils.UpdateUserBalance(temp_balance-Float.parseFloat(curr_order.getOrder_amount()),curr_order.getUser_id());
                            DBUtils.InsertOrderInfo(curr_order);
                            DBUtils.UpdateCarState(curr_order.getCar_number(),"已租");
                            DBUtils.InsertWalletDetail(user.getPhone(),curr_order.getOrder_time(),"租车",Float.parseFloat(curr_order.getOrder_amount()));
                        }
                    }).start();
                    //更新用户余额信息
                    user.setBalance(temp_balance-Float.parseFloat(curr_order.getOrder_amount()));
                    startActivity(intent);
                    OrderSubmit.instance_orderSubmit.finish();
                    SelectCar.instance_SelectCar.finish();
                    finish();
                }else{
                    Toast.makeText(OrderPay.this, "延长订单成功", Toast.LENGTH_SHORT).show();
                    final float temp_amount = Float.parseFloat(curr_order.getOrder_amount());
                    final String re_time =curr_order.getReturn_time();
                    final String or_time = curr_order.getOrder_time();
                    final String or_number = curr_order.getOrder_number();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBUtils.UpdateOrder(temp_amount+order_amo,re_time,or_time,or_number);
                            DBUtils.UpdateUserBalance(temp_balance - temp_amount,user.getPhone());
                            DBUtils.InsertWalletDetail(user.getPhone(),or_time,"延长租车",temp_amount);
                        }
                    }).start();
                    //更新用户余额信息
                    user.setBalance(temp_balance-Float.parseFloat(curr_order.getOrder_amount()));
                    curr_order = copy_order;
                    isDelay = false;
                    instance_OrderDelay.finish();
                    instance_OrderDetail.finish();
                    finish();
                }

            }else{
                Toast.makeText(OrderPay.this, "您的余额不足，请充值后再试！", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(OrderPay.this, "暂不支持除账户余额支付的其他类型支付！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 对价钱格式进行初始化，并更新到界面
     */
    private void InitPrice() {
        float price = Float.parseFloat(curr_order.getOrder_amount());
        //保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String order_amount = "￥ "+decimalFormat.format(price);
        TextView price_view = findViewById(R.id.price);
        price_view.setText(order_amount);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(isDelay){
                isDelay = false;
                curr_order = copy_order;
            }
            finish();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
