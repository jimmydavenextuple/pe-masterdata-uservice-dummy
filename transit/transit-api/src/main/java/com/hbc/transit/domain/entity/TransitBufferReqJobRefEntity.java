package com.hbc.transit.domain.entity;

import com.hbc.transit.domain.enums.TransitBufferReqJobRefEnum;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(
    name = "transit_buffer_req_jobs_reference",
    indexes = @Index(name = "extReferenceId_key", columnList = "ext_reference_id"))
@Builder
public class TransitBufferReqJobRefEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "ext_reference_id")
  private String extReferenceId;

  @Column(name = "transit_buffer_req_id")
  private Long transitBufferReqId;

  @Column(name = "action")
  private TransitBufferReqJobRefEnum action;
}
