package com.example.bottombar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.adapter.CarTypeAdapter;
import com.example.bottombar.entity.Order;

import static com.example.bottombar.activity.MainActivity.curr_order;

/**
 * 此类与视图提交订单相连，是提交订单的后台
 * 根据不同的订单信息，显示订单的各种信息，用户可选择提交或者返回
 */
public class OrderSubmit extends AppCompatActivity {
    private Order order ;
    public static OrderSubmit instance_orderSubmit;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance_orderSubmit = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);
        Intent intent=getIntent();
        String carPicture=intent.getStringExtra("carPicture");
        bitmap=stringToBitmap(carPicture);
        InitInfo();

        Button submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected(OrderSubmit.this)){
                    Intent intent = new Intent(OrderSubmit.this,OrderPay.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(OrderSubmit.this, "当前网络不可用，请连接网络" , Toast.LENGTH_SHORT).show();
                }

            }
        });
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
     * 提交订单界面初始化，根据不同的订单信息设置不同的值
     */
    private void InitInfo () {
        order = curr_order;
        ImageView car_image = findViewById(R.id.car_imageview);
        TextView car_type = findViewById(R.id.car_type_submit);
        TextView car_name=findViewById(R.id.car_name_submit);
        TextView take_time = findViewById(R.id.take_time);
        TextView return_time = findViewById(R.id.return_time);
        TextView take_place = findViewById(R.id.take_place);
        TextView price_rent = findViewById(R.id.price_rent);
        TextView price_deposit = findViewById(R.id.price_deposit);

        car_image.setImageBitmap(bitmap);

        car_type.setText(order.getCar_type());
        car_name.setText(order.getCar_name());
        take_time.setText("取车时间： "+order.getTake_time());
        return_time.setText("还车时间： "+order.getReturn_time());
        take_place.setText(String.format(this.getResources().getString(R.string.take_place_item),order.getTake_place()));
        price_rent.setText(order.getOrder_amount()+'元');
        price_deposit.setText(order.getCar_deposit()+"元");
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
}

