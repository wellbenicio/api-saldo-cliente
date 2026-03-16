package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrincipalContaTest {

    @Test
    void getNameDeveRetornarIdCliente() {
        PrincipalConta principalConta = new PrincipalConta("cliente-123", "12345678901", Set.of("saldo:read"));

        assertEquals("cliente-123", principalConta.getName());
    }
}
