package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaldoContaControlador.class)
@Import(TratadorGlobalExcecao.class)
@AutoConfigureMockMvc(addFilters = false)
class SaldoContaControladorValidacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;

    @Test
    void deveRetornarBadRequestQuandoCabecalhoObrigatorioNaoForInformado() throws Exception {
        mockMvc.perform(get("/v1/contas/12345/saldo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.detalhes[0].campo").value("requisicao"));
    }

    @Test
    void deveRetornarBadRequestQuandoPathVariableForEmBranco() throws Exception {
        mockMvc.perform(get("/v1/contas/%20/saldo")
                        .header("X-Id-Titular", "titular-123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.detalhes[0].campo").value("consultar.idConta"));
    }
}
