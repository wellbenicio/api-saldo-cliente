package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class ProcessadorRegistroSaldoBatch implements ItemProcessor<RegistroArquivoSaldoBatch, SaldoConta> {

    @Override
    public SaldoConta process(RegistroArquivoSaldoBatch item) {
        return new SaldoConta(
            new Conta(item.idConta(), item.idTitular()),
            new BigDecimal(item.valor()),
            item.moeda(),
            OffsetDateTime.parse(item.atualizadoEm())
        );
    }
}
