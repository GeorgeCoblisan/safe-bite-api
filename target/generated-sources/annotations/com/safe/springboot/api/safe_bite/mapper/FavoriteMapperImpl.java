package com.safe.springboot.api.safe_bite.mapper;

import com.safe.springboot.api.safe_bite.dto.FavoriteResponseDTO;
import com.safe.springboot.api.safe_bite.dto.FavoriteWithProductPropertiesDto;
import com.safe.springboot.api.safe_bite.model.Favorite;
import com.safe.springboot.api.safe_bite.model.Product;
import com.safe.springboot.api.safe_bite.model.ProductIngredient;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-22T19:55:30+0300",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class FavoriteMapperImpl implements FavoriteMapper {

    @Override
    public FavoriteResponseDTO toDto(Favorite favorite) {
        if ( favorite == null ) {
            return null;
        }

        FavoriteResponseDTO.FavoriteResponseDTOBuilder favoriteResponseDTO = FavoriteResponseDTO.builder();

        favoriteResponseDTO.productName( favorite.getProductName() );

        return favoriteResponseDTO.build();
    }

    @Override
    public FavoriteResponseDTO toDtoWithProduct(Favorite favorite) {
        if ( favorite == null ) {
            return null;
        }

        FavoriteResponseDTO.FavoriteResponseDTOBuilder favoriteResponseDTO = FavoriteResponseDTO.builder();

        favoriteResponseDTO.productName( favoriteProductName( favorite ) );
        favoriteResponseDTO.barcode( favoriteProductBarcode( favorite ) );
        favoriteResponseDTO.customName( favorite.getProductName() );

        return favoriteResponseDTO.build();
    }

    @Override
    public FavoriteWithProductPropertiesDto toFavoriteWithProduct(Favorite favorite) {
        if ( favorite == null ) {
            return null;
        }

        FavoriteWithProductPropertiesDto.FavoriteWithProductPropertiesDtoBuilder favoriteWithProductPropertiesDto = FavoriteWithProductPropertiesDto.builder();

        favoriteWithProductPropertiesDto.customName( favorite.getProductName() );
        List<ProductIngredient> productIngredients = favoriteProductProductIngredients( favorite );
        favoriteWithProductPropertiesDto.riskLevels( mapRiskLevels( productIngredients ) );

        return favoriteWithProductPropertiesDto.build();
    }

    private String favoriteProductName(Favorite favorite) {
        Product product = favorite.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getName();
    }

    private String favoriteProductBarcode(Favorite favorite) {
        Product product = favorite.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getBarcode();
    }

    private List<ProductIngredient> favoriteProductProductIngredients(Favorite favorite) {
        Product product = favorite.getProduct();
        if ( product == null ) {
            return null;
        }
        return product.getProductIngredients();
    }
}
