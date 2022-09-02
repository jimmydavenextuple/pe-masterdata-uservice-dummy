package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityServiceTest {

  @Mock private PostalCodeTimeZoneService postalCodeTimeZoneService;
  @Mock private TransitService transitService;
  @InjectMocks private CsvDownloadUtilityService csvDownloadUtilityService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void downloadTransitTimesForSourceAndDestinationRegion()
      throws PostalCodeTimezoneServiceException, TransitServiceException,
          CsvDownloadUtilityServiceException {
    when(postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA));

    when(transitService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    String csvContents =
        csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_REGION,
            TestUtil.DESTINATION_REGION);
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneService, times(2)).getFsaList(anyString(), anyString());
    verify(transitService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }

  @Test
  void downloadTransitTimesForSourceAndDestinationRegionException()
      throws PostalCodeTimezoneServiceException, TransitServiceException {
    when(postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA + "1"));

    when(transitService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    Exception exception =
        Assertions.assertThrows(
            CsvDownloadUtilityServiceException.class,
            () ->
                csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_REGION,
                    TestUtil.DESTINATION_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimeZoneService, times(2)).getFsaList(anyString(), anyString());
    verify(transitService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }
}
