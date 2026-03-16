package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade.ObservabilidadeMetricasAplicacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class ManipuladorAcessoNegado implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final ObservabilidadeMetricasAplicacao observabilidadeMetricasAplicacao;

    public ManipuladorAcessoNegado(
            ObjectMapper objectMapper,
            ObservabilidadeMetricasAplicacao observabilidadeMetricasAplicacao
    ) {
        this.objectMapper = objectMapper;
        this.observabilidadeMetricasAplicacao = observabilidadeMetricasAplicacao;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        observabilidadeMetricasAplicacao.incrementarNegacoesAcesso();

        ErroApiResposta erroApiResposta = new ErroApiResposta(
                "ACESSO_NEGADO",
                "Usuário autenticado sem permissão para acessar este recurso.",
                OffsetDateTime.now()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), erroApiResposta);
    }
}
