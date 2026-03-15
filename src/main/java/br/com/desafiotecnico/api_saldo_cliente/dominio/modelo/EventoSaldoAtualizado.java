package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record EventoSaldoAtualizado(
        String idConta,
        String idTitular,
        BigDecimal novoValorSaldo,
        OffsetDateTime ocorridoEm
) {

    public EventoSaldoAtualizado {
        Objects.requireNonNull(idConta, "idConta é obrigatório");
        Objects.requireNonNull(idTitular, "idTitular é obrigatório");
        Objects.requireNonNull(novoValorSaldo, "novoValorSaldo é obrigatório");
        Objects.requireNonNull(ocorridoEm, "ocorridoEm é obrigatório");
    }
}
