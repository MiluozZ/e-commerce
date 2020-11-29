package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String login( String originUrl,Model model){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
