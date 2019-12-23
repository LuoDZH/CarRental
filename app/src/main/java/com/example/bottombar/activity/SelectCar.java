package com.example.bottombar.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.R;
import com.example.bottombar.adapter.CarTypeAdapter;
import com.example.bottombar.adapter.SelectCarListViewAdapter;
import com.example.bottombar.entity.CarType;

import java.util.ArrayList;
import java.util.List;

import static com.example.bottombar.activity.MainActivity.curr_order;
import static com.example.bottombar.fragment.Fragment2.isRefresh;

/**
 * 此类与视图选择车辆相连，是选择车辆的后台
 * 根据不同的点击操作，会选择不同的车辆，并提示不同的信息
 */
public class SelectCar extends AppCompatActivity {
    final static String TAG="SelectCar";
    public static SelectCar instance_SelectCar;
    //存储汽车种类的列表
    private String []carTypes = {"经济型","商务型","豪华型"};
    private List<CarType> carList = new ArrayList<>();
    private List<CarType> curr_carList = new ArrayList<>();

    private DrawerLayout dlShow;//侧滑界面
    private GridView brand;//侧滑界面的品牌部分布局
    private GridView price;//侧滑界面的价格部分布局
    private String[] priceList = {"000~199","200~399", "400~599", "600~799"};
    int flag;//判定要不要执行筛选价格功能
    RecyclerView recycle_view;
    CarTypeAdapter orderAdapter;
    Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
        @Override
        public boolean handleMessage(Message message) {
            if(message.what == 0) {
                init_list(carTypes[0]);
                init_recycle(recycle_view,curr_carList);
                return true;
            }else{
                Toast.makeText(SelectCar.this,"当前门店没有这种类型的车",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance_SelectCar = this;
        setContentView(R.layout.activity_select_car);
        dlShow=(DrawerLayout)findViewById(R.id.dlShow);
        isRefresh=true;

        if (isNetworkConnected(SelectCar.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg=new Message();
                    carList= DBUtils.GetCarItem(curr_order.getTake_shop_number());
                    Log.d(TAG, "run:carList.size() "+carList.size());
                    if(carList.isEmpty()){
                        msg.what = 1;
                    }
                    else{
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);

                }
            }).start();
        }else{
            Toast.makeText(SelectCar.this,"当前网络不可用，请连接网络",Toast.LENGTH_SHORT).show();
        }


        //筛选汽车 界面
        price= (GridView)findViewById(R.id.gprice);
        final TextView price_edit=(TextView)findViewById(R.id.choose_price);
        Button screen=(Button)findViewById(R.id.choose_carType);

        final SelectCarListViewAdapter arrayAdapter3 = new SelectCarListViewAdapter(SelectCar.this,R.layout.item_screen_car, priceList);
        arrayAdapter3.notifyDataSetChanged();
        price.setAdapter(arrayAdapter3);

        price.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                flag=1;//设置为1,表示将启动价格过滤器，当price_edit有变化时会自动启动
                String tprice=priceList[position];
                price_edit.setText(tprice);
                arrayAdapter3.setCurrentItem(position);
                arrayAdapter3.notifyDataSetChanged();

            }
        });

        ListView listView=(ListView)findViewById(R.id.shop_area);
        ArrayAdapter<String>listadapter=new ArrayAdapter<String>(SelectCar.this,android.R.layout.simple_list_item_1,carTypes);
        listView.setAdapter(listadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                flag=0;//设置为0,表示此时不用价格过滤器进行过滤
                String car_type=carTypes[position];
                curr_carList.clear();
                init_list(car_type);
                init_recycle(recycle_view,curr_carList);
                price_edit.setText("");
                if(curr_carList.size()>0)
                    Toast.makeText(SelectCar.this,car_type,Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(SelectCar.this,"目前没有"+car_type+"的汽车",Toast.LENGTH_SHORT).show();
            }
        });


        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开侧滑布局
                dlShow.openDrawer(Gravity.RIGHT);
            }
        });
        //只有price_edit有数字时才能执行判断
        Log.d(TAG, "onCreate: flag"+flag);

            price_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(price_edit!=null&&flag==1) {
                        recycle_view = (RecyclerView) findViewById(R.id.cars);
                        //设置recyclerview的布局方式，此处设为线性
                        recycle_view.setLayoutManager(new LinearLayoutManager(SelectCar.this));
                        //这里orderAdapter传入总的carList，避免在某种类型中筛选
                        orderAdapter = new CarTypeAdapter(SelectCar.this, carList);
                        orderAdapter.getFilter().filter(charSequence.toString());
                        orderAdapter.notifyDataSetChanged();
                        recycle_view.setAdapter(orderAdapter);
                        Log.d(TAG, "执行了onTextChanged:curr_carList大小为 " + curr_carList.size());
                        Log.d(TAG, "执行了onTextChanged:carList 大小为" + carList.size());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

}
    /**
     *  此函数用于初始化recycleview的数据填充
     *  以及recycleview 的显示格式
     */
    public void init_recycle(RecyclerView recycle_view,List<CarType>curr_carList) {
         recycle_view = (RecyclerView) findViewById(R.id.cars);
        //设置recyclerview的布局方式，此处设为线性
        recycle_view.setLayoutManager(new LinearLayoutManager(SelectCar.this));
        orderAdapter = new CarTypeAdapter(SelectCar.this,curr_carList );
        recycle_view.setAdapter(orderAdapter);
        Log.d(TAG, "执行了init_recycle:curr_carList大小为 "+curr_carList.size());
        Log.d(TAG, "执行了init_recycle:carList 大小为"+carList.size());

    }
    /**
     * 根据不同的汽车类型，初始化车辆list
     * @param car_type 汽车类型
     */
    public void init_list (String car_type) {
        for (int i = 0; i < carList.size(); i++) {
            if (carList.get(i).getCar_type().equals(car_type)){
                //保证同名车辆只出现一次
                int flag = 1;
                for (int j =0;j< curr_carList.size();j++)
                {
                    if (curr_carList.get(j).getCar_name().equals(carList.get(i).getCar_name())){
                        flag = 0;
                    }
                }
                if(flag == 1)
                    curr_carList.add(carList.get(i));
            }
        }
        Log.d(TAG, "执行了inilist:curr_carList大小为 "+curr_carList.size());
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


