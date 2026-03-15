package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoConectoresExternos {

    // Classe reservada para configuração de clientes externos (MQ, NFS, AWS).
    // Em produção, os valores devem ser injetados por variáveis de ambiente,
    // com credenciais em mecanismo seguro (ex.: AWS Secrets Manager).
}
