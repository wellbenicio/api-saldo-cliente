package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.LeitorArquivoBatchSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class LeitorArquivoBatchSaldoNfsAdaptador implements LeitorArquivoBatchSaldoPortaSaida {

    @Override
    public Stream<SaldoConta> lerSaldosConsolidados() {
        // Em produção: configurar endpoint NFS, caminho de arquivo e credenciais via variáveis de ambiente e secret manager.
        // Neste estágio: retornamos stream vazio para manter o escopo sem regras complexas.
        return Stream.empty();
    }
}
