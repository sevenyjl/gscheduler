package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/13 16:21
 * @Description
 * @Version 1.0
 */

@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "ProcessDefinitionApi")

public interface ProcessDefinitionApi {
    @PostMapping(value = "/projects/{projectName}/process/save")
    public Result createProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                          @PathVariable String projectName,
                                          @RequestParam(value = "name", required = true) String name,
                                          @RequestParam(value = "processDefinitionJson", required = true) String json,
                                          @RequestParam(value = "locations", required = true) String locations,
                                          @RequestParam(value = "connects", required = true) String connects,
                                          @RequestParam(value = "description", required = false) String description);

    @PostMapping(value = "/projects/{projectName}/process/copy")
    public Result copyProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                        @PathVariable String projectName,
                                        @RequestParam(value = "processId", required = true) int processId);

    @GetMapping(value = "/projects/{projectName}/process/verify-name")
    public Result verifyProcessDefinitionName(@RequestHeader(name = "Cookie") String sessinoId,
                                              @PathVariable String projectName,
                                              @RequestParam(value = "name", required = true) String name);

    @PostMapping(value = "/projects/{projectName}/process/update")
    public Result updateProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                          @PathVariable String projectName,
                                          @RequestParam(value = "name", required = true) String name,
                                          @RequestParam(value = "id", required = true) int id,
                                          @RequestParam(value = "processDefinitionJson", required = true) String processDefinitionJson,
                                          @RequestParam(value = "locations", required = false) String locations,
                                          @RequestParam(value = "connects", required = false) String connects,
                                          @RequestParam(value = "description", required = false) String description);

    @PostMapping(value = "/projects/{projectName}/process/release")
    public Result releaseProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                           @PathVariable String projectName,
                                           @RequestParam(value = "processId", required = true) int processId,
                                           @RequestParam(value = "releaseState", required = true) int releaseState);


    @GetMapping(value = "/projects/{projectName}/process/select-by-id")
    public Result queryProcessDefinitionById(@RequestHeader(name = "Cookie") String sessinoId,
                                             @PathVariable String projectName,
                                             @RequestParam("processId") Integer processId
    );

    @GetMapping(value = "/projects/{projectName}/process/list")
    public Result queryProcessDefinitionList(@RequestHeader(name = "Cookie") String sessinoId,
                                             @PathVariable String projectName
    );

    @GetMapping(value = "/projects/{projectName}/process/list-paging")
    public Result queryProcessDefinitionListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                                   @PathVariable String projectName,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam("pageSize") Integer pageSize,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                   @RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId);

    @GetMapping(value = "/projects/{projectName}/process/view-tree")
    public Result viewTree(@RequestHeader(name = "Cookie") String sessinoId,
                           @PathVariable String projectName,
                           @RequestParam("processId") Integer id,
                           @RequestParam("limit") Integer limit);

    @GetMapping(value = "/projects/{projectName}/processgen-task-list")
    public Result getNodeListByDefinitionId(
            @RequestHeader(name = "Cookie") String sessinoId,
            @PathVariable String projectName,
            @RequestParam("processDefinitionId") Integer processDefinitionId);

    @GetMapping(value = "/projects/{projectName}/processget-task-list")
    public Result getNodeListByDefinitionIdList(
            @RequestHeader(name = "Cookie") String sessinoId,
            @PathVariable String projectName,
            @RequestParam("processDefinitionIdList") String processDefinitionIdList);

    @GetMapping(value = "/projects/{projectName}/process/delete")
    public Result deleteProcessDefinitionById(@RequestHeader(name = "Cookie") String sessinoId,
                                              @PathVariable String projectName,
                                              @RequestParam("processDefinitionId") Integer processDefinitionId
    );


    @GetMapping(value = "/projects/{projectName}/process/batch-delete")
    public Result batchDeleteProcessDefinitionByIds(@RequestHeader(name = "Cookie") String sessinoId,
                                                    @PathVariable String projectName,
                                                    @RequestParam("processDefinitionIds") String processDefinitionIds
    );

    @GetMapping(value = "/projects/{projectName}/process/export")
    public void batchExportProcessDefinitionByIds(@RequestHeader(name = "Cookie") String sessinoId,
                                                  @PathVariable String projectName,
                                                  @RequestParam("processDefinitionIds") String processDefinitionIds);

    @GetMapping(value = "/projects/{projectName}/process/queryProcessDefinitionAllByProjectId")
    public Result queryProcessDefinitionAllByProjectId(@RequestHeader(name = "Cookie") String sessinoId,
                                                       @RequestParam("projectId") Integer projectId);

}
