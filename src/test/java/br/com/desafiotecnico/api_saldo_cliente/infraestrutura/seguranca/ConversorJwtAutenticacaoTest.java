package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversorJwtAutenticacaoTest {

    private final ConversorJwtAutenticacao conversorJwtAutenticacao = new ConversorJwtAutenticacao();

    @Test
    void deveConverterJwtParaPrincipalContaUsandoClaimsPadrao() {
        Jwt jwt = criarJwt(Map.of(
                "idCliente", "cliente-001",
                "documento", "12345678900",
                "scope", "saldo:read conta:consulta"
        ));

        JwtAuthenticationToken autenticacao = (JwtAuthenticationToken) conversorJwtAutenticacao.convert(jwt);

        PrincipalConta principalConta = assertInstanceOf(PrincipalConta.class, autenticacao.getPrincipal());
        assertEquals("cliente-001", principalConta.idCliente());
        assertEquals("12345678900", principalConta.documento());
        assertEquals("cliente-001", principalConta.getName());
        assertTrue(principalConta.perfisOuScopes().containsAll(Set.of("SCOPE_saldo:read", "SCOPE_conta:consulta", "saldo:read", "conta:consulta")));
    }


    @Test
    void deveAceitarClaimPerfisOuScopesComoFonteDeAutorizacao() {
        Jwt jwt = criarJwt(Map.of(
                "idCliente", "cliente-001",
                "documento", "12345678900",
                "perfisOuScopes", "saldo:read conta:consulta"
        ));

        JwtAuthenticationToken autenticacao = (JwtAuthenticationToken) conversorJwtAutenticacao.convert(jwt);
        PrincipalConta principalConta = assertInstanceOf(PrincipalConta.class, autenticacao.getPrincipal());

        assertTrue(principalConta.perfisOuScopes().contains("saldo:read"));
        assertTrue(principalConta.perfisOuScopes().contains("conta:consulta"));
    }

    @Test
    void deveUsarFallbacksParaClaimsAlternativos() {
        Jwt jwt = criarJwt(Map.of(
                "sub", "cliente-sub-001",
                "cpf", "98765432100",
                "scp", "saldo:read"
        ));

        JwtAuthenticationToken autenticacao = (JwtAuthenticationToken) conversorJwtAutenticacao.convert(jwt);
        PrincipalConta principalConta = assertInstanceOf(PrincipalConta.class, autenticacao.getPrincipal());

        assertEquals("cliente-sub-001", principalConta.idCliente());
        assertEquals("98765432100", principalConta.documento());
        assertTrue(principalConta.perfisOuScopes().contains("SCOPE_saldo:read"));
        assertTrue(principalConta.perfisOuScopes().contains("saldo:read"));
    }

    @Test
    void deveManterRetrocompatibilidadeComClaimEscopo() {
        Jwt jwt = criarJwt(Map.of(
                "idCliente", "cliente-001",
                "documento", "12345678900",
                "escopo", "saldo:read"
        ));

        JwtAuthenticationToken autenticacao = (JwtAuthenticationToken) conversorJwtAutenticacao.convert(jwt);
        PrincipalConta principalConta = assertInstanceOf(PrincipalConta.class, autenticacao.getPrincipal());

        assertTrue(principalConta.perfisOuScopes().contains("saldo:read"));
    }

    @Test
    void deveFalharQuandoJwtNaoContiverClaimObrigatoriaIdCliente() {
        Jwt jwt = criarJwt(Map.of(
                "documento", "12345678900",
                "scope", "saldo:read"
        ));

        assertThrows(JwtException.class, () -> conversorJwtAutenticacao.convert(jwt));
    }

    @Test
    void deveFalharQuandoJwtNaoContiverClaimObrigatoriaDocumento() {
        Jwt jwt = criarJwt(Map.of(
                "idCliente", "cliente-001",
                "scope", "saldo:read"
        ));

        assertThrows(JwtException.class, () -> conversorJwtAutenticacao.convert(jwt));
    }

    @Test
    void deveFalharQuandoJwtNaoContiverClaimObrigatoriaPerfisOuScopes() {
        Jwt jwt = criarJwt(Map.of(
                "idCliente", "cliente-001",
                "documento", "12345678900"
        ));

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
