package com.example.bottombar.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 创建于 2018/12/3 14:42
 * 此类为订单类，每一条订单都是order类的一个对象
 * 继承了Parcelable类
 */
public class Order implements Parcelable {
    //订单编号
    private String order_number;
    //取车时间
    private String take_time;
    //还车时间
    private String return_time;
    //汽车编号
    private String car_number;
    //取车门店编号
    private String take_shop_number;
    //还车门店编号
    private String return_shop_number;
    //订单建立时间
    private String order_time;
    //取车地点
    private String take_place;
    //取车地点门店名称
    private String take_shop_name;
    //用户id
    private String user_id;
    //车类型、车名
    private String car_type,car_name;
    //订单状态
    private String order_state;
    //订单金额
    private String order_amount;
    //车辆押金
    private String car_deposit;

    public Order(){}
    public Order(String order_number,String take_time,String return_time,String take_place
            ,String user_id,String order_amount,String car_type,String car_name,String order_state,String order_time,String take_shop_name,String car_number){
        this.car_name = car_name;
        this.car_type = car_type;
        this.user_id = user_id;
        this.take_place = take_place;
        this.return_time = return_time;
        this.take_time = take_time;
        this.order_number = order_number;
        this.order_state = order_state;
        this.order_amount = order_amount;
        this.order_time = order_time;
        this.take_shop_name = take_shop_name;
        this.car_number = car_number;
    }

    public String getCar_name() {
        return car_name;
    }

    public String getOrder_number() {
        return order_number;
    }

    public String getTake_place() {
        return take_place;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCar_type() {
        return car_type;
    }

    public String getReturn_time() {
        return return_time;
    }

    public String getTake_time() {
        return take_time;
    }

    public String getOrder_state() {
        return order_state;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public String getCar_deposit(){return car_deposit;}
    /**
     * 设置订单金额
     * 对于某个订单，在取车时间和还车时间已经确定的情况下，通过daily_rent即可算出订单金额
     * @param daily_rent 日租金
     */
    public void setOrder_amount(float daily_rent) throws ParseException {
        Calendar take_time = Calendar.getInstance();
        Calendar return_time = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        take_time.setTime(df.parse(this.take_time));
        return_time.setTime(df.parse(this.return_time));
        long days =(return_time.getTime().getTime()-take_time.getTime().getTime())/(24*60*60*1000);
        if((return_time.getTime().getTime()-take_time.getTime().getTime())%(24*60*60*1000)!=0)
        {
            days ++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.order_amount = decimalFormat.format(days*daily_rent);
    }

    public String getTake_shop_name(){return take_shop_name;}

    public void setTake_shop_name(String take_shop_name) {
        this.take_shop_name = take_shop_name;
    }

    public String getCar_number() {
        return car_number;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getReturn_shop_number() {
        return return_shop_number;
    }

    public String getTake_shop_number() {
        return take_shop_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public void setReturn_shop_number(String return_shop_number) {
        this.return_shop_number = return_shop_number;
    }

    public void setReturn_time(String return_time) {
        this.return_time = return_time;
    }

    public void setTake_place(String take_place) {
        this.take_place = take_place;
    }

    public void setTake_shop_number(String take_shop_number) {
        this.take_shop_number = take_shop_number;
    }

    public void setTake_time(String take_time) {
        this.take_time = take_time;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setCar_deposit(String car_deposit){this.car_deposit = car_deposit;}

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 继承Parcelable必须要写的方法，用于传入order到栈中
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //该方法将类的数据写入外部提供的Parcel中.即打包需要传递的数据到Parcel容器保存，
        // 以便从parcel容器获取数据
        parcel.writeString(order_number);
        parcel.writeString(take_time);
        parcel.writeString(return_time);
        parcel.writeString(take_place);
        parcel.writeString(user_id);
        parcel.writeString(car_type);
        parcel.writeString(car_name);
        parcel.writeString(order_state);
        parcel.writeString(order_amount);
        parcel.writeString(order_time);
        parcel.writeString(take_shop_name);
        parcel.writeString(car_number);
    }

    /**
     * 继承Parcelable必须要写的方法，用于传出order
     */
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    /**
     * 定义如何读出order对象的所有属性
     * @param in
     */
    protected Order(Parcel in) {
        order_number = in.readString();
        take_time = in.readString();
        return_time = in.readString();
        take_place = in.readString();
        user_id = in.readString();
        car_type = in.readString();
        car_name = in.readString();
        order_state = in.readString();
        order_amount = in.readString();
        order_time = in.readString();
        take_shop_name = in.readString();
        car_number = in.readString();
    }
}
