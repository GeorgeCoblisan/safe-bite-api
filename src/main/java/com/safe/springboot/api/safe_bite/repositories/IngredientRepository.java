package com.safe.springboot.api.safe_bite.repositories;

import com.safe.springboot.api.safe_bite.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    List<Ingredient> findByCodeIn(List<String> codes);
}
