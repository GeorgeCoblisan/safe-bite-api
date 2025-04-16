package com.safe.springboot.api.safe_bite.controllers;

import com.safe.springboot.api.safe_bite.dto.CreateFavoriteDTO;
import com.safe.springboot.api.safe_bite.dto.FavoriteResponseDTO;
import com.safe.springboot.api.safe_bite.dto.FavoriteWithProductPropertiesDto;
import com.safe.springboot.api.safe_bite.services.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(favoriteService.getFavoriteByUserId(userId));
    }

    @GetMapping("/products-entity")
    public ResponseEntity<List<FavoriteWithProductPropertiesDto>> getFavoritesWithProductEntity(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(favoriteService.getFavoritesWithProduct(userId));
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateFavoriteDTO favoriteDto) {
        UUID userId = UUID.fromString(jwt.getSubject());
        favoriteService.addFavorite(userId, favoriteDto.getProductBarcode(), favoriteDto.getProductName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productBarcode}")
    public ResponseEntity<FavoriteResponseDTO> patchFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable String productBarcode, @RequestParam String productName) {
        UUID userId = UUID.fromString(jwt.getSubject());
        FavoriteResponseDTO updatedFavorite = favoriteService.patchFavorite(userId, productBarcode, productName);
        return ResponseEntity.ok(updatedFavorite);
    }

    @DeleteMapping("/{productBarcode}")
    public ResponseEntity<?> deleteFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable String productBarcode) {
        UUID userId = UUID.fromString(jwt.getSubject());
        favoriteService.deleteFavorite(userId, productBarcode);
        return ResponseEntity.noContent().build();
    }
}
