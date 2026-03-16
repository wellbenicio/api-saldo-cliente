package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.LeitorArquivoBatchSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.LeitorRegistroArquivoSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.ProcessadorRegistroSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.PropriedadesBatchSaldo;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class LeitorArquivoBatchSaldoNfsAdaptador implements LeitorArquivoBatchSaldoPortaSaida {

    private final LeitorRegistroArquivoSaldoBatch leitorRegistroArquivoSaldoBatch;
    private final ProcessadorRegistroSaldoBatch processadorRegistroSaldoBatch;
    private final PropriedadesBatchSaldo propriedadesBatchSaldo;

    public LeitorArquivoBatchSaldoNfsAdaptador(
            LeitorRegistroArquivoSaldoBatch leitorRegistroArquivoSaldoBatch,
            ProcessadorRegistroSaldoBatch processadorRegistroSaldoBatch,
            PropriedadesBatchSaldo propriedadesBatchSaldo
    ) {
        this.leitorRegistroArquivoSaldoBatch = leitorRegistroArquivoSaldoBatch;
        this.processadorRegistroSaldoBatch = processadorRegistroSaldoBatch;
        this.propriedadesBatchSaldo = propriedadesBatchSaldo;
    }

    @Override
    public Stream<SaldoConta> lerSaldosConsolidados() {
        var itemReader = leitorRegistroArquivoSaldoBatch.criarLeitor(
                propriedadesBatchSaldo.caminhoArquivoEntrada().toString(),
                propriedadesBatchSaldo.getDelimitador()
        );
        List<SaldoConta> saldos = new ArrayList<>();

        try {
            itemReader.open(new ExecutionContext());
            while (true) {
                var registro = itemReader.read();
                if (registro == null) {
                    break;
                }
                var saldo = processadorRegistroSaldoBatch.process(registro);
                if (saldo != null) {
                    saldos.add(saldo);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao processar arquivo de saldo batch", e);
        } finally {
            itemReader.close();
        }

        return saldos.stream();
    }
}
