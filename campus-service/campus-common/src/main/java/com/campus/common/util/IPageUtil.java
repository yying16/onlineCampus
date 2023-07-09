package com.campus.common.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义分页工具类
 */
public class IPageUtil {

    /**
     * 这里的list是字符串数组，要先把字符串转成json，再结合分页器
     */
    public static <T> IPage listToIPage(List<T> list, long currentPage, long pageSize) {
        List<JSONObject> ret = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            ret.add(JSONObject.parseObject(String.valueOf(list.get(i))));
//        }
        ret = list.stream().map(i -> JSONObject.parseObject(String.valueOf(i))).collect(Collectors.toList());
        IPage<JSONObject> page = new Page<>();
        if(ret.size()-(currentPage-1)*pageSize<=0){
            currentPage = 1;
        }
        return page.setRecords(ret.subList((int) ((currentPage - 1) * pageSize),Math.min((int) (currentPage * pageSize),ret.size())))
                .setCurrent(currentPage).setSize(pageSize).setTotal(ret.size());
    }
}
