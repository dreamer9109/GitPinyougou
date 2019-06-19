package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeiXinPayService;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {


    @Reference
    private WeiXinPayService weiXinPayService;

    //提交订单到微信支付端获得二维码123
    @RequestMapping("/createNative")
    public Map createNative() {
        IdWorker idWorker = new IdWorker();
        Map resultMap = weiXinPayService.createNative(idWorker.nextId() + "", "1");
        return resultMap;
    }


    //监控用户支付状态返回相应信息
    @RequestMapping("/checkPayStatus")
    public Result checkPayStatus(String out_trade_no) {

        int i = 0;
        while (true) {
            Map map = weiXinPayService.checkPayStatus(out_trade_no);
            if (map == null) {

                return new Result(false, "支付失败！");

            }

            if (map.get("trade_states").equals("SUCCESS")){
                return new Result(true, "支付成功！");
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
            if (i>=100){
                return  new Result(false, "二维码超时！");
            }

        }
    }


}
