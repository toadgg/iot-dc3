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

package com.dc3.center.data.service.rabbit;

import com.dc3.center.data.service.DeviceEventService;
import com.dc3.common.bean.driver.DeviceEvent;
import com.dc3.common.constant.Common;
import com.dc3.common.utils.RedisUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 接收驱动发送过来的设备事件数据
 * 其中包括：设备心跳事件、在线、离线、故障等其他事件
 *
 * @author pnoker
 */
@Slf4j
@Component
public class DeviceEventReceiver {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private DeviceEventService deviceEventService;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @RabbitHandler
    @RabbitListener(queues = "#{deviceEventQueue.name}")
    public void deviceEventReceive(Channel channel, Message message, DeviceEvent deviceEvent) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            if (null == deviceEvent || null == deviceEvent.getDeviceId()) {
                log.error("Invalid device event: {}", deviceEvent);
                return;
            }
            log.debug("Device event, From: {}, Received: {}", message.getMessageProperties().getReceivedRoutingKey(), deviceEvent);

            if (Common.Device.Event.HEARTBEAT.equals(deviceEvent.getType())) {
                // Save device heartbeat to Redis
                redisUtil.setKey(
                        Common.Cache.DEVICE_STATUS_KEY_PREFIX + deviceEvent.getDeviceId(),
                        deviceEvent.getContent(),
                        deviceEvent.getTimeOut(),
                        deviceEvent.getTimeUnit()
                );
            } else {
                // Save device event to MongoDB
                threadPoolExecutor.execute(() -> deviceEventService.addDeviceEvent(deviceEvent));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
