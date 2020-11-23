package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class IndexController {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/")
    public String categoryList(Model model){
        List<Map<String, Object>> categoryList = getCategoryList();
        model.addAttribute("list",categoryList);
        return "index/index";
    }


    //测试静态页面
//    @GetMapping("/")
//    public String categoryList(){
//        return "index";
//    }

    //页面静态化
    @GetMapping("/creatHtml")
    @ResponseBody
    public Result creatHtml() throws IOException {
        List<Map<String, Object>> categoryList = getCategoryList();
        Context context = new Context();
        context.setVariable("list",categoryList);
        Writer writer = new FileWriter(new File("D:\\index.html"));
        templateEngine.process("index/index.html",context,writer);
        return Result.ok();
    }


    //获取首页分类列表
    private List<Map<String, Object>> getCategoryList() {
        List<Map<String, Object>> categoryList = new ArrayList<>();
        List<BaseCategoryView> category = productFeignClient.getCategory();
        Map<Long, List<BaseCategoryView>> listMap1 = category.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        int index = 1;
        for (Map.Entry<Long, List<BaseCategoryView>> map1 : listMap1.entrySet()) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("index",index++);
            categoryMap.put("categoryId",map1.getValue().get(0).getCategory1Id());
            categoryMap.put("categoryName",map1.getValue().get(0).getCategory1Name());

            List<Map<String, Object>> categoryList2 = new ArrayList<>();
            Map<Long, List<BaseCategoryView>> listMap2 = map1.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> map2 : listMap2.entrySet()) {
                Map<String, Object> categoryMap2 = new HashMap<>();
                categoryMap2.put("categoryId",map2.getValue().get(0).getCategory2Id());
                categoryMap2.put("categoryName",map2.getValue().get(0).getCategory2Name());
                List<Map<String, Object>> categoryList3 = new ArrayList<>();
                for (BaseCategoryView map3 : map2.getValue()) {
                    Map<String, Object> categoryMap3 = new HashMap<>();
                    categoryMap3.put("categoryId",map3.getCategory3Id());
                    categoryMap3.put("categoryName",map3.getCategory3Name());

                    categoryList3.add(categoryMap3);
                }
                categoryMap2.put("categoryChild",categoryList3);
                categoryList2.add(categoryMap2);
            }
            categoryMap.put("categoryChild",categoryList2);
            categoryList.add(categoryMap);
        }
        return categoryList;
    }


}
