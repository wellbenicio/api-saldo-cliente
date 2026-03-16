package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import java.security.Principal;

public record PrincipalConta(String idTitular) implements Principal {

    @Override
    public String getName() {
        return idTitular;
    }
}
