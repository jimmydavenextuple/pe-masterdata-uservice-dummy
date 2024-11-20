/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingRequestForFormulaValidation;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingSolutionForFormulaValidation;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
@Data
public class PostConstructValidationBean {

  public static final String SAMPLE_REQUEST_JSON =
      "classpath:SampleRequestForFormulaValidation.json";
  public static final String SAMPLE_SOLUTION_JSON =
      "classpath:SampleSolutionForFormulaValidation.json";
  private SampleSourcingRequestForFormulaValidation sampleSourcingRequestForFormulaValidation;

  private SampleSourcingSolutionForFormulaValidation sampleSourcingSolutionForFormulaValidation;

  private StandardEvaluationContext spelStandardEvaluationContext;

  private final ResourceLoader resourceLoader;

  @PostConstruct
  public void PostConstructValidationBean() throws IOException {

    Resource requestResource = resourceLoader.getResource(SAMPLE_REQUEST_JSON);
    Resource solutionResource = resourceLoader.getResource(SAMPLE_SOLUTION_JSON);

    ObjectMapper objectMapper = new ObjectMapper();
    this.sampleSourcingRequestForFormulaValidation =
        objectMapper.readValue(
            requestResource.getInputStream(), SampleSourcingRequestForFormulaValidation.class);

    this.sampleSourcingSolutionForFormulaValidation =
        objectMapper.readValue(
            solutionResource.getInputStream(), SampleSourcingSolutionForFormulaValidation.class);
  }

