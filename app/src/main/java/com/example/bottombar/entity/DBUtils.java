package com.example.bottombar.entity;

/**
 * Created by hasee on 2018/11/6.
 */

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    private static final String TAG = "DBUtils";
    private static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "47.xxx.xxx.101";
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "user", "123456");//登陆数据库使用的对象与密码
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }
    
    public static String GetUserPwdByPhone(String phone) {//获取用户密码
        String pwd = null;
        Connection conn = getConnection("car_rent");
        String sql = "select user_pwd from user where user_phone = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,phone);
            ResultSet res = ps.executeQuery();
            if (res == null) {
                return null;
            }
            else {
                while (res.next()) {
                    pwd = res.getString(1);
                }
            }
            conn.close();
            ps.close();
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");

        }
        return pwd;
    }

    public static int InsertUserInfo(String phone,String pwd) {//插入用户信息
        Connection conn = getConnection("car_rent");
        String sql = "insert into user(user_phone,user_name,user_pwd,state) VALUES(?,?,?,?)";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, phone);
            ps.setString(3, pwd);
            ps.setInt(4, 0);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateUserState(String phone,String id,String name) {//更新用户认证状态
        Connection conn = getConnection("car_rent");
        String sql = "update user set user_name=?,id=?,state=? where user_phone=?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, id);
            ps.setInt(3, 1);
            ps.setString(4,phone);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateUserBalance(float balance,String phone) {//更新余额
        Connection conn = getConnection("car_rent");
        String sql = "update user set balance=? where user_phone=?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setFloat(1, balance);
            ps.setString(2,phone);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateUserDeposit(float deposit,String phone) {//更新押金
        Connection conn = getConnection("car_rent");
        String sql = "update user set deposit=? where user_phone=?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setFloat(1,deposit);
            ps.setString(2,phone);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static User GetUserInfo(String user_phone) {//获取用户对象信息
        Connection conn = getConnection("car_rent");
        String sql = "select * from user where user_phone = ?";
        User user = null;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user_phone );
            ResultSet rs = ps.executeQuery();
            String phone = null;
            String name = null;
            String pwd = null;
            String ID = null;
            int state = 0;
            float balance = 0;
            float deposit = 0;
            int score = 60;
            while (rs.next()) {
                phone = rs.getString(1);
                name = rs.getString(2);
                pwd = rs.getString(3);
                ID = rs.getString(4);
                state = rs.getInt(5);
                balance = rs.getFloat(6);
                deposit = rs.getFloat(7);
                score = rs.getInt(8);
            }
            user = new User(phone, name, pwd, ID, state, balance, deposit, score);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return user;
    }

    public static List<Order> GetFinishedOrder(String user_id){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT * FROM order_information WHERE user_phone = ? AND (order_state = '已完成' OR order_state = '已取消') ORDER BY order_time DESC";
        List<Order> order =new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user_id );
            ResultSet resultSet = ps.executeQuery();
            String order_number = null;
            String take_time = null;
            String return_time= null;
            String take_place= null;
            String car_type= null,car_name= null;
            String order_state= null;
            String order_amount= null;
            String order_time = null;
            String take_shop_name = null;
            String car_number = null;
            if (resultSet == null) {
                return null;
            }
            else {
                while (resultSet.next()) {
                    order_number = resultSet.getString(1);
                    take_time = resultSet.getString(2);
                    return_time = resultSet.getString(3);
                    take_place = resultSet.getString(4);
                    order_state = resultSet.getString(5);
                    order_amount = resultSet.getString(7);
                    car_name = resultSet.getString(8);
                    car_type = resultSet.getString(9);
                    order_time = resultSet.getString(10);
                    take_shop_name = resultSet.getString(11);
                    car_number = resultSet.getString(12);
                    order .add(new Order(order_number,take_time,return_time,take_place,user_id,
                            order_amount,car_type,car_name,order_state,order_time,take_shop_name,car_number));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return order;
    }

    public static float getDaily_rent (String car_name) {
        Connection connection = getConnection("car_rent");
        String sql = "SELECT daily_rent FROM car_type WHERE car_name = ?";
        float daily_rent = 0.0f;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, car_name);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                daily_rent = resultSet.getFloat(1);
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return daily_rent;
    }

    public static List<Order> GetUnfinishedOrder(String user_id){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT * FROM order_information WHERE user_phone = ? AND !(order_state = '已完成' OR order_state = '已取消') ORDER BY order_time DESC";
        List<Order> order =new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user_id );
            ResultSet resultSet = ps.executeQuery();
            String order_number = null;
            String take_time = null;
            String return_time= null;
            String take_place= null;
            String car_type= null,car_name= null;
            String order_state= null;
            String order_amount= null;
            String order_time = null;
            String take_shop_name = null;
            String car_number = null;
            if (resultSet == null) {
                return null;
            }
            else {
                while (resultSet.next()) {
                    order_number = resultSet.getString(1);
                    take_time = resultSet.getString(2);
                    return_time = resultSet.getString(3);
                    take_place = resultSet.getString(4);
                    order_state = resultSet.getString(5);
                    order_amount = resultSet.getString(7);
                    car_name = resultSet.getString(8);
                    car_type = resultSet.getString(9);
                    order_time = resultSet.getString(10);
                    take_shop_name = resultSet.getString(11);
                    car_number = resultSet.getString(12);
                    order .add(new Order(order_number,take_time,return_time,take_place,user_id,
                            order_amount,car_type,car_name,order_state,order_time,take_shop_name,car_number));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return order;
    }

    public static int UpdateOrderState (String order_number,String state) {
        Connection connection = getConnection("car_rent");
        String sql = "UPDATE order_info SET order_state = ? WHERE order_number = ?";
        int result = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, state);
            ps.setString(2, order_number);
            result = ps.executeUpdate();

            connection.close();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    public static int CheckOrder(String phone) {//更新押金
        Connection conn = getConnection("car_rent");
        String sql = "SELECT * FROM order_info WHERE user_phone=? AND (order_state=? OR order_state=?)";
        ResultSet res;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,phone);
            ps.setString(2,"未还车");
            ps.setString(3,"已延长");
            res = ps.executeQuery();
            if(!res.next())
                return 1;
            else{
                conn.close();
                ps.close();
                Log.e(TAG, String.valueOf(res));
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return 0;
    }

    public static List<String> GetShopArea(String city){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT DISTINCT shop_area FROM shop WHERE shop_city = ?";
        List<String> shopAreaList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, city);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                shopAreaList.add(resultSet.getString(1));
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return shopAreaList;
    }

    public static List<Shop> GetShopItem(String city){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT * FROM shop WHERE shop_city = ?";
        List<Shop> shopItemList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,city);
            ResultSet resultSet = ps.executeQuery();
            String shopNumber;
            String shopCity;
            String shopArea;
            String shopName;
            String shopAddress;
            String shopPhone;
            String shopHours;
            while (resultSet.next()) {
                shopNumber = resultSet.getString(1);
                shopCity = resultSet.getString(2);
                shopArea = resultSet.getString(3);
                shopName = resultSet.getString(4);
                shopAddress = resultSet.getString(5);
                shopPhone = resultSet.getString(6);
                shopHours = resultSet.getString(7);
                shopItemList.add(new Shop(shopNumber,shopCity,shopArea,shopName,shopAddress,shopPhone,shopHours));
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return shopItemList;
    }

    public static List<WalletDetail> GetDetailItem(String phone){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT time,operation,amount FROM wallet_detail WHERE user_phone=?  ORDER BY time DESC";
        List<WalletDetail> WalletDetailItemList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,phone);
            ResultSet resultSet = ps.executeQuery();
            String time;
            String operation;
            Float amount;
            while (resultSet.next()) {
                time = resultSet.getString(1);
                operation = resultSet.getString(2);
                amount = resultSet.getFloat(3);
                WalletDetailItemList.add(new WalletDetail(time,operation,amount));
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return WalletDetailItemList;
    }

    public static int InsertWalletDetail(String phone,String time,String operation,float amount) {
        Connection conn = getConnection("car_rent");
        String sql = "insert into wallet_detail(user_phone,time,operation,amount) VALUES(?,?,?,?)";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, time);
            ps.setString(3, operation);
            ps.setFloat(4, amount);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static List<CarType> GetCarItem(String shop_number){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT  * FROM car_info WHERE shop_number=?";
        List<CarType> carItemList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,shop_number);
            ResultSet resultSet = ps.executeQuery();
            String car_number;
            String car_name;
            String shop_num;
            String car_picture;
            String car_brand;
            String car_type;
            String dailyRent;
            String carDeposit;
            while (resultSet.next()) {
                car_number = resultSet.getString(1);
                car_name = resultSet.getString(2);
                shop_num = resultSet.getString(3);
                car_picture = resultSet.getString(4);
                car_brand = resultSet.getString(5);
                car_type = resultSet.getString(6);
                dailyRent = resultSet.getString(7);
                carDeposit = resultSet.getString(8);
                carItemList.add(new CarType(car_number,car_name,shop_num,car_picture,car_brand,car_type,dailyRent,carDeposit));
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return carItemList;
    }

    public static int InsertOrderInfo(Order order) {
        Connection conn = getConnection("car_rent");
        String sql = "insert into order_info(order_number,user_phone,car_number,take_shop,take_time,return_time,order_amount,order_time) VALUES(?,?,?,?,?,?,?,?)";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, order.getOrder_number());
            ps.setString(2, order.getUser_id());
            ps.setString(3, order.getCar_number());
            ps.setString(4, order.getTake_shop_number());
            ps.setString(5, order.getTake_time());
            ps.setString(6, order.getReturn_time());
            ps.setFloat(7,  Float.parseFloat(order.getOrder_amount()));
            ps.setString(8,order.getOrder_time());
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateCarState(String car_number,String state) {//更新车辆状态
        Connection conn = getConnection("car_rent");
        String sql = "update car set car_state=? where car_number=?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,state);
            ps.setString(2,car_number);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateUserPwd(String pwd,String phone) {//更新车辆状态
        Connection conn = getConnection("car_rent");
        String sql = "update user set user_pwd=? where user_phone=?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,pwd);
            ps.setString(2,phone);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static int UpdateOrder(Float order_amount,String return_time,String order_time,String order_number) {//更新车辆状态
        Connection conn = getConnection("car_rent");
        String sql = "update order_info set return_time = ? ,order_amount = ? ,order_time = ? ,order_state = '已延长' where order_number = ?";
        int res=0;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,return_time);
            ps.setFloat(2,order_amount);
            ps.setString(3,order_time);
            ps.setString(4,order_number);
            res = ps.executeUpdate();
            conn.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " 数据操作异常");
        }
        return res;
    }

    public static String GetCarImage(String car_name){
        Connection connection = getConnection("car_rent");
        String sql = "SELECT car_picture FROM car_type WHERE car_name = ?";
        String car_picture = new String("");
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,car_name);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                car_picture = resultSet.getString(1);
            }
            connection.close();
            ps.close();
            resultSet.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return car_picture;
    }
}

