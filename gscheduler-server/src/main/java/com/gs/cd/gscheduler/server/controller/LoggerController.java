///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.gs.cd.gscheduler.server.controller;
//
//
//import com.gs.cd.cloud.common.HttpHeadersParam;
//import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
//import com.gs.cd.cloud.utils.jwt.JwtUtils;
//
//import com.gs.cd.gscheduler.api.service.LoggerService;
//import com.gs.cd.cloud.common.ApiResult;
//
//
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//
//
///**
// * log controller
// */
//
//@RestController
//@RequestMapping("/log")
//public class LoggerController {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoggerController.class);
//
//
//    @Autowired
//    private LoggerService loggerService;
//
//    /**
//     * query task log
//     *
//     * @param taskInstanceId task instance id
//     * @param skipNum        skip number
//     * @param limit          limit
//     * @return task log content
//     */
//
//
//    @GetMapping(value = "/detail")
//
//
//    public ApiResult queryLog(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                           @RequestParam(value = "taskInstanceId") int taskInstanceId,
//                           @RequestParam(value = "skipLineNum") int skipNum,
//                           @RequestParam(value = "limit") int limit) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info(
//                "login user {}, view {} task instance log ,skipLineNum {} , limit {}", loginUser.getUserName(), taskInstanceId, skipNum, limit);
//        return loggerService.queryLog(taskInstanceId, skipNum, limit);
//    }
//
//
//    /**
//     * download log file
//     *
//     * @param taskInstanceId task instance id
//     * @return log file content
//     */
//
//
//    @GetMapping(value = "/download-log")
//    @ResponseBody
//
//    public ResponseEntity downloadTaskLog(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                                          @RequestParam(value = "taskInstanceId") int taskInstanceId) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        byte[] logBytes = loggerService.getLogBytes(taskInstanceId);
//        return ResponseEntity
//                .ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + System.currentTimeMillis() + ".log" + "\"")
//                .body(logBytes);
//    }
//
//}
