package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class CartController {
    @Autowired
    private CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam(name = "skuId") Long skuId, @RequestParam(name = "skuNum") Integer skuNum, RedirectAttributes attributes){
        CartInfo cartInfo = cartFeignClient.addCart(skuId,skuNum);
        String cartJson = JSON.toJSONString(cartInfo);
        cartJson = cartJson.substring(1,cartJson.length()-1);
        String[] split = cartJson.split(",");
        int i = 0;
        for (String s : split) {
            if (i != 2){
                String[] split1 = s.split(":",2);
                attributes.addAttribute(split1[0].substring(1,split1[0].length()-1),split1[1]);
            }else {
                String[] split1 = s.split(":",2);
                attributes.addAttribute(split1[0].substring(1,split1[0].length()-1),split1[1].substring(1,split1[1].length()-1));
            }
            i++;
        }
        return "redirect:http://cart.gmall.com/addToCart";
    }

    @GetMapping("/addToCart")
    public String addToCart(CartInfo cartInfo, Model model){
        System.out.println(cartInfo.toString());
        model.addAttribute("cartInfo",cartInfo);
        return "cart/addCart";
    }
}
