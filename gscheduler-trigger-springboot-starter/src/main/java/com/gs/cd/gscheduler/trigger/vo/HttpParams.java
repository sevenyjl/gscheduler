package com.gs.cd.gscheduler.trigger.vo;

import cn.hutool.http.Method;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * @Author seven
 * @Date 2021/5/12 14:27
 * @Description
 * @Version 1.0
 */
@Data
public class HttpParams {
    private Method method;
    private String url;
    private Map<String, String> header;
    private Map<String, Object> form;
    private Map<String, Object> paramMap;
    private String body;

    public HttpParams(Method method, String url, Map<String, String> header, Map<String, Object> form, Map<String, Object> paramMap, String body) {
        this.method = method;
        this.url = url;
        this.header = header;
        this.form = form;
        this.paramMap = paramMap;
        this.body = body;
    }

    public HttpParams() {
    }
}
