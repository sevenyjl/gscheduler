package com.gs.cd.gscheduler.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.common.entity.Command;
import com.gs.cd.gscheduler.common.entity.RelationProcessInstance;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2021-04-30
 */
public interface CommandService extends IService<Command> {

    Boolean verifyIsNeedCreateCommand(Command command);
}
