package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.mensageria;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsumirEventoSaldoAtualizadoPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsumirEventoSaldoAtualizadoComando;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.mensageria.ConexaoMqJmsSimulada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada que simula um listener MQ/JMS.
 *
 * Em projeto real este componente seria acionado por listener JMS (ex.: @JmsListener)
 * ou biblioteca específica do IBM MQ, sem acoplamento com o controlador HTTP.
 */
@Component
public class ConsumidorSaldoMqJmsSimuladoAdaptador {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumidorSaldoMqJmsSimuladoAdaptador.class);

    private final ConsumirEventoSaldoAtualizadoPortaEntrada consumirEventoSaldoAtualizadoPortaEntrada;

    public ConsumidorSaldoMqJmsSimuladoAdaptador(
            ConexaoMqJmsSimulada conexaoMqJmsSimulada,
            ConsumirEventoSaldoAtualizadoPortaEntrada consumirEventoSaldoAtualizadoPortaEntrada
    ) {
        this.consumirEventoSaldoAtualizadoPortaEntrada = consumirEventoSaldoAtualizadoPortaEntrada;
        LOGGER.info("Consumidor de saldo iniciado em modo simulado. Conexão conceitual: {}", conexaoMqJmsSimulada.descricaoConexao());
    }

    public void simularRecebimento(ConsumirEventoSaldoAtualizadoComando comando) {
        // Fluxo quase em tempo real: mensagem recebida -> porta de entrada da aplicação ->
        // regras de idempotência/ordenação -> atualização de saldo no repositório.
        consumirEventoSaldoAtualizadoPortaEntrada.consumir(comando);
    }
}
