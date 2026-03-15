package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import java.util.Objects;

public record Conta(String idConta, String idTitular) {

    public Conta {
        Objects.requireNonNull(idConta, "idConta é obrigatório");
        Objects.requireNonNull(idTitular, "idTitular é obrigatório");
    }
}
