package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.entity.User;
import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author seven
 * @Date 2021/4/13 15:44
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "UsersApi")
public interface UsersApi {
    @PostMapping(value = "/users/create")
    public Result createUser(@RequestHeader(name = "Cookie") String sessinoId,
                             @RequestParam(value = "userName") String userName,
                             @RequestParam(value = "userPassword") String userPassword,
                             @RequestParam(value = "tenantId") int tenantId,
                             @RequestParam(value = "queue", required = false, defaultValue = "") String queue,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "phone", required = false) String phone);

    @GetMapping(value = "/users/list-paging")
    public Result queryUserList(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam("pageNo") Integer pageNo,
                                @RequestParam(value = "searchVal", required = false) String searchVal,
                                @RequestParam("pageSize") Integer pageSize);

    @PostMapping(value = "/users/update")
    public Result updateUser(@RequestHeader(name = "Cookie") String sessinoId,
                             @RequestParam(value = "id") int id,
                             @RequestParam(value = "userName") String userName,
                             @RequestParam(value = "userPassword") String userPassword,
                             @RequestParam(value = "queue", required = false, defaultValue = "") String queue,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "tenantId") int tenantId,
                             @RequestParam(value = "phone", required = false) String phone);

    @PostMapping(value = "/users/delete")
    public Result delUserById(@RequestHeader(name = "Cookie") String sessinoId);

    @PostMapping(value = "/users/grant-project")
    public Result grantProject(@RequestHeader(name = "Cookie") String sessinoId,
                               @RequestParam(value = "userId") int userId,
                               @RequestParam(value = "projectIds") String projectIds);

    @PostMapping(value = "/users/grant-file")
    public Result grantResource(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam(value = "userId") int userId,
                                @RequestParam(value = "resourceIds") String resourceIds);

    @PostMapping(value = "/users/grant-udf-func")
    public Result grantUDFFunc(@RequestHeader(name = "Cookie") String sessinoId,
                               @RequestParam(value = "userId") int userId,
                               @RequestParam(value = "udfIds") String udfIds);

    @PostMapping(value = "/users/grant-datasource")
    public Result grantDataSource(@RequestHeader(name = "Cookie") String sessinoId,
                                  @RequestParam(value = "userId") int userId,
                                  @RequestParam(value = "datasourceIds") String datasourceIds);

    @GetMapping(value = "/users/get-user-info")
    public Result getUserInfo(@RequestHeader(name = "Cookie") String sessinoId);

    @GetMapping(value = "/users/list")
    public Result<List<User>> listUser(@RequestHeader(name = "Cookie") String sessinoId);

    @GetMapping(value = "/users/list-all")
    public Result listAll(@RequestHeader(name = "Cookie") String sessinoId);

    @GetMapping(value = "/users/verify-user-name")
    public Result verifyUserName(@RequestHeader(name = "Cookie") String sessinoId,
                                 @RequestParam(value = "userName") String userName
    );

    @GetMapping(value = "/users/unauth-user")
    public Result unauthorizedUser(@RequestHeader(name = "Cookie") String sessinoId,
                                   @RequestParam("alertgroupId") Integer alertgroupId);

    @GetMapping(value = "/users/authed-user")
    public Result authorizedUser(@RequestHeader(name = "Cookie") String sessinoId,
                                 @RequestParam("alertgroupId") Integer alertgroupId);

}
