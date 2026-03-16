package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeitorRegistroArquivoSaldoBatchItemReaderTest {

    @Test
    void deveLerRegistrosConvertendoCampos() {
        LeitorRegistroArquivoSaldoBatchItemReader reader = new LeitorRegistroArquivoSaldoBatchItemReader(Collections.singletonList(
                new String[]{"1", "2", "10.00", "BRL", "2024-01-15T10:15:30-03:00", "5"}
        ));

        var registro = reader.read();

        assertEquals(1L, registro.idConta());
        assertEquals(2L, registro.idTitular());
        assertEquals(5, registro.versaoSaldo());
        assertNull(reader.read());
    }
}
