package com.nextuple.node.cache.domain;

import com.nextuple.core.node.NodeValidationDetails;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeDetails implements Serializable {
  private static final long serialVersionUID = -9164965188615272223L;

  private String nodeName;

  private String fulfillmentType;

  private String nodeNo;

  private Date activeDate;

  private String marketName;

  private Date websiteActiveDate;

  //  private Address address;

  private List<Serializable> slotCollections;

  private String mapImage;

  private List<NodeValidationDetails> nodes;
}
