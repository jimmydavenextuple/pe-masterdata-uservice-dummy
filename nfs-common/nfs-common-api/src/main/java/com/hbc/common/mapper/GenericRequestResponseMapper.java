package com.hbc.common.mapper;

public interface GenericRequestResponseMapper<I, O, EK, EV> {

  EK requestToEntityKey(I request);

  EV requestToEntity(I request);

  O entityToResponse(EV entity);
}
