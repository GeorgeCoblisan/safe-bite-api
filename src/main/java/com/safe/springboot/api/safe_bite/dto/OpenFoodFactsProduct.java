package com.safe.springboot.api.safe_bite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenFoodFactsProduct {
    @JsonProperty("additives_original_tags")
    private List<String> additives_original_tags;

    @JsonProperty("additives_tags")
    private List<String> additives_tags;

    @JsonProperty("product_name_ro")
    private String productNameRo;
}
