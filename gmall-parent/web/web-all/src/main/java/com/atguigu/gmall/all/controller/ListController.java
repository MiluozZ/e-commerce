package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.list.feign.ServiceListClient;
import com.atguigu.gmall.model.list.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class ListController {
    @Autowired
    private ServiceListClient serviceListClient;

    @GetMapping("/list.html")
    public String search(SearchParam searchParam, Model model){
        SearchResponseVo searchResponseVo = serviceListClient.search(searchParam);
        model.addAttribute("searchParam",searchParam);
        List<SearchResponseTmVo> trademarkList = searchResponseVo.getTrademarkList();
        model.addAttribute("trademarkList",trademarkList);
        List<SearchResponseAttrVo> attrsList = searchResponseVo.getAttrsList();
        model.addAttribute("attrsList", attrsList);
        List<Goods> goodsList = searchResponseVo.getGoodsList();
        model.addAttribute("goodsList", goodsList);
        Integer pageNo = searchResponseVo.getPageNo();
        model.addAttribute("pageNo", pageNo);
        Long totalPages = searchResponseVo.getTotalPages();
        model.addAttribute("totalPages", totalPages);


        String trademarkParam = buildTrademarkParam(searchParam);
        model.addAttribute("trademarkParam",trademarkParam);
        List<Map<String, String>> propsParamList = buildPropsParamList(searchParam);
        model.addAttribute("propsParamList",propsParamList);
        String urlParam = buildUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);
        Map orderMap = buildOrderMap(searchParam);
        model.addAttribute("orderMap",orderMap);

        return "list/index";
    }

    private Map buildOrderMap(SearchParam searchParam) {
        String order = searchParam.getOrder();
        Map map = new HashMap<>();
        if (!StringUtils.isEmpty(order)){
            String[] o = order.split(":");
            map.put("type",o[0]);
            map.put("sort",o[1]);
            return map;
        }else{
            map.put("type","1");
            map.put("sort","desc");
            return map;
        }
    }

    private String buildUrlParam(SearchParam searchParam) {
        StringBuilder sb = new StringBuilder();
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)){
            if (sb.length() > 0 ){
                sb.append("&keyword=").append(keyword);
            }else {
                sb.append("keyword=").append(keyword);
            }
        }
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            if (sb.length() > 0){
                sb.append("&trademark=").append(trademark);
            }else {
                sb.append("trademark=").append(trademark);
            }
        }
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0){
            for (String prop : props) {
                if (sb.length() > 0){
                    sb.append("&props=").append(prop);
                }else {
                    sb.append("props=").append(prop);
                }
            }
        }

        return "/list.html?" + sb.toString();
    }


    private List<Map<String, String>> buildPropsParamList(SearchParam searchParam) {
        String[] props = searchParam.getProps();
        List<Map<String, String>> propsParamList = null;
        //props='+prop.attrId+':'+prop.attrValue+':'+prop.attrName
        if (null != props && props.length > 0){
            propsParamList = new ArrayList<>();
            for (String prop : props) {
                Map<String, String> map = new HashMap();
                String[] p = prop.split(":");
                map.put("attrName",p[2]);
                map.put("attrValue",p[1]);
                map.put("attrId",p[0]);
                propsParamList.add(map);
            }
            return propsParamList;
        }
        return null;
    }

    private String buildTrademarkParam(SearchParam searchParam) {
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            String[] trade = StringUtils.split(trademark, ":");
            return "品牌：" + trade[1];
        }
        return null;
    }
}
