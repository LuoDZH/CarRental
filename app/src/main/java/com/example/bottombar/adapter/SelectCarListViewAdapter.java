package com.example.bottombar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bottombar.R;


public class SelectCarListViewAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String [] strings;
    private int currentItem;
    //这是资源item
    private int textResourceId;
    public SelectCarListViewAdapter(Context mContext, int textResourceId , String [] strings){
        super(mContext,textResourceId,strings);
        this.textResourceId=textResourceId;
        this.mContext=mContext;
        this.strings =strings;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        int currentItem=i;
//        View gridView=View.inflate(mContext, R.layout.item,null);
        View gridView=LayoutInflater.from(getContext()).inflate(textResourceId,viewGroup,false);
        TextView textView =(TextView)gridView.findViewById(R.id.text);
        textView.setText(strings[i]);
        if(i==currentItem){
           textView.setTextColor(Color.RED);
        }else{
            textView.setTextColor(Color.BLACK);
        }
        return gridView;
    }
}
