package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        //获取登陆人
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆人2" + name);


        //从cookie中提取购物车数据
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }

        List<Cart> cartList1 = JSON.parseArray(cartList, Cart.class);

        if (name.equals("anonymousUser")) {//用户没有登陆（从cookie中读数据）

            return cartList1;
        } else {//用户已经登陆（从redis中读数据）
            //获取redis购物车
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);

            if (cartListFromRedis.size() > 0) {//判断当本地购物车（即cookie中有数据是才执行合并操作，否则依然返回redis中数据）
                //获取合并后的购物车
                List<Cart> list = cartService.mergeCartList(cartList1, cartListFromRedis);
                //再次将合并后的购物车存入到redis中
                cartService.saveCartListToRedis(name, list);
                //将cookie中的数据清除
                CookieUtil.deleteCookie(request, response, "cartList");
                return list;
            }

            return cartListFromRedis;

        }


    }

    @RequestMapping("/addGoodsToCartList")
    //@CrossOrigin(origins = "http//localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        //设置允许跨域请求
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");


        //获取登陆人
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆人1" + name);

        try {
            //从cookie中提取购物车数据
            List<Cart> cartList = findCartList();

            //
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if (name.equals("anonymousUser")) {//用户没有登陆（将数据存入到cookie中）
                //将cartList存入cookie中
                String s = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cartList", s, 3600 * 24, "UTF-8");
            } else {//用户已经登陆（将数据存入到redis中）
                cartService.saveCartListToRedis(name, cartList);
            }


            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "添加失败");
        }

    }


    @Reference
    private UserService userService;
    /**
     * 查询结算页地址
     */
    @RequestMapping("/findAddress")
    public List<TbAddress> findAddress(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbAddress> addressList = userService.findAddress(name);

        return addressList;

    }


}
