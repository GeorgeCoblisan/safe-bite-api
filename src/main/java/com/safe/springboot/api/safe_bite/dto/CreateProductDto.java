package com.safe.springboot.api.safe_bite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDto {
    private String barcode;
}
