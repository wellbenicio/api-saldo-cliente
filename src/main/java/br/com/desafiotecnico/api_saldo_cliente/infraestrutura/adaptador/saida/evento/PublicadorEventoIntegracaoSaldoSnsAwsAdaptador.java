package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.evento;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.PublicadorEventoIntegracaoSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoIntegracaoSaldoAtualizado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador esqueleto para publicação em AWS SNS.
 *
 * <p>Em produção, SNS atua como fanout e os consumidores assinam o tópico via SQS,
 * desacoplando produtores/consumidores e melhorando resiliência operacional.</p>
 */
@Component
@ConditionalOnProperty(name = "saldo.integracao.evento.publicador", havingValue = "sns")
public class PublicadorEventoIntegracaoSaldoSnsAwsAdaptador implements PublicadorEventoIntegracaoSaldoPortaSaida {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicadorEventoIntegracaoSaldoSnsAwsAdaptador.class);

    @Override
    public void publicar(EventoIntegracaoSaldoAtualizado evento) {
        // Esqueleto proposital: não há integração real neste desafio técnico.
        // Em projeto real:
        // 1) usar AWS SDK v2 com SnsClient configurado via IAM Role e região;
        // 2) ler Topic ARN por variável de ambiente/secrets (ex.: SALDO_AWS_SNS_TOPIC_ARN);
        // 3) serializar evento em JSON e publicar com metadados (message attributes);
        // 4) garantir observabilidade (traceId/correlationId) e política de retry;
        // 5) consumidores assinam por SQS para isolamento de falhas e reprocessamento.
        LOGGER.info(
                "Publicador SNS esqueleto acionado. idEventoIntegracao={} idConta={} topicArn={}",
                evento.idEventoIntegracao(),
                evento.idConta(),
                "${SALDO_AWS_SNS_TOPIC_ARN}"
        );
    }
}
