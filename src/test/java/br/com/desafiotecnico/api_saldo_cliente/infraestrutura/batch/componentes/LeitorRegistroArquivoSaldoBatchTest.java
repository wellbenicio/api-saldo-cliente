package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeitorRegistroArquivoSaldoBatchTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    void deveLerArquivoDelimitadoNoFormatoOficial() throws Exception {
        Path arquivoEntrada = diretorioTemporario.resolve("saldos.csv");
        Files.writeString(arquivoEntrada, String.join("\n",
                "idConta|idTitular|valor|moeda|atualizadoEm",
                "conta-1|titular-1|10.00|BRL|2024-01-15T10:15:30-03:00"
        ));

        LeitorRegistroArquivoSaldoBatch leitorRegistroArquivoSaldoBatch = new LeitorRegistroArquivoSaldoBatch();
        var itemReader = leitorRegistroArquivoSaldoBatch.criarLeitor(arquivoEntrada.toString(), "|");

        itemReader.open(new ExecutionContext());
        RegistroArquivoSaldoBatch registro = itemReader.read();
        RegistroArquivoSaldoBatch fimArquivo = itemReader.read();
        itemReader.close();

        assertEquals("conta-1", registro.idConta());
        assertEquals("titular-1", registro.idTitular());
        assertEquals("10.00", registro.valor());
        assertNull(fimArquivo);
    }
}
