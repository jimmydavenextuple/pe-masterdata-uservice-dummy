package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.NodeRequestMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeMapper implements FeignClientMapper {

  private final NodeFeign nodeFeign;

  private final Logger logger = LoggerFactory.getLogger(NodeMapper.class);

  @Setter private JobTypeEnum jobType;

  public static final NodeRequestMapper INSTANCE = Mappers.getMapper(NodeRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.NODES;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return null;
  }

  @Override
  public Class mapTODto() {
    return NodeDataUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var nodeDataUpload = (NodeDataUpload) request;
    String action = nodeDataUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            nodeFeign.createNode(INSTANCE.convertToNodeRequest(nodeDataUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            nodeFeign.updateNodeDetails(
                nodeDataUpload.getNodeId(),
                nodeDataUpload.getOrgId(),
                INSTANCE.convertToNodeUpdationRequest(nodeDataUpload)));
      case DELETE:
        return ResponseEntity.ok(
            nodeFeign.deleteNode(nodeDataUpload.getNodeId(), nodeDataUpload.getOrgId()));
      default:
        {
          logger.error("Invalid action type: {}", nodeDataUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + nodeDataUpload.getAction());
        }
    }
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobType = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }
}
