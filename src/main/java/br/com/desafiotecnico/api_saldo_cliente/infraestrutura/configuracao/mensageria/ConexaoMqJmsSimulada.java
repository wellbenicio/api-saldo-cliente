package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.mensageria;

/**
 * Classe conceitual de conexão MQ/JMS.
 *
 * Neste desafio não existe conexão real com IBM MQ.
 * Em projeto real esta classe encapsularia ConnectionFactory/JMSContext e
 * faria bootstrap com TLS, credenciais e leitura de secrets.
 */
public class ConexaoMqJmsSimulada {

    private final PropriedadesMqJmsSaldo propriedades;

    public ConexaoMqJmsSimulada(PropriedadesMqJmsSaldo propriedades) {
        this.propriedades = propriedades;
    }

    public String descricaoConexao() {
        return "host=" + propriedades.getHost()
                + ", porta=" + propriedades.getPorta()
                + ", channel=" + propriedades.getChannel()
                + ", queueManager=" + propriedades.getQueueManager()
                + ", fila=" + propriedades.getFilaAtualizacao();
    }
}
