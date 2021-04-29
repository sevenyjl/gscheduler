package com.gs.cd.gscheduler.api.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.common.entity.Schedule;
import com.gs.cd.gscheduler.common.entity.TaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * task instance service
 */
public interface TaskInstanceService extends IService<TaskInstance> {

    List<TaskInstance> findValidTaskListByProcessId(Integer processInstanceId);
}
