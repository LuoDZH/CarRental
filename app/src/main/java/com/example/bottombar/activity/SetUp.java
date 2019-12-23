package com.example.bottombar.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bottombar.R;
import com.example.bottombar.entity.User;

import static com.example.bottombar.activity.MainActivity.user;

public class SetUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        TextView tv_balance=findViewById(R.id.textView_phone);
        tv_balance.setText(user.getPhone());
        TextView tv_deposit=findViewById(R.id.textView_name);
        tv_deposit.setText(user.getName());
        TextView tv_score=findViewById(R.id.textView_id);
        if(!user.getID().equals("")) {
            tv_score.setText(user.getID());
        }
        else{
            tv_score.setText("暂无信息");
        }

        (findViewById(R.id.modify_pwd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetUp.this,ModifyPassword.class);
                startActivity(intent);
            }
        });

        (findViewById(R.id.button_log_off)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=new User();
                Intent intent = new Intent(SetUp.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }
}
