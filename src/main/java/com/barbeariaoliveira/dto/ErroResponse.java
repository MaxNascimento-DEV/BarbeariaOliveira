package com.barbeariaoliveira.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResponse(
     
    int status,
    String mensagem,
    Instant timestamp, 
    Map<String, String> campos
    
) {

}
