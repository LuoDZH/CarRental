package com.example.bottombar.entity;

import android.app.Application;

/**
 * Created by hasee on 2018/11/12.
 */

public class User extends Application{
    private String phone;//用户账号
    private String name;//用户真实姓名
    private String pwd;//用户密码
    private String ID;//用户身份证号
    private int state;//用户状态
    private float balance;//用户余额
    private float deposit;//用户押金
    private int score;//用户积分

    public User()//空构造函数（全局对象必须拥有）
    {
        phone="";name="";pwd="";ID="";state=0;balance=0;deposit=0;score=60;
    }

    public User(String phone_1,String name_1,String pwd_1,String ID_1,int state_1,float balance_1,float deposit_1,int score_1)//构造函数
    {
        phone=phone_1;name=name_1;pwd=pwd_1;ID=ID_1;state=state_1;balance=balance_1;deposit=deposit_1;score=score_1;
    }

    /*public void setUser(String phone_1,String name_1,String pwd_1,String ID_1,int state_1,int balance_1,int deposit_1,int score_1)
    {
        phone=phone_1;name=name_1;pwd=pwd_1;ID=ID_1;state=state_1;balance=balance_1;deposit=deposit_1;score=score_1;
    }*/

    //获取设置对象属性
    public String getPhone() {
        return phone;
    }//Ps:编译器提供的就是getPhone 不是故意写出小写字母+大写字母的

    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    public int getState() {
        return state;
    }

    public String getID() {
        return ID;
    }

    public float getBalance() {
        return balance;
    }

    public float getDeposit() {
        return deposit;
    }

    public int getScore() {
        return score;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setDeposit(float deposit) {
        this.deposit = deposit;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();//超类onCreate
    }
}
