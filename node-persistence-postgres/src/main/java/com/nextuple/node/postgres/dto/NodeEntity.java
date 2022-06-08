package com.nextuple.node.postgres.dto;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@javax.persistence.Entity
@IdClass(NodeKey.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "mydb")
public class NodeEntity implements com.nextuple.common.dto.Entity {

    @Column
    @Id
    private String nodeId;
    @Id
    private String orgId;
    private String description;
    private String lat;
    private String timezone;
    private LocalDateTime updateTime;
    private String updateUser;

    @Override
    public LocalDateTime getUpdateTime() {
        return null;
    }

    @Override
    public String getUpdateUser() {
        return null;
    }

    @Override
    public void setUpdateTime(LocalDateTime updateTime) {

    }

    @Override
    public void setUpdateUser(String updateUser) {

    }
}
