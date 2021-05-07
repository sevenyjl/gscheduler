/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.cd.gscheduler.server.worker.task.python;


import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.CommandType;
import com.gs.cd.gscheduler.common.process.Property;
import com.gs.cd.gscheduler.common.task.AbstractParameters;
import com.gs.cd.gscheduler.common.task.python.PythonParameters;
import com.gs.cd.gscheduler.common.utils.JSONUtils;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
import com.gs.cd.gscheduler.server.utils.ParamUtils;
import com.gs.cd.gscheduler.server.worker.task.AbstractTask;
import com.gs.cd.gscheduler.server.worker.task.CommandExecuteResult;
import com.gs.cd.gscheduler.server.worker.task.PythonCommandExecutor;
import org.slf4j.Logger;

import java.util.Map;

/**
 *  python task
 */
public class PythonTask extends AbstractTask {

  /**
   *  python parameters
   */
  private PythonParameters pythonParameters;

  /**
   *  task dir
   */
  private String taskDir;

  /**
   * python command executor
   */
  private PythonCommandExecutor pythonCommandExecutor;

  /**
   * taskExecutionContext
   */
  private TaskExecutionContext taskExecutionContext;

  /**
   * constructor
   * @param taskExecutionContext taskExecutionContext
   * @param logger    logger
   */
  public PythonTask(TaskExecutionContext taskExecutionContext, Logger logger) {
    super(taskExecutionContext, logger);
    this.taskExecutionContext = taskExecutionContext;

    this.pythonCommandExecutor = new PythonCommandExecutor(this::logHandle,
            taskExecutionContext,
            logger);
  }

  @Override
  public void init() {
    logger.info("python task params {}", taskExecutionContext.getTaskParams());

    pythonParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), PythonParameters.class);

    if (!pythonParameters.checkParameters()) {
      throw new RuntimeException("python task params is not valid");
    }
  }

  @Override
  public void handle() throws Exception {
    try {
      //  construct process
      CommandExecuteResult commandExecuteResult = pythonCommandExecutor.run(buildCommand());

      setExitStatusCode(commandExecuteResult.getExitStatusCode());
      setAppIds(commandExecuteResult.getAppIds());
      setProcessId(commandExecuteResult.getProcessId());
    }
    catch (Exception e) {
      logger.error("python task failure", e);
      setExitStatusCode(Constants.EXIT_CODE_FAILURE);
      throw e;
    }
  }

  @Override
  public void cancelApplication(boolean cancelApplication) throws Exception {
    // cancel process
    pythonCommandExecutor.cancelApplication();
  }

  /**
   * build command
   * @return raw python script
   * @throws Exception exception
   */
  private String buildCommand() throws Exception {
    String rawPythonScript = pythonParameters.getRawScript().replaceAll("\\r\\n", "\n");

    // replace placeholder
    Map<String, Property> paramsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
            taskExecutionContext.getDefinedParams(),
            pythonParameters.getLocalParametersMap(),
            CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
            taskExecutionContext.getScheduleTime());
    if (paramsMap != null){
      rawPythonScript = ParameterUtils.convertParameterPlaceholders(rawPythonScript, ParamUtils.convert(paramsMap));
    }

    logger.info("raw python script : {}", pythonParameters.getRawScript());
    logger.info("task dir : {}", taskDir);

    return rawPythonScript;
  }

  @Override
  public AbstractParameters getParameters() {
    return pythonParameters;
  }



}
