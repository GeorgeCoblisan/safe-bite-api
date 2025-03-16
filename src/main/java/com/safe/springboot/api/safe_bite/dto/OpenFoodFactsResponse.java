package com.safe.springboot.api.safe_bite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsResponse {
    private OpenFoodFactsProduct product;
}
