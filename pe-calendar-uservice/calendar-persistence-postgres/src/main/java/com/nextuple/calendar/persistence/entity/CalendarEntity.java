/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.entity;

import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.calendar.persistence.entity.key.CalendarKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "calendars")
@IdClass(CalendarKey.class)
public class CalendarEntity extends CommonBaseEntity {

  @Id
  @Column(name = "calendar_id")
  private String calendarId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  private String description;

  @Column(name = "is_monday_working")
  private Boolean isMondayWorking;

  @Column(name = "is_tuesday_working")
  private Boolean isTuesdayWorking;

  @Column(name = "is_wednesday_working")
  private Boolean isWednesdayWorking;

  @Column(name = "is_thursday_working")
  private Boolean isThursdayWorking;

  @Column(name = "is_friday_working")
  private Boolean isFridayWorking;

  @Column(name = "is_saturday_working")
  private Boolean isSaturdayWorking;

  @Column(name = "is_sunday_working")
  private Boolean isSundayWorking;

  @Type(JsonBinaryType.class)
  @Column(name = "exception_days", columnDefinition = "jsonb")
  private List<ExceptionDays> exceptionDays;
}
