package com.nextuple.pe.webhook.service;

import com.nextuple.common.exception.CommonServiceException;
import java.util.List;

public interface ClientWebhookService {
  void publishToClientWebhook(String tenantId, String eventName, List<String> responseMessage)
      throws CommonServiceException;
}
