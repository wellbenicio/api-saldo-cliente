package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class EscritorSaldoContaBatch implements ItemWriter<SaldoConta> {

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;

    public EscritorSaldoContaBatch(RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida) {
        this.repositorioSaldoContaPortaSaida = repositorioSaldoContaPortaSaida;
    }

    @Override
    public void write(Chunk<? extends SaldoConta> chunk) {
        chunk.getItems().forEach(repositorioSaldoContaPortaSaida::salvar);
    }
}
