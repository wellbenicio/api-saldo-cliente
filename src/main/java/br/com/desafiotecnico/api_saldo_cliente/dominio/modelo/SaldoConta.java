package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record SaldoConta(Conta conta, BigDecimal valor, OffsetDateTime atualizadoEm) {

    public SaldoConta {
        Objects.requireNonNull(conta, "conta é obrigatória");
        Objects.requireNonNull(valor, "valor é obrigatório");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm é obrigatório");
    }
}
