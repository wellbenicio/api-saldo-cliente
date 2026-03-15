package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class ValidadorJwt {

    private final SecretKey chaveAssinatura;

    public ValidadorJwt(@Value("${seguranca.jwt.segredo}") String segredo) {
        this.chaveAssinatura = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extrairClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(chaveAssinatura)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException excecao) {
            throw new TokenJwtInvalidoExcecao("Token JWT inválido ou expirado.", excecao);
        }
    }
}
