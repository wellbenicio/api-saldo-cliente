package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.LeitorRegistroArquivoSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.ProcessadorRegistroSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.RegistroArquivoSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.PropriedadesBatchSaldo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.file.FlatFileItemReader;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeitorArquivoBatchSaldoNfsAdaptadorTest {

    private final LeitorRegistroArquivoSaldoBatch leitorRegistroArquivoSaldoBatch = mock(LeitorRegistroArquivoSaldoBatch.class);
    private final ProcessadorRegistroSaldoBatch processadorRegistroSaldoBatch = mock(ProcessadorRegistroSaldoBatch.class);
    private final PropriedadesBatchSaldo propriedadesBatchSaldo = new PropriedadesBatchSaldo();

    @TempDir
    Path diretorioTemporario;

    @Test
    void deveRetornarVazioQuandoLeitorNaoRetornarRegistros() throws Exception {
        FlatFileItemReader<RegistroArquivoSaldoBatch> itemReader = mock(FlatFileItemReader.class);
        when(itemReader.read()).thenReturn(null);
        when(leitorRegistroArquivoSaldoBatch.criarLeitor(propriedadesBatchSaldo.caminhoArquivoEntrada().toString(), "|"))
                .thenReturn(itemReader);

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(
                leitorRegistroArquivoSaldoBatch,
                processadorRegistroSaldoBatch,
                propriedadesBatchSaldo
        );

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(0, saldos.size());
    }

    @Test
    void deveLerRegistrosComFormatoEsperado() throws Exception {
        propriedadesBatchSaldo.setDiretorioEntrada(diretorioTemporario);
        propriedadesBatchSaldo.setNomeArquivo("saldos.csv");

        RegistroArquivoSaldoBatch registro1 = new RegistroArquivoSaldoBatch("conta-1", "titular-1", "100.50", "BRL", "2026-01-01T10:15:30Z");
        RegistroArquivoSaldoBatch registro2 = new RegistroArquivoSaldoBatch("conta-2", "titular-2", "20.00", "USD", "2026-01-01T11:00:00Z");

        SaldoConta saldo1 = new SaldoConta(new Conta("conta-1", "titular-1"), new BigDecimal("100.50"), "BRL", OffsetDateTime.now(), OffsetDateTime.parse("2026-01-01T10:15:30Z"), null);
        SaldoConta saldo2 = new SaldoConta(new Conta("conta-2", "titular-2"), new BigDecimal("20.00"), "USD", OffsetDateTime.now(), OffsetDateTime.parse("2026-01-01T11:00:00Z"), null);

        FlatFileItemReader<RegistroArquivoSaldoBatch> itemReader = mock(FlatFileItemReader.class);
        when(itemReader.read()).thenReturn(registro1, registro2, null);
        when(leitorRegistroArquivoSaldoBatch.criarLeitor(propriedadesBatchSaldo.caminhoArquivoEntrada().toString(), "|"))
                .thenReturn(itemReader);
        when(processadorRegistroSaldoBatch.process(registro1)).thenReturn(saldo1);
        when(processadorRegistroSaldoBatch.process(registro2)).thenReturn(saldo2);

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(
                leitorRegistroArquivoSaldoBatch,
                processadorRegistroSaldoBatch,
                propriedadesBatchSaldo
        );

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(2, saldos.size());
        assertEquals("conta-1", saldos.get(0).conta().idConta());
        assertEquals("titular-1", saldos.get(0).conta().idTitular());
        assertEquals("BRL", saldos.get(0).moeda());
    }
}
