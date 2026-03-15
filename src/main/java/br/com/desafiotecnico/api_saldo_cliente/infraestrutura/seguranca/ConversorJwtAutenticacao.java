package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversorJwtAutenticacao implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final List<String> CLAIMS_ID_TITULAR = List.of("idTitular", "titular_id", "sub");

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String idTitular = extrairIdTitular(jwt);
        PrincipalConta principalConta = new PrincipalConta(idTitular);

        return new JwtAuthenticationToken(jwt, jwtGrantedAuthoritiesConverter.convert(jwt), principalConta.idTitular()) {
            @Override
            public Object getPrincipal() {
                return principalConta;
            }
        };
    }

    private String extrairIdTitular(Jwt jwt) {
        return CLAIMS_ID_TITULAR.stream()
                .map(jwt::getClaimAsString)
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElseThrow(() -> new JwtException("Token JWT sem identificação de titular."));
    }
}
