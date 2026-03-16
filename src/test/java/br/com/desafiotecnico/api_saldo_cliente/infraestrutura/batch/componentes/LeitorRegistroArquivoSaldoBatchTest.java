package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeitorRegistroArquivoSaldoBatchTest {

    @Test
    void deveLerLinhasDoArquivoConsolidadoComFlatFileItemReader() throws Exception {
        Path arquivo = Files.createTempFile("saldos-consolidados", ".csv");
        Files.writeString(
            arquivo,
            "idConta|idTitular|valor|moeda|atualizadoEm\n1|2|10.50|BRL|2024-01-15T10:15:30Z\n",
            StandardCharsets.UTF_8
        );

        LeitorRegistroArquivoSaldoBatch componente = new LeitorRegistroArquivoSaldoBatch();
        var leitor = componente.criarLeitor(arquivo.toString(), "|");
        leitor.open(new ExecutionContext());

        RegistroArquivoSaldoBatch primeiroRegistro = leitor.read();
        RegistroArquivoSaldoBatch fimArquivo = leitor.read();

        assertEquals("1", primeiroRegistro.idConta());
        assertEquals("2", primeiroRegistro.idTitular());
        assertEquals("10.50", primeiroRegistro.valor());
        assertEquals("BRL", primeiroRegistro.moeda());
        assertEquals("2024-01-15T10:15:30Z", primeiroRegistro.atualizadoEm());
        assertNull(fimArquivo);

        leitor.close();
    }
}
