///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.gs.cd.gscheduler.server.worker.task.flink;
//
//import com.gs.cd.gscheduler.common.enums.CommandType;
//import com.gs.cd.gscheduler.common.process.Property;
//import com.gs.cd.gscheduler.common.process.ResourceInfo;
//import com.gs.cd.gscheduler.common.task.AbstractParameters;
//import com.gs.cd.gscheduler.common.task.flink.FlinkParameters;
//import com.gs.cd.gscheduler.common.utils.JSONUtils;
//import com.gs.cd.gscheduler.common.utils.ParameterUtils;
//import com.gs.cd.gscheduler.common.utils.StringUtils;
//import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
//import com.gs.cd.gscheduler.server.utils.FlinkArgsUtils;
//import com.gs.cd.gscheduler.server.utils.ParamUtils;
//import com.gs.cd.gscheduler.server.worker.task.AbstractYarnTask;
//import com.gs.cd.gscheduler.dao.entity.Resource;
//import org.slf4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * flink task
// */
//public class FlinkTask extends AbstractYarnTask {
//
//  /**
//   *  flink command
//   */
//  private static final String FLINK_COMMAND = "flink";
//  private static final String FLINK_RUN = "run";
//
//  /**
//   *  flink parameters
//   */
//  private FlinkParameters flinkParameters;
//
//  /**
//   * taskExecutionContext
//   */
//  private TaskExecutionContext taskExecutionContext;
//
//  public FlinkTask(TaskExecutionContext taskExecutionContext, Logger logger) {
//    super(taskExecutionContext, logger);
//    this.taskExecutionContext = taskExecutionContext;
//  }
//
//  @Override
//  public void init() {
//
//    logger.info("flink task params {}", taskExecutionContext.getTaskParams());
//
//    flinkParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), FlinkParameters.class);
//
//    if (!flinkParameters.checkParameters()) {
//      throw new RuntimeException("flink task params is not valid");
//    }
//    flinkParameters.setQueue(taskExecutionContext.getQueue());
//    setMainJarName();
//
//
//    if (StringUtils.isNotEmpty(flinkParameters.getMainArgs())) {
//      String args = flinkParameters.getMainArgs();
//
//
//      // replace placeholder
//      Map<String, Property> paramsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
//              taskExecutionContext.getDefinedParams(),
//              flinkParameters.getLocalParametersMap(),
//              CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
//              taskExecutionContext.getScheduleTime());
//
//      logger.info("param Map : {}", paramsMap);
//      if (paramsMap != null ){
//
//        args = ParameterUtils.convertParameterPlaceholders(args, ParamUtils.convert(paramsMap));
//        logger.info("param args : {}", args);
//      }
//      flinkParameters.setMainArgs(args);
//    }
//  }
//
//  /**
//   * create command
//   * @return command
//   */
//  @Override
//  protected String buildCommand() {
//    List<String> args = new ArrayList<>();
//
//    args.add(FLINK_COMMAND);
//    args.add(FLINK_RUN);
//    logger.info("flink task args : {}", args);
//    // other parameters
//    args.addAll(FlinkArgsUtils.buildArgs(flinkParameters));
//
//    String command = ParameterUtils
//            .convertParameterPlaceholders(String.join(" ", args), taskExecutionContext.getDefinedParams());
//
//    logger.info("flink task command : {}", command);
//
//    return command;
//  }
//
//  @Override
//  protected void setMainJarName() {
//    // main jar
//    ResourceInfo mainJar = flinkParameters.getMainJar();
//    if (mainJar != null) {
//      int resourceId = mainJar.getId();
//      String resourceName;
//      if (resourceId == 0) {
//        resourceName = mainJar.getRes();
//      } else {
//        Resource resource = processService.getResourceById(flinkParameters.getMainJar().getId());
//        if (resource == null) {
//          logger.error("resource id: {} not exist", resourceId);
//          throw new RuntimeException(String.format("resource id: %d not exist", resourceId));
//        }
//        resourceName = resource.getFullName().replaceFirst("/", "");
//      }
//      mainJar.setRes(resourceName);
//      flinkParameters.setMainJar(mainJar);
//    }
//  }
//
//  @Override
//  public AbstractParameters getParameters() {
//    return flinkParameters;
//  }
//}
