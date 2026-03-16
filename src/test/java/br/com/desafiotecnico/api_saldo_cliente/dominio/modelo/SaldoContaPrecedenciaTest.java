package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaldoContaPrecedenciaTest {

    @Test
    void deveSobrescreverQuandoVersaoNovaForMaior() {
        SaldoConta saldoExistente = saldo("1", "2026-01-01T10:00:00Z", 2L, "100.00");
        SaldoConta saldoNovo = saldo("1", "2025-12-31T10:00:00Z", 3L, "110.00");

        assertTrue(saldoNovo.deveSobrescrever(saldoExistente));
    }

    @Test
    void naoDeveSobrescreverQuandoVersaoNovaForMenor() {
        SaldoConta saldoExistente = saldo("1", "2026-01-01T10:00:00Z", 3L, "100.00");
        SaldoConta saldoNovo = saldo("1", "2026-01-02T10:00:00Z", 2L, "110.00");

        assertFalse(saldoNovo.deveSobrescrever(saldoExistente));
    }

    @Test
    void deveSobrescreverQuandoDataReferenciaForMaisRecenteNaAusenciaDeVersao() {
        SaldoConta saldoExistente = saldo("1", "2026-01-01T10:00:00Z", null, "100.00");
        SaldoConta saldoNovo = saldo("1", "2026-01-01T11:00:00Z", null, "110.00");

        assertTrue(saldoNovo.deveSobrescrever(saldoExistente));
    }

    @Test
    void naoDeveSobrescreverEmEmpateDeVersaoOuReferencia() {
        SaldoConta saldoExistente = saldo("1", "2026-01-01T10:00:00Z", 2L, "100.00");
        SaldoConta saldoNovoMesmaVersao = saldo("1", "2026-01-01T11:00:00Z", 2L, "110.00");

        SaldoConta saldoSemVersaoExistente = saldo("2", "2026-01-01T10:00:00Z", null, "100.00");
        SaldoConta saldoSemVersaoMesmoHorario = saldo("2", "2026-01-01T10:00:00Z", null, "110.00");

        assertFalse(saldoNovoMesmaVersao.deveSobrescrever(saldoExistente));
        assertFalse(saldoSemVersaoMesmoHorario.deveSobrescrever(saldoSemVersaoExistente));
    }

    private SaldoConta saldo(String idConta, String dataHoraReferencia, Long versaoSaldo, String valor) {
        return new SaldoConta(
                new Conta(idConta, "titular-001"),
                new BigDecimal(valor),
                "BRL",
                OffsetDateTime.parse("2026-01-02T00:00:00Z"),
                OffsetDateTime.parse(dataHoraReferencia),
                versaoSaldo
        );
    }
}
