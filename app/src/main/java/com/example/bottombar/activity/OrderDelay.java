package com.example.bottombar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.OnMultiClickListener;
import com.example.bottombar.entity.Order;

import org.feezu.liuli.timeselector.TimeSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.bottombar.activity.MainActivity.curr_order;


public class OrderDelay extends AppCompatActivity {
    private float daily_rent;
    private Order order;
    EditText new_time;
    public static boolean isDelay = false;
    public static OrderDelay instance_OrderDelay;
    public static float order_amo;
    public static Order copy_order;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_delay);
        copy_order = curr_order;

        instance_OrderDelay = this;
        Intent intent =	getIntent();
        order = intent.getParcelableExtra("order");
        bitmap = stringToBitmap(intent.getStringExtra("bitmap"));

        ImageView car_image = findViewById(R.id.car_imageview);
        TextView order_number = findViewById(R.id.order_number);
        TextView car_name = findViewById(R.id.car_name);
        TextView car_type = findViewById(R.id.car_type);
        new_time = findViewById(R.id.new_time);
        final TextView addititon_rent = findViewById(R.id.additional_rent);

        car_image.setImageBitmap(bitmap);
        order_number.setText("订单号： " + order.getOrder_number());
        car_name.setText(order.getCar_name());
        car_type.setText(order.getCar_type());
        new_time.setText("请选择还车时间：");
        addititon_rent.setText("0");
        order.setTake_time(order.getReturn_time());
        order_amo = Float.parseFloat(order.getOrder_amount());


        new Thread(new Runnable() {
            @Override
            public void run() {
                daily_rent = DBUtils.getDaily_rent(order.getCar_name());
            }
        }).start();

        //获取当前时间——时
        String str_time = null;
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
        Calendar calendar= Calendar.getInstance();
        Date r_time = new Date();
        try {
            r_time = formatter.parse(order.getReturn_time());
            calendar.setTime(r_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        order.setTake_time(formatter.format(r_time));
        str_time = formatter.format(r_time);

        //设置选择取车时间弹窗框
        final TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                new_time.setText(time);
                order.setReturn_time(new_time.getText().toString());
                try {
                    order.setOrder_amount(daily_rent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                addititon_rent.setText(order.getOrder_amount());
            }
        }, str_time, "2019-12-31 23:59","9:00","18:00");
        new_time.setFocusable(false);          //设置文本框用户不可写，但可响应点击事件
        new_time.setFocusableInTouchMode(false);
        new_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelector.show();
            }
        });

        Button submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                if (isNetworkConnected(OrderDelay.this)){
                    if(!new_time.getText().toString().equals("请选择还车时间：")){
                        if(order.getTake_time().equals(order.getReturn_time())){
                            Toast.makeText(OrderDelay.this, "日期未改变，请重新选择!" , Toast.LENGTH_SHORT).show();
                        }else{
                            onCreateDialog();
                        }
                    }else{
                        Toast.makeText(OrderDelay.this, "请选择还车时间!" , Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(OrderDelay.this, "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void onCreateDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("您将延长订单！");
        builder.setMessage("原还车时间为："+ order.getTake_time() + "\n新还车时间为：" + order.getReturn_time());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                curr_order = order;
                Intent intent = new Intent(OrderDelay.this,OrderPay.class);
                isDelay = true;
                startActivity(intent);
            }
        });
        builder.setNegativeButton("手滑了！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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
