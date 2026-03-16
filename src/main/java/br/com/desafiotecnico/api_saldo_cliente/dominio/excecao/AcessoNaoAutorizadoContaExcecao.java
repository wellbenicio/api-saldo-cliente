package br.com.desafiotecnico.api_saldo_cliente.dominio.excecao;

public class AcessoNaoAutorizadoContaExcecao extends ExcecaoDominio {

    public AcessoNaoAutorizadoContaExcecao(String idConta, String idTitular) {
        super("Acesso não autorizado para titular " + idTitular + " na conta " + idConta);
    }
}
