package com.safe.springboot.api.safe_bite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFavoriteDTO {
    private String productBarcode;

    private String productName;
}
