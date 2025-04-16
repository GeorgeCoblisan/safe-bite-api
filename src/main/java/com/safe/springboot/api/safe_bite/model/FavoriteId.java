package com.safe.springboot.api.safe_bite.model;

import java.io.Serializable;
import java.util.UUID;

public class FavoriteId implements Serializable {
    private UUID userId;
    private UUID productId;
}
