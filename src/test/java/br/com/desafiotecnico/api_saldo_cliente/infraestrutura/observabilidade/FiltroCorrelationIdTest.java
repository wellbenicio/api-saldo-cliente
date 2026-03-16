package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FiltroCorrelationIdTest {

    private final FiltroCorrelationId filtro = new FiltroCorrelationId();

    @AfterEach
    void limparMdcAposTeste() {
        MDC.remove(FiltroCorrelationId.CHAVE_MDC_CORRELATION_ID);
    }

    @Test
    void deveReutilizarCorrelationIdDoHeaderNaResposta() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FiltroCorrelationId.HEADER_CORRELATION_ID, "corr-id-existente-001");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, new MockFilterChain());

        assertEquals("corr-id-existente-001", response.getHeader(FiltroCorrelationId.HEADER_CORRELATION_ID));
        assertNull(MDC.get(FiltroCorrelationId.CHAVE_MDC_CORRELATION_ID));
    }

    @Test
    void deveGerarUuidQuandoHeaderNaoForInformadoOuEstiverEmBranco() throws ServletException, IOException {
        MockHttpServletRequest requestSemHeader = new MockHttpServletRequest();
        MockHttpServletResponse responseSemHeader = new MockHttpServletResponse();

        filtro.doFilter(requestSemHeader, responseSemHeader, new MockFilterChain());

        String correlationIdGeradoSemHeader = responseSemHeader.getHeader(FiltroCorrelationId.HEADER_CORRELATION_ID);
        UUID.fromString(correlationIdGeradoSemHeader);
        assertNull(MDC.get(FiltroCorrelationId.CHAVE_MDC_CORRELATION_ID));

        MockHttpServletRequest requestHeaderEmBranco = new MockHttpServletRequest();
        requestHeaderEmBranco.addHeader(FiltroCorrelationId.HEADER_CORRELATION_ID, "   ");
        MockHttpServletResponse responseHeaderEmBranco = new MockHttpServletResponse();

        filtro.doFilter(requestHeaderEmBranco, responseHeaderEmBranco, new MockFilterChain());

        String correlationIdGeradoHeaderEmBranco = responseHeaderEmBranco.getHeader(FiltroCorrelationId.HEADER_CORRELATION_ID);
        UUID.fromString(correlationIdGeradoHeaderEmBranco);
        assertNull(MDC.get(FiltroCorrelationId.CHAVE_MDC_CORRELATION_ID));
    }

    @Test
    void deveLimparMdcMesmoQuandoFilterChainLancarExcecao() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(ServletException.class,
                () -> filtro.doFilter(request, response, (req, res) -> {
                    throw new ServletException("falha no chain");
                }));

        assertNull(MDC.get(FiltroCorrelationId.CHAVE_MDC_CORRELATION_ID));
    }
}
