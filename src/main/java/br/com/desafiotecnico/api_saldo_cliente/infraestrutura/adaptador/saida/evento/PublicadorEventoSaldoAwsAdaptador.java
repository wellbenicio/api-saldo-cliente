package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.evento;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.PublicadorEventoSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoSaldoAtualizado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PublicadorEventoSaldoAwsAdaptador implements PublicadorEventoSaldoPortaSaida {

    private static final Logger logger = LoggerFactory.getLogger(PublicadorEventoSaldoAwsAdaptador.class);

    @Override
    public void publicar(EventoSaldoAtualizado eventoSaldoAtualizado) {
        // Em produção: configurar integração com SNS/SQS, credenciais IAM e endpoint/region por variáveis de ambiente e secret manager.
        logger.info("Publicação simulada de evento de saldo atualizado para conta {}", eventoSaldoAtualizado.idConta());
    }
}
