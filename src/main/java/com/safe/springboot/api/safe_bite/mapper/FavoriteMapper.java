package com.safe.springboot.api.safe_bite.mapper;


import com.safe.springboot.api.safe_bite.dto.FavoriteResponseDTO;
import com.safe.springboot.api.safe_bite.dto.FavoriteWithProductPropertiesDto;
import com.safe.springboot.api.safe_bite.enums.RiskLevel;
import com.safe.springboot.api.safe_bite.model.Favorite;
import com.safe.springboot.api.safe_bite.model.ProductIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {
    FavoriteMapper INSTANCE = Mappers.getMapper(FavoriteMapper.class);

    FavoriteResponseDTO toDto(Favorite favorite);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "barcode", source = "product.barcode")
    @Mapping(target = "customName", source = "productName")
    FavoriteResponseDTO toDtoWithProduct(Favorite favorite);

    @Mapping(target = "customName", source = "productName")
    @Mapping(target = "riskLevels", source = "product.productIngredients")
    FavoriteWithProductPropertiesDto toFavoriteWithProduct(Favorite favorite);

    default List<RiskLevel> mapRiskLevels(List<ProductIngredient> ingredients) {
        if (ingredients == null) return List.of();
        return ingredients.stream()
                .map(pi -> pi.getIngredient().getRiskLevel())
                .toList();
    }

}
