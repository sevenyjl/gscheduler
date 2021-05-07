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
//package com.gs.cd.gscheduler.server.worker.task.spark;
//
//import com.gs.cd.gscheduler.common.enums.CommandType;
//import com.gs.cd.gscheduler.common.enums.SparkVersion;
//import com.gs.cd.gscheduler.common.process.Property;
//import com.gs.cd.gscheduler.common.process.ResourceInfo;
//import com.gs.cd.gscheduler.common.task.AbstractParameters;
//import com.gs.cd.gscheduler.common.task.spark.SparkParameters;
//import com.gs.cd.gscheduler.common.utils.JSONUtils;
//import com.gs.cd.gscheduler.common.utils.ParameterUtils;
//import com.gs.cd.gscheduler.common.utils.StringUtils;
//import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
//import com.gs.cd.gscheduler.server.utils.ParamUtils;
//import com.gs.cd.gscheduler.server.utils.SparkArgsUtils;
//import com.gs.cd.gscheduler.server.worker.task.AbstractYarnTask;
//import com.gs.cd.gscheduler.dao.entity.Resource;
//import org.slf4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * spark task
// */
//public class SparkTask extends AbstractYarnTask {
//
//  /**
//   * spark1 command
//   */
//  private static final String SPARK1_COMMAND = "${SPARK_HOME1}/bin/spark-submit";
//
//  /**
//   * spark2 command
//   */
//  private static final String SPARK2_COMMAND = "${SPARK_HOME2}/bin/spark-submit";
//
//  /**
//   *  spark parameters
//   */
//  private SparkParameters sparkParameters;
//
//  /**
//   * taskExecutionContext
//   */
//  private TaskExecutionContext taskExecutionContext;
//
//  public SparkTask(TaskExecutionContext taskExecutionContext, Logger logger) {
//    super(taskExecutionContext, logger);
//    this.taskExecutionContext = taskExecutionContext;
//  }
//
//  @Override
//  public void init() {
//
//    logger.info("spark task params {}", taskExecutionContext.getTaskParams());
//
//    sparkParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), SparkParameters.class);
//
//    if (!sparkParameters.checkParameters()) {
//      throw new RuntimeException("spark task params is not valid");
//    }
//    sparkParameters.setQueue(taskExecutionContext.getQueue());
//
//    setMainJarName();
//
//    if (StringUtils.isNotEmpty(sparkParameters.getMainArgs())) {
//      String args = sparkParameters.getMainArgs();
//
//      // replace placeholder
//      Map<String, Property> paramsMap = ParamUtils.convert(ParamUtils.getUserDefParamsMap(taskExecutionContext.getDefinedParams()),
//              taskExecutionContext.getDefinedParams(),
//              sparkParameters.getLocalParametersMap(),
//              CommandType.of(taskExecutionContext.getCmdTypeIfComplement()),
//              taskExecutionContext.getScheduleTime());
//
//      if (paramsMap != null ){
//        args = ParameterUtils.convertParameterPlaceholders(args, ParamUtils.convert(paramsMap));
//      }
//      sparkParameters.setMainArgs(args);
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
//    //spark version
//    String sparkCommand = SPARK2_COMMAND;
//
//    if (SparkVersion.SPARK1.name().equals(sparkParameters.getSparkVersion())) {
//      sparkCommand = SPARK1_COMMAND;
//    }
//
//    args.add(sparkCommand);
//
//    // other parameters
//    args.addAll(SparkArgsUtils.buildArgs(sparkParameters));
//
//    String command = ParameterUtils
//            .convertParameterPlaceholders(String.join(" ", args), taskExecutionContext.getDefinedParams());
//
//    logger.info("spark task command : {}", command);
//
//    return command;
//  }
//
//  @Override
//  protected void setMainJarName() {
//    // main jar
//    ResourceInfo mainJar = sparkParameters.getMainJar();
//    if (mainJar != null) {
//      int resourceId = mainJar.getId();
//      String resourceName;
//      if (resourceId == 0) {
//        resourceName = mainJar.getRes();
//      } else {
//        Resource resource = processService.getResourceById(sparkParameters.getMainJar().getId());
//        if (resource == null) {
//          logger.error("resource id: {} not exist", resourceId);
//          throw new RuntimeException(String.format("resource id: %d not exist", resourceId));
//        }
//        resourceName = resource.getFullName().replaceFirst("/", "");
//      }
//      mainJar.setRes(resourceName);
//      sparkParameters.setMainJar(mainJar);
//    }
//  }
//
//  @Override
//  public AbstractParameters getParameters() {
//    return sparkParameters;
//  }
//}
