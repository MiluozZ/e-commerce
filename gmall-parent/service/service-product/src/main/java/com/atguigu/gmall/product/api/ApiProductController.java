package com.atguigu.gmall.product.api;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ApiProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "前台商品管理")
@RequestMapping("/api/product")
public class ApiProductController {
    @Autowired
    private ApiProductService apiProductService;

    @ApiOperation("根据spuId获取对象映射")
    @GetMapping("/inner/getSkuValueIdsMap/{spuId}")
    public Map<String, String> getSkuValueIdsMap(@PathVariable Long spuId){
        HashMap<String, String> map = new HashMap<>();
        List<Map<String, String>> mapList = apiProductService.getSkuValueIdsMap(spuId);
        mapList.forEach(stringStringMap -> {
            map.put(stringStringMap.get("saleAttrId"),String.valueOf(stringStringMap.get("sku_id")));
        });
        return map;
    }


    @ApiOperation("根据skuId获取spu销售属性")
    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable(value = "skuId")Long skuId, @PathVariable(value = "spuId")Long spuId){
        return apiProductService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }


    @ApiOperation("根据skuId查询库存商品价格")
    @GetMapping("/inner/getSkuPrice/{skuId}")
    public BigDecimal getPriceById(@PathVariable(value = "skuId") Long skuId){
        return apiProductService.getPrice(skuId);
    }


    @ApiOperation("根据skuId查询分类信息")
    @GetMapping("/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryById(@PathVariable(value = "category3Id") Long category3Id){
        return apiProductService.getCategoryById(category3Id);
    }


    @ApiOperation("根据skuID查询sku信息")
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId){
        return apiProductService.getSkuInfo(skuId);
    }

    @ApiOperation("查询所有分类信息")
    @GetMapping("/inner/getCategoryView")
    public List<BaseCategoryView> getCategory(){
        return apiProductService.getCategory();
    }

    @ApiOperation("查询品牌信息")
    @GetMapping("/inner/tradeMark/{skuId}")
    public BaseTrademark getTradeMark(@PathVariable(name = "skuId")Long skuId){
        return apiProductService.getTradeMark(skuId);
    }

    @ApiOperation("查询搜索属性")
    @GetMapping("/inner/searchAttr/{skuId}")
    public List<SearchAttr> getSearchAttr(@PathVariable(name = "skuId")Long skuId){
        return apiProductService.getSearchAttr(skuId);
    }

}
