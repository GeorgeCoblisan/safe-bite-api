package com.safe.springboot.api.safe_bite.dto;

import com.safe.springboot.api.safe_bite.enums.RiskLevel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteWithProductPropertiesDto {
    private String customName;

    private List<RiskLevel> riskLevels;
}
