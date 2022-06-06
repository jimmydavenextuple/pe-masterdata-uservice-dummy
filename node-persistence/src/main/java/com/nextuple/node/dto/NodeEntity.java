package com.nextuple.node.dto;

import com.nextuple.common.dto.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NodeEntity implements Entity {

    private String orgId;
    private String nodeId;
    private String description;
    private String lat;
    private String timezone;
    private LocalDateTime updateTime;
    private String updateUser;
}
