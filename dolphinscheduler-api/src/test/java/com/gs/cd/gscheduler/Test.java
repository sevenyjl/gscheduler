package com.gs.cd.gscheduler;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * @Author seven
 * @Date 2021/4/13 14:24
 * @Description
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {
        HttpRequest get = HttpUtil.createGet("http://10.201.82.113:12345/dolphinscheduler/users/get-user-info?_t=0.026034493778436696");
        get.header("Cookie","sessionId=c5744ce2-574a-4466-a615-394447556a9f;");
        HttpResponse execute = get.execute();
        System.out.println(execute.body());
    }
}
