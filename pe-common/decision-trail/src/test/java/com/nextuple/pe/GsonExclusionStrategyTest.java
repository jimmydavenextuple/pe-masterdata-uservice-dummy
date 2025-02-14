package com.nextuple.pe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class GsonExclusionStrategyTest {
  @InjectMocks GsonExclusionStrategy strategy;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should exclude fields with exclude annotation")
  void shouldSkipFieldPositiveTest() {
    FieldAttributes fieldAttributes = Mockito.mock(FieldAttributes.class);
    GsonExclude mockAnnotation = mock(GsonExclude.class);
    when(fieldAttributes.getAnnotation(any())).thenReturn(mockAnnotation);
    Assertions.assertTrue(strategy.shouldSkipField(fieldAttributes));
  }

  @Test
  @DisplayName("Should not exclude fields without exclude annotation")
  void shouldSkipFieldNegativeTest() {
    FieldAttributes fieldAttributes = Mockito.mock(FieldAttributes.class);
    when(fieldAttributes.getAnnotation(any())).thenReturn(null);
    Assertions.assertFalse(strategy.shouldSkipField(fieldAttributes));
  }

  @Test
  @DisplayName("Serialized string should not contain excluded fields")
  void shouldSkipFieldWithExculdeAnnotationTest() throws NoSuchFieldException {
    GsonTestExampleClass class1 = new GsonTestExampleClass();
    class1.setField1("abc");
    class1.setField2("xyz");
    class1.setField3("mno");
    var eventString =
        new GsonBuilder()
            .serializeNulls()
            .addSerializationExclusionStrategy(strategy)
            .create()
            .toJson(class1);

    Assertions.assertFalse(eventString.contains("field1"));
    Assertions.assertTrue(eventString.contains("field2"));
  }
}
