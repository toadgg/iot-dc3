/*
 * Copyright 2018-2020 Pnoker. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc3.common.bean.driver;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author pnoker
 */
@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serviceName;
    private String type;
    private Object content;

    @Transient
    private int timeOut = 15;

    @Transient
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    private Long originTime;

    public DriverEvent(String serviceName, String type, Object content) {
        this.serviceName = serviceName;
        this.type = type;
        this.content = content;
        this.originTime = System.currentTimeMillis();
    }

    public DriverEvent(String serviceName, String type, Object content, int timeOut, TimeUnit timeUnit) {
        this.serviceName = serviceName;
        this.type = type;
        this.content = content;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.originTime = System.currentTimeMillis();
    }
}
