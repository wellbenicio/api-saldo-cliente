package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.ConfiguracaoSeguranca;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ConversorJwtAutenticacao;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ManipuladorAcessoNegado;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.ManipuladorAutenticacaoNaoAutenticado;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2026-01-01T10:15:30Z");
        SaldoConta saldoConta = new SaldoConta(
                new Conta("12345", "titular-001"),
                new BigDecimal("100.00"),
                "BRL",
                atualizadoEm
        );

        when(consultarSaldoContaPortaEntrada.consultar(ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenReturn(saldoConta);

        assertSaidaSaldoContaDto(
                get("/v1/contas/12345/saldo")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "titular-001")))
                        .accept(APPLICATION_JSON),
                "12345",
                "titular-001",
                100.00,
                "BRL",
                "2026-01-01T10:15:30Z"
        );
    }

    @Test
    void deveRetornar403QuandoSolicitanteNaoForTitular() throws Exception {
        when(consultarSaldoContaPortaEntrada.consultar(ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenThrow(new AcessoNaoAutorizadoContaExcecao("12345", "titular-999"));

        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "titular-999")))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"));
    }

    @Test
    void deveRetornar401QuandoTokenNaoContiverIdentificadorTitular() throws Exception {
        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", " ").claim("scope", "saldo:read")))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NAO_AUTENTICADO"));
    }

    private void assertSaidaSaldoContaDto(
            RequestBuilder requisicao,
            String idConta,
            String idTitular,
            double valor,
            String moeda,
            String atualizadoEm
    ) throws Exception {
        mockMvc.perform(requisicao)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConta").value(idConta))
                .andExpect(jsonPath("$.idClienteTitular").value(idTitular))
                .andExpect(jsonPath("$.valorSaldo").value(valor))
                .andExpect(jsonPath("$.moeda").value(moeda))
                .andExpect(jsonPath("$.dataHoraUltimaAtualizacao").value(atualizadoEm));
    }
}
