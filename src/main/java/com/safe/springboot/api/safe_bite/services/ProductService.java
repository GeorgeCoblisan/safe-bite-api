package com.safe.springboot.api.safe_bite.services;

import com.safe.springboot.api.safe_bite.dto.CreateProductDto;
import com.safe.springboot.api.safe_bite.dto.OpenFoodFactsProduct;
import com.safe.springboot.api.safe_bite.dto.OpenFoodFactsResponse;
import com.safe.springboot.api.safe_bite.dto.ProductDTO;
import com.safe.springboot.api.safe_bite.enums.SourceProduct;
import com.safe.springboot.api.safe_bite.helperservices.TextractService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.text.Normalizer;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final RestTemplate restTemplate;

    private final IngredientRepository ingredientRepository;

    private final TextractService textractService;

    private static final String OPEN_FOOD_FACTS_API = "https://world.openfoodfacts.org/api/v3/product/";

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, RestTemplate restTemplate, IngredientRepository ingredientRepository, TextractService textractService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.restTemplate = restTemplate;
        this.ingredientRepository = ingredientRepository;
        this.textractService = textractService;
    }

    public ProductDTO findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with barcode: " + barcode));
    }

    @Transactional
    public ProductDTO addProduct(CreateProductDto createProduct) throws IOException {
        String barcode = createProduct.getBarcode();
        String base64Image = createProduct.getBase64Image();

        Optional<Product> existingProduct = productRepository.findByBarcode(barcode);

        if (existingProduct.isPresent()) {
            return productMapper.toDto(existingProduct.get());
        }

        List<Ingredient> matchedIngredients = List.of();

        Product product;

        if (base64Image != null && !base64Image.isBlank()) {
            String cleanedBase64 = base64Image.replaceFirst("^data:image/[^;]+;base64,", "");
            byte[] decodedBytes = Base64.getDecoder().decode(cleanedBase64);
            InputStream imageStream = new ByteArrayInputStream(decodedBytes);

            String extractedText = textractService.extractText(imageStream);

            List<Ingredient> allIngredients = ingredientRepository.findAll();

            String normalizedText = normalize(extractedText);

            matchedIngredients = allIngredients.stream()
                    .filter(i -> {
                        String normalizedName = normalize(i.getName());
                        String normalizedCode = i.getCode() != null ? i.getCode().toLowerCase() : "";
                        return normalizedText.contains(normalizedName) || normalizedText.contains(normalizedCode);
                    })
                    .toList();

            product = Product.builder()
                    .barcode(barcode)
                    .name(null)
                    .sourceProduct(SourceProduct.USER)
                    .lastUpdated(LocalDateTime.now())
                    .build();
        } else {
            OpenFoodFactsProduct openFoodFactsProduct = this.getProduct(barcode);

            List<String> additiveCodes = this.extractAdditiveCodes(openFoodFactsProduct);

            matchedIngredients = additiveCodes.isEmpty() ? List.of() : ingredientRepository.findByCodeIn(additiveCodes);

            product = Product.builder()
                    .barcode(barcode)
                    .name(openFoodFactsProduct.getProductNameRo() != null
                            ? openFoodFactsProduct.getProductNameRo()
                            : openFoodFactsProduct.getProductNameEn())
                    .sourceProduct(SourceProduct.OPENFOODFACTS)
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }

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

        return additives != null ? additives.stream()
                .filter(tag -> tag.startsWith("en:"))
                .map(tag -> tag.replace("en:", "").toUpperCase())
                .collect(Collectors.toList()) : List.of();
    }

    private String normalize(String input) {
        if (input == null)
            return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

}
