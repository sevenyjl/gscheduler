package com.gs.cd.gscheduler.common.enums.utils;

import cn.hutool.http.Header;
import cn.hutool.http.Method;
import com.gs.cd.gscheduler.common.enums.entity.HttpParams;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author seven
 * @Date 2021/4/27 15:13
 * @Description
 * @Version 1.0
 */
public class HttpParamsUtils {
    public static HttpParams createHttpParams(Method method, String url, Map<String, String> header, Map<String, Object> form, Map<String, Object> paramMap, String body) {
        return new HttpParams(method, url, header, form, paramMap, body);
    }

    public static HttpParams getHttpParams(String url, Map<String, String> header, Map<String, Object> paramMap) {
        return new HttpParams(Method.GET, url, header, null, paramMap, null);
    }

    public static HttpParams getHttpParams(String url, Map<String, Object> paramMap) {
        return new HttpParams(Method.GET, url, null, null, paramMap, null);
    }

    public static HttpParams getHttpParams(String url) {
        return new HttpParams(Method.GET, url, null, null, null, null);
    }

    public static HttpParams postHttpParams(String url, Map<String, String> header, Map<String, Object> form, Map<String, Object> paramMap, String body) {
        return new HttpParams(Method.POST, url, header, form, paramMap, body);
    }

    public static HttpParams postHttpParams(String url, Map<String, Object> form, Map<String, Object> paramMap, String body) {
        return new HttpParams(Method.POST, url, null, form, paramMap, body);
    }

    public static HttpParams jsonHttpParams(String url, Map<String, String> header, String jsonBody) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(Header.CONTENT_TYPE.getValue(), "application/json");
        return new HttpParams(Method.POST, url, header, null, null, jsonBody);
    }
}
