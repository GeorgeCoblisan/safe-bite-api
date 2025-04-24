package com.safe.springboot.api.safe_bite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDto {
    String barcode;

    String base64Image;
}
