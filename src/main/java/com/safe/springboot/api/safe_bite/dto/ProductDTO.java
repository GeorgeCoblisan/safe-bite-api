package com.safe.springboot.api.safe_bite.dto;

import lombok.*;

import javax.xml.transform.Source;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String barcode;

    private Source sourceProduct;

    private LocalDateTime lastUpdated;

    private List<IngredientDTO> ingredients;
}
