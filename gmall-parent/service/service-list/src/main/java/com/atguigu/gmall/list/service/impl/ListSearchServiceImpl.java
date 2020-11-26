package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.dao.ListSearchDao;
import com.atguigu.gmall.list.service.ListSearchService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Miluo
 * @description
 **/
@Service
public class ListSearchServiceImpl implements ListSearchService {
    @Autowired
    private ListSearchDao listSearchDao;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    //上架
    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        goods.setId(skuInfo.getId());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(categoryView.getCategory1Id());
        goods.setCategory1Name(categoryView.getCategory1Name());
        goods.setCategory2Id(categoryView.getCategory2Id());
        goods.setCategory2Name(categoryView.getCategory2Name());
        goods.setCategory3Id(categoryView.getCategory3Id());
        goods.setCategory3Name(categoryView.getCategory3Name());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        BaseTrademark tradeMark = productFeignClient.getTradeMark(skuInfo.getTmId());
        goods.setTmId(tradeMark.getId());
        goods.setTmName(tradeMark.getTmName());
        goods.setTmLogoUrl(tradeMark.getLogoUrl());
        List<SearchAttr> searchAttrList = productFeignClient.getSearchAttr(skuId);
        goods.setAttrs(searchAttrList);

        listSearchDao.save(goods);
    }

    //下架
    @Override
    public void cancelSale(Long skuId) {
        listSearchDao.deleteById(skuId);
    }

    //根据查询商品增加热度
    @Override
    public void increaseHotScore(Long skuId) {
        Double score = redisTemplate.opsForZSet().incrementScore("hotScore", skuId, 1);
        if ((score%10) == 0){
            Optional<Goods> byId = listSearchDao.findById(skuId);
            Goods goods = byId.get();
            goods.setHotScore(Math.round(score));
            listSearchDao.save(goods);
        }
    }

    //商品查询
    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = buildSourceBuilder(searchParam);
        searchRequest.source(searchSourceBuilder);
        SearchResponseVo responseVo = null;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            responseVo = buildSearchBuilder(response);
            responseVo.setPageNo(searchParam.getPageNo());
            responseVo.setPageSize(searchParam.getPageSize());
            responseVo.setTotalPages((responseVo.getPageSize() + responseVo.getTotal() - 1)/responseVo.getPageSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseVo;

    }

    //解析结果
    private SearchResponseVo buildSearchBuilder(SearchResponse response) {
        SearchResponseVo responseVo = new SearchResponseVo();
        SearchHits hits = response.getHits();
        responseVo.setTotal(hits.getTotalHits());

        //商品
        SearchHit[] hits1 = hits.getHits();
        if (hits1 != null && hits1.length > 0){
            List<Goods> goodsList = Arrays.stream(hits1).map(hit -> {
                Goods goods = JSON.parseObject(hit.getSourceAsString(), Goods.class);
                //高亮
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && highlightFields.size() > 0) {
                    HighlightField title = highlightFields.get("title");
                    if (null != title) {
                        //有高亮名称 优先使用高亮名称   没有使用原来 的普通名称
                        goods.setTitle(title.fragments()[0].toString());
                    }
                }
                return goods;
            }).collect(Collectors.toList());
            responseVo.setGoodsList(goodsList);
        }

        //品牌
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregationMap.get("tmIdAgg");
        List<SearchResponseTmVo> responseTmVos = tmIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            searchResponseTmVo.setTmId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) bucket.getAggregations().asMap().get("tmNameAgg");
            searchResponseTmVo.setTmName(tmNameAgg.getBuckets().get(0).toString());
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) bucket.getAggregations().asMap().get("tmLogoUrlAgg");
            searchResponseTmVo.setTmLogoUrl(tmLogoUrlAgg.getBuckets().get(0).toString());
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        responseVo.setTrademarkList(responseTmVos);

        //平台属性
        ParsedNested attrsAgg = (ParsedNested) aggregationMap.get("attrsAgg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attrIdAgg");
        List<SearchResponseAttrVo> attrVos = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo responseAttrVo = new SearchResponseAttrVo();
            responseAttrVo.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) bucket.getAggregations().get("attrNameAgg");
            responseAttrVo.setAttrName(attrNameAgg.getBuckets().get(0).toString());
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            responseAttrVo.setAttrValueList(attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList()));
            return responseAttrVo;
        }).collect(Collectors.toList());
        responseVo.setAttrsList(attrVos);

        return responseVo;
    }

    //抽取sourceBuilder
    private SearchSourceBuilder buildSourceBuilder(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.关键词
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword));
        }else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }

        //2.三级分类
        Long category1Id = searchParam.getCategory1Id();
        if (null != category1Id){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1Id",category1Id));
        }
        Long category2Id = searchParam.getCategory2Id();
        if (null != category2Id){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2Id",category2Id));
        }
        Long category3Id = searchParam.getCategory3Id();
        if (null != category3Id){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category3Id",category3Id));
        }

        //3.品牌
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            String[] trade = StringUtils.split(trademark, ":");
            boolQueryBuilder.filter(QueryBuilders.termQuery("tmId",trade[0]));
        }

        //4.平台属性分类 *
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0){
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            Arrays.stream(props).forEach((p) ->{
                String[] prop = p.split(":");
                //[1]id [2]value [3]name
                BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",prop[0]));
//                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrName",prop[2]));
                subBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",prop[1]));
                boolQuery.must(QueryBuilders.nestedQuery("attrs",subBoolQuery, ScoreMode.None));
            });
            boolQueryBuilder.filter(boolQuery);
        }
        searchSourceBuilder.query(boolQueryBuilder);

        //5.分页
        Integer pageNo = searchParam.getPageNo();
        Integer pageSize = searchParam.getPageSize();
        searchSourceBuilder.from((pageNo - 1) * pageSize);
        searchSourceBuilder.size(pageSize);

        //6.排序
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] order1 = StringUtils.split(order, ":");
            String o = null;
            switch (order1[0]){
                case "1":
                    o = "hotScore";
                    break;
                case "2":
                    o = "price";
                    break;
                default:
                    o = "createTime";
                    break;
            }
            searchSourceBuilder.sort(o,order1[1].equalsIgnoreCase("desc")?SortOrder.DESC:SortOrder.ASC);
        }else{
            searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        }


        //7.高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<font style='color:red'>").postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);


        //8.品牌聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("tmIdAgg").field("tmId")
                        .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"))
        );


        //9.平台聚合
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("attrsAgg","attrs")
                        .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")))
        );

        return searchSourceBuilder;


    }

}
