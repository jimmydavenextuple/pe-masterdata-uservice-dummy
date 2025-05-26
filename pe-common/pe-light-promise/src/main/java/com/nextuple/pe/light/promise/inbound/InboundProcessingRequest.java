package com.nextuple.pe.light.promise.inbound;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InboundProcessingRequest {


    @NotBlank(message = "nodeId can't be blank")
    @Length(max = 50)
    private String nodeId;

    @NotBlank(message = "orgId can't be blank")
    @Length(max = 50)
    private String orgId;

    @NotBlank(message = "ruleGroup can't be blank")
    private String ruleGroup;

    private String ruleFilterStrategy = "inbound-processing-time-filter";
    @NotNull(message = "ruleEvaluationRequest can't be null")
    private Object ruleEvaluationRequest;


}