  @PostConstruct
  public void SpelContext() throws NoSuchMethodException {
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.registerFunction("abs", Math.class.getDeclaredMethod("abs", int.class));
    context.registerFunction("abs", Math.class.getDeclaredMethod("abs", long.class));
    context.registerFunction("abs", Math.class.getDeclaredMethod("abs", float.class));
    context.registerFunction("abs", Math.class.getDeclaredMethod("abs", double.class));
    context.registerFunction("sin", Math.class.getDeclaredMethod("sin", double.class));
    context.registerFunction("cos", Math.class.getDeclaredMethod("cos", double.class));
    context.registerFunction("tan", Math.class.getDeclaredMethod("tan", double.class));
    context.registerFunction(
        "atan2", Math.class.getDeclaredMethod("atan2", new Class[] {double.class, double.class}));
    context.registerFunction("sqrt", Math.class.getDeclaredMethod("sqrt", double.class));
    context.registerFunction("log", Math.class.getDeclaredMethod("log", double.class));
    context.registerFunction("log10", Math.class.getDeclaredMethod("log10", double.class));
    context.registerFunction(
        "pow", Math.class.getDeclaredMethod("pow", new Class[] {double.class, double.class}));
    context.registerFunction("exp", Math.class.getDeclaredMethod("exp", double.class));
    context.registerFunction(
        "min", Math.class.getDeclaredMethod("min", new Class[] {int.class, int.class}));
    context.registerFunction(
        "min", Math.class.getDeclaredMethod("min", new Class[] {float.class, float.class}));
    context.registerFunction(
        "min", Math.class.getDeclaredMethod("min", new Class[] {long.class, long.class}));
    context.registerFunction(
        "min", Math.class.getDeclaredMethod("min", new Class[] {double.class, double.class}));
    context.registerFunction(
        "max", Math.class.getDeclaredMethod("max", new Class[] {int.class, int.class}));
    context.registerFunction(
        "max", Math.class.getDeclaredMethod("max", new Class[] {float.class, float.class}));
    context.registerFunction(
        "max", Math.class.getDeclaredMethod("max", new Class[] {long.class, long.class}));
    context.registerFunction(
        "max", Math.class.getDeclaredMethod("max", new Class[] {double.class, double.class}));
    context.registerFunction("floor", Math.class.getDeclaredMethod("floor", double.class));
    context.registerFunction("ceil", Math.class.getDeclaredMethod("ceil", double.class));
    context.registerFunction("rint", Math.class.getDeclaredMethod("rint", double.class));
    context.registerFunction(
        "addExact", Math.class.getDeclaredMethod("addExact", new Class[] {int.class, int.class}));
    context.registerFunction(
        "addExact", Math.class.getDeclaredMethod("addExact", new Class[] {long.class, long.class}));
    context.registerFunction(
        "decrementExact", Math.class.getDeclaredMethod("decrementExact", long.class));
    context.registerFunction(
        "decrementExact", Math.class.getDeclaredMethod("decrementExact", int.class));
    context.registerFunction(
        "incrementExact", Math.class.getDeclaredMethod("incrementExact", int.class));
    context.registerFunction(
        "incrementExact", Math.class.getDeclaredMethod("incrementExact", long.class));
    context.registerFunction(
        "multiplyExact",
        Math.class.getDeclaredMethod("multiplyExact", new Class[] {int.class, int.class}));
    context.registerFunction(
        "multiplyExact",
        Math.class.getDeclaredMethod("multiplyExact", new Class[] {long.class, long.class}));
    context.registerFunction(
        "multiplyExact",
        Math.class.getDeclaredMethod("multiplyExact", new Class[] {long.class, int.class}));
    context.registerFunction(
        "multiplyHigh",
        Math.class.getDeclaredMethod("multiplyHigh", new Class[] {long.class, long.class}));
    context.registerFunction(
        "negateExact", Math.class.getDeclaredMethod("negateExact", long.class));
    context.registerFunction("negateExact", Math.class.getDeclaredMethod("negateExact", int.class));
    context.registerFunction(
        "subtractExact",
        Math.class.getDeclaredMethod("subtractExact", new Class[] {int.class, int.class}));
    context.registerFunction(
        "subtractExact",
        Math.class.getDeclaredMethod("subtractExact", new Class[] {long.class, long.class}));
    context.registerFunction(
        "fma",
        Math.class.getDeclaredMethod(
            "fma", new Class[] {double.class, double.class, double.class}));
    context.registerFunction(
        "fma",
        Math.class.getDeclaredMethod("fma", new Class[] {float.class, float.class, float.class}));
    context.registerFunction(
        "copySign",
        Math.class.getDeclaredMethod("copySign", new Class[] {float.class, float.class}));
    context.registerFunction(
        "copySign",
        Math.class.getDeclaredMethod("copySign", new Class[] {double.class, double.class}));
    context.registerFunction("signum", Math.class.getDeclaredMethod("signum", float.class));
    context.registerFunction("signum", Math.class.getDeclaredMethod("signum", double.class));
    context.registerFunction(
        "scalb", Math.class.getDeclaredMethod("scalb", new Class[] {double.class, int.class}));
    context.registerFunction(
        "scalb", Math.class.getDeclaredMethod("scalb", new Class[] {float.class, int.class}));
    context.registerFunction(
        "getExponent", Math.class.getDeclaredMethod("getExponent", float.class));
    context.registerFunction(
        "getExponent", Math.class.getDeclaredMethod("getExponent", double.class));
    context.registerFunction(
        "floorMod", Math.class.getDeclaredMethod("floorMod", new Class[] {long.class, int.class}));
    context.registerFunction(
        "floorMod", Math.class.getDeclaredMethod("floorMod", new Class[] {int.class, int.class}));
    context.registerFunction(
        "floorMod", Math.class.getDeclaredMethod("floorMod", new Class[] {long.class, long.class}));
    context.registerFunction("asin", Math.class.getDeclaredMethod("asin", double.class));
    context.registerFunction("acos", Math.class.getDeclaredMethod("acos", double.class));
    context.registerFunction("atan", Math.class.getDeclaredMethod("atan", double.class));
    context.registerFunction("cbrt", Math.class.getDeclaredMethod("cbrt", double.class));
    context.registerFunction(
        "IEEEremainder",
        Math.class.getDeclaredMethod("IEEEremainder", new Class[] {double.class, double.class}));
    context.registerFunction(
        "floorDiv", Math.class.getDeclaredMethod("floorDiv", new Class[] {long.class, long.class}));
    context.registerFunction(
        "floorDiv", Math.class.getDeclaredMethod("floorDiv", new Class[] {int.class, int.class}));
    context.registerFunction(
        "floorDiv", Math.class.getDeclaredMethod("floorDiv", new Class[] {long.class, int.class}));
    context.registerFunction("powerOfTwoD", Math.class.getDeclaredMethod("powerOfTwoD", int.class));
    context.registerFunction("powerOfTwoF", Math.class.getDeclaredMethod("powerOfTwoF", int.class));
    context.registerFunction("sinh", Math.class.getDeclaredMethod("sinh", double.class));
    context.registerFunction("cosh", Math.class.getDeclaredMethod("cosh", double.class));
    context.registerFunction("tanh", Math.class.getDeclaredMethod("tanh", double.class));
    context.registerFunction(
        "hypot", Math.class.getDeclaredMethod("hypot", new Class[] {double.class, double.class}));
    context.registerFunction("expm1", Math.class.getDeclaredMethod("expm1", double.class));
    context.registerFunction("log1p", Math.class.getDeclaredMethod("log1p", double.class));
    context.registerFunction("toRadians", Math.class.getDeclaredMethod("toRadians", double.class));
    context.registerFunction("toDegrees", Math.class.getDeclaredMethod("toDegrees", double.class));
    context.registerFunction("round", Math.class.getDeclaredMethod("round", double.class));
    context.registerFunction("round", Math.class.getDeclaredMethod("round", float.class));
    context.registerFunction("random", Math.class.getDeclaredMethod("random", new Class[] {}));
    context.registerFunction("toIntExact", Math.class.getDeclaredMethod("toIntExact", long.class));
    context.registerFunction(
        "multiplyFull",
        Math.class.getDeclaredMethod("multiplyFull", new Class[] {int.class, int.class}));
    context.registerFunction("absExact", Math.class.getDeclaredMethod("absExact", int.class));
    context.registerFunction("absExact", Math.class.getDeclaredMethod("absExact", long.class));
    context.registerFunction("ulp", Math.class.getDeclaredMethod("ulp", double.class));
    context.registerFunction("ulp", Math.class.getDeclaredMethod("ulp", float.class));
    context.registerFunction(
        "nextAfter",
        Math.class.getDeclaredMethod("nextAfter", new Class[] {double.class, double.class}));
    context.registerFunction(
        "nextAfter",
        Math.class.getDeclaredMethod("nextAfter", new Class[] {float.class, double.class}));
    context.registerFunction("nextUp", Math.class.getDeclaredMethod("nextUp", double.class));
    context.registerFunction("nextUp", Math.class.getDeclaredMethod("nextUp", float.class));
    context.registerFunction("nextDown", Math.class.getDeclaredMethod("nextDown", double.class));
    context.registerFunction("nextDown", Math.class.getDeclaredMethod("nextDown", float.class));
    this.spelStandardEvaluationContext = context;
  }
}
