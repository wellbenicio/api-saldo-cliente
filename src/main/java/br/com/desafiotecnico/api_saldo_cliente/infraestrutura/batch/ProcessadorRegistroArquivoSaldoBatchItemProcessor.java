package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo.RegistroArquivoSaldoBatch;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorRegistroArquivoSaldoBatchItemProcessor implements ItemProcessor<RegistroArquivoSaldoBatch, SaldoConta> {

    @Override
    public SaldoConta process(RegistroArquivoSaldoBatch item) {
        return new SaldoConta(
                new Conta(item.idConta().toString(), item.idTitular().toString()),
                item.valor(),
                item.moeda(),
                item.dataHoraReferencia(),
                item.dataHoraReferencia(),
                null
        );
    }
}
