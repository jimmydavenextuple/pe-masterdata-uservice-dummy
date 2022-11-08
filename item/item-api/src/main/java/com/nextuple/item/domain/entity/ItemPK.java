package com.nextuple.item.domain.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemPK implements Serializable {

  private String itemId;
  private String orgId;
  private String uom;
}
