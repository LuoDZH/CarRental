package com.example.bottombar.activity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hasee on 2019/5/22.
 */
public class RegisterTest {

    Register r = new Register();
    String pwd1 = "Jiang";
    String pwd2 = "Jiang123456";
    String pwd3 = "Jiang123";
    String pwd4 = "jiang123";
    String pwd5 = "JIANG123";
    String pwd6 = "1234567";
    String pwd7 = "jiangxinyu";

    @Test
    public void checkPwd_1() throws Exception {
        boolean result = r.CheckPwd(pwd1);
        // 第一个参数："checkPwd()" 打印的tag信息 （可省略）
        // 第二个参数： true 期望得到的结果
        // 第三个参数  result：实际返回的结果
        // 第四个参数  0 误差范围（可省略）
        assertEquals("checkPwd_1()",true,result);
    }
    @Test
    public void checkPwd_2() throws Exception {
        boolean result = r.CheckPwd(pwd2);
        assertEquals("checkPwd_2()",true,result);
    }
    @Test
    public void checkPwd_3() throws Exception {
        boolean result = r.CheckPwd(pwd3);
        assertEquals("checkPwd_3()",true,result);
    }
    @Test
    public void checkPwd_4() throws Exception {
        boolean result = r.CheckPwd(pwd4);
        assertEquals("checkPwd_4()",true,result);
    }
    @Test
    public void checkPwd5() throws Exception {
        boolean result = r.CheckPwd(pwd5);
        assertEquals("checkPwd_5()",true,result);
    }
    @Test
    public void checkPwd6() throws Exception {
        boolean result = r.CheckPwd(pwd2);
        assertEquals("checkPwd_6()",true,result);
    }
    @Test
    public void checkPwd7() throws Exception {
        boolean result = r.CheckPwd(pwd2);
        assertEquals("checkPwd_7()",true,result);
    }
}