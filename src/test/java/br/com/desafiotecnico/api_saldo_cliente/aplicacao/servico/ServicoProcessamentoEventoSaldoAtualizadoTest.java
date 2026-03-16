package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsumirEventoSaldoAtualizadoComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioEventoProcessadoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServicoProcessamentoEventoSaldoAtualizadoTest {

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida = mock(RepositorioSaldoContaPortaSaida.class);
    private final RepositorioEventoProcessadoPortaSaida repositorioEventoProcessadoPortaSaida = mock(RepositorioEventoProcessadoPortaSaida.class);

    private final ServicoProcessamentoEventoSaldoAtualizado servico = new ServicoProcessamentoEventoSaldoAtualizado(
            repositorioSaldoContaPortaSaida,
            repositorioEventoProcessadoPortaSaida
    );

    @Test
    void deveProcessarEventoNovo() {
        ConsumirEventoSaldoAtualizadoComando comando = new ConsumirEventoSaldoAtualizadoComando(
                "evt-001", "conta-123", "titular-001", new BigDecimal("2500.10"),
                OffsetDateTime.parse("2026-03-10T10:15:30Z"), 10L, "MQ_JMS_SIMULADO"
        );

        when(repositorioEventoProcessadoPortaSaida.jaProcessado("evt-001")).thenReturn(false);
        when(repositorioSaldoContaPortaSaida.buscarPorIdConta("conta-123")).thenReturn(Optional.empty());
        when(repositorioSaldoContaPortaSaida.salvar(any(SaldoConta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        servico.consumir(comando);

        verify(repositorioSaldoContaPortaSaida).salvar(any(SaldoConta.class));
        verify(repositorioEventoProcessadoPortaSaida).registrarProcessamento("evt-001", "MQ_JMS_SIMULADO");
    }

    @Test
    void deveIgnorarEventoDuplicado() {
        ConsumirEventoSaldoAtualizadoComando comando = new ConsumirEventoSaldoAtualizadoComando(
                "evt-duplicado", "conta-123", "titular-001", new BigDecimal("9999.99"),
                OffsetDateTime.parse("2026-03-10T10:15:30Z"), 99L, "MQ_JMS_SIMULADO"
        );

        when(repositorioEventoProcessadoPortaSaida.jaProcessado("evt-duplicado")).thenReturn(true);

        servico.consumir(comando);

        verify(repositorioSaldoContaPortaSaida, never()).salvar(any(SaldoConta.class));
        verify(repositorioEventoProcessadoPortaSaida, never()).registrarProcessamento(any(), any());
    }

    @Test
    void deveIgnorarEventoForaDeOrdemSemSobrescreverSaldoAtual() {
        SaldoConta saldoAtual = new SaldoConta(
                new Conta("conta-123", "titular-001"),
                new BigDecimal("500.00"),
                "BRL",
                OffsetDateTime.parse("2026-03-10T11:00:00Z"),
                OffsetDateTime.parse("2026-03-10T11:00:00Z"),
                20L
        );

        ConsumirEventoSaldoAtualizadoComando comando = new ConsumirEventoSaldoAtualizadoComando(
                "evt-antigo", "conta-123", "titular-001", new BigDecimal("300.00"),
                OffsetDateTime.parse("2026-03-10T10:00:00Z"), 19L, "MQ_JMS_SIMULADO"
        );

        when(repositorioEventoProcessadoPortaSaida.jaProcessado("evt-antigo")).thenReturn(false);
        when(repositorioSaldoContaPortaSaida.buscarPorIdConta("conta-123")).thenReturn(Optional.of(saldoAtual));

        servico.consumir(comando);

        verify(repositorioSaldoContaPortaSaida, never()).salvar(any(SaldoConta.class));
        verify(repositorioEventoProcessadoPortaSaida).registrarProcessamento(eq("evt-antigo"), eq("MQ_JMS_SIMULADO"));
        assertEquals(new BigDecimal("500.00"), saldoAtual.valor());
    }
}
