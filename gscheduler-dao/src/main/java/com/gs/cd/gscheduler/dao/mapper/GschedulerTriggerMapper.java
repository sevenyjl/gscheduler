package com.gs.cd.gscheduler.dao.mapper;

import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
public interface GschedulerTriggerMapper extends BaseMapper<GschedulerTrigger> {

    @Select("SELECT tenant_code FROM \"kipf_platform\".\"sys_tenant_own_store\"")
    List<String> listAllTenantCode();

    @Select("SELECT * FROM \"#{tenantCode}\".\"gscheduler_trigger\"")
    List<GschedulerTrigger> listByTenantCode(String tenantCode);
}
