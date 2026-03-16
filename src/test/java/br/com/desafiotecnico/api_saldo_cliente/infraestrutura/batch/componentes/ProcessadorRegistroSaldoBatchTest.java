package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessadorRegistroSaldoBatchTest {

    @Test
    void deveMapearRegistroDeArquivoParaSaldoConta() throws Exception {
        ProcessadorRegistroSaldoBatch processador = new ProcessadorRegistroSaldoBatch();
        RegistroArquivoSaldoBatch registro = new RegistroArquivoSaldoBatch(
            "10",
            "20",
            "125.50",
            "BRL",
            "2024-01-15T10:15:30-03:00"
        );

        var saldoConta = processador.process(registro);

        assertEquals("10", saldoConta.conta().idConta());
        assertEquals("20", saldoConta.conta().idTitular());
        assertEquals(new BigDecimal("125.50"), saldoConta.valor());
        assertEquals("BRL", saldoConta.moeda());
        assertEquals(OffsetDateTime.parse("2024-01-15T10:15:30-03:00"), saldoConta.atualizadoEm());
    }
}
