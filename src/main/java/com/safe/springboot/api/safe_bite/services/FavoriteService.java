package com.safe.springboot.api.safe_bite.services;

import com.safe.springboot.api.safe_bite.dto.FavoriteResponseDTO;
import com.safe.springboot.api.safe_bite.dto.FavoriteWithProductPropertiesDto;
import com.safe.springboot.api.safe_bite.mapper.FavoriteMapper;
import com.safe.springboot.api.safe_bite.model.Favorite;
import com.safe.springboot.api.safe_bite.model.Product;
import com.safe.springboot.api.safe_bite.repositories.FavoriteRepository;
import com.safe.springboot.api.safe_bite.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    private final FavoriteMapper favoriteMapper;

    private final ProductRepository productRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.favoriteMapper = favoriteMapper;
    }

    public List<FavoriteResponseDTO> getFavoriteByUserId(UUID userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        if (favorites.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No favorites found with userId: " + userId);
        }

        return favorites.stream()
                .map(favoriteMapper::toDtoWithProduct)
                .collect(Collectors.toList());
    }

    public List<FavoriteWithProductPropertiesDto> getFavoritesWithProduct(UUID userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(favoriteMapper::toFavoriteWithProduct)
                .collect(Collectors.toList());
    }

    public void addFavorite(UUID userId, String productBarcode, String productName) {
        Product product = productRepository.findByBarcode(productBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productBarcode));

        if(favoriteRepository.existsByUserIdAndProductId(userId, product.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Favorite product already exists with id: " + productBarcode);
        }

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .productId(product.getId())
                .productName(productName)
                .build();

        favoriteRepository.save(favorite);
    }

    public FavoriteResponseDTO patchFavorite(UUID userId, String productBarcode, String productName) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductBarcode(userId, productBarcode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        favorite.setProductName(productName);
        favoriteRepository.save(favorite);

        return favoriteMapper.toDtoWithProduct(favorite);
    }

    @Transactional
    public void deleteFavorite(UUID userId, String productBarcode) {
        Product product = productRepository.findByBarcode(productBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productBarcode));

        favoriteRepository.deleteFavoriteByUserIdAndProductId(userId, product.getId());
    }
}