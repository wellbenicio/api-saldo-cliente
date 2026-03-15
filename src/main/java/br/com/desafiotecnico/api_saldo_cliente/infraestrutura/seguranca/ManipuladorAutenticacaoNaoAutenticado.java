package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class ManipuladorAutenticacaoNaoAutenticado implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ManipuladorAutenticacaoNaoAutenticado(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErroApiResposta erroApiResposta = new ErroApiResposta(
                "NAO_AUTENTICADO",
                "Autenticação obrigatória para acessar este recurso.",
                OffsetDateTime.now()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), erroApiResposta);
    }
}
