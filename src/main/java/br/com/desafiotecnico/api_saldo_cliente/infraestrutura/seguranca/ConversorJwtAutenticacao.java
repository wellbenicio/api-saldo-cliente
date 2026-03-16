package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConversorJwtAutenticacao implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final List<String> CLAIMS_ID_CLIENTE = List.of("idCliente", "sub");
    private static final List<String> CLAIMS_DOCUMENTO = List.of("documento", "cpf", "cnpj");
    private static final List<String> CLAIMS_PERFIS_OU_SCOPES = List.of("perfisOuScopes", "scope", "scp");

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String idCliente = extrairClaimObrigatoria(jwt, CLAIMS_ID_CLIENTE, "identificação do cliente");
        String documento = extrairClaimObrigatoria(jwt, CLAIMS_DOCUMENTO, "documento do cliente");
        Set<String> perfisOuScopes = extrairPerfisOuScopes(jwt);

        PrincipalConta principalConta = new PrincipalConta(idCliente, documento, perfisOuScopes);
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);

        return new JwtAuthenticationToken(jwt, authorities, principalConta.getName()) {
            @Override
            public Object getPrincipal() {
                return principalConta;
            }
        };
    }

    private String extrairClaimObrigatoria(Jwt jwt, List<String> nomesClaims, String descricao) {
        return nomesClaims.stream()
                .map(jwt::getClaimAsString)
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElseThrow(() -> new JwtException("Token JWT sem " + descricao + "."));
    }

    private Set<String> extrairPerfisOuScopes(Jwt jwt) {
        Set<String> perfisOuScopes = new LinkedHashSet<>();

        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        if (authorities != null) {
            authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(valor -> valor != null && !valor.isBlank())
                    .forEach(perfisOuScopes::add);
        }

        CLAIMS_PERFIS_OU_SCOPES.stream()
                .map(jwt::getClaimAsString)
                .filter(valor -> valor != null && !valor.isBlank())
                .flatMap(valor -> List.of(valor.split("\\s+")).stream())
                .map(String::trim)
                .filter(valor -> !valor.isBlank())
                .forEach(perfisOuScopes::add);

        if (perfisOuScopes.isEmpty()) {
            throw new JwtException("Token JWT sem perfis ou scopes.");
        }

        return Set.copyOf(perfisOuScopes);
    }
}
