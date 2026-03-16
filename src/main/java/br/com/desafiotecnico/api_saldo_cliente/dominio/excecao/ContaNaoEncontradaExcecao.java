package br.com.desafiotecnico.api_saldo_cliente.dominio.excecao;

public class ContaNaoEncontradaExcecao extends ExcecaoDominio {

    public ContaNaoEncontradaExcecao(String idConta) {
        super("Conta não encontrada para id: " + idConta);
    }
}
