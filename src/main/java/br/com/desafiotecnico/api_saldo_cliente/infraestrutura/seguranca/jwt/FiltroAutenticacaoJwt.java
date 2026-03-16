package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @deprecated Fluxo legado mantido apenas para compatibilidade de merge.
 * A autenticação efetiva usa Spring Security OAuth2 Resource Server.
 */
@Deprecated(forRemoval = false)
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
