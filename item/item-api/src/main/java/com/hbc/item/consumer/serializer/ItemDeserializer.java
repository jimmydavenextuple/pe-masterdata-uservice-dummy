package com.hbc.item.consumer.serializer;

import com.hbc.streams.promising.messages.PromisingRecord;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class ItemDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

  @Override
  public void configure(Map configs, boolean isKey) {
    // do nothing
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(String topic, byte[] bytes) {
    T returnObject = null;

    try {
      log.debug("binary message='{}'", DatatypeConverter.printHexBinary(bytes));
      if (bytes != null) {
        log.debug("data='{}'", DatatypeConverter.printHexBinary(bytes));

        DatumReader<GenericRecord> datumReader =
            new SpecificDatumReader<>(PromisingRecord.getClassSchema());
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);

        returnObject = (T) datumReader.read(null, decoder);
        log.debug("deserialized data='{}'", returnObject);
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
