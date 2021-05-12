
package com.gs.cd.gscheduler.trigger.server.entity;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.Data;
import cn.hutool.http.Method;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author seven
 * @Date 2021/4/27 14:39
 * @Description
 * @Version 1.0
 */
@Data
@Slf4j
public class HttpParams implements ITrigger {
    @NonNull
    private Method method;
    @NonNull
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

    public HttpRequest getHttpRequest() {
        HttpRequest request = HttpUtil.createRequest(method, url);
        if (header != null) {
            header.forEach(request::header);
        }
        if (form != null) {
            request.form(form);
        }
        if (paramMap != null) {
            request.form(paramMap);
        }
        if (body != null) {
            request.body(body);
        }
        return request;
    }

    @Override
    public void execute() throws Exception{
        HttpResponse execute = getHttpRequest().execute();
        if (execute.isOk()) {
            log.debug("执行成功，执行返回值：" + execute.body());
        } else {
            log.error("执行请求失败：" + this);
            throw new Exception("执行请求失败：" + this);
        }
    }
}
