package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
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
    void deveRetornarSaldoQuandoEntradaValida() throws Exception {
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2025-01-01T10:15:30Z");
        SaldoConta saldoConta = new SaldoConta(
                new Conta("12345", "titular-001"),
                new BigDecimal("100.00"),
                "BRL",
                atualizadoEm
        );

        when(consultarSaldoContaPortaEntrada.consultar(ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenReturn(saldoConta);

        mockMvc.perform(get("/v1/contas/{idConta}/saldo", "12345")
                        .header("X-Id-Titular", "titular-001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConta").value("12345"))
                .andExpect(jsonPath("$.idClienteTitular").value("titular-001"))
                .andExpect(jsonPath("$.valorSaldo").value(100.00))
                .andExpect(jsonPath("$.moeda").value("BRL"))
                .andExpect(jsonPath("$.dataHoraUltimaAtualizacao").value("2025-01-01T10:15:30Z"));
    }

    @Test
    void deveRetornarBadRequestQuandoIdContaInvalida() throws Exception {
        mockMvc.perform(get("/v1/contas/{idConta}/saldo", "123")
                        .header("X-Id-Titular", "titular-001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.detalhes[0].campo").value("consultar.idConta"))
                .andExpect(jsonPath("$.detalhes[0].mensagem").value("Parâmetro 'idConta' deve ter entre 5 e 20 caracteres."));
    }

    @Test
    void deveRetornarBadRequestQuandoTitularInvalido() throws Exception {
        mockMvc.perform(get("/v1/contas/{idConta}/saldo", "12345")
                        .header("X-Id-Titular", "abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.detalhes[0].campo").value("consultar.idTitular"))
                .andExpect(jsonPath("$.detalhes[0].mensagem").value("Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres."));
    }

    @Test
    void deveRetornarBadRequestQuandoCabecalhoObrigatorioNaoForInformado() throws Exception {
        mockMvc.perform(get("/v1/contas/{idConta}/saldo", "12345")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.detalhes[0].campo").value("requisicao"));
    }
}
