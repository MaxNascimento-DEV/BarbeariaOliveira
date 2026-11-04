package com.barbeariaoliveira.util;

public class TelefoneUtils {

    private static final String CODIGO_BRASIL = "55";

    private TelefoneUtils(){}

    public static String normalizar(String telefone){
        return telefone.replaceAll("\\D", "");
    }

    /**
     * Formato exigido pelos links do WhatsApp (wa.me): só dígitos, com o código do país.
     * Ex.: "(81) 98888-1111" -> "5581988881111". Devolve null se não houver número.
     */
    public static String paraWhatsApp(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String digitos = normalizar(telefone);
        boolean jaTemCodigoPais = digitos.startsWith(CODIGO_BRASIL) && digitos.length() >= 12;
        return jaTemCodigoPais ? digitos : CODIGO_BRASIL + digitos;
    }
}
