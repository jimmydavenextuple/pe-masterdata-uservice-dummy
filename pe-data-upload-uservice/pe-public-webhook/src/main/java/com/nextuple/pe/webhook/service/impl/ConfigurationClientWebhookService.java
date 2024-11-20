package com.nextuple.pe.webhook.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import com.nextuple.pe.webhook.domain.dtos.WebhookDetail;
import com.nextuple.pe.webhook.service.ClientWebhookService;
import com.nextuple.pe.webhook.service.WebhookDetailsService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ConfigurationClientWebhookService implements ClientWebhookService {
  @Autowired private WebhookDetailsService webhookDetailsService;

  @Override
  public void publishToClientWebhook(
      String tenantId, String eventName, List<String> responseMessage)
      throws CommonServiceException {
    HttpStatus webhookStatus = invokeWebhook(tenantId, eventName, responseMessage);
    if (webhookStatus != null && webhookStatus.isError()) {
      log.error("Received error response status {} while sending to client", webhookStatus);
      throw new CommonServiceException(
          "Received error response  while sending to client", webhookStatus, 0x1771, null);
    }
  }

  private HttpStatus invokeWebhook(
      String tenantId, String eventName, List<String> responseMessage) {
    try {
      Optional<WebhookConfigDetails> optionalConfigDetails =
          webhookDetailsService.getWebhookConfigDetails(tenantId, eventName);
      WebhookConfigDetails webhookConfigDetails = optionalConfigDetails.orElse(null);

      if (Objects.nonNull(webhookConfigDetails)
          && !ObjectUtils.isEmpty(webhookConfigDetails.getWebhookDetail().getEndpoint())) {
        WebhookDetail webhookDetail = webhookConfigDetails.getWebhookDetail();
        WebClient webClient = WebClient.create();
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(webhookDetail.getHeaders())) {
          webhookDetail.getHeaders().entrySet().stream()
              .forEach(x -> headers.add(x.getKey(), x.getValue() + ""));
        }
        webClient
            .method(HttpMethod.valueOf(webhookDetail.getMethod()))
            .uri(webhookDetail.getEndpoint())
            .headers(httpHeaders -> httpHeaders.addAll(headers))
            .bodyValue(responseMessage)
            .exchangeToMono(
                response -> {
                  HttpStatus status = (HttpStatus) response.statusCode();
                  if (status.equals(HttpStatus.OK)) return response.bodyToMono(String.class);
                  else if (status.is4xxClientError())
                    return Mono.error(new HttpClientErrorException(status));
                  else return Mono.error(new HttpServerErrorException(status));
                })
            .subscribe(
                response -> log.debug("PE Response published to client webhook {}", response),
                error -> log.debug("Error sending the PE Response to client {}", error));
        return HttpStatus.OK;
      } else {
        log.error("Webhook details not found for {} and {}", tenantId, eventName);
        return HttpStatus.BAD_REQUEST;
      }
    } catch (HttpClientErrorException e) {
      log.error("Client Error from upstream while pushing delivery webhook message: {}", e);
      return (HttpStatus) e.getStatusCode();
    } catch (HttpServerErrorException e) {
      log.error(
          "Server Error from upstream while pushing delivery webhook message: {} , body = {}",
          e,
          e.getResponseBodyAsString());
      return (HttpStatus) e.getStatusCode();
    } catch (Exception e) {
      log.error("Error in publishing the delivery update to client {}", e);
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}
