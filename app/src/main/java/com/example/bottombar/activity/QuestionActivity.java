package com.example.bottombar.activity;

/*
作为显示问题标题的列表界面
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.bottombar.R;
import com.example.bottombar.entity.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    //将Question中的具体标题和内容都赋值给现在的标题和内容
    Question question=new Question();
    private String []title=question.getTitle();
    private String [] content=question.getContent();
    private int [] image={R.drawable.dayu};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ListView listView=(ListView)findViewById(R.id.question_listView);
        List<Map<String,Object>>data=new ArrayList<>();
        List<String>titlelist=new ArrayList<>();
        //通过循环加入图标，标题和内容
        for(int i = 0; i< title.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image",image[0]);
            map.put("title", title[i]);
            map.put("content",content[i]);
            data.add(map);
            titlelist.add(title[i]);

        }

        //simpleAdapter的五个参数分别是，context,map型的list（存储了所有的数据和映射）,listview中的布局item,item中每个控件的名字（从data的key中提取），item中每个控件的id
        //所以这个与后来的title，content是两个东西，不能混淆
        SimpleAdapter adapter=new SimpleAdapter (this,data,
                R.layout.item_question,new String[]{"title","image"}, new int[]{R.id.title_item,R.id.image_item});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", title[position]);
                bundle.putString("content",content[position] );
                Intent intent=new Intent();
                intent.putExtras(bundle);
                intent.setClass(QuestionActivity.this,QuestionContentActivtiy.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}



