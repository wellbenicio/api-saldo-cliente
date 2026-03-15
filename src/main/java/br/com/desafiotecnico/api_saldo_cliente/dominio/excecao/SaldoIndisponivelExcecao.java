package br.com.desafiotecnico.api_saldo_cliente.dominio.excecao;

public class SaldoIndisponivelExcecao extends ExcecaoDominio {

    public SaldoIndisponivelExcecao(String idConta) {
        super("Saldo indisponível para conta: " + idConta);
    }
}
