//package com.gs.cd.gscheduler.server.controller;
//
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.gs.cd.cloud.common.ApiResult;
//import com.gs.cd.gsnow.api.service.GSnowService;
//import com.gs.cd.gscheduler.api.enums.Status;
//import com.gs.cd.cloud.common.ApiResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//
///**
// * schedulerx
// *
// * @Author seven
// * @Date 2021/2/5 15:53
// * @Description
// * @Version 1.0
// */
//@RestController
//@RequestMapping("etl")
//public class EtlController {
//    @Autowired
//    GSnowService gSnowService;
//
//    /**
//     * 获取收集器集合
//     *
//     * @return
//     */
//    @GetMapping("collector/list")
//    public ApiResult listEtlCollector() {
//        ApiResult search = gSnowService.searchAll();
//        if (search.isSuccess()) {
//            JSONObject jsonObject = JSONUtil.parseObj(search.getData());
//            ArrayList<JSONObject> jsonObjects = new ArrayList<>();
//            jsonObject.forEach((k, v) -> {
//                JSONArray j = (JSONArray) v;
//                j.forEach(s -> {
//                    JSONObject ss = (JSONObject) s;
//                    ss.set("id", ss.getStr("tenantCode") + "||" + ss.getStr("id"));
//                    ss.set("name", k + "--" + ss.getStr("name"));
//                    jsonObjects.add(ss);
//                });
//            });
//            return Result.success(jsonObjects);
//        } else {
//            return Result.errorWithArgs(Status.GSNOW_ERROR, search.getMsg());
//        }
//    }
//}
