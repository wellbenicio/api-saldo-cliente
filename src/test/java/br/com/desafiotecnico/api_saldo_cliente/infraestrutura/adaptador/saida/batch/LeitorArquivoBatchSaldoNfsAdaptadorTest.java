package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.PropriedadesBatchSaldo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeitorArquivoBatchSaldoNfsAdaptadorTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    void deveRetornarVazioQuandoArquivoNaoExiste() {
        PropriedadesBatchSaldo propriedades = new PropriedadesBatchSaldo();
        propriedades.setDiretorioEntrada(diretorioTemporario);
        propriedades.setNomeArquivo("nao-existe.csv");

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(propriedades);

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(0, saldos.size());
    }

    @Test
    void deveLerArquivoComFormatoEsperado() throws Exception {
        Path arquivo = diretorioTemporario.resolve("saldos.csv");
        Files.writeString(arquivo,
                "conta-1;titular-1;100.50;BRL;2026-01-01T10:15:30Z\n" +
                        "conta-2;titular-2;20.00;USD;2026-01-01T11:00:00Z\n");

        PropriedadesBatchSaldo propriedades = new PropriedadesBatchSaldo();
        propriedades.setDiretorioEntrada(diretorioTemporario);
        propriedades.setNomeArquivo("saldos.csv");

        LeitorArquivoBatchSaldoNfsAdaptador adaptador = new LeitorArquivoBatchSaldoNfsAdaptador(propriedades);

        List<SaldoConta> saldos = adaptador.lerSaldosConsolidados().toList();

        assertEquals(2, saldos.size());
        assertEquals("conta-1", saldos.get(0).conta().idConta());
        assertEquals("titular-1", saldos.get(0).conta().idTitular());
        assertEquals("BRL", saldos.get(0).moeda());
    }
}
