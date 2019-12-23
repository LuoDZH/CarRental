package com.example.bottombar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.OnMultiClickListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.bottombar.activity.MainActivity.user;

public class Wallet extends Activity {
    private static final String TAG = "Wallet";//打印日志标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        final ProgressDialog progressDialog = new ProgressDialog(Wallet.this);
        //progressDialog.setTitle("这是一个 progressDialog");//2.设置标题
        progressDialog.setMessage("正在加载中，请稍等...");//3.设置显示内容
        progressDialog.setCancelable(false);//4.设置可否用back键关闭对话框

        //文本显示
        TextView tv_balance=findViewById(R.id.textView_balance);
        tv_balance.setText(user.getBalance()+"元");
        TextView tv_deposit=findViewById(R.id.textView_deposit);
        tv_deposit.setText(user.getDeposit()+"元");
        TextView tv_score=findViewById(R.id.textView_score);
        tv_score.setText(user.getScore()+"分");

        final Handler handler1 = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                switch (msg.what){
                    case 0:
                        Toast.makeText(Wallet.this, "请输入正确的金额", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Wallet.this, "总金额不得超过1,000,000元", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(Wallet.this, "充值成功！", Toast.LENGTH_SHORT).show();//提示充值成功
                        refresh();//刷新活动
                        break;
                    case 3:
                        Toast.makeText(Wallet.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        //余额充值按钮，因悬浮框为半自定义，不能写成函数，只能重复使用代码，否则需要的代码会更多
        (findViewById(R.id.button_recharge_balance)).setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if(user.getState()==0) {  //用户未认证不得充值
                    Toast.makeText(Wallet.this, "请先认证", Toast.LENGTH_SHORT).show();
                }
                else{
                    view = getLayoutInflater().inflate(R.layout.recharge_withdraw, null);//获取recharge_withdraw.xml文件用于半自定义的弹窗
                    final EditText editText = (EditText) view.findViewById(R.id.editText_amounts);//获取文本框对象

                    TextView title = new TextView(Wallet.this);//设置弹窗标题参数，使其居中 //因悬浮框为半自定义，不能写成函数
                    title.setText("充值余额");
                    title.setGravity(Gravity.CENTER);
                    title.setPadding(10, 10, 10, 10);
                    title.setTextSize(23);

                    AlertDialog dialog = new AlertDialog.Builder(Wallet.this)
                            .setCustomTitle(title)//设置对话框的标题
                            .setView(view)        //获取视图（半自定义弹窗需获取）
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {//设置取消按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置确认按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Message msg = new Message();
                                            String money=editText.getText().toString();
                                            final float amounts;
                                            if(money.isEmpty())
                                            {
                                                msg.what = 0;
                                            }
                                            else{
                                                amounts= Float.parseFloat(money);//获取输入金额
                                                if(amounts<=0||decimal_num(money)>2)//输入金额<=0或者小数位大于2 提示
                                                {
                                                    msg.what = 0;
                                                }
                                                else if(amounts+user.getBalance()>999999)
                                                {
                                                    msg.what = 1;
                                                }
                                                else {
                                                    if (network_check() == 0) {
                                                        final String tem=(new BigDecimal(String.valueOf(user.getBalance())).add(new BigDecimal( String.valueOf(amounts)))).toString();
                                                        final float temp= Float.parseFloat(tem);
                                                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                        final Date curDate = new Date(System.currentTimeMillis());
                                                        new Thread(new Runnable() {//调用线程更新数据库字段
                                                            @Override
                                                            public void run() {
                                                                DBUtils.UpdateUserBalance(temp, user.getPhone());
                                                                DBUtils.InsertWalletDetail(user.getPhone(), formatter.format(curDate), "充值余额", amounts);
                                                            }
                                                        }).start();
                                                        user.setBalance(temp); //设置用户对象属性
                                                        msg.what = 2;
                                                    } else {
                                                        msg.what = 3;
                                                    }
                                                }
                                            }
                                            handler1.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();//退出弹窗
                                }
                            }).create();
                    dialog.show();
                }
            }
        });

        final Handler handler2 = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                switch (msg.what){
                    case 0:
                        Toast.makeText(Wallet.this, "请输入正确的金额", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Wallet.this, "总金额不得超过1,000,000元", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(Wallet.this, "充值成功！", Toast.LENGTH_SHORT).show();//提示充值成功
                        refresh();//刷新活动
                        break;
                    case 3:
                        Toast.makeText(Wallet.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        //押金充值按钮，因悬浮框为半自定义，不能写成函数
        (findViewById(R.id.button_recharge_deposit)).setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if(user.getState()==0)
                    Toast.makeText(Wallet.this,"请先认证", Toast.LENGTH_SHORT).show();
                else{
                    view = getLayoutInflater().inflate(R.layout.recharge_withdraw, null);
                    final EditText editText = (EditText) view.findViewById(R.id.editText_amounts);

                    TextView title = new TextView(Wallet.this);
                    title.setText("充值押金");
                    title.setGravity(Gravity.CENTER);
                    title.setPadding(10, 10, 10, 10);
                    title.setTextSize(23);

                    AlertDialog dialog = new AlertDialog.Builder(Wallet.this)
                            .setCustomTitle(title)//设置对话框的标题
                            .setView(view)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Message msg = new Message();
                                            String money=editText.getText().toString();
                                            Log.e(TAG, money);
                                            final float amounts;
                                            if(money.isEmpty())
                                            {
                                                msg.what = 0;
                                            }
                                            else{
                                                    amounts= Float.parseFloat(money);//获取输入金额
                                                    Log.e(TAG, String.valueOf(amounts));
                                                    if(amounts<=0||decimal_num(money)>2)//输入金额<=0或者小数位大于2 提示
                                                    {
                                                        msg.what = 0;
                                                    }
                                                    else if(amounts+user.getDeposit()>999999) {
                                                        msg.what = 1;
                                                    }
                                                    else {
                                                        if (network_check() == 0) {
                                                            //final float temp = user.getDeposit() + amounts;
                                                            final String tem=(new BigDecimal(String.valueOf(user.getDeposit())).add(new BigDecimal( String.valueOf(amounts)))).toString();
                                                            final float temp= Float.parseFloat(tem);
                                                            Log.e(TAG, String.valueOf(temp));
                                                            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                            final Date curDate = new Date(System.currentTimeMillis());
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    DBUtils.UpdateUserDeposit(temp, user.getPhone());
                                                                    DBUtils.InsertWalletDetail(user.getPhone(), formatter.format(curDate), "充值押金", amounts);
                                                                }
                                                            }).start();
                                                            user.setDeposit(temp);
                                                            msg.what = 2;
                                                        } else {
                                                            msg.what = 3;
                                                        }
                                                    }
                                                }
                                            handler2.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
            }
        });

        final Handler handler3 = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                switch (msg.what){
                    case 0:
                        Toast.makeText(Wallet.this, "请输入正确的金额", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Wallet.this, "提现成功！", Toast.LENGTH_SHORT).show();//提示充值成功
                        refresh();//刷新活动
                        break;
                    case 2:
                        Toast.makeText(Wallet.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        //余额提现按钮，因悬浮框为半自定义，不能写成函数
        (findViewById(R.id.button_withdraw_balance)).setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if(user.getState()==0) {  //用户未认证不得充值
                    Toast.makeText(Wallet.this, "请先认证", Toast.LENGTH_SHORT).show();
                }
                else{
                    view = getLayoutInflater().inflate(R.layout.recharge_withdraw, null);
                    final EditText editText = (EditText) view.findViewById(R.id.editText_amounts);

                    TextView title = new TextView(Wallet.this);
                    title.setText("提现余额");
                    title.setGravity(Gravity.CENTER);
                    title.setPadding(10, 10, 10, 10);
                    title.setTextSize(23);

                    AlertDialog dialog = new AlertDialog.Builder(Wallet.this)
                            .setCustomTitle(title)//设置对话框的标题
                            .setView(view)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Message msg = new Message();
                                            String money=editText.getText().toString();
                                            final float amounts;
                                            if(money.isEmpty())
                                            {
                                                msg.what = 0;
                                            }
                                            else {
                                                amounts = Float.parseFloat(money);//获取输入金额
                                                if (amounts <= 0 || amounts > user.getBalance() || decimal_num(money) > 2)//输入金额<=0或者小数位大于2 提示
                                                {
                                                    msg.what = 0;
                                                } else {
                                                    if (network_check() == 0) {
                                                        final String tem=(new BigDecimal(String.valueOf(user.getBalance())).subtract(new BigDecimal( String.valueOf(amounts)))).toString();
                                                        final float temp= Float.parseFloat(tem);
                                                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                        final Date curDate = new Date(System.currentTimeMillis());
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                DBUtils.UpdateUserBalance(temp, user.getPhone());
                                                                DBUtils.InsertWalletDetail(user.getPhone(), formatter.format(curDate), "提现余额", amounts);
                                                            }
                                                        }).start();
                                                        user.setBalance(temp);
                                                        msg.what = 1;
                                                    } else {
                                                        msg.what = 2;
                                                    }
                                                }
                                            }
                                            handler3.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
            }
        });

        final Handler handler4 = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                switch (msg.what){
                    case 0:
                        Toast.makeText(Wallet.this, "请输入正确的金额", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(Wallet.this, "提现成功！", Toast.LENGTH_SHORT).show();//提示充值成功
                        refresh();//刷新活动
                        break;
                    case 2:
                        Toast.makeText(Wallet.this, "网络不可用！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        final Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
            @Override
            public boolean handleMessage(Message message) {
                if(message.what==1) {
                    View view;
                    view = getLayoutInflater().inflate(R.layout.recharge_withdraw, null);
                    final EditText editText = (EditText) view.findViewById(R.id.editText_amounts);

                    TextView title = new TextView(Wallet.this);
                    title.setText("提现押金");
                    title.setGravity(Gravity.CENTER);
                    title.setPadding(10, 10, 10, 10);
                    title.setTextSize(23);

                    AlertDialog dialog = new AlertDialog.Builder(Wallet.this)
                            .setCustomTitle(title)//设置对话框的标题
                            .setView(view)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Message msg = new Message();
                                            String money=editText.getText().toString();
                                            final float amounts;
                                            if(money.isEmpty())
                                            {
                                                msg.what = 0;
                                            }
                                            else {
                                                    amounts = Float.parseFloat(money);//获取输入金额
                                                    if (amounts <= 0 || amounts > user.getDeposit() || decimal_num(money) > 2)//输入金额<=0或者小数位大于2 提示
                                                    {
                                                        msg.what = 0;
                                                    } else {
                                                        if (network_check() == 0) {
                                                            final String tem=(new BigDecimal(String.valueOf(user.getDeposit())).subtract(new BigDecimal( String.valueOf(amounts)))).toString();
                                                            final float temp= Float.parseFloat(tem);
                                                            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                            final Date curDate = new Date(System.currentTimeMillis());
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    DBUtils.UpdateUserDeposit(temp, user.getPhone());
                                                                    DBUtils.InsertWalletDetail(user.getPhone(), formatter.format(curDate), "提现押金", amounts);
                                                                }
                                                            }).start();
                                                            user.setDeposit(temp);
                                                            msg.what = 1;
                                                        } else {
                                                            msg.what = 2;
                                                        }
                                                    }
                                            }
                                            handler4.sendMessage(msg);
                                        }
                                    }).start();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
                else{
                    Toast.makeText(Wallet.this, "未还车不可提现押金", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //押金提现按钮
        (findViewById(R.id.button_withdraw_deposit)).setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if(user.getState()==0) {  //用户未认证不得充值
                    Toast.makeText(Wallet.this, "请先认证", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = DBUtils.CheckOrder(user.getPhone());
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        (findViewById(R.id.textView_wallte_detail)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Wallet.this,CheckWalletDetail.class);
                startActivity(intent);
            }
        });
    }

    public void refresh() {//刷新当前页面
        onCreate(null);
    }

    public int network_check(){//网络检查
        int ret = 1;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("ping -c 3 www.baidu.com");
            ret = p.waitFor();//ret为0时即表示网络可用
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int decimal_num(String num){//获取小数位
        int decimal_Digits = 0;
        Log.e(TAG, num);
        int indexOf = num.indexOf(".");
        if(indexOf==-1)
        {
            return decimal_Digits;
        }
        else
        {
            decimal_Digits = num.length() - 1 - indexOf;
            Log.e(TAG, String.valueOf(decimal_Digits));
            return decimal_Digits;
        }
    }
}