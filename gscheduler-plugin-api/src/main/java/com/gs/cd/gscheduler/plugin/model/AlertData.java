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
package com.gs.cd.gscheduler.plugin.model;

/**
 * AlertData
 */
public class AlertData {

    /**
     * alert primary key
     */
    private int id;
    /**
     * title
     */
    private String title;
    /**
     * content
     */
    private String content;
    /**
     * log
     */
    private String log;
    /**
     * alertgroup_id
     */
    private int alertGroupId;
    /**
     * receivers
     */
    private String receivers;
    /**
     * show_type
     */
    private String showType;
    /**
     * receivers_cc
     */
    private String receiversCc;

    public int getId() {
        return id;
    }

    public AlertData setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AlertData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AlertData setContent(String content) {
        this.content = content;
        return this;
    }

    public String getLog() {
        return log;
    }

    public AlertData setLog(String log) {
        this.log = log;
        return this;
    }

    public int getAlertGroupId() {
        return alertGroupId;
    }

    public AlertData setAlertGroupId(int alertGroupId) {
        this.alertGroupId = alertGroupId;
        return this;
    }

    public String getReceivers() {
        return receivers;
    }

    public AlertData setReceivers(String receivers) {
        this.receivers = receivers;
        return this;
    }

    public String getReceiversCc() {
        return receiversCc;
    }

    public AlertData setReceiversCc(String receiversCc) {
        this.receiversCc = receiversCc;
        return this;
    }

    public String getShowType() {
        return showType;
    }

    public AlertData setShowType(String showType) {
        this.showType = showType;
        return this;
    }

}
