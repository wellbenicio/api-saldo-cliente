package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessadorRegistroSaldoBatchTest {

    @Test
    void deveMapearRegistroArquivoParaSaldoConta() throws Exception {
        ProcessadorRegistroSaldoBatch processador = new ProcessadorRegistroSaldoBatch();
        RegistroArquivoSaldoBatch registro = new RegistroArquivoSaldoBatch(
                "conta-1",
                "titular-1",
                "10.00",
                "BRL",
                "2024-01-15T10:15:30-03:00"
        );

        var saldoConta = processador.process(registro);

        assertEquals("conta-1", saldoConta.conta().idConta());
        assertEquals("titular-1", saldoConta.conta().idTitular());
        assertEquals(new BigDecimal("10.00"), saldoConta.valor());
        assertEquals("BRL", saldoConta.moeda());
        assertEquals(OffsetDateTime.parse("2024-01-15T10:15:30-03:00"), saldoConta.atualizadoEm());
    }
}
