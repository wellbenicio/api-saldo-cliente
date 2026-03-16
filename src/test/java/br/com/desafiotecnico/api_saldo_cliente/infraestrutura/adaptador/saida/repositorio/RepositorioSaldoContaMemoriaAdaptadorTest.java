package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositorioSaldoContaMemoriaAdaptadorTest {

    @Test
    void naoDeveSobrescreverSaldoMaisNovoComSaldoMaisAntigo() {
        RepositorioSaldoContaMemoriaAdaptador repositorio = new RepositorioSaldoContaMemoriaAdaptador();

        SaldoConta saldoMaisNovo = new SaldoConta(
                new Conta("conta-1", "titular-1"),
                new BigDecimal("500.00"),
                "BRL",
                OffsetDateTime.parse("2026-01-01T10:05:00Z"),
                OffsetDateTime.parse("2026-01-01T10:05:00Z"),
                null
        );

        SaldoConta saldoMaisAntigo = new SaldoConta(
                new Conta("conta-1", "titular-1"),
                new BigDecimal("100.00"),
                "BRL",
                OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                null
        );

        repositorio.salvar(saldoMaisNovo);
        SaldoConta saldoPersistido = repositorio.salvar(saldoMaisAntigo);

        assertEquals(saldoMaisNovo, saldoPersistido);
    }
}
