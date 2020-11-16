package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> getAttrInfoById(@Param(value = "category1Id") String category1Id,@Param(value = "category2Id") String category2Id,@Param(value = "category3Id") String category3Id);
}
