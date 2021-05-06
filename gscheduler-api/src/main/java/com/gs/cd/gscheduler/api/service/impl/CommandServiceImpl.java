package com.gs.cd.gscheduler.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.api.service.CommandService;
import com.gs.cd.gscheduler.api.service.RelationProcessInstanceService;
import com.gs.cd.gscheduler.common.entity.Command;
import com.gs.cd.gscheduler.common.entity.RelationProcessInstance;
import com.gs.cd.gscheduler.common.enums.CommandType;
import com.gs.cd.gscheduler.dao.mapper.CommandMapper;
import com.gs.cd.gscheduler.dao.mapper.RelationProcessInstanceMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gs.cd.gscheduler.common.Constants.CMDPARAM_RECOVER_PROCESS_ID_STRING;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2021-04-30
 */
@Service
public class CommandServiceImpl extends ServiceImpl<CommandMapper, Command> implements CommandService {

    /**
     * check the input command exists in queue list
     *
     * @param command command
     * @return create command result
     */
    @Override
    public Boolean verifyIsNeedCreateCommand(Command command) {
        Boolean isNeedCreate = true;
        Map<CommandType, Integer> cmdTypeMap = new HashMap<CommandType, Integer>();
        cmdTypeMap.put(CommandType.REPEAT_RUNNING, 1);
        cmdTypeMap.put(CommandType.RECOVER_SUSPENDED_PROCESS, 1);
        cmdTypeMap.put(CommandType.START_FAILURE_TASK_PROCESS, 1);
        CommandType commandType = command.getCommandType();

        if (cmdTypeMap.containsKey(commandType)) {
            JSONObject cmdParamObj = (JSONObject) JSON.parse(command.getCommandParam());
            JSONObject tempObj;
            int processInstanceId = cmdParamObj.getInteger(CMDPARAM_RECOVER_PROCESS_ID_STRING);
            List<Command> commands = list();
            // todo 查询优化
            for (Command tmpCommand : commands) {
                if (cmdTypeMap.containsKey(tmpCommand.getCommandType())) {
                    tempObj = (JSONObject) JSON.parse(tmpCommand.getCommandParam());
                    if (tempObj != null && processInstanceId == tempObj.getInteger(CMDPARAM_RECOVER_PROCESS_ID_STRING)) {
                        isNeedCreate = false;
                        break;
                    }
                }
            }
        }
        return isNeedCreate;
    }
}
