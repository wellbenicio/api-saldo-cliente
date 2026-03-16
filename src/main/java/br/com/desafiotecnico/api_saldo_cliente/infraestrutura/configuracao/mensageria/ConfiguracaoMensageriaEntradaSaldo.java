package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.mensageria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PropriedadesMqJmsSaldo.class)
public class ConfiguracaoMensageriaEntradaSaldo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguracaoMensageriaEntradaSaldo.class);

    @Bean
    public ConexaoMqJmsSimulada conexaoMqJmsSimulada(PropriedadesMqJmsSaldo propriedades) {
        // Em produção este bean criaria uma conexão real IBM MQ/JMS com host, channel,
        // queue manager, usuário/senha e secrets resolvidos de cofre de segredos.
        ConexaoMqJmsSimulada conexao = new ConexaoMqJmsSimulada(propriedades);
        LOGGER.info("Mensageria de entrada configurada em modo simulado: {}", conexao.descricaoConexao());
        return conexao;
    }
}
