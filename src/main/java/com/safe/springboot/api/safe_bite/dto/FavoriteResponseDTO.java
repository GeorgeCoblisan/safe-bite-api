package com.safe.springboot.api.safe_bite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponseDTO {
    private String customName;

    private String barcode;

    private String productName;
}
