package com.safe.springboot.api.safe_bite.dto;

import com.safe.springboot.api.safe_bite.enums.RiskLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDTO {
    private String name;

    private String code;

    private RiskLevel riskLevel;

    private String effects;
}
