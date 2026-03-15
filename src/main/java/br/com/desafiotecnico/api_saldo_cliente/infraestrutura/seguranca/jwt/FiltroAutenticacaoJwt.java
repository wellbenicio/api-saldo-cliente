package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.UsuarioAutenticado;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final ValidadorJwt validadorJwt;
    private final ObjectMapper objectMapper;

    public FiltroAutenticacaoJwt(ValidadorJwt validadorJwt, ObjectMapper objectMapper) {
        this.validadorJwt = validadorJwt;
        this.objectMapper = objectMapper;
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
            escreverErroNaoAutenticado(response, "Token JWT ausente ou inválido.");
            return;
        }

        String token = cabecalhoAutorizacao.substring(PREFIXO_BEARER.length());

        try {
            Claims claims = validadorJwt.extrairClaims(token);
            UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(
                    claims.getSubject(),
                    claims.get("documento", String.class),
                    parseScopes(claims.get("escopo", String.class))
            );

            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, Set.of());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
            filterChain.doFilter(request, response);
        } catch (TokenJwtInvalidoExcecao excecao) {
            SecurityContextHolder.clearContext();
            escreverErroNaoAutenticado(response, "Token JWT inválido ou expirado.");
        }
    }

    private Set<String> parseScopes(String scopes) {
        if (scopes == null || scopes.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(scopes.split("\\s+"))
                .filter(valor -> !valor.isBlank())
                .collect(Collectors.toSet());
    }

    private void escreverErroNaoAutenticado(HttpServletResponse response, String mensagem) throws IOException {
        ErroApiResposta erroApiResposta = new ErroApiResposta(
                "NAO_AUTENTICADO",
                mensagem,
                OffsetDateTime.now()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), erroApiResposta);
    }
}
