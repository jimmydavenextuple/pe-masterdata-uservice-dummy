package com.nextuple.item.substitution.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSubstitutionBatchServiceImpl extends BatchService<ItemSubstitutionFeedDto> {
    @Override
    public TaskInformation getTaskInformation() {
        return null;
    }

    @Override
    public TypeReference<BatchRequest<ItemSubstitutionFeedDto>> getTypeReference() {
        return null;
    }

    @Override
    public String createRecordImpl(ItemSubstitutionFeedDto payload) throws CommonServiceException {
        return "";
    }

    @Override
    public String updateRecordImpl(ItemSubstitutionFeedDto payload) throws CommonServiceException {
        return "";
    }

    @Override
    public String deleteRecordImpl(ItemSubstitutionFeedDto payload) throws CommonServiceException {
        return "";
    }

    @Override
    public void checkForOutdatedRecord(BatchRequest<ItemSubstitutionFeedDto> batchRequest) throws CommonServiceException {

    }
}
