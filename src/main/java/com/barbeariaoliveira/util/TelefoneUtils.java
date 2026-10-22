package com.barbeariaoliveira.util;

public class TelefoneUtils {
    
    private TelefoneUtils(){}

    public static String normalizar(String telefone){
        return telefone.replaceAll("\\D", "");
    }
}
