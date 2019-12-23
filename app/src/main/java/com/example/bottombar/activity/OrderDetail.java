package com.example.bottombar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.entity.OnMultiClickListener;
import com.example.bottombar.entity.Order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.bottombar.activity.MainActivity.user;
import static com.example.bottombar.fragment.Fragment2.isRefresh;

/**
 * 此类与视图订单详情相连，是订单详情的后台
 * 根据不同的订单信息，显示订单的各种详细信息，用户可选择取消订单或延长订单
 */
public class OrderDetail extends AppCompatActivity {
    Order order = new Order();
    public Button cancel_button;
    public Button delay_button;
    public static OrderDetail instance_OrderDetail;
    Bitmap bitmap;
    String str_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        instance_OrderDetail = this;
        Intent intent =	getIntent();
        order = intent.getParcelableExtra("order");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                str_bitmap = DBUtils.GetCarImage(order.getCar_name());
                bitmap = stringToBitmap(str_bitmap);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InitInfo();

        cancel_button = findViewById(R.id.cancel_button);
        delay_button = findViewById(R.id.delay_button);

        if (order.getOrder_state().equals("已取消")||order.getOrder_state().equals("已完成")||order.getOrder_state().equals("已还车"))
        {
            cancel_button.setVisibility(View.INVISIBLE);
            delay_button.setVisibility(View.INVISIBLE);
        }

        cancel_button.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                SimpleDateFormat formatter  =  new  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date curDate =  new Date(System.currentTimeMillis());
                Date take_time = new Date();
                try {
                    take_time = formatter.parse(order.getTake_time());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (curDate.before(take_time) && order.getOrder_state().equals("未取车")){
                    onCreateDialog();
                }else{
                    Toast.makeText(OrderDetail.this, "当前订单状态无法取消订单，请联系客服获取更多信息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delay_button.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                SimpleDateFormat formatter  =  new  SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date curDate =  new Date(System.currentTimeMillis());
                Date return_time = new Date();
                try {
                    return_time = formatter.parse(order.getReturn_time());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(curDate.after(return_time)||order.getOrder_state().equals("已延长")){
                    Toast.makeText(OrderDetail.this,"当前订单无法延长",Toast.LENGTH_SHORT).show();
                }else{
                    if (isNetworkConnected(OrderDetail.this)){
                        Intent intent = new Intent(OrderDetail.this,OrderDelay.class);
                        isRefresh = true;
                        intent.putExtra("order",order);
                        intent.putExtra("bitmap",str_bitmap);
                        startActivity(intent);
                    }else{
                        Toast.makeText(OrderDetail.this, "当前网络不可用，请连接网络!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void onCreateDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("确定要取消订单么？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //1.返现，2.添加钱包明细，3.订单信息不删除，改为已取消,4.车状态改为未租
                final float balance = user.getBalance();
                if (isNetworkConnected(OrderDetail.this)){
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleDateFormat formatter  =  new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date curDate =  new Date(System.currentTimeMillis());
                            DBUtils.UpdateUserBalance(balance + Float.parseFloat(order.getOrder_amount()),order.getUser_id());
                            DBUtils.InsertWalletDetail(order.getUser_id(),formatter.format(curDate),"退款",Float.parseFloat(order.getOrder_amount()));
                            DBUtils.UpdateOrderState(order.getOrder_number(),"已取消");
                            DBUtils.UpdateCarState(order.getCar_number(),"未租");
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    user.setBalance(balance + Float.parseFloat(order.getOrder_amount()));
                    Toast.makeText(OrderDetail.this, "取消订单成功！", Toast.LENGTH_SHORT).show();
                    order.setOrder_state("已取消");
                    cancel_button.setVisibility(View.INVISIBLE);
                    delay_button.setVisibility(View.INVISIBLE);
                    InitInfo();
                    isRefresh = true;
                }else {
                    Toast.makeText(OrderDetail.this, "当前网络不可用，请连接网络", Toast.LENGTH_SHORT).show();
                }
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

    /**
     * 订单详情界面初始化，根据不同的订单信息设置不同的值
     */
    private void InitInfo () {
        TextView order_number = findViewById(R.id.order_number);
        TextView order_state = findViewById(R.id.order_state);
        ImageView car_image = findViewById(R.id.car_imageview);
        TextView car_name = findViewById(R.id.car_name);
        TextView car_type = findViewById(R.id.car_type);
        TextView take_time = findViewById(R.id.take_time);
        TextView return_time = findViewById(R.id.return_time);
        TextView take_place = findViewById(R.id.take_place);
        TextView price = findViewById(R.id.price);
        TextView phone = findViewById(R.id.tel);
        TextView order_time = findViewById(R.id.order_time);
        TextView take_shop_name = findViewById(R.id.shop_name);

        Calendar t_time = Calendar.getInstance();
        Calendar r_time = Calendar.getInstance();
        Calendar o_time = Calendar.getInstance();
        final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        final DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        try {
            t_time.setTime(df1.parse(order.getTake_time()));
            r_time.setTime(df1.parse(order.getReturn_time()));
            o_time.setTime(df3.parse(order.getOrder_time()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm E",Locale.getDefault());

        String t_date = df2.format(t_time.getTime());
        String r_date = df2.format(r_time.getTime());
        String o_date = df3.format(o_time.getTime());

        order_number.setText(String.format(this.getResources().getString(R.string.order_info_item), order.getOrder_number()));
        order_state.setText(order.getOrder_state());

        car_image.setImageBitmap(bitmap);
        car_name.setText(order.getCar_name());
        car_type.setText(order.getCar_type());
        take_time.setText("取车时间： "+ t_date);
        return_time.setText("还车时间： "+ r_date);
        order_time.setText("订单生成时间： " + o_date);
        take_place.setText(String.format(this.getResources().getString(R.string.take_place_item), order.getTake_place()));
        take_shop_name.setText("门店名称： " + order.getTake_shop_name());
        phone.setText("客户联系方式： "+ order.getUser_id());
        price.setText(order.getOrder_amount());
    }
}