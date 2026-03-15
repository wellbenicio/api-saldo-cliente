package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.UsuarioAutenticado;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
            UsuarioAutenticado usuarioAutenticado = criarUsuarioAutenticado(claims);

            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, Collections.emptyList());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
            filterChain.doFilter(request, response);
        } catch (TokenJwtInvalidoExcecao excecao) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Token JWT inválido ou expirado.", excecao);
        }
    }

    /**
     * Claims esperadas no JWT:
     * - idCliente: identificador principal do cliente (fallback explícito: claim sub);
     * - documento: documento do cliente (fallback explícito: idCliente);
     * - perfis/scopes: permissões do usuário (aceita claims "perfis", "scopes" ou "scope").
     */
    private UsuarioAutenticado criarUsuarioAutenticado(Claims claims) {
        String idCliente = obterIdCliente(claims);
        String documento = obterClaimComoTexto(claims, "documento", idCliente);
        Set<String> perfisScopes = obterPerfisScopes(claims);

        return new UsuarioAutenticado(idCliente, documento, perfisScopes);
    }

    private String obterIdCliente(Claims claims) {
        return obterClaimComoTexto(claims, "idCliente", claims.getSubject());
    }

    private String obterClaimComoTexto(Claims claims, String nomeClaim, String fallback) {
        Object valor = claims.get(nomeClaim);
        if (valor instanceof String texto && !texto.isBlank()) {
            return texto;
        }

        if (valor != null) {
            String texto = valor.toString();
            if (!texto.isBlank()) {
                return texto;
            }
        }

        return fallback;
    }

    private Set<String> obterPerfisScopes(Claims claims) {
        Object perfis = claims.get("perfis");
        if (perfis != null) {
            return normalizarColecaoClaim(perfis);
        }

        Object scopes = claims.get("scopes");
        if (scopes != null) {
            return normalizarColecaoClaim(scopes);
        }

        Object scope = claims.get("scope");
        if (scope != null) {
            return normalizarColecaoClaim(scope);
        }

        return Collections.emptySet();
    }

    private Set<String> normalizarColecaoClaim(Object claim) {
        if (claim instanceof Collection<?> colecao) {
            return colecao.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(valor -> !valor.isBlank())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (claim instanceof String texto) {
            return Arrays.stream(texto.split("\\s+"))
                    .map(String::trim)
                    .filter(valor -> !valor.isBlank())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        String valor = claim.toString().trim();
        if (valor.isBlank()) {
            return Collections.emptySet();
        }

        return Set.of(valor);
    }
}
