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
//import com.gs.cd.cloud.common.ApiResult;
//
//
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//
//
//
///**
// * queue controller
// */
//
//@RestController
//@RequestMapping("/queue")
//public class QueueController {
//    @Autowired
//    private QApi
//
//
//    /**
//     * query queue list
//     *
//     * @return queue list
//     */
//
//    @GetMapping(value = "/list")
//
//
//    public ApiResult queryList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                            @RequestHeader(HttpHeadersParam.TOKEN) String token) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info("login user {}, query queue list", loginUser.getUserName());
//        Map<String, Object> result = queueService.queryList(loginUser);
//        return returnDataList(result);
//    }
//
//    /**
//     * query queue list paging
//     *
//     * @param pageNo    page number
//     * @param searchVal search value
//     * @param pageSize  page size
//     * @return queue list
//     */
//
//
//    @GetMapping(value = "/list-paging")
//
//
//    public ApiResult queryQueueListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                                       @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                                       @RequestParam("pageNo") Integer pageNo,
//                                       @RequestParam(value = "searchVal", required = false) String searchVal,
//                                       @RequestParam("pageSize") Integer pageSize) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info("login user {}, query queue list,search value:{}", loginUser.getUserName(), searchVal);
//        Map<String, Object> result = checkPageParams(pageNo, pageSize);
//        if (result.get(Constants.STATUS) != Status.SUCCESS) {
//            return returnDataListPaging(result);
//        }
//
//        searchVal = ParameterUtils.handleEscapes(searchVal);
//        result = queueService.queryList(loginUser, searchVal, pageNo, pageSize);
//        return returnDataListPaging(result);
//    }
//
//    /**
//     * create queue
//     *
//     * @param queue     queue
//     * @param queueName queue name
//     * @return create result
//     */
//
//
//    @PostMapping(value = "/create")
//
//
//    public ApiResult createQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                              @RequestParam(value = "queue") String queue,
//                              @RequestParam(value = "queueName") String queueName) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info("login user {}, create queue, queue: {}, queueName: {}",
//                loginUser.getUserName(), queue, queueName);
//        Map<String, Object> result = queueService.createQueue(loginUser, queue, queueName);
//        return returnDataList(result);
//    }
//
//    /**
//     * update queue
//     *
//     * @param queue     queue
//     * @param id        queue id
//     * @param queueName queue name
//     * @return update result code
//     */
//
//
//    @PostMapping(value = "/update")
//
//
//    public ApiResult updateQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                              @RequestParam(value = "id") int id,
//                              @RequestParam(value = "queue") String queue,
//                              @RequestParam(value = "queueName") String queueName) {
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info("login user {}, update queue, id: {}, queue: {}, queueName: {}",
//                loginUser.getUserName(), id, queue, queueName);
//        Map<String, Object> result = queueService.updateQueue(loginUser, id, queue, queueName);
//        return returnDataList(result);
//    }
//
//    /**
//     * verify queue and queue name
//     *
//     * @param queue     queue
//     * @param queueName queue name
//     * @return true if the queue name not exists, otherwise return false
//     */
//
//
//    @PostMapping(value = "/verify-queue")
//
//
//    public ApiResult verifyQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
//                              @RequestParam(value = "queue") String queue,
//                              @RequestParam(value = "queueName") String queueName
//    ) {
//
//        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
//        logger.info("login user {}, verfiy queue: {} queue name: {}",
//                loginUser.getUserName(), queue, queueName);
//        return queueService.verifyQueue(queue, queueName);
//    }
//
//
//}
