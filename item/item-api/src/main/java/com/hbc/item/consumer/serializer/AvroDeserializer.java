package com.hbc.item.consumer.serializer;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

  protected final Class<T> targetType;

  public AvroDeserializer(Class<T> targetType) {
    this.targetType = targetType;
  }

  @Override
  public void configure(Map configs, boolean isKey) {
    // do nothing
  }

  @Override
  public T deserialize(String topic, byte[] bytes) {
    T returnObject = null;

    try {

      if (bytes != null) {
        DatumReader<SpecificRecordBase> datumReader =
            new SpecificDatumReader<>(targetType.newInstance().getSchema());
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        returnObject = (T) datumReader.read(null, decoder);
        log.info("deserialized data='{}'", returnObject.toString());
      }
    } catch (Exception e) {
      log.error("Unable to Deserialize bytes[] ", e);
    }

    return returnObject;
  }

  @Override
  public void close() {
    // do nothing
  }
}
