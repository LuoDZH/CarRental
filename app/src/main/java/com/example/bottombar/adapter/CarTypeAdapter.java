package com.example.bottombar.adapter;

/**
 * Created by hasee on 2018/11/28.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.activity.OrderSubmit;
import com.example.bottombar.R;
import com.example.bottombar.entity.CarType;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.bottombar.activity.MainActivity.curr_order;

public class CarTypeAdapter extends RecyclerView.Adapter<CarTypeAdapter.ViewHolder>{
    final static String TAG="CarTypeAdapter";
    private Context context;
    CarType carType;
    private List<CarType>mCarTypeList;
    private List<CarType>mFilterList;

    private Bitmap bitmap;
    public Bitmap getBitmap(){
        return bitmap;
    }

    static class  ViewHolder extends RecyclerView.ViewHolder{
        //设置监听器用
        CardView cardView;
        ImageView car_image;
        TextView car_type,car_name,daily_rent;
        //获取每个条目的图片和文字
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.car_item);
            car_image = itemView.findViewById(R.id.car_image);
            car_name = itemView.findViewById(R.id.car_name);
            car_type = itemView.findViewById(R.id.car_type);
            daily_rent = itemView.findViewById(R.id.daily_rent);
        }
    }
    //构造方法传入对应的列表，从activity中传入，adapter接受并执行
    public CarTypeAdapter(Context context, List<CarType>carTypeList){
        this.context=context;
        mCarTypeList=carTypeList;
        mFilterList=carTypeList;
    }
    public void setL(List<CarType>carTypes){
        this.mCarTypeList=carTypes;
        this.mFilterList=carTypes;
    }
    //创建viewholder实例，加载car_type_item布局,并传入到构造方法中，返回
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_car_type,parent,false);
        return new ViewHolder(view);
    }

    //对item子项进行赋值，在每个子项被滚动到屏幕时执行，通过position得到当前的
    //  CarType实例，再将数值设置到ViewHolder中
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //用过滤过的mFilterList来显示汽车信息
        carType=mFilterList.get(position);
        viewHolder.car_name.setText(carType.getCar_name());
        viewHolder.car_type.setText(carType.getCar_type());
        viewHolder.daily_rent.setText(carType.getDailyRent());
        final String carPicture=carType.getCarPicture();
        bitmap=stringToBitmap(carPicture);
        viewHolder.car_image.setImageBitmap(bitmap);

        //设置监听器
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "CardView"+position+"被点击！", Toast.LENGTH_SHORT).show();
                //将列表中对应的参数传到当前订单中，这是订单表中的属性
                curr_order.setCar_number(mFilterList.get(position).getCar_number());
                curr_order.setCar_name(mFilterList.get(position).getCar_name());
                curr_order.setCar_type(mFilterList.get(position).getCar_type());
                curr_order.setCar_deposit(mCarTypeList.get(position).getCarDeposit());
                try {
                    //传入日租金
                    curr_order.setOrder_amount(Float.parseFloat(mFilterList.get(position).getDailyRent()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context,OrderSubmit.class);
                intent.putExtra("carPicture",carPicture);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }


    //这里fitler继承自wiget
    public Filter getFilter(){
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //初始化过滤结果对象
                FilterResults results=new FilterResults();
                //获得搜索关键字
                String charString=charSequence.toString();
                //存放暂时过滤结果
                ArrayList<CarType>list=new ArrayList<>();
                //没有过滤数据就使用源数据
                if(charString.isEmpty()){
                    mFilterList=mCarTypeList;
                }else {
                    List<CarType>filteredList=new ArrayList<>();
                    for (int i=0;i< mCarTypeList.size();i++) {
                        CarType carType=mCarTypeList.get(i);
                        String rent=carType.getDailyRent();
                        //判定数值小于100时的情况
                        if(carType.getDailyRent().length()==5){
                            rent="0"+carType.getDailyRent();
                        }
                        if (rent.compareTo(charString.substring(0,3))>0
                                &&rent.compareTo(charString.substring(4,7))<=0) {
                            filteredList.add(carType);
                            Log.d(TAG, "进来了: "+rent+" "+charString+"  filteredList.size "+filteredList.size());
                        }
                        else {
                            Log.d(TAG, "没进来，"+rent+" "+charString+" "+ " filteredList.size"+filteredList.size()+" "+
                                    rent.compareTo(charString));

                        }
                    }
                    mFilterList=filteredList;
                   for(int i=0;i<mFilterList.size();i++){
                       CarType carType=mFilterList.get(i);
                       for(int j=i+1;j<mFilterList.size();j++){
                           CarType carType1=mFilterList.get(j);
                           if(carType.getCar_type().equals(carType1.getCar_type())&&
                                   carType.getCar_name().equals(carType1.getCar_name())){
                               mFilterList.remove(j);
                               //列表长度的减了，计数跟着减
                               j--;
                           }
                       }
                   }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=mFilterList;
                return filterResults;
            }
            //返回过滤后的值
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterList=(ArrayList<CarType>)filterResults.values;
                if(mFilterList.size()==0){
                    Toast.makeText(context,"目前无车辆",Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            }
        };
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
    public void  list(){
        if(mCarTypeList.size()>0)
        for(int i=0;i<mCarTypeList.size();i++){
            Log.d(TAG, "list: "+mCarTypeList.get(i));
       }
    }

}

