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
