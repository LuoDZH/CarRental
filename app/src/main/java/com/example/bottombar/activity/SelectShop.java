package com.example.bottombar.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.bottombar.entity.DBUtils;
import com.example.bottombar.R;
import com.example.bottombar.entity.Shop;
import com.example.bottombar.adapter.ShopAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * 此类与视图选择门店相连，是选择们门店的后台
 * 根据不同的点击操作，会选择不同的门店
 */
public class SelectShop extends AppCompatActivity {
    Shop shop=new Shop();
    private static final String TAG = "SelectShop";

    List<Shop> shopList=new ArrayList<>();
    List<Shop> cur_shopList = new ArrayList<>();
    public static SelectShop instance_SelectShop;
    List<String> shopAreaList = null;

    ShopAdapter shopAdapter;
    private List<Shop>filterShop;

    Handler handler = new Handler(new Handler.Callback() {//handler对象对线程传出信息进行处理
        @Override
        public boolean handleMessage(Message message) {
            if(message.what==1){
                Toast.makeText(SelectShop.this,"当前城市没有门店",Toast.LENGTH_SHORT).show();
            }
            else{
                init_listview();
            }
            return true;
        }
    });

    //设定过滤器
    private List<Shop>filter(List<Shop>shops,String text){
        List<Shop>shopArrayList=new ArrayList<>();
        if(shops!=null&&text!=null) {
            for (Shop shop : shops) {
                if (shop.getShopAddress().contains(text) || shop.getShopName().contains(text)) {
                    shopArrayList.add(shop);
                }
            }
        }
        //当删掉已输入字符时，还原回之前的情况
        if(text.equals(""))shopArrayList=cur_shopList;
        return shopArrayList;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);
        instance_SelectShop = this;
        //获取从上一个界面传过来的数据
        Intent intent = getIntent();
        final String shop_city = intent.getStringExtra("shop_city");
        Log.e(TAG, shop_city);

        //通过handle将数据库中的数据传到本地列表
        if (isNetworkConnected(SelectShop.this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg=new Message();
                    shopAreaList= DBUtils.GetShopArea(shop_city);
                    shopList = DBUtils.GetShopItem(shop_city);
                    Log.d(TAG, "run: shoplist"+shopList.size());
                    if(shopAreaList.isEmpty()){
                        msg.what=1;
                    }
                    else{
                        msg.what=0;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }else{
            Toast.makeText(SelectShop.this,"当前网络不可用，请连接网络",Toast.LENGTH_SHORT).show();
        }

        //筛选门店
        final SearchView searchView=(SearchView)findViewById(R.id.searchView);
        searchView.setQueryHint("请输入地址或店名");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterShop=filter(shopList,s);
                Log.d(TAG, "返回fitler方法后: "+filterShop.size());
                //将返回结果放到适配器中
                if(shopList.size()!=0)
                shopAdapter.setFilter(filterShop);

                return true;
            }
        });

    }
    /**
     * 初始化ListView的各种值，并设置其子项的监听
     */
    public void init_listview() {
        ListView listView=(ListView)findViewById(R.id.choose_place_shop_listView);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(SelectShop.this,android.R.layout.simple_list_item_1,shopAreaList);
        listView.setAdapter(adapter);
        init_list(shopAreaList.get(0));
        initRecycle();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                cur_shopList.clear();
                init_list (shopAreaList.get(position));
                initRecycle();
            }
        });
    }
    /**
     *  此函数用于初始化recycleview的数据填充
     *  以及recycleview 的显示格式
     */
    public void initRecycle(){
        RecyclerView recycle_view = (RecyclerView) findViewById(R.id.shop_recycleview);
        recycle_view.setLayoutManager(new LinearLayoutManager(SelectShop.this));
        shopAdapter = new ShopAdapter(SelectShop.this,cur_shopList );
        recycle_view.setAdapter(shopAdapter);
    }

    /**
     * 根据不同的门店地区，初始化门店list
     * @param shop_area 门店地区
     */
    public void init_list (String shop_area) {
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getShopArea().equals(shop_area)){
                cur_shopList.add(shopList.get(i));
            }
        }
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
