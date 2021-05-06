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
package com.gs.cd.gscheduler.api.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.nio.file.Path;
import java.util.List;

/**
 * page info
 *
 * @param <T> model
 */
@Data
public class PageInfo<T> {
    /**
     * list
     */
    private List<T> totalList;
    /**
     * total count
     */
    private Integer total = 0;
    /**
     * page size
     */
    private Integer pageSize = 20;
    /**
     * current page
     */
    private Integer totalPage = 0;
    /**
     * pageNo
     */
    private Integer pageNo;

    public PageInfo(Integer pageSize, Integer pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public PageInfo() {
    }

    public static <B> PageInfo<B> pageInfoTrans(IPage<B> page) {
        PageInfo<B> pageInfo = new PageInfo<B>(Math.toIntExact(page.getSize()), Math.toIntExact(page.getCurrent()));
        pageInfo.total = Math.toIntExact(page.getTotal());
        pageInfo.totalList = page.getRecords();
        pageInfo.totalPage = Math.toIntExact(page.getPages());
        return pageInfo;
    }
}