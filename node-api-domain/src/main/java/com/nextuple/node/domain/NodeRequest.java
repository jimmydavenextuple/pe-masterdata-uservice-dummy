package com.nextuple.node.domain;

import com.nextuple.common.domain.GenericNodeRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder(toBuilder = true)
public class NodeRequest implements GenericNodeRequest {

    private static final long serialVersionUID = 1651518097264274804L;

    @NotNull(message = "{orgId.notnull.msg}")
    private String nodeId;

    @NotNull(message = "{orgId.notnull.msg}")
    private String orgId;
}
