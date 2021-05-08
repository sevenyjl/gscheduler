package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * @Author seven
 * @Date 2021/4/13 15:02
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "LoginApi")
public interface LoginApi {
    @PostMapping(value = "/login")
    public Result login(@RequestParam(value = "userName") String userName,
                        @RequestParam(value = "userPassword") String userPassword);

    @PostMapping(value = "/signOut")
    public Result signOut(@RequestHeader(name = "Cookie") String sessinoId);
}
