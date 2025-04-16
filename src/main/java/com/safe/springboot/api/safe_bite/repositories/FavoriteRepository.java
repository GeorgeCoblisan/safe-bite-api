package com.safe.springboot.api.safe_bite.repositories;

import com.safe.springboot.api.safe_bite.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    List<Favorite> findByUserId(UUID userId);
    Optional<Favorite> findByUserIdAndProductBarcode(UUID userId, String productBarcode);
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    void deleteFavoriteByUserIdAndProductId(UUID userId, UUID productId);
}
