package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultarSaldoContaRequisicao(
        @NotBlank(message = "Parâmetro 'idConta' é obrigatório.")
        @Size(min = 5, max = 20, message = "Parâmetro 'idConta' deve ter entre 5 e 20 caracteres.")
        String idConta
) {
}
