package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import java.security.Principal;
import java.util.Set;

public record UsuarioAutenticado(
        String idCliente,
        String documento,
        Set<String> perfisScopes
) implements Principal {

    @Override
    public String getName() {
        return idCliente;
    }
}

