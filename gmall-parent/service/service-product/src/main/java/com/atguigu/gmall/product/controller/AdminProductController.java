package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.AdminProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
@RequestMapping("/admin/product")
@Api(tags = "后台商品管理")
public class AdminProductController {
    @Autowired
    private AdminProductService adminProductService;

    @ApiOperation("根据spuId获取销售属性")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable long spuId){
        List<SpuSaleAttr> spuSaleAttrList = adminProductService.getSpuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }

    @ApiOperation("根据spuId获取图片列表")
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageListBySpuId(@PathVariable long spuId){
        List<SpuImage> spuImageList = adminProductService.getSpuImageListBySpuId(spuId);
        return Result.ok(spuImageList);
    }


    @ApiOperation("添加spu")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        adminProductService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @ApiOperation("获取spu分页列表")
    @GetMapping("/{page}/{limit}")
    public Result getSpuPagesList(@PathVariable int page,@PathVariable int limit,@RequestParam(value = "category3Id",required = false) int id){
        IPage<SpuInfo> infoIPage = adminProductService.getSpuPagesList(page,limit,id);
        return Result.ok(infoIPage);
    }



    @ApiOperation("获取销售属性")
    @GetMapping("/baseSaleAttrList")
    public Result getSaleAttr(){
        List<BaseSaleAttr> saleAttrList = adminProductService.getSaleAttr();
        return Result.ok(saleAttrList);
    }


    @ApiOperation("获取品牌列表")
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList2(){
        List<BaseTrademark> trademarkList = adminProductService.getTrademarkList2();
        return Result.ok(trademarkList);
    }


    @ApiOperation("获取品牌分页列表")
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result getTrademarkList(@PathVariable int page,@PathVariable int limit){
        IPage<BaseTrademark> trademarkIPage = adminProductService.getTrademarkList(page,limit);
        return Result.ok(trademarkIPage);
    }


    @ApiOperation("根据ID获取平台属性")
    @GetMapping("getAttrValueList/{id}")
    public Result getAttrValueList(@PathVariable long id){
        List<BaseAttrValue> baseAttrValueList = adminProductService.getAttrValueList(id);
        return Result.ok(baseAttrValueList);
    }



    @ApiOperation("添加平台属性")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        adminProductService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }


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
