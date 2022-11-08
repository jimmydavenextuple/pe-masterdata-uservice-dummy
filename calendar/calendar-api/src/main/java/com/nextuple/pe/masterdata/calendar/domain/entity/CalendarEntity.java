package com.nextuple.pe.masterdata.calendar.domain.entity;

import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.pe.masterdata.calendar.domain.entity.primarykey.CalendarPK;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "calendars")
@IdClass(CalendarPK.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CalendarEntity {

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

  @Type(type = "jsonb")
  @Column(name = "exception_days", columnDefinition = "jsonb")
  private List<ExceptionDays> exceptionDays;
}
