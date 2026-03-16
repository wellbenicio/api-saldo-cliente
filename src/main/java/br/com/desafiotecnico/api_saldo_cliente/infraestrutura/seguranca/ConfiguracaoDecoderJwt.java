package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt.PropriedadesSegurancaJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class ConfiguracaoDecoderJwt {

    @Bean
    public JwtDecoder jwtDecoder(PropriedadesSegurancaJwt propriedadesSegurancaJwt) {
        String segredoAssinatura = propriedadesSegurancaJwt.getSegredoAssinatura();

        if (!StringUtils.hasText(segredoAssinatura)) {
            throw new IllegalStateException("Configuração obrigatória ausente: seguranca.jwt.segredo-assinatura");
        }

        SecretKey chave = new SecretKeySpec(segredoAssinatura.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
