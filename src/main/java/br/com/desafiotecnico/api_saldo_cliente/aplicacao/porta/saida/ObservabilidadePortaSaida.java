package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

public interface ObservabilidadePortaSaida {

    void incrementarConsultasSaldo();

    void incrementarNegacoesAcesso();

    void incrementarFalhasBatch();

    void incrementarFalhasProcessamentoEvento();
}

