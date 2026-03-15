package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FiltroAutenticacaoCabecalho extends OncePerRequestFilter {

    private static final String CABECALHO_ID_CLIENTE = "X-Id-Cliente";
    private static final String CABECALHO_DOCUMENTO = "X-Documento";
    private static final String CABECALHO_SCOPES = "X-Scopes";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String idCliente = request.getHeader(CABECALHO_ID_CLIENTE);
        String documento = request.getHeader(CABECALHO_DOCUMENTO);
        String scopes = request.getHeader(CABECALHO_SCOPES);

        if (StringUtils.hasText(idCliente)) {
            UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(idCliente, documento, parseScopes(scopes));
            UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, Collections.emptyList());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }

        filterChain.doFilter(request, response);
    }

    private Set<String> parseScopes(String scopes) {
        if (!StringUtils.hasText(scopes)) {
            return Set.of();
        }
        return Arrays.stream(scopes.split("\\s+"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}
