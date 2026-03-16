package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SemReferenciasTipoLegadoTest {

    private static final String TIPO_LEGADO = "ConsultarSaldoConta" + "Requisicao";

    @Test
    void naoDeveExistirReferenciaAoTipoLegadoEmCodigoOuDocumentacao() throws IOException {
        List<Path> diretoriosParaValidar = List.of(
                Path.of("src/main/java"),
                Path.of("src/test/java"),
                Path.of("docs")
        );

        for (Path diretorio : diretoriosParaValidar) {
            validarSemTipoLegadoNoDiretorio(diretorio);
        }

        validarArquivoSemTipoLegado(Path.of("README.md"));
    }

    private void validarSemTipoLegadoNoDiretorio(Path diretorio) throws IOException {
        if (!Files.exists(diretorio)) {
            return;
        }

        try (Stream<Path> caminhos = Files.walk(diretorio)) {
            List<Path> referenciasLegadas = caminhos
                    .filter(Files::isRegularFile)
                    .filter(this::arquivoTextual)
                    .filter(arquivo -> !arquivo.equals(Path.of("src/test/java/br/com/desafiotecnico/api_saldo_cliente/infraestrutura/adaptador/entrada/http/SemReferenciasTipoLegadoTest.java")))
                    .filter(this::contemTipoLegado)
                    .toList();

            assertTrue(
                    referenciasLegadas.isEmpty(),
                    () -> "Foram encontradas referências legadas em: " + referenciasLegadas
            );
        }
    }

    private void validarArquivoSemTipoLegado(Path arquivo) throws IOException {
        if (!Files.exists(arquivo)) {
            return;
        }

        assertTrue(
                !contemTipoLegado(arquivo),
                () -> "Foi encontrada referência legada em: " + arquivo
        );
    }

    private boolean arquivoTextual(Path arquivo) {
        String nome = arquivo.getFileName().toString();
        return nome.endsWith(".java") || nome.endsWith(".md") || nome.endsWith(".adoc") || nome.endsWith(".txt");
    }

    private boolean contemTipoLegado(Path arquivo) {
        try {
            String conteudo = Files.readString(arquivo, StandardCharsets.UTF_8);
            return conteudo.contains(TIPO_LEGADO);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + arquivo, e);
        }
    }
}
