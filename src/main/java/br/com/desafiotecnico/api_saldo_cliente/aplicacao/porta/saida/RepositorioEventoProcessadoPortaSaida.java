package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

public interface RepositorioEventoProcessadoPortaSaida {

    boolean jaProcessado(String idEvento);

    void registrarProcessamento(String idEvento, String origem);
}
