package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.mensageria;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saldo.integracao.mq")
public class PropriedadesMqJmsSaldo {

    /**
     * Host do broker MQ/JMS em ambiente real (ex.: endpoint IBM MQ).
     */
    private String host;

    /**
     * Porta de conexão do broker.
     */
    private Integer porta;

    /**
     * Channel usado na conexão do cliente com IBM MQ.
     */
    private String channel;

    /**
     * Queue manager responsável pela fila de eventos de saldo.
     */
    private String queueManager;

    /**
     * Nome da fila de atualização de saldo.
     */
    private String filaAtualizacao;

    /**
     * Credencial de usuário técnico para autenticação no broker.
     */
    private String usuario;

    /**
     * Senha técnica. Em produção deve vir de secret manager, nunca hardcoded.
     */
    private String senha;

    /**
     * Referência de secret externo (ex.: AWS Secrets Manager, Vault, etc).
     */
    private String segredoReferencia;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public String getFilaAtualizacao() {
        return filaAtualizacao;
    }

    public void setFilaAtualizacao(String filaAtualizacao) {
        this.filaAtualizacao = filaAtualizacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSegredoReferencia() {
        return segredoReferencia;
    }

    public void setSegredoReferencia(String segredoReferencia) {
        this.segredoReferencia = segredoReferencia;
    }
}
