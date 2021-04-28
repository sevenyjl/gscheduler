package com.gs.cd.gscheduler.common.entity;

import cn.hutool.json.JSONUtil;
import org.junit.Test;
import com.gs.cd.gscheduler.common.utils.HttpParamsUtils;

import java.util.Map;

/**
 * @Author seven
 * @Date 2021/4/28 11:14
 * @Description
 * @Version 1.0
 */
public class HttpParamsTest {
    @Test
    public void testGet() {
        HttpParams httpParams = HttpParamsUtils.getHttpParams("localhost:18077");
        System.out.println(JSONUtil.toJsonStr(httpParams));
    }
    @Test
    public void testGetWithParams() {
        HttpParams httpParams = HttpParamsUtils.getHttpParams("localhost:18077", Map.of("参数1","参数1"));
        System.out.println(JSONUtil.toJsonStr(httpParams));
    }
    @Test
    public void testPost() {
//        HttpParams httpParams = HttpParamsUtils.postHttpParams("localhost:18077", Map.of("参数1","参数1"));
//        System.out.println(JSONUtil.toJsonStr(httpParams));
    }
    @Test
    public void testPostJSON() {
        HttpParams httpParams = HttpParamsUtils.jsonHttpParams("localhost:18077", Map.of("tenant_code","xxxx"), "['JSONSTR']");
        System.out.println(JSONUtil.toJsonStr(httpParams));

    }
}