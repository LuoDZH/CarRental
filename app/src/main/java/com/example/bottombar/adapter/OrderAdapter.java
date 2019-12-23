package com.example.bottombar.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.activity.OrderDetail;
import com.example.bottombar.R;
import com.example.bottombar.entity.Order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 此类用于为RecyclerView创建适配器，使其中的每一个子项个性化
 * 创建于 2018/12/3 14:32
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> mOrderList;
    private Context context;

    /**
     * 声明子项中所有的view
     * 并进行连接
     */
    static class ViewHolder extends	RecyclerView.ViewHolder	{
        CardView order_info;
        TextView order_number,take_time,return_time,take_place,tel,car_info,order_state;
        public ViewHolder(View view)	{
            super(view);
            order_info =	view.findViewById(R.id.card_view);
            order_number = view.findViewById(R.id.order_number);
            take_time = view.findViewById(R.id.take_time);
            return_time = view.findViewById(R.id.return_time);
            take_place = view.findViewById(R.id.take_place);
            tel = view.findViewById(R.id.order_time);
            car_info = view.findViewById(R.id.carinfo);
            order_state = view.findViewById(R.id.order_state);
        }
    }

    /**
     * OrderAdapter构造函数，传入一个list和标志context
     * @param context 上下文
     * @param OrderList 订单列表
     */
    public OrderAdapter (Context context,List <Order> OrderList){
        this.context = context;
        mOrderList = OrderList;
    }

    /**
     * 获取与之对应的layout，并返回视图类型
     * @param parent 父视图
     * @param viewType 视图类型
     */
    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_infocard,parent,false);
        return new ViewHolder(view);
    }

    /**
     * 根据信息，对子项中的各种视图自定义
     * @param viewHolder 视图holder
     * @param position 当前子项位置
     */
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder viewHolder, final int position) {
        Order order = mOrderList.get(position);
        Calendar take_time = Calendar.getInstance();
        Calendar return_time = Calendar.getInstance();
        final DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        try {
            take_time.setTime(df1.parse(order.getTake_time()));
            return_time.setTime(df1.parse(order.getReturn_time()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd E",Locale.getDefault());
        String t_date = df2.format(take_time.getTime());
        String r_date = df2.format(return_time.getTime());

        //根据订单信息来重新填充视图
        viewHolder.order_number.setText(String.format(context.getResources().getString(R.string.order_info_item), order.getOrder_number()));
        viewHolder.take_time.setText(String.format(context.getResources().getString(R.string.take_time),t_date));
        viewHolder.return_time.setText(String.format(context.getResources().getString(R.string.return_time),r_date));
        viewHolder.take_place.setText(String.format(context.getResources().getString(R.string.take_place_item), order.getTake_place()));
        viewHolder.tel.setText(String.format(context.getResources().getString(R.string.tel_item), order.getUser_id()));
        viewHolder.car_info.setText(String.format(context.getResources().getString(R.string.carInfo_item), order.getCar_name(), order.getCar_type()));
        viewHolder.order_state.setText(order.getOrder_state());
        viewHolder.order_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "CardView被点击！", Toast.LENGTH_SHORT).show();
                if (isNetworkConnected(context)){
                    Intent intent = new Intent(context,OrderDetail.class);
                    intent.putExtra("order", mOrderList.get(position));
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "当前网络不可用，请连接网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 返回列表大小
     */
    @Override
    public int getItemCount() {
        return mOrderList.size();
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