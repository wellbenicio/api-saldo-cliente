package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record ConsumirEventoSaldoAtualizadoComando(
        String idEvento,
        String idConta,
        String idTitular,
        BigDecimal novoValorSaldo,
        OffsetDateTime ocorridoEm,
        Long versaoSaldo,
        String origem
) {

    public ConsumirEventoSaldoAtualizadoComando {
        Objects.requireNonNull(idEvento, "idEvento é obrigatório");
        Objects.requireNonNull(idConta, "idConta é obrigatório");
        Objects.requireNonNull(idTitular, "idTitular é obrigatório");
        Objects.requireNonNull(novoValorSaldo, "novoValorSaldo é obrigatório");
        Objects.requireNonNull(ocorridoEm, "ocorridoEm é obrigatório");
        if (origem == null || origem.isBlank()) {
            origem = "MQ_JMS_SIMULADO";
        }
    }
}
