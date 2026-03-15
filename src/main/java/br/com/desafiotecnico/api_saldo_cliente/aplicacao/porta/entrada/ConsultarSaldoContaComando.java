package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultarSaldoContaComando(
        @NotBlank(message = "Parâmetro 'idConta' é obrigatório.")
        @Size(min = 5, max = 20, message = "Parâmetro 'idConta' deve ter entre 5 e 20 caracteres.")
        String idConta,

        @NotBlank(message = "Cabeçalho 'X-Id-Titular' é obrigatório.")
        @Size(min = 5, max = 20, message = "Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres.")
        String idTitularSolicitante
) {
}
