package com.hbc.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotBlank;

import com.hbc.transit.domain.enums.TransitBufferReqJobRefEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferReqJobRefRequest implements Serializable {

    @Length(max = 50)
    private String extReferenceId;

    private Long transitBufferReqId;

    private TransitBufferReqJobRefEnum action;
}