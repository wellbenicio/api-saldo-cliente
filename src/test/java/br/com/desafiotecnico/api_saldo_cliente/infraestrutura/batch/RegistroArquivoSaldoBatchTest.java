package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo.RegistroArquivoSaldoBatch;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistroArquivoSaldoBatchTest {

    @Test
    void deveConverterCamposTextoParaTipos() {
        RegistroArquivoSaldoBatch registro = RegistroArquivoSaldoBatch.deCamposTexto(
                "10",
                "20",
                "125.50",
                "BRL",
                "2024-01-15T10:15:30-03:00",
                "3"
        );

        assertEquals(10L, registro.idConta());
        assertEquals(20L, registro.idTitular());
        assertEquals(new BigDecimal("125.50"), registro.valor());
        assertEquals("BRL", registro.moeda());
        assertEquals(3, registro.versaoSaldo());
    }

    @Test
    void deveFalharQuandoIdContaForInvalido() {
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> new RegistroArquivoSaldoBatch(0L, 20L, new BigDecimal("1.00"), "BRL", java.time.OffsetDateTime.now(), 1));

        assertEquals("idConta deve ser maior que zero", excecao.getMessage());
    }
}
