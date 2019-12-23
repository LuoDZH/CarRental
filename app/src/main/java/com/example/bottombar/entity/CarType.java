package com.example.bottombar.entity;

/**
 * Created by hasee on 2018/11/28.
 */

public class CarType extends Throwable {
    private String car_number;//汽车编号
    private String car_name;//汽车名，如宝马x7
    private String shop_number;//汽车所属门牌号
    private String carPicture;
    private String car_brand;//汽车品牌，如宝马
    private String car_type;//汽车类型，如经济型，豪华型
    private String dailyRent;//日租金
    private String carDeposit;//汽车押金

    //构造方法传入参数
    public CarType(String car_number,String car_name,String shop_number,String carPicture,String car_brand,String car_type,String dailyRent,String carDeposit)
    {
        this.car_number=car_number;
        this.car_name=car_name;
        this.shop_number=shop_number;
        this.carPicture=carPicture;
        this.car_brand=car_brand;
        this.car_type=car_type;
        this.dailyRent=dailyRent;
        this.carDeposit=carDeposit;
    }
    public CarType(){}

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
    }

    public void setCarDeposit(String carDeposit) {
        this.carDeposit = carDeposit;
    }

    public void setDailyRent(String dailyRent) {
        this.dailyRent = dailyRent;
    }

    public void setCarPicture(String carPicture) {
        this.carPicture = carPicture;
    }

    public void setShop_number(String shop_number) {
        this.shop_number = shop_number;
    }

    public String getCar_number() {
        return car_number;
    }

    public String getCar_type() {
        return car_type;
    }

    public String getCar_name() {
        return car_name;
    }

    public String getCarPicture() {
        return carPicture;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public String getDailyRent() {
        return dailyRent;
    }

    public String getCarDeposit() {
        return carDeposit;
    }

    public String getShop_number() {
        return shop_number;
    }
}

