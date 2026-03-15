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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaldoContaControlador.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TratadorGlobalExcecao.class)
class SaldoContaControladorValidacaoTeste {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;

    @Test
    void deveRetornarSaldoQuandoEntradaValida() throws Exception {
        SaldoConta saldoConta = new SaldoConta(new Conta("12345", "titular-001"), new BigDecimal("100.00"), "BRL", OffsetDateTime.now());
        when(consultarSaldoContaPortaEntrada.consultar(ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenReturn(saldoConta);

        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header("X-Id-Titular", "titular-001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conta.idConta").value("12345"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoIdContaInvalida() throws Exception {
        mockMvc.perform(get("/v1/contas/%20/saldo")
                        .header("X-Id-Titular", "titular-001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Parâmetro 'idConta' é obrigatório."));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoTitularInvalido() throws Exception {
        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header("X-Id-Titular", "abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres."));
    }
}
