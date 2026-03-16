package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ManipuladorAcessoNegadoTest {

    @Test
    void deveRetornarResposta403EmFormatoJsonEIncrementarNegacoesAcesso() throws Exception {
        ObservabilidadePortaSaida observabilidadePortaSaida = mock(ObservabilidadePortaSaida.class);
        ManipuladorAcessoNegado manipulador = new ManipuladorAcessoNegado(new ObjectMapper().findAndRegisterModules(), observabilidadePortaSaida);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException excecaoAcessoNegado = new AccessDeniedException("acesso negado");

        manipulador.handle(request, response, excecaoAcessoNegado);

        assertEquals(403, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertTrue(response.getContentAsString().contains("\"codigo\":\"ACESSO_NEGADO\""));
        verify(observabilidadePortaSaida).incrementarNegacoesAcesso();
    }
}
