package com.nextuple.carrier;

import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.entity.CarrierServiceEntity;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.pojo.PageParams;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_ID = "carrier-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service-1";
  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String SORT_BY = "carrierId";
  public static final String SORT_ORDER_DESC = "desc";
  public static final String SORT_ORDER_ASC = "ASC";
  private static final String CARRIER_ID_2 = "carrier-2";

  public CarrierServiceRequest getCarrierServiceRequest() {
    return CarrierServiceRequest.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceUpdateRequest getCarrierServiceUpdateRequest() {
    return CarrierServiceUpdateRequest.builder()
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceUpdateResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceEntity getCarrierServiceEntity() {
    return CarrierServiceEntity.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceEntity getUpdatedCarrierServiceEntity() {
    return CarrierServiceEntity.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName("carrier-name-1")
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse2() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID_2)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public List<CarrierServiceResponse> getCarrierServiceResponseList() {
    return List.of(getCarrierServiceResponse(), getCarrierServiceResponse2());
  }

  public CarrierServiceEntity getCarrierServiceEntity2() {
    return CarrierServiceEntity.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID_2)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public List<CarrierServiceEntity> getCarrierServiceEntityList() {
    return List.of(getCarrierServiceEntity(), getCarrierServiceEntity2());
  }

  public Page<CarrierServiceResponse> createPageCarrierServiceResponse(
      int totalPage, List<CarrierServiceResponse> carrierServiceResponses, int totalElements) {
    return new Page<CarrierServiceResponse>() {
      @Override
      public int getTotalPages() {
        return totalPage;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CarrierServiceResponse, ? extends U> converter) {
        return null;
      }

      @Override
      public int getNumber() {
        return 0;
      }

      @Override
      public int getSize() {
        return 0;
      }

      @Override
      public int getNumberOfElements() {
        return 0;
      }

      @Override
      public List<CarrierServiceResponse> getContent() {
        return carrierServiceResponses;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return null;
      }

      @Override
      public boolean isFirst() {
        return false;
      }

      @Override
      public boolean isLast() {
        return false;
      }

      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public boolean hasPrevious() {
        return false;
      }

      @Override
      public Pageable nextPageable() {
        return null;
      }

      @Override
      public Pageable previousPageable() {
        return null;
      }

      @Override
      public Iterator<CarrierServiceResponse> iterator() {
        return null;
      }
    };
  }

  public PageParams getPageParams(
      Optional<Integer> pageNo,
      Optional<Integer> pageSize,
      Optional<String> sortBy,
      Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setPageSize(pageSize);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public List<CarrierCacheKeyDto> getCarrierCacheKeyDtoList() {
    CarrierCacheKeyDto carrierCacheKeyDto1 =
        CarrierCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierId(CARRIER_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .build();

    CarrierCacheKeyDto carrierCacheKeyDto2 =
        CarrierCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierId(CARRIER_ID_2)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .build();

    return List.of(carrierCacheKeyDto1, carrierCacheKeyDto2);
  }
}
