package com.safe.springboot.api.safe_bite.model;

import com.safe.springboot.api.safe_bite.enums.SourceProduct;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String barcode;

    @Column()
    private String name;

    @Enumerated(EnumType.STRING)
    private SourceProduct sourceProduct;

    @Column()
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductIngredient> productIngredients = new ArrayList<>();
}
