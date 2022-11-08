package com.nextuple.item.domain.mapper;

import java.util.Objects;
import org.mapstruct.Mapper;

@Mapper
public interface CharSequenceMapper {
  default String map(CharSequence charSequence) {
    return Objects.isNull(charSequence) ? null : charSequence.toString();
  }
}
