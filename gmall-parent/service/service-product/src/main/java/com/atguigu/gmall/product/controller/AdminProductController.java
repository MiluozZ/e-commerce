package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.AdminProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @ApiOperation("商品二级分类")
    @GetMapping("/getCategory2/{id}")
    public Result getCategory2(@PathVariable String id){
        List<BaseCategory2> category2s = adminProductService.getCategory2(id);
        return Result.ok(category2s);
    }

    @ApiOperation("商品三级分类")
    @GetMapping("/getCategory3/{id}")
    public Result getCategory3(@PathVariable String id){
        List<BaseCategory3> category2s = adminProductService.getCategory3(id);
        return Result.ok(category2s);
    }
}
