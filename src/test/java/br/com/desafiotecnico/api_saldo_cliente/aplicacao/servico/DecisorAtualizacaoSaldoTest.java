package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisorAtualizacaoSaldoTest {

    private final DecisorAtualizacaoSaldo decisorAtualizacaoSaldo = new DecisorAtualizacaoSaldo();

    @Test
    void deveAtualizarQuandoNovoSaldoPossuirVersaoMaior() {
        boolean deveAtualizar = decisorAtualizacaoSaldo.deveAtualizar(
                3L,
                4L,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T09:00:00Z")
        );

        assertTrue(deveAtualizar);
    }

    @Test
    void naoDeveAtualizarQuandoNovoSaldoPossuirVersaoMenor() {
        boolean deveAtualizar = decisorAtualizacaoSaldo.deveAtualizar(
                5L,
                4L,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T11:00:00Z")
        );

        assertFalse(deveAtualizar);
    }

    @Test
    void semVersaoDeveAtualizarQuandoTimestampNovoForMaisRecente() {
        boolean deveAtualizar = decisorAtualizacaoSaldo.deveAtualizar(
                null,
                null,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T10:00:01Z")
        );

        assertTrue(deveAtualizar);
    }

    @Test
    void semVersaoNaoDeveAtualizarQuandoTimestampNovoForMaisAntigo() {
        boolean deveAtualizar = decisorAtualizacaoSaldo.deveAtualizar(
                null,
                null,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T09:59:59Z")
        );

        assertFalse(deveAtualizar);
    }

    @Test
    void empateDeVersaoOuTimestampNaoDeveAtualizarSeguindoPoliticaAtual() {
        boolean empateVersao = decisorAtualizacaoSaldo.deveAtualizar(
                7L,
                7L,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T11:00:00Z")
        );

        boolean empateTimestamp = decisorAtualizacaoSaldo.deveAtualizar(
                null,
                null,
                OffsetDateTime.parse("2026-01-10T10:00:00Z"),
                OffsetDateTime.parse("2026-01-10T10:00:00Z")
        );

        assertFalse(empateVersao);
        assertFalse(empateTimestamp);
    }
}
