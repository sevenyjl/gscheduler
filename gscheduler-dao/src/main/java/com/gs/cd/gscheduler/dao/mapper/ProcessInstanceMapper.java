
package com.gs.cd.gscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.cd.gscheduler.common.entity.ProcessInstance;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * process instance mapper interface
 */
public interface ProcessInstanceMapper extends BaseMapper<ProcessInstance> {

}
