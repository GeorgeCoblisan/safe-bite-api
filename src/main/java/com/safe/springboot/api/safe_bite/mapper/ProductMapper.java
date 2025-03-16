package com.safe.springboot.api.safe_bite.mapper;

import com.safe.springboot.api.safe_bite.dto.IngredientDTO;
import com.safe.springboot.api.safe_bite.dto.ProductDTO;
import com.safe.springboot.api.safe_bite.model.Product;
import com.safe.springboot.api.safe_bite.model.ProductIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "ingredients", expression = "java(mapIngredients(product))")
    ProductDTO toDto(Product product);

    Product toProduct(ProductDTO productDTO);

    default List<IngredientDTO> mapIngredients(Product product) {
        if (product.getProductIngredients() == null) {
            return List.of();
        }

        return product.getProductIngredients().stream()
                .map(ProductIngredient::getIngredient)
                .map(ingredient -> IngredientDTO.builder()
                        .name(ingredient.getName())
                        .code(ingredient.getCode())
                        .type(ingredient.getType())
                        .riskLevel(ingredient.getRiskLevel())
                        .effects(ingredient.getEffects())
                        .build())
                .collect(Collectors.toList());
    }
}
