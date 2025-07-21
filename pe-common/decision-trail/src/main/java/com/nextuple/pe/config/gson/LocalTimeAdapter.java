/*
 *
 *  * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.pe.config.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

  @Override
  public LocalTime deserialize(
      JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return LocalTime.parse(json.getAsString(), formatter);
  }

  @Override
  public JsonElement serialize(
      LocalTime localTime, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(localTime.format(formatter));
  }
}
