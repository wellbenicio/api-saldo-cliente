package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final ValidadorJwt validadorJwt;

    public FiltroAutenticacaoJwt(ValidadorJwt validadorJwt) {
        this.validadorJwt = validadorJwt;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/v1/contas/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cabecalhoAutorizacao = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (cabecalhoAutorizacao == null || !cabecalhoAutorizacao.startsWith(PREFIXO_BEARER)) {
            throw new InsufficientAuthenticationException("Token JWT ausente ou inválido.");
        }

        String token = cabecalhoAutorizacao.substring(PREFIXO_BEARER.length());

        try {
            Claims claims = validadorJwt.extrairClaims(token);
            String sujeito = claims.getSubject();

            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(sujeito, null, Collections.emptyList());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
            filterChain.doFilter(request, response);
        } catch (TokenJwtInvalidoExcecao excecao) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Token JWT inválido ou expirado.", excecao);
        }
    }
}
