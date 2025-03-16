package com.safe.springboot.api.safe_bite.controllers;

import com.safe.springboot.api.safe_bite.dto.ProductDTO;
import com.safe.springboot.api.safe_bite.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<ProductDTO> getProductByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(productService.findByBarcode(barcode));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestParam String barcode) {
        return ResponseEntity.ok(productService.addProduct(barcode));
    }
}
