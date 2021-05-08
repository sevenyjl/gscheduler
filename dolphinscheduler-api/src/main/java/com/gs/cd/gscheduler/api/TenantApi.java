package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.entity.Tenant;
import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author seven
 * @Date 2021/4/13 18:13
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}/tenant", name = "TenantApi")
public interface TenantApi {

    @PostMapping(value = "/create")
    public Result createTenant(@RequestHeader(name = "Cookie") String sessinoId,
                               @RequestParam(value = "tenantCode") String tenantCode,
                               @RequestParam(value = "tenantName") String tenantName,
                               @RequestParam(value = "queueId") int queueId,
                               @RequestParam(value = "description", required = false) String description);

    @GetMapping(value = "/list-paging")
    public Result queryTenantlistPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                        @RequestParam("pageNo") Integer pageNo,
                                        @RequestParam(value = "searchVal", required = false) String searchVal,
                                        @RequestParam("pageSize") Integer pageSize);

    @GetMapping(value = "/list")
    public Result<List<Tenant>> queryTenantlist(@RequestHeader(name = "Cookie") String sessinoId);


    @PostMapping(value = "/update")
    public Result updateTenant(@RequestHeader(name = "Cookie") String sessinoId,
                               @RequestParam(value = "id") int id,
                               @RequestParam(value = "tenantCode") String tenantCode,
                               @RequestParam(value = "tenantName") String tenantName,
                               @RequestParam(value = "queueId") int queueId,
                               @RequestParam(value = "description", required = false) String description);

    @PostMapping(value = "/delete")
    public Result deleteTenantById(@RequestHeader(name = "Cookie") String sessinoId,
                                   @RequestParam(value = "id") int id);

    @GetMapping(value = "/verify-tenant-code")
    public Result verifyTenantCode(@RequestHeader(name = "Cookie") String sessinoId,
                                   @RequestParam(value = "tenantCode") String tenantCode
    );
}
