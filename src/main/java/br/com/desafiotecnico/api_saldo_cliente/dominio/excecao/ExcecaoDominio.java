package br.com.desafiotecnico.api_saldo_cliente.dominio.excecao;

public class ExcecaoDominio extends RuntimeException {

    public ExcecaoDominio(String mensagem) {
        super(mensagem);
    }
}
