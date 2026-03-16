package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import java.security.Principal;
import java.util.Set;

public record PrincipalConta(String idCliente, String documento, Set<String> perfisOuScopes) implements Principal {

    @Override
    public String getName() {
        return idCliente;
    }
}
