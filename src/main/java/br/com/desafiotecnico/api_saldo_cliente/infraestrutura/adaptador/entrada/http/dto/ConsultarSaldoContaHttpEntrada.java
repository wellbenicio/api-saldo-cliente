package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto;

import jakarta.validation.constraints.NotBlank;

public record ConsultarSaldoContaHttpEntrada(
        @NotBlank(message = "idConta é obrigatório")
        String idConta,
        String idTitularSolicitante
) {
}
