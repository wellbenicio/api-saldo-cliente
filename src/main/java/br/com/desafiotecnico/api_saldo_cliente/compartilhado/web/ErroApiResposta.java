package br.com.desafiotecnico.api_saldo_cliente.compartilhado.web;

import java.time.OffsetDateTime;
import java.util.List;

public record ErroApiResposta(
        String codigo,
        String mensagem,
        OffsetDateTime timestamp,
        List<DetalheErroValidacao> detalhes
) {

    public ErroApiResposta(String codigo, String mensagem, OffsetDateTime timestamp) {
        this(codigo, mensagem, timestamp, List.of());
    }

    public record DetalheErroValidacao(
            String campo,
            String mensagem
    ) {
    }
}
