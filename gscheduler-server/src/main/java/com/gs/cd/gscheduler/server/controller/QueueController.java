package com.gs.cd.gscheduler.server.controller;


import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.cloud.common.ApiResult;


import com.gs.cd.gscheduler.api.QueueApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * queue controller
 */

@RestController
@RequestMapping("/queue")
public class QueueController {
    @Autowired
    private QueueApi queueApi;


    /**
     * query queue list
     *
     * @return queue list
     */

    @GetMapping(value = "/list")


    public ApiResult queryList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode) {
        return queueApi.queryList(TenantCodeService.getSessionId(tenantCode)).apiResult();
    }

    /**
     * query queue list paging
     *
     * @param pageNo    page number
     * @param searchVal search value
     * @param pageSize  page size
     * @return queue list
     */


    @GetMapping(value = "/list-paging")
    public ApiResult queryQueueListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam(value = "searchVal", required = false) String searchVal,
                                          @RequestParam("pageSize") Integer pageSize) {
        return queueApi.queryQueueListPaging(TenantCodeService.getSessionId(tenantCode), pageNo, searchVal, pageSize).apiResult();
    }

    /**
     * create queue
     *
     * @param queue     queue
     * @param queueName queue name
     * @return create result
     */


    @PostMapping(value = "/create")
    public ApiResult createQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestParam(value = "queue") String queue,
                                 @RequestParam(value = "queueName") String queueName) {
        return queueApi.createQueue(TenantCodeService.getSessionId(tenantCode), queue, queueName).apiResult();
    }

    /**
     * update queue
     *
     * @param queue     queue
     * @param id        queue id
     * @param queueName queue name
     * @return update result code
     */


    @PostMapping(value = "/update")
    public ApiResult updateQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestParam(value = "id") int id,
                                 @RequestParam(value = "queue") String queue,
                                 @RequestParam(value = "queueName") String queueName) {
        return queueApi.updateQueue(TenantCodeService.getSessionId(tenantCode), id, queue, queueName).apiResult();
    }

    /**
     * verify queue and queue name
     *
     * @param queue     queue
     * @param queueName queue name
     * @return true if the queue name not exists, otherwise return false
     */


    @PostMapping(value = "/verify-queue")
    public ApiResult verifyQueue(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestParam(value = "queue") String queue,
                                 @RequestParam(value = "queueName") String queueName
    ) {
        return queueApi.verifyQueue(TenantCodeService.getSessionId(tenantCode), queue, queueName).apiResult();
    }


}
