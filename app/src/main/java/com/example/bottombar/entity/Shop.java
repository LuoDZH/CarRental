package com.example.bottombar.entity;

/**
 * Created by hasee on 2018/11/28.
 */

public class Shop {
    //构造私有属性，对应于数据库中的字段
    private String shopNumber;
    private String shopCity;
    private String shopArea;
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String shopHours;
    public Shop(){};
    public Shop(String shopNumber,String shopCity,String shopArea,String shopName,String shopAddress,String shopPhone,String shopHours){
        this.shopNumber = shopNumber;//门店号
        this.shopCity = shopCity;//门店所处城市
        this.shopArea = shopArea;//门店所处地区
        this.shopName = shopName;//门店名
        this.shopAddress = shopAddress;//门店具体地址
        this.shopPhone = shopPhone;//门店联系方式
        this.shopHours = shopHours;//门店营业时间
    }

    public String getShopArea(){return shopArea;}

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getShopCity() {
        return shopCity;
    }

    public void setShopCity(String shopCity) {
        this.shopCity = shopCity;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getShopHours() {
        return shopHours;
    }

    public void setShopHours(String shopHours) {
        this.shopHours = shopHours;
    }
}

