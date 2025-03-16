package com.safe.springboot.api.safe_bite.dto;

import com.safe.springboot.api.safe_bite.enums.SourceProduct;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String barcode;

    private String name;

    private SourceProduct sourceProduct;

    private LocalDateTime lastUpdated;

    private List<IngredientDTO> ingredients;
}
