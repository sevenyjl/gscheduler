
package com.gs.cd.gscheduler.server.controller;


import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;


import com.gs.cd.cloud.common.ApiResult;


import com.gs.cd.gscheduler.api.DataAnalysisApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * data analysis controller
 */

@RestController
@RequestMapping("projects/analysis")
public class DataAnalysisController {
    @Autowired
    private DataAnalysisApi dataAnalysisApi;

    /**
     * statistical task instance status data
     *
     * @param startDate count start date
     * @param endDate   count end date
     * @param projectId project id
     * @return task instance count data
     */


    @GetMapping(value = "/task-state-count")
    public ApiResult countTaskState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate,
                                    @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        return dataAnalysisApi.countTaskState(TenantCodeService.getSessionId(tenantCode), startDate, endDate, projectId).apiResult();
    }

    /**
     * statistical process instance status data
     *
     * @param startDate start date
     * @param endDate   end date
     * @param projectId project id
     * @return process instance data
     */


    @GetMapping(value = "/process-state-count")


    public ApiResult countProcessInstanceState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                               @RequestParam(value = "startDate", required = false) String startDate,
                                               @RequestParam(value = "endDate", required = false) String endDate,
                                               @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        return dataAnalysisApi.countProcessInstanceState(TenantCodeService.getSessionId(tenantCode), startDate, endDate, projectId).apiResult();
    }

    /**
     * statistics the process definition quantities of certain person
     *
     * @param projectId project id
     * @return definition count in project id
     */
    @GetMapping(value = "/define-user-count")
    public ApiResult countDefinitionByUser(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        return dataAnalysisApi.countDefinitionByUser(TenantCodeService.getSessionId(tenantCode), projectId).apiResult();
    }


    /**
     * statistical command status data
     *
     * @param startDate start date
     * @param endDate   end date
     * @param projectId project id
     * @return command state in project id
     */
    @GetMapping(value = "/command-state-count")
    public ApiResult countCommandState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                       @RequestParam(value = "startDate", required = false) String startDate,
                                       @RequestParam(value = "endDate", required = false) String endDate,
                                       @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        return dataAnalysisApi.countCommandState(TenantCodeService.getSessionId(tenantCode), startDate, endDate, projectId).apiResult();
    }

    /**
     * queue count
     *
     * @param projectId project id
     * @return queue state count
     */
    @GetMapping(value = "/queue-count")
    public ApiResult countQueueState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                     @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        return dataAnalysisApi.countQueueState(TenantCodeService.getSessionId(tenantCode), projectId).apiResult();
    }


}
