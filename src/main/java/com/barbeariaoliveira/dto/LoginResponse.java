package com.barbeariaoliveira.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        Instant expiraEm) {
}
