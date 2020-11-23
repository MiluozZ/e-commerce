package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Miluo
 * @description
 **/
public interface ListSearchDao extends ElasticsearchRepository<Goods,Long> {
}
