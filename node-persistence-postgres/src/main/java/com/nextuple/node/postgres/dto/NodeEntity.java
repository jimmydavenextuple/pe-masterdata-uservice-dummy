package com.nextuple.node.postgres.dto;

import com.nextuple.common.persistence.postgres.dto.PostgresEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.time.LocalDateTime;

@javax.persistence.Entity
@IdClass(NodeKey.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "mydb")
public class NodeEntity implements PostgresEntity {

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
