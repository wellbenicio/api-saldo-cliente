package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.LeitorArquivoBatchSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.LeitorRegistroArquivoSaldoBatchItemReader;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.ProcessadorRegistroArquivoSaldoBatchItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class LeitorArquivoBatchSaldoNfsAdaptador implements LeitorArquivoBatchSaldoPortaSaida {

    private final LeitorRegistroArquivoSaldoBatchItemReader itemReader;
    private final ProcessadorRegistroArquivoSaldoBatchItemProcessor itemProcessor;

    public LeitorArquivoBatchSaldoNfsAdaptador(
            LeitorRegistroArquivoSaldoBatchItemReader itemReader,
            ProcessadorRegistroArquivoSaldoBatchItemProcessor itemProcessor
    ) {
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
    }

    @Override
    public Stream<SaldoConta> lerSaldosConsolidados() {
        // Em produção: configurar endpoint NFS, caminho de arquivo e credenciais via variáveis de ambiente e secret manager.
        List<SaldoConta> saldos = new ArrayList<>();

        try {
            while (true) {
                var registro = itemReader.read();
                if (registro == null) {
                    break;
                }
                var saldo = itemProcessor.process(registro);
                if (saldo != null) {
                    saldos.add(saldo);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar arquivo de saldo batch", e);
        }

        return saldos.stream();
    }
}
