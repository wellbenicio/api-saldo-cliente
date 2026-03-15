package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorJwtTest {

    private static final String SEGREDO_VALIDO = "segredo-jwt-api-saldo-cliente-2026-chave-segura";

    @Test
    void deveFazerParsingDosClaimsQuandoTokenForValido() {
        ValidadorJwt validadorJwt = new ValidadorJwt(SEGREDO_VALIDO);
        String token = gerarTokenAssinado(SEGREDO_VALIDO, "titular-001", "conta:saldo:consultar");

        Claims claims = validadorJwt.extrairClaims(token);

        assertThat(claims.getSubject()).isEqualTo("titular-001");
        assertThat(claims.get("escopo", String.class)).isEqualTo("conta:saldo:consultar");
    }

    @Test
    void deveRejeitarTokenInvalido() {
        ValidadorJwt validadorJwt = new ValidadorJwt(SEGREDO_VALIDO);

        assertThatThrownBy(() -> validadorJwt.extrairClaims("token-invalido"))
                .isInstanceOf(TokenJwtInvalidoExcecao.class)
                .hasMessage("Token JWT inválido ou expirado.");
    }

    private String gerarTokenAssinado(String segredo, String sujeito, String escopo) {
        Instant agora = Instant.now();
        SecretKey chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(sujeito)
                .claim("escopo", escopo)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(5, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }
}
