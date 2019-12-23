package com.example.bottombar.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.bottombar.R;

/*
查看具体问题信息的界面，接受不同的问题信息，显示不同的界面
 */
public class QuestionContentActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_content_activtiy);
        //通过Bundle获得传过来的对应选中的title和content，然后将它赋值给相应的textview
        Bundle bundle=getIntent().getExtras();
        String questionTitle=bundle.getString("title");
        String questionContent=bundle.getString("content");
        TextView title=(TextView) findViewById(R.id.question_title);
        title.setText(questionTitle);
        TextView content=(TextView) findViewById(R.id.question_content);
        content.setText(questionContent);


    }

}
