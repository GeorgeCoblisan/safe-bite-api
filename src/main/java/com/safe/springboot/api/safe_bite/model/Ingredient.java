package com.safe.springboot.api.safe_bite.model;

import com.safe.springboot.api.safe_bite.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity()
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "text")
    private String effects;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL)
    private List<ProductIngredient> productIngredients;
}
