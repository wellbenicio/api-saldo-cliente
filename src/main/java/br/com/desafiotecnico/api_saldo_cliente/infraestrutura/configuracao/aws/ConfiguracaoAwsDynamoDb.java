package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
@Profile("aws")
public class ConfiguracaoAwsDynamoDb {

    @Bean
    public DynamoDbClient dynamoDbClient(
            @Value("${saldo.persistencia.aws.dynamodb.regiao}") String regiao,
            @Value("${saldo.persistencia.aws.dynamodb.endpoint:}") String endpoint
    ) {
        var builder = DynamoDbClient.builder()
                // Região alvo da tabela DynamoDB (ex.: sa-east-1).
                .region(Region.of(regiao))
                // Credenciais NÃO devem ser hardcoded. O provider busca, em ordem:
                // IAM Role (ECS/EKS/EC2), variáveis de ambiente ou perfis/secret providers.
                .credentialsProvider(DefaultCredentialsProvider.create());

        if (!endpoint.isBlank()) {
            // Endpoint opcional para ambiente de teste/localstack. Em produção, usar endpoint padrão AWS.
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    @Bean
    public ClienteDynamoDbEsqueleto clienteDynamoDbEsqueleto(DynamoDbClient dynamoDbClient) {
        return new ClienteDynamoDbEsqueleto(dynamoDbClient);
    }
}
