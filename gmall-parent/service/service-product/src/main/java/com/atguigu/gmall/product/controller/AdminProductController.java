package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.AdminProductService;
import com.baomidou.mybatisplus.extension.api.R;
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

    @ApiOperation("根据分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoById(
            @PathVariable(value = "category1Id") String category1Id,
            @PathVariable(value = "category2Id") String category2Id,
            @PathVariable(value = "category3Id") String category3Id){
        List<BaseAttrInfo> list = adminProductService.getAttrInfoById(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }


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
