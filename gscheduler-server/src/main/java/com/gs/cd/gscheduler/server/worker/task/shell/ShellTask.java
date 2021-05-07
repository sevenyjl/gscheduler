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
package com.gs.cd.gscheduler.server.worker.task.shell;

import static java.util.Calendar.DAY_OF_MONTH;

import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.CommandType;
import com.gs.cd.gscheduler.common.process.Property;
import com.gs.cd.gscheduler.common.task.AbstractParameters;
import com.gs.cd.gscheduler.common.task.shell.ShellParameters;
import com.gs.cd.gscheduler.common.utils.DateUtils;
import com.gs.cd.gscheduler.common.utils.JSONUtils;
import com.gs.cd.gscheduler.common.utils.OSUtils;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
import com.gs.cd.gscheduler.server.utils.ParamUtils;
import com.gs.cd.gscheduler.server.worker.task.AbstractTask;
import com.gs.cd.gscheduler.server.worker.task.CommandExecuteResult;
import com.gs.cd.gscheduler.server.worker.task.ShellCommandExecutor;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * shell task
 */
public class ShellTask extends AbstractTask {

  /**
   * shell parameters
   */
  private ShellParameters shellParameters;

  /**
   * shell command executor
   */
  private ShellCommandExecutor shellCommandExecutor;

  /**
   *  taskExecutionContext
   */
  private TaskExecutionContext taskExecutionContext;

  /**
   * constructor
   * @param taskExecutionContext taskExecutionContext
   * @param logger    logger
   */
  public ShellTask(TaskExecutionContext taskExecutionContext, Logger logger) {
    super(taskExecutionContext, logger);

    this.taskExecutionContext = taskExecutionContext;
    this.shellCommandExecutor = new ShellCommandExecutor(this::logHandle,
            taskExecutionContext,
            logger);
  }

  @Override
  public void init() {
    logger.info("shell task params {}", taskExecutionContext.getTaskParams());

    shellParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), ShellParameters.class);

    if (!shellParameters.checkParameters()) {
      throw new RuntimeException("shell task params is not valid");
    }
  }

  @Override
  public void handle() throws Exception {
    try {
      // construct process
      CommandExecuteResult commandExecuteResult = shellCommandExecutor.run(buildCommand());
      setExitStatusCode(commandExecuteResult.getExitStatusCode());
      setAppIds(commandExecuteResult.getAppIds());
      setProcessId(commandExecuteResult.getProcessId());
    } catch (Exception e) {
      logger.error("shell task error", e);
      setExitStatusCode(Constants.EXIT_CODE_FAILURE);
      throw e;
    }
  }

  @Override
  public void cancelApplication(boolean cancelApplication) throws Exception {
    // cancel process
    shellCommandExecutor.cancelApplication();
  }

  /**
   * create command
   * @return file name
   * @throws Exception exception
   */
  private String buildCommand() throws Exception {
    // generate scripts
    String fileName = String.format("%s/%s_node.%s",
            taskExecutionContext.getExecutePath(),
            taskExecutionContext.getTaskAppId(), OSUtils.isWindows() ? "bat" : "sh");

    Path path = new File(fileName).toPath();

    if (Files.exists(path)) {
      return fileName;
    }

    String script = shellParameters.getRawScript().replaceAll("\\r\\n", "\n");
    /**
     *  combining local and global parameters
     */
    Map<String, Property> paramsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
        taskExecutionContext.getDefinedParams(),
        shellParameters.getLocalParametersMap(),
        CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
        taskExecutionContext.getScheduleTime());
    // replace variable TIME with $[YYYYmmddd...] in shell file when history run job and batch complement job
    if (taskExecutionContext.getScheduleTime() != null) {
      if (paramsMap == null) {
        paramsMap = new HashMap<>();
      }
      Date date = taskExecutionContext.getScheduleTime();
      if (CommandType.COMPLEMENT_DATA.getCode() == taskExecutionContext.getCmdTypeIfComplement()) {
        date = DateUtils.add(taskExecutionContext.getScheduleTime(), DAY_OF_MONTH, 1);
      }
      String dateTime = DateUtils.format(date, Constants.PARAMETER_FORMAT_TIME);
      Property p = new Property();
      p.setValue(dateTime);
      p.setProp(Constants.PARAMETER_DATETIME);
      paramsMap.put(Constants.PARAMETER_DATETIME, p);
    }
    script = ParameterUtils.convertParameterPlaceholders(script, ParamUtils.convert(paramsMap));

    shellParameters.setRawScript(script);

    logger.info("raw script : {}", shellParameters.getRawScript());
    logger.info("task execute path : {}", taskExecutionContext.getExecutePath());

    Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
    FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

    if (OSUtils.isWindows()) {
      Files.createFile(path);
    } else {
      Files.createFile(path, attr);
    }

    Files.write(path, shellParameters.getRawScript().getBytes(), StandardOpenOption.APPEND);

    return fileName;
  }

  @Override
  public AbstractParameters getParameters() {
    return shellParameters;
  }

}
