package com.hbc.fsa.node.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
  private String street;
  private String city;
  private String province;
  private String postalCode;
  private String country;
  private String latitude;
  private String longitude;
}
