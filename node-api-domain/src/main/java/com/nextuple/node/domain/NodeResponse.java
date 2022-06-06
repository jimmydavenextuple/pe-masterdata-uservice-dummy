package com.nextuple.node.domain;

import com.nextuple.common.domain.GenericNodeResponse;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NodeResponse implements GenericNodeResponse {

    private static final long serialVersionUID = 1651518097264274804L;

    @NotNull(message = "{orgId.notnull.msg}")
    private String nodeId;

    @NotNull(message = "{orgId.notnull.msg}")
    private String orgId;
}
