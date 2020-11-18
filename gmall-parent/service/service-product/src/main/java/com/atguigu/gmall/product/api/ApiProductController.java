package com.atguigu.gmall.product.api;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.ApiProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("根据skuID查询sku信息")
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo attrValueList(@PathVariable(value = "skuId") Long skuId){
        return apiProductService.getSkuInfo(skuId);
    }

}
