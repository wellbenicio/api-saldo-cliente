package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConversorJwtAutenticacaoTest {

    private final ConversorJwtAutenticacao conversorJwtAutenticacao = new ConversorJwtAutenticacao();

    @Test
    void deveConverterJwtParaPrincipalContaUsandoClaimSub() {
        Jwt jwt = criarJwt(Map.of("sub", "titular-001"));

        JwtAuthenticationToken autenticacao = (JwtAuthenticationToken) conversorJwtAutenticacao.convert(jwt);

        PrincipalConta principalConta = assertInstanceOf(PrincipalConta.class, autenticacao.getPrincipal());
        assertEquals("titular-001", principalConta.idTitular());
    }

    @Test
    void deveFalharQuandoJwtNaoContiverClaimIdentificacaoDoTitular() {
        Jwt jwt = criarJwt(Map.of("scope", "saldo:read"));

        assertThrows(JwtException.class, () -> conversorJwtAutenticacao.convert(jwt));
    }

    private Jwt criarJwt(Map<String, Object> claims) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                claims
        );
    }
}
