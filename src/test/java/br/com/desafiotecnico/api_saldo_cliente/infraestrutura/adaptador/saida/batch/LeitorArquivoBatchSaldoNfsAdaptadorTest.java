package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.LeitorRegistroArquivoSaldoBatchItemReader;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.ProcessadorRegistroArquivoSaldoBatchItemProcessor;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo.RegistroArquivoSaldoBatch;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeitorArquivoBatchSaldoNfsAdaptadorTest {

    private final LeitorRegistroArquivoSaldoBatchItemReader itemReader = mock(LeitorRegistroArquivoSaldoBatchItemReader.class);
    private final ProcessadorRegistroArquivoSaldoBatchItemProcessor itemProcessor = mock(ProcessadorRegistroArquivoSaldoBatchItemProcessor.class);

    @Test
    void deveRetornarVazioQuandoLeitorNaoRetornarRegistros() throws Exception {
        when(itemReader.read()).thenReturn(null);

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(itemReader, itemProcessor);

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(0, saldos.size());
    }

    @Test
    void deveLerRegistrosComFormatoEsperado() throws Exception {
        RegistroArquivoSaldoBatch registro1 = new RegistroArquivoSaldoBatch(1L, 1L, new BigDecimal("100.50"), "BRL", OffsetDateTime.parse("2026-01-01T10:15:30Z"), 1);
        RegistroArquivoSaldoBatch registro2 = new RegistroArquivoSaldoBatch(2L, 2L, new BigDecimal("20.00"), "USD", OffsetDateTime.parse("2026-01-01T11:00:00Z"), 2);

        SaldoConta saldo1 = new SaldoConta(new Conta("conta-1", "titular-1"), new BigDecimal("100.50"), "BRL", OffsetDateTime.now(), OffsetDateTime.parse("2026-01-01T10:15:30Z"), null);
        SaldoConta saldo2 = new SaldoConta(new Conta("conta-2", "titular-2"), new BigDecimal("20.00"), "USD", OffsetDateTime.now(), OffsetDateTime.parse("2026-01-01T11:00:00Z"), null);

        when(itemReader.read()).thenReturn(registro1, registro2, null);
        when(itemProcessor.process(registro1)).thenReturn(saldo1);
        when(itemProcessor.process(registro2)).thenReturn(saldo2);

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(itemReader, itemProcessor);

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(2, saldos.size());
        assertEquals("conta-1", saldos.get(0).conta().idConta());
        assertEquals("titular-1", saldos.get(0).conta().idTitular());
        assertEquals("BRL", saldos.get(0).moeda());
    }
}
