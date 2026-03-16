package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManipuladorAutenticacaoNaoAutenticadoTest {

    @Test
    void deveRetornarResposta401EmFormatoJsonComCodigoNaoAutenticado() throws Exception {
        ManipuladorAutenticacaoNaoAutenticado manipulador = new ManipuladorAutenticacaoNaoAutenticado(new ObjectMapper().findAndRegisterModules());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        manipulador.commence(request, response, new BadCredentialsException("credenciais inválidas"));

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertTrue(response.getContentAsString().contains("\"codigo\":\"NAO_AUTENTICADO\""));
    }
}
