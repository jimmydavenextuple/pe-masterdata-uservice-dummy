/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.core.consumer;

import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;

import java.util.Map;

public class TestUtil {
    public LocalCacheUpdateEvent getLocalCacheUpdateEvent(
            Map<String, Object> message, String entityName) {
        LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
        LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
        localCacheUpdateMessage.setMessage(message);
        localCacheUpdateMessage.setEntityName(entityName);
        localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

        return localCacheUpdateEvent;
    }

    public LocalCacheUpdateEvent getLocalCacheUpdateEvent(String entityName) {
        LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
        LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
        Map<String, Object> message = createCacheUpdateMessage(entityName);
        localCacheUpdateMessage.setMessage(message);
        localCacheUpdateMessage.setEntityName(entityName);
        localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

        return localCacheUpdateEvent;
    }

    private Map<String, Object> createCacheUpdateMessage(String entityName) {
        return Map.of();
    }
}
