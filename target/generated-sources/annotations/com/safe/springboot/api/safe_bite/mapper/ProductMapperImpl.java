package com.safe.springboot.api.safe_bite.mapper;

import com.safe.springboot.api.safe_bite.dto.ProductDTO;
import com.safe.springboot.api.safe_bite.model.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-16T13:14:34+0200",
    comments = "version: 1.6.0.Beta1, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO.ProductDTOBuilder productDTO = ProductDTO.builder();

        productDTO.barcode( product.getBarcode() );
        productDTO.name( product.getName() );
        productDTO.sourceProduct( product.getSourceProduct() );
        productDTO.lastUpdated( product.getLastUpdated() );

        productDTO.ingredients( mapIngredients(product) );

        return productDTO.build();
    }

    @Override
    public Product toProduct(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.barcode( productDTO.getBarcode() );
        product.name( productDTO.getName() );
        product.sourceProduct( productDTO.getSourceProduct() );
        product.lastUpdated( productDTO.getLastUpdated() );

        return product.build();
    }
}
