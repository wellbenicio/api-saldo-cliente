package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class ConfiguracaoDecoderJwt {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${seguranca.jwt.chave-assinatura:api-saldo-cliente-chave-assinatura-2026}") String chaveAssinatura) {
        SecretKey chave = new SecretKeySpec(chaveAssinatura.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
