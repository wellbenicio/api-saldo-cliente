package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando;

import jakarta.validation.constraints.NotBlank;

public record ConsultarSaldoContaComando(
        @NotBlank(message = "idConta é obrigatório")
        String idConta,
        String idTitularSolicitante
) {
}
