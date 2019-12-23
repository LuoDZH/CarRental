package com.example.bottombar.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bottombar.R;
import com.example.bottombar.activity.Map;
import com.example.bottombar.activity.QuestionActivity;
import com.example.bottombar.activity.SelectCar;
import com.example.bottombar.activity.SelectShop;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import org.feezu.liuli.timeselector.TimeSelector;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.bottombar.activity.MainActivity.curr_order;

public class Fragment1 extends Fragment {
    @Nullable
    private List<HotCity> hotCities;
    private static String city="";
    private static final String TAG = "Fragment1";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment1, container, false);//为碎片获取对应布局
        //获取文本框对象
        final EditText editText_taketime = (EditText) view.findViewById(R.id.editText_taketime);//取车时间
        final EditText editText_returntime = (EditText) view.findViewById(R.id.editText_returntime);//还车时间
        final EditText editText_address = (EditText) view.findViewById(R.id.editText_address);//取车地点
        final EditText editText_city = (EditText) view.findViewById(R.id.editText_city);//取车城市



        Time time = new Time();//获取当前时间——时
        time.setToNow();
        int hour = time.hour;
        Log.e(TAG, String.valueOf(hour));

        String str_time = null;
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
        SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");
        Calendar calendar= Calendar.getInstance();
        if(hour<9)//当前时间小于九点，取车时间开始值为当天九点
        {
            str_time = formatter1.format(calendar.getTime())+" 9:00";
            Log.e(TAG, str_time);
        }
        else if (hour>17) //当前时间大于下午五点点，取车时间开始值为第二天天九点
        {
            calendar.add(Calendar.DATE,1);
            str_time = formatter1.format(calendar.getTime())+" 9:00";
            Log.e(TAG, str_time);
        }
        else//否则开始值为当前时间
        {
            str_time = formatter.format(calendar.getTime());
            Log.e(TAG, str_time);
        }

        //设置选择取车时间弹窗框
        final TimeSelector timeSelector = new TimeSelector(getContext(), new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                editText_taketime.setText(time);
                curr_order.setTake_time(editText_taketime.getText().toString());
            }
        }, str_time, "2019-12-31 23:59","9:00","18:00");
        editText_taketime.setFocusable(false);          //设置文本框用户不可写，但可响应点击事件
        editText_taketime.setFocusableInTouchMode(false);
        editText_taketime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelector.show();
            }
        });

        //设置选择还车时间弹窗框
        final TimeSelector timeSelector1 = new TimeSelector(getContext(), new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                Calendar take_time = Calendar.getInstance();//转换字符串到日期时间
                Calendar return_time = Calendar.getInstance();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                try {
                    take_time.setTime(df.parse(editText_taketime.getText().toString()));
                    return_time.setTime(df.parse(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(take_time.before(return_time))
                {
                    editText_returntime.setText(time);
                    curr_order.setReturn_time(editText_returntime.getText().toString());
                }
                else{
                    Toast.makeText(getContext(),"还车时间需大于取车时间",Toast.LENGTH_SHORT).show();
                }
            }
        }, str_time, "2019-12-31 23:59","9:00","18:00");
        editText_returntime.setFocusable(false);
        editText_returntime.setFocusableInTouchMode(false);
        editText_returntime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_taketime.getText().toString().equals(""))
                {
                    Toast.makeText(getContext(),"请先选择取车时间",Toast.LENGTH_SHORT).show();
                }
                else
                    timeSelector1.show();
            }
        });

        //设置选择地区文本框
        editText_address.setFocusable(false);
        editText_address.setFocusableInTouchMode(false);
        editText_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_city.getText().toString().equals(""))//提示未选城市
                {
                    Toast.makeText(getContext(),"请先选择城市",Toast.LENGTH_SHORT).show();
                }
                else
                {   //跳转选择门店页面
                    Intent intent = new Intent(getActivity(),SelectShop.class);
                    intent.putExtra("shop_city",editText_city.getText().toString());//传入城市信息
                    startActivity(intent);
                }
            }
        });

        hotCities = new ArrayList<>();//设置热门城市
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));
        hotCities.add(new HotCity("长沙", "湖南", "101210101"));

        //设置选择城市文本框
        editText_city.setFocusable(false);
        editText_city.setFocusableInTouchMode(false);
        editText_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPicker.getInstance()    //弹出选择城市
                        .setFragmentManager(getFragmentManager())
                        .enableAnimation(true)
                        .setAnimationStyle(R.style.DefaultCityPickerAnimation)
                        .setLocatedCity(null)
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                editText_city.setText(data == null ? "长沙" : String.format("%s", data.getName()));//设置当前城市
                                city=editText_city.getText().toString();

                                if(!editText_address.getText().toString().equals("")){//更改城市后，清空地区文本框信息
                                    editText_address.setText("");
                                    curr_order.setTake_shop_name("");
                                }
                            }
                            @Override
                            public void onLocate() {    //定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.getInstance().locateComplete(new LocatedCity("长沙", "湖南", "101250101"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                            }
                        })
                        .show();
            }
        });

        //去选车按钮
        (view.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断信息是否都已填写
                if(!editText_address.getText().toString().equals("") && !editText_city.getText().toString().equals("") && !editText_taketime.getText().toString().equals("") && !editText_returntime.getText().toString().equals(""))
                {
                    Calendar take_time = Calendar.getInstance();//转换字符串到日期时间
                    Calendar return_time = Calendar.getInstance();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    try {
                        take_time.setTime(df.parse(editText_taketime.getText().toString()));
                        return_time.setTime(df.parse(editText_returntime.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(take_time.before(return_time))//判断是否还车时间大于选车时间
                    {
                        Intent intent = new Intent(getActivity(),SelectCar.class);//跳转选车界面
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getContext(),"还车时间需大于取车时间",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(),"请填选完整信息",Toast.LENGTH_SHORT).show();

            }
        });

        // 联系客服
        (view.findViewById(R.id.imageButton2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call("15574946385");
            }
        });
        //查找地图
        (view.findViewById(R.id.imageButton4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(),Map.class);
                startActivity(intent);
            }
        });
        (view.findViewById(R.id.imageButton3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(),QuestionActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void onResume(){//更新碎片信息，更新充值、提现操作后的钱包信息显示
        super.onResume();
        EditText editText_address = getView().findViewById(R.id.editText_address);//获取文本框对象
        EditText editText_taketime = getView().findViewById(R.id.editText_taketime);//获取文本框对象
        EditText editText_returntime = getView().findViewById(R.id.editText_returntime);//获取文本框对象
        EditText editText_city = getView().findViewById(R.id.editText_city);
        if(curr_order.getTake_shop_name()!=null)
            editText_address.setText(curr_order.getTake_shop_name());
        if(!city.equals(""))
            editText_city.setText(city);
        if (curr_order.getTake_time()!=null)
            editText_taketime.setText(curr_order.getTake_time());
        if (curr_order.getReturn_time()!=null)
            editText_returntime.setText(curr_order.getReturn_time());
    }

    private void call(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }
}
