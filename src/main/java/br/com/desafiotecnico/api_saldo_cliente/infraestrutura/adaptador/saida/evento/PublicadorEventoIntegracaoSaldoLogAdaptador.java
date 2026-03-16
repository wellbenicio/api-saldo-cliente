package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.evento;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.PublicadorEventoIntegracaoSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoIntegracaoSaldoAtualizado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "saldo.integracao.evento.publicador",
        havingValue = "log",
        matchIfMissing = true
)
public class PublicadorEventoIntegracaoSaldoLogAdaptador implements PublicadorEventoIntegracaoSaldoPortaSaida {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicadorEventoIntegracaoSaldoLogAdaptador.class);

    @Override
    public void publicar(EventoIntegracaoSaldoAtualizado evento) {
        LOGGER.info(
                "evento_integracao_saldo_atualizado idEventoIntegracao={} idEventoOrigem={} idConta={} idTitular={} saldoAtual={} moeda={} versaoSaldo={} ocorridoEm={} publicadoEm={} origemAtualizacao={}",
                evento.idEventoIntegracao(),
                evento.idEventoOrigem(),
                evento.idConta(),
                evento.idTitular(),
                evento.saldoAtual(),
                evento.moeda(),
                evento.versaoSaldo(),
                evento.ocorridoEm(),
                evento.publicadoEm(),
                evento.origemAtualizacao()
        );
    }
}
