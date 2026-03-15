package br.com.desafiotecnico.api_saldo_cliente.compartilhado.web;

import java.time.OffsetDateTime;

public record ErroApiResposta(
        String codigo,
        String mensagem,
        OffsetDateTime timestamp
) {
}
