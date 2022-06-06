package com.nextuple.node.dto.key;

import com.nextuple.common.dto.key.EntityKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeEntityKey implements EntityKey {

    private String orgId;
    private String nodeId;
}
