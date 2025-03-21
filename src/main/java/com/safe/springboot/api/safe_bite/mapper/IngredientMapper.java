package com.safe.springboot.api.safe_bite.mapper;

import com.safe.springboot.api.safe_bite.dto.IngredientDTO;
import com.safe.springboot.api.safe_bite.model.ProductIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    @Named("mapIngredients")
    static List<IngredientDTO> mapIngredients(List<ProductIngredient> productIngredients) {
        if (productIngredients == null) {
            return List.of();
        }
        return productIngredients.stream()
                .map(pi -> IngredientDTO.builder()
                        .name(pi.getIngredient().getName())
                        .code(pi.getIngredient().getCode())
                        .type(pi.getIngredient().getType())
                        .riskLevel(pi.getIngredient().getRiskLevel())
                        .effects(pi.getIngredient().getEffects())
                        .build())
                .collect(Collectors.toList());
    }
}
