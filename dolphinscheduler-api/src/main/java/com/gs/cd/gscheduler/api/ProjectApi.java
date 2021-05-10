package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author seven
 * @Date 2021/4/13 16:06
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}/projects", name = "ProjectApi")
public interface ProjectApi {

    @PostMapping(value = "/create")
    public Result createProject(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "userName", required = false, defaultValue = "defaultUser") String userName);


    @PostMapping(value = "/update")
    public Result updateProject(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam("projectId") Integer projectId,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "userName", required = false, defaultValue = "defaultUser") String userName);


    @GetMapping(value = "/query-by-id")
    public Result queryProjectById(@RequestHeader(name = "Cookie") String sessinoId,
                                   @RequestParam("projectId") Integer projectId);


    @GetMapping(value = "/list-paging")
    public Result queryProjectListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                         @RequestParam(value = "searchVal", required = false) String searchVal,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam("pageNo") Integer pageNo
    );

    @GetMapping(value = "/delete")
    public Result deleteProject(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam("projectId") Integer projectId
    );


    @GetMapping(value = "/unauth-project")
    public Result queryUnauthorizedProject(@RequestHeader(name = "Cookie") String sessinoId,
                                           @RequestParam("userId") Integer userId);


    @GetMapping(value = "/authed-project")
    public Result queryAuthorizedProject(@RequestHeader(name = "Cookie") String sessinoId,
                                         @RequestParam("userId") Integer userId);


    @PostMapping(value = "/import-definition")
    public Result importProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("projectName") String projectName,
                                          @RequestParam(value = "userName", required = false, defaultValue = "defaultUser") String userName);


    @GetMapping(value = "/query-project-list")
    public Result queryAllProjectList(@RequestHeader(name = "Cookie") String sessinoId);


}
