package com.nextuple.pe.webhook.domain.dtos;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookDetail implements Serializable {

  private static final long serialVersionUID = -2998873805153081369L;
  private String method;
  private String endpoint;
  private ClientCert clientCert;
  private Map<String, String> headers;

  @Data
  public static class ClientCert implements Serializable {

    private static final long serialVersionUID = -8781586665544018464L;
    private String keyType;
    private String keyProvider;
    private String base64EncodedKey;
    private String password;
  }
}
