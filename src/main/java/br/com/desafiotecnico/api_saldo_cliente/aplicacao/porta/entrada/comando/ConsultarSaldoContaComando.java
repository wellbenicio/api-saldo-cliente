package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultarSaldoContaComando(
        @NotBlank(message = "Parâmetro 'idConta' é obrigatório.")
        @Size(min = 5, max = 20, message = "Parâmetro 'idConta' deve ter entre 5 e 20 caracteres.")
        String idConta,

        @NotBlank(message = "Identificador do titular autenticado é obrigatório.")
        @Size(min = 5, max = 20, message = "Identificador do titular autenticado deve ter entre 5 e 20 caracteres.")
        String idTitularSolicitante
) {
}
