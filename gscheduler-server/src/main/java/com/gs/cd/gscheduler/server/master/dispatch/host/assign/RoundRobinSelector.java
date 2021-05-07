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
package com.gs.cd.gscheduler.server.master.dispatch.host.assign;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round robin selector
 * @param <T> T
 */
@Service
public class RoundRobinSelector<T> implements Selector<T> {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public T select(Collection<T> source) {
        if (source == null || source.size() == 0) {
            throw new IllegalArgumentException("Empty source.");
        }

        /**
         * if only one , return directly
         */
        if (source.size() == 1) {
            return (T)source.toArray()[0];
        }

        int size = source.size();
        /**
         * round robin
         */
        return (T) source.toArray()[index.getAndIncrement() % size];
    }
}
