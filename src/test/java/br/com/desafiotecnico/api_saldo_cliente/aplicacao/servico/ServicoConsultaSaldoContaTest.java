package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade.ObservabilidadeMetricasAplicacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServicoConsultaSaldoContaTest {

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida = mock(RepositorioSaldoContaPortaSaida.class);
    private final ObservabilidadeMetricasAplicacao observabilidadeMetricasAplicacao = mock(ObservabilidadeMetricasAplicacao.class);
    private final ServicoConsultaSaldoConta servicoConsultaSaldoConta = new ServicoConsultaSaldoConta(
            repositorioSaldoContaPortaSaida,
            observabilidadeMetricasAplicacao
    );

    @Test
    void deveRetornarSaldoQuandoTitularSolicitanteForDonoDaConta() {
        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando("12345", "titular-001");
        SaldoConta saldoContaEsperado = new SaldoConta(
                new Conta("12345", "titular-001"),
                new BigDecimal("1024.99"),
                "BRL",
                OffsetDateTime.parse("2026-01-11T10:15:30Z"),
                OffsetDateTime.parse("2026-01-11T10:15:30Z"),
                null
        );

        when(repositorioSaldoContaPortaSaida.buscarPorIdConta("12345")).thenReturn(Optional.of(saldoContaEsperado));

        SaldoConta saldoConta = servicoConsultaSaldoConta.consultar(comando);

        assertEquals(saldoContaEsperado, saldoConta);
        verify(observabilidadeMetricasAplicacao).incrementarConsultasSaldo();
    }

    @Test
    void deveLancarAcessoNaoAutorizadoQuandoTitularSolicitanteForDiferenteDoDonoDaConta() {
        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando("12345", "titular-999");
        SaldoConta saldoConta = new SaldoConta(
                new Conta("12345", "titular-001"),
                new BigDecimal("1024.99"),
                "BRL",
                OffsetDateTime.parse("2026-01-11T10:15:30Z"),
                OffsetDateTime.parse("2026-01-11T10:15:30Z"),
                null
        );

        when(repositorioSaldoContaPortaSaida.buscarPorIdConta("12345")).thenReturn(Optional.of(saldoConta));

        assertThrows(AcessoNaoAutorizadoContaExcecao.class, () -> servicoConsultaSaldoConta.consultar(comando));
        verify(observabilidadeMetricasAplicacao).incrementarNegacoesAcesso();
    }

    @Test
    void deveLancarContaNaoEncontradaQuandoContaNaoExistir() {
        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando("conta-inexistente", "titular-001");

        when(repositorioSaldoContaPortaSaida.buscarPorIdConta("conta-inexistente")).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaExcecao.class, () -> servicoConsultaSaldoConta.consultar(comando));
    }
}
