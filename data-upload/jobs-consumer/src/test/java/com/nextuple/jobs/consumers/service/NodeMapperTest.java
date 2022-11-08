package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NodeMapperTest {

  @Mock private NodeFeign nodeFeign;
  @InjectMocks private NodeMapper nodeMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() {
    Class nodeDataUpload = nodeMapper.mapTODto();
    Assertions.assertEquals(NodeDataUpload.class, nodeDataUpload);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = nodeMapper.getModule();
    Assertions.assertEquals(ModuleEnum.NODES, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(() -> nodeMapper.setJobType(JobTypeEnum.UPLOAD_NODES));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(nodeMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNull(nodeMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateNode() {
    Object object = testUtil.getNodeDataUpload("CREATE");
    when(nodeFeign.createNode(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdateNodeDetails() {
    Object object = testUtil.getNodeDataUpload("UPDATE");
    when(nodeFeign.updateNodeDetails(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiDeleteNode() {
    Object object = testUtil.getNodeDataUpload("DELETE");
    when(nodeFeign.deleteNode(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getNodeDataUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
