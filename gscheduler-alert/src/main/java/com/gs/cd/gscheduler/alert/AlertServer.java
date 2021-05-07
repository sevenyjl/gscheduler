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
package com.gs.cd.gscheduler.alert;

import com.gs.cd.gscheduler.alert.plugin.EmailAlertPlugin;
import com.gs.cd.gscheduler.alert.runner.AlertSender;
import com.gs.cd.gscheduler.alert.utils.Constants;
import com.gs.cd.gscheduler.alert.utils.PropertyUtils;
import com.gs.cd.gscheduler.common.plugin.FilePluginManager;
import com.gs.cd.gscheduler.common.thread.Stopper;
import com.gs.cd.gscheduler.dao.AlertDao;
import com.gs.cd.gscheduler.dao.DaoFactory;
import com.gs.cd.gscheduler.dao.entity.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * alert of start
 */
public class AlertServer {
    private static final Logger logger = LoggerFactory.getLogger(AlertServer.class);
    /**
     * Alert Dao
     */
    private AlertDao alertDao = DaoFactory.getDaoInstance(AlertDao.class);

    private AlertSender alertSender;

    private static AlertServer instance;

    private FilePluginManager alertPluginManager;

    private static final String[] whitePrefixes = new String[]{"com.gs.cd.gscheduler.plugin.utils."};

    private static final String[] excludePrefixes = new String[]{
            "com.gs.cd.gscheduler.plugin.",
            "ch.qos.logback.",
            "org.slf4j."
    };

    public AlertServer() {
        alertPluginManager =
                new FilePluginManager(PropertyUtils.getString(Constants.PLUGIN_DIR), whitePrefixes, excludePrefixes);
        // add default alert plugins
        alertPluginManager.addPlugin(new EmailAlertPlugin());
    }

    public synchronized static AlertServer getInstance() {
        if (null == instance) {
            instance = new AlertServer();
        }
        return instance;
    }

    public void start() {
        logger.info("alert server ready start ");
        while (Stopper.isRunning()) {
            try {
                Thread.sleep(Constants.ALERT_SCAN_INTERVAL);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            List<Alert> alerts = alertDao.listWaitExecutionAlert();
            alertSender = new AlertSender(alerts, alertDao, alertPluginManager);
            alertSender.run();
        }
    }


    public static void main(String[] args) {
        AlertServer alertServer = AlertServer.getInstance();
        alertServer.start();
    }

}
