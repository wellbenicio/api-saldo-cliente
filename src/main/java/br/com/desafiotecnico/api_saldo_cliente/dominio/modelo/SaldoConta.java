package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record SaldoConta(
        Conta conta,
        BigDecimal valor,
        String moeda,
        OffsetDateTime atualizadoEm,
        OffsetDateTime dataHoraReferencia,
        Long versaoSaldo
) {

    public SaldoConta {
        Objects.requireNonNull(conta, "conta é obrigatória");
        Objects.requireNonNull(valor, "valor é obrigatório");
        Objects.requireNonNull(moeda, "moeda é obrigatória");
        Objects.requireNonNull(atualizadoEm, "atualizadoEm é obrigatório");
        Objects.requireNonNull(dataHoraReferencia, "dataHoraReferencia é obrigatória");
    }

    public boolean deveSobrescrever(SaldoConta saldoExistente) {
        if (saldoExistente == null) {
            return true;
        }

        if (versaoSaldo != null || saldoExistente.versaoSaldo() != null) {
            long versaoNovo = versaoSaldo == null ? Long.MIN_VALUE : versaoSaldo;
            long versaoAtual = saldoExistente.versaoSaldo() == null ? Long.MIN_VALUE : saldoExistente.versaoSaldo();
            return versaoNovo > versaoAtual;
        }

        return dataHoraReferencia.isAfter(saldoExistente.dataHoraReferencia());
    }
}
