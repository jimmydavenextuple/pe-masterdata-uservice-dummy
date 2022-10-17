package com.hbc.transit.domain.outbound;
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
public class TransitBufferReqJobRefResponse implements Serializable {

    private Long id;

    private String extReferenceId;

    private Long transitBufferReqId;

    private TransitBufferReqJobRefEnum action;
}