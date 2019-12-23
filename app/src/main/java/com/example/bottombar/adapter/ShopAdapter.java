package com.example.bottombar.adapter;

/**
 * Created by hasee on 2018/11/28.
 */
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bottombar.R;
import com.example.bottombar.entity.Shop;

import java.util.List;

import static com.example.bottombar.activity.MainActivity.curr_order;
import static com.example.bottombar.activity.SelectShop.instance_SelectShop;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder>{
    //表示当前上下文环境
    private Context context;
    //存储列表在Adapter，列表内容将会表现在recyclerview中
    private List<Shop> shopList;
    //private Shop shop;
    static class  ViewHolder extends RecyclerView.ViewHolder{
        //设置监听器用
        CardView shop_item;
        TextView shop_address;
        TextView shop_name;
        TextView shop_time;
        TextView contact_tel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shop_item = itemView.findViewById(R.id.shop_cardView);
            shop_name = itemView.findViewById(R.id.shop_name);
            shop_address = itemView.findViewById(R.id.shop_address);
            shop_time = itemView.findViewById(R.id.shop_time);
            contact_tel = itemView.findViewById(R.id.contact_tel);
        }
    }
    //构造方法传入对应的列表
    public ShopAdapter(Context context, List<Shop>shops){
        this.context=context;
        this.shopList=shops;
    }

    //创建viewholder实例，加载car_type_item布局,并传入到构造方法中，返回
    @Override
    public ShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_shop,parent,false);
        return new ViewHolder(view);
    }

    //对item子项进行赋值，在每个子项被滚动到屏幕时执行，通过position得到当前的
//    CarType实例，再将数值设置到ViewHolder中
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Shop shop=shopList.get(position);
        viewHolder.shop_address.setText(shop.getShopAddress());
        viewHolder.shop_name.setText(shop.getShopName());
        viewHolder.contact_tel.setText(shop.getShopPhone());
        viewHolder.shop_time.setText(shop.getShopHours());
        viewHolder.shop_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "CardView被点击！", Toast.LENGTH_SHORT).show();
                curr_order.setReturn_shop_number(shopList.get(position).getShopNumber());
                curr_order.setTake_shop_number(shopList.get(position).getShopNumber());
                curr_order.setTake_place(shopList.get(position).getShopAddress());
                curr_order.setTake_shop_name(shopList.get(position).getShopName());
                instance_SelectShop.finish();
            }
        });
    }

    //重写函数之一，返回列表大小
    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public void setFilter(List<Shop>filterShop){
        shopList=filterShop;
        notifyDataSetChanged();
    }
}
