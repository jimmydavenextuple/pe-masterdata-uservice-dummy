package com.nextuple.calendar.consumer.constants;

public class CalendarConstants {
  public static final String CALENDAR_ID = "Unique identifier of the calendar.";
  public static final String CALENDAR_ID_EXAMPLE = "CALENDAR2023";
  public static final String ORG_ID = "Unique identifier of the organization.";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";
  public static final String DESCRIPTION = "Description of the calendar to be created.";
  public static final String DESCRIPTION_EXAMPLE = "Calendar for year 2023";
  public static final String IS_MONDAY_WORKING =
      "Flag indicating if the node or carrier is working on monday.";
  public static final String IS_TUESDAY_WORKING =
      "Flag indicating if the node or carrier is working on tuesday.";
  public static final String IS_WEDNESDAY_WORKING =
      "Flag indicating if the node or carrier is working on wednesday.";
  public static final String IS_THURSDAY_WORKING =
      "Flag indicating if the node or carrier is working on thursday.";
  public static final String IS_FRIDAY_WORKING =
      "Flag indicating if the node or carrier is working on friday.";
  public static final String IS_SATURDAY_WORKING =
      "Flag indicating if the node or carrier is working on saturday.";
  public static final String IS_SUNDAY_WORKING =
      "Flag indicating if the node or carrier is working on sunday.";
  public static final String EXCEPTION_DAYS = "List of the exception days or dates";
  public static final String CARRIER_SERVICE_ID = "Unique identifier of the carrier service.";
  public static final String CARRIER_SERVICE_ID_EXAMPLE = "ALL-SDND";
  public static final String EFFECTIVE_DATE = "Effective date of the calendar.";
  public static final String EFFECTIVE_DATE_EXAMPLE = "2023-01-01";
  public static final String NODE_CS_DESCRIPTION = "Description of node carrier service calendar.";
  public static final String NODE_CS_DESCRIPTION_EXAMPLE = "Calendar for NODE01/ALL-SDND";
  public static final String SHIPPING_STAGE =
      "Shipping stage of the carrier service. Shipping stage can be PICKUP, TRANSIT, DELIVERY, RECEIVING or ALL.";
  public static final String SHIPPING_STAGE_EXAMPLE = "PICKUP";
  public static final String NODE_ID = "Unique identifier of the node.";
  public static final String NODE_ID_EXAMPLE = "NODE01";

  private CalendarConstants() {}
}
