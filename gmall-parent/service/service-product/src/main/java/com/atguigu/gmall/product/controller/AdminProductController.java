package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.AdminProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@RestController
@CrossOrigin
@RequestMapping("/admin/product")
@Api(tags = "后台商品管理")
public class AdminProductController {
    @Autowired
    private AdminProductService adminProductService;

    @ApiOperation("商品一级分类")
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = adminProductService.getCategory1();
        return Result.ok(category1s);
    }
}
