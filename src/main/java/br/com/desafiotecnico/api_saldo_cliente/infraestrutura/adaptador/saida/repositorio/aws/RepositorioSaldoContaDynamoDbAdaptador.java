package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.aws;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.aws.ClienteDynamoDbEsqueleto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("aws-exemplo")
public class RepositorioSaldoContaDynamoDbAdaptador implements RepositorioSaldoContaPortaSaida {

    private final ClienteDynamoDbEsqueleto clienteDynamoDbEsqueleto;
    private final String nomeTabela;

    public RepositorioSaldoContaDynamoDbAdaptador(
            ClienteDynamoDbEsqueleto clienteDynamoDbEsqueleto,
            @Value("${saldo.persistencia.aws.dynamodb.tabela-saldo-consolidado}") String nomeTabela
    ) {
        this.clienteDynamoDbEsqueleto = clienteDynamoDbEsqueleto;
        this.nomeTabela = nomeTabela;
    }

    @Override
    public Optional<SaldoConta> buscarPorIdConta(String idConta) {
        // Esqueleto profissional: integração real ficará para fase de conexão AWS.
        // Tabela DynamoDB esperada: saldo_consolidado.
        // Região e endpoint vêm da ConfiguracaoAwsDynamoDb via propriedades por profile aws-exemplo.
        // Credenciais devem ser providas por IAM Role, secrets ou variáveis de ambiente.
        clienteDynamoDbEsqueleto.cliente();
        return Optional.empty();
    }

    @Override
    public SaldoConta salvar(SaldoConta saldoConta) {
        // Esqueleto intencional sem integração real neste desafio.
        throw new UnsupportedOperationException(
                "Persistência real no DynamoDB não implementada neste contexto de avaliação. Tabela alvo: " + nomeTabela
        );
    }
}
