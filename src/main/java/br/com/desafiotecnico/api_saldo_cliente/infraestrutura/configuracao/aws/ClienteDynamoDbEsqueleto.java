package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.aws;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class ClienteDynamoDbEsqueleto {

    private final DynamoDbClient dynamoDbClient;

    public ClienteDynamoDbEsqueleto(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public DynamoDbClient cliente() {
        return dynamoDbClient;
    }
}
