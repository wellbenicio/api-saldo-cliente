package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

public class TokenJwtInvalidoExcecao extends RuntimeException {

    public TokenJwtInvalidoExcecao(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
