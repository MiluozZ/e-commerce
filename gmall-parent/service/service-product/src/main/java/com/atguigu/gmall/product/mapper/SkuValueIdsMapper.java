package com.atguigu.gmall.product.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
public interface SkuValueIdsMapper {

    List<Map<String, String>> getSkuValueIdsMap(Long spuId);
}
