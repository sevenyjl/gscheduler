package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/13 15:37
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "AccessTokenApi")
public interface AccessTokenApi {
    @PostMapping(value = "/access-token/create")
    public Result createToken(@RequestHeader(name = "Cookie") String sessinoId,
                              @RequestParam(value = "userId") int userId,
                              @RequestParam(value = "expireTime") String expireTime,
                              @RequestParam(value = "token") String token);

    @PostMapping(value = "/access-token/generate")
    public Result generateToken(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam(value = "userId") int userId,
                                @RequestParam(value = "expireTime") String expireTime);

    @GetMapping(value = "/access-token/list-paging")
    @ResponseStatus(HttpStatus.OK)
    public Result queryAccessTokenList(@RequestHeader(name = "Cookie") String sessinoId,
                                       @RequestParam("pageNo") Integer pageNo,
                                       @RequestParam(value = "searchVal", required = false) String searchVal,
                                       @RequestParam("pageSize") Integer pageSize);

    @PostMapping(value = "/access-token/delete")
    public Result delAccessTokenById(@RequestHeader(name = "Cookie") String sessinoId,
                                     @RequestParam(value = "id") int id);

    @PostMapping(value = "/access-token/update")
    public Result updateToken(@RequestHeader(name = "Cookie") String sessinoId,
                              @RequestParam(value = "id") int id,
                              @RequestParam(value = "userId") int userId,
                              @RequestParam(value = "expireTime") String expireTime,
                              @RequestParam(value = "token") String token);
}
