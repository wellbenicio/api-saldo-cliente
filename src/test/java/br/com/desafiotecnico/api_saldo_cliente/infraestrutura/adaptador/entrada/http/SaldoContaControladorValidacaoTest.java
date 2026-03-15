package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt.FiltroAutenticacaoJwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaldoContaControlador.class)
@Import({
        TratadorGlobalExcecao.class,
        ConfiguracaoSeguranca.class,
        ConversorJwtAutenticacao.class,
        ManipuladorAutenticacaoNaoAutenticado.class,
        ManipuladorAcessoNegado.class
})
@AutoConfigureMockMvc
class SaldoContaControladorValidacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FiltroAutenticacaoJwt filtroAutenticacaoJwt;

    @MockitoBean
    private ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;

    @MockitoBean
    private JwtDecoder jwtDecoder;

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
                        .principal(usuarioAutenticado("titular-001"))
                        .accept(APPLICATION_JSON),
                "12345",
                "titular-001",
                100.00,
                "BRL",
                "2026-01-01T10:15:30Z"
        );
    }

    @Test
    void deveRetornarSaldoComCamposObrigatoriosQuandoEntradaValida() throws Exception {
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2026-12-31T23:59:59Z");
        SaldoConta saldoConta = new SaldoConta(
                new Conta("998877", "titular-777"),
                new BigDecimal("450.25"),
                "USD",
                atualizadoEm
        );

        when(consultarSaldoContaPortaEntrada.consultar(ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenReturn(saldoConta);

        assertSaidaSaldoContaDto(
                get("/v1/contas/998877/saldo")
                        .principal(usuarioAutenticado("titular-777"))
                        .accept(APPLICATION_JSON),
                "998877",
                "titular-777",
                450.25,
                "USD",
                "2026-12-31T23:59:59Z"
        );
    }

        @Test
    void deveRetornarErroPadronizadoQuandoIdContaInvalida() throws Exception {
        mockMvc.perform(get("/v1/contas/%20/saldo")
                        .principal(usuarioAutenticado("titular-123"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ACESSO_NAO_AUTORIZADO"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoTitularInvalido() throws Exception {
        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header("X-Id-Titular", "abc")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres."))
                .andExpect(jsonPath("$.detalhes[0].campo").value("idTitular"))
                .andExpect(jsonPath("$.detalhes[0].mensagem").value("Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres."));
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
