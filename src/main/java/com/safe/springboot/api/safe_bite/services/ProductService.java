package com.safe.springboot.api.safe_bite.services;

import com.safe.springboot.api.safe_bite.dto.OpenFoodFactsProduct;
import com.safe.springboot.api.safe_bite.dto.OpenFoodFactsResponse;
import com.safe.springboot.api.safe_bite.dto.ProductDTO;
import com.safe.springboot.api.safe_bite.enums.SourceProduct;
import com.safe.springboot.api.safe_bite.mapper.ProductMapper;
import com.safe.springboot.api.safe_bite.model.Ingredient;
import com.safe.springboot.api.safe_bite.model.Product;
import com.safe.springboot.api.safe_bite.model.ProductIngredient;
import com.safe.springboot.api.safe_bite.repositories.IngredientRepository;
import com.safe.springboot.api.safe_bite.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final RestTemplate restTemplate;

    private final IngredientRepository ingredientRepository;

    private static final String OPEN_FOOD_FACTS_API = "https://world.openfoodfacts.org/api/v3/product/";

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, RestTemplate restTemplate, IngredientRepository ingredientRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.restTemplate = restTemplate;
        this.ingredientRepository = ingredientRepository;
    }

    public ProductDTO findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with barcode: " + barcode));
    }

    @Transactional
    public ProductDTO addProduct(String barcode) {
        Optional<Product> existingProduct = productRepository.findByBarcode(barcode);

        if (existingProduct.isPresent()) {
            return productMapper.toDto(existingProduct.get());
        }

        OpenFoodFactsProduct openFoodFactsProduct = this.getProduct(barcode);

        List<String> additiveCodes = this.extractAdditiveCodes(openFoodFactsProduct);

        List<Ingredient> matchedIngredients = additiveCodes.isEmpty() ? List.of() : ingredientRepository.findByCodeIn(additiveCodes);

        Product product = Product.builder()
                .barcode(barcode)
                .name(openFoodFactsProduct.getProductNameRo() != null
                        ? openFoodFactsProduct.getProductNameRo()
                        : null)
                .sourceProduct(SourceProduct.OPENFOODFACTS)
                .lastUpdated(LocalDateTime.now())
                .build();

        if(!matchedIngredients.isEmpty()) {
            List<ProductIngredient> productIngredients = matchedIngredients.stream()
                    .map(ingredient -> ProductIngredient.builder()
                            .product(product)
                            .ingredient(ingredient)
                            .build())
                    .toList();

            product.setProductIngredients(productIngredients);
        }

        productRepository.save(product);

        return productMapper.toDto(product);
    }

    private OpenFoodFactsProduct getProduct(String barcode) {
        String openFoodFactsUrl = OPEN_FOOD_FACTS_API + barcode + ".json";

        try {
            ResponseEntity<OpenFoodFactsResponse> response = restTemplate.getForEntity(openFoodFactsUrl, OpenFoodFactsResponse.class);

            if (response.getBody() == null || response.getBody().getProduct() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product from OpenFoodFacts API not found with barcode: " + barcode);
            }

            return Objects.requireNonNull(response.getBody()).getProduct();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product from OpenFoodFacts API not found with barcode: " + barcode);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching product from OpenFoodFacts API", e);
        }
    }

    private List<String> extractAdditiveCodes(OpenFoodFactsProduct openFoodFactsProduct) {
        List<String> additives = openFoodFactsProduct.getAdditives_original_tags() != null && !openFoodFactsProduct.getAdditives_original_tags().isEmpty()
                ? openFoodFactsProduct.getAdditives_original_tags()
                : openFoodFactsProduct.getAdditives_tags();

        return additives.stream()
                .filter(tag -> tag.startsWith("en:"))
                .map(tag -> tag.replace("en:", "").toUpperCase())
                .collect(Collectors.toList());
    }
}
