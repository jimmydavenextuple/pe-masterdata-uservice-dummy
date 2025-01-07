package com.nextuple.csvdownload.util;

import static com.nextuple.csvdownload.util.NodeCalendarUtil.getActiveCalendarForNodeIdAndCarrier;

import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

@ExtendWith(MockitoExtension.class)
class NodeCalendarUtilTest {
  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @Description("Test get Active Calendar Response - Happy Path")
  void getActiveCalendarResponseTest() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 1);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdf.format(cal.getTime());
    List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarResponses =
        List.of(
            NodeCarrierServiceCalendarResponse.builder()
                .nodeId("Node 1")
                .carrierServiceId("ALL_SDND")
                .calendarId("calendarID")
                .orgId("BAY")
                .effectiveDate(formattedDate)
                .build());
    Optional<NodeCarrierServiceCalendarResponse> noeCarrierServiceCalendarResponse =
        getActiveCalendarForNodeIdAndCarrier(nodeCarrierServiceCalendarResponses);
    Assertions.assertTrue(noeCarrierServiceCalendarResponse.isPresent());
  }

  @Test
  @Description("Test get Active Calendar Response - Empty Scenario")
  void getActiveCalendarResponseTestEmptyScenario() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, -1);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdf.format(cal.getTime());
    List<NodeCarrierServiceCalendarResponse> nodeCarrierServiceCalendarResponses =
        List.of(
            NodeCarrierServiceCalendarResponse.builder()
                .nodeId("Node 1")
                .carrierServiceId("ALL_SDND")
                .calendarId("calendarID")
                .orgId("BAY")
                .effectiveDate(formattedDate)
                .build());
    Optional<NodeCarrierServiceCalendarResponse> noeCarrierServiceCalendarResponse =
        getActiveCalendarForNodeIdAndCarrier(nodeCarrierServiceCalendarResponses);
    Assertions.assertTrue(noeCarrierServiceCalendarResponse.isPresent());
  }
}
