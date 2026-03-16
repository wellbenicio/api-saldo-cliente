package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo.RegistroArquivoSaldoBatch;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessadorRegistroArquivoSaldoBatchItemProcessorTest {

    @Test
    void deveMapearRegistroArquivoParaSaldoConta() throws Exception {
        ProcessadorRegistroArquivoSaldoBatchItemProcessor processador = new ProcessadorRegistroArquivoSaldoBatchItemProcessor();
        OffsetDateTime referencia = OffsetDateTime.parse("2024-01-15T10:15:30-03:00");
        RegistroArquivoSaldoBatch registro = new RegistroArquivoSaldoBatch(1L, 2L, new BigDecimal("10.00"), "BRL", referencia, 0);

        var saldoConta = processador.process(registro);

        assertEquals("1", saldoConta.conta().idConta());
        assertEquals("2", saldoConta.conta().idTitular());
        assertEquals(new BigDecimal("10.00"), saldoConta.valor());
        assertEquals("BRL", saldoConta.moeda());
        assertEquals(referencia, saldoConta.atualizadoEm());
    }
}
