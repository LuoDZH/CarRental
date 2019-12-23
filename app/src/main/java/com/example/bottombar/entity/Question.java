package com.example.bottombar.entity;
//这个类专门用来写具体的标题和规则
public class Question {
    private String [] title ={"取还车辆","计费规则","预定车辆","认证审核",
            "订单支付","订单修改","违章处理","出险理赔","退款说明"};
    private String [] content={
            "我如何办理取车手续？\n"+"本人携带身份证、驾驶证、信用卡及面签协议书（限校园业务）到指定地点提车即可。\n\n"+
            "我租的车其他人可以开吗？\n"+"不可以。车辆必须只能由预订本人使用，不得出租、转借。\n\n"+
             "用车期间油费怎么计算，我需要加油吗？\n"+"行驶期间的油费需要您自理，还车时车辆管理员会根据您的油量使用情况和原油位进行对比，以多退少补原则处理油费。您需确保还车时油箱中有油。\n\n"+
             "还车业务如何办理？\n"+"请您按照约定时间到达指定还车地点，也可预订上门取车服务，车辆运营专员会核实车况（如外观、轮胎等）后填写相关单据，如无问题，您结算订单即可。\n\n",

            "租金如何计算？\n"+"租金=车辆日租金*租车天数。租车天数向上取整，如1.5天按2天计算\n\n"+
              "油费如何计算？\n"+"按照取还车的油量差按多退少补原则处理油费。油费=油量比例差值*油箱容积*油费市场价。",

            "我怎样在快租车预订车辆？\n"+"注册并完成实名认证后，设置取/还车的时间、地点，选择心仪车辆，提交预订即可。\n\n"+
            "未按时还车怎么办？\n"+"●未提前告知且延期还车1小时以上的；\n" +
                    "●未经门店或平台同意强行不还车的；\n" +
                    "满足任一以上情况，则门店有权立即收回车辆。客户除支付正常租车费用外，还需按超期部分租金的300%支付违约金。",

            "我怎样实名认证？\n"+"登录账户，在“我的”页面点击“实名认证”，输入真实姓名和身份证号即可。",

            "订单金额是预定时先付还是可以还车时付全款？\n"+"预订时先付租金，车辆押金在取车前缴纳即可。",

            "每个订单可以延长租车一次；在取车时间之前都可以选择取消订单。",

            "我在租车期间发生违章如何处理?\n"+"因违章记录30-45天后才能查询结果，工作人员会在您行程结束后30天开始查询车辆违章情况，若出现违章情况，工作人员会和您联系处理，原则上您需要自行处理违章，如不方便也可以和门店工作人员协商并委托其代为处理。门店工作人员或快租车平台有权从违章押金中扣除相应款项，不足部分需您补足。",

            "用车期间遇到事故出险怎么办？车辆发生故障怎么办？\n"+"第一时间向交通公安部门报警，并致电门店电话，门店人员将会指导您联系保险公司进行事故理赔。如需道路救援服务，请与门店工作人员协商。如无法联系到门店，可联系快租车客服 13762769230",

            "押金退款\n" + "押金于还车后待确认无违章记录，30-45天内返还，部分城市有延期。"};

    public String[] getTitle() {
        return title;
    }
    public String[] getContent() {
        return content;
    }
}