package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo.RegistroArquivoSaldoBatch;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeitorRegistroArquivoSaldoBatchItemReader implements ItemReader<RegistroArquivoSaldoBatch> {

    private final List<String[]> linhasArquivo;
    private int indice;

    public LeitorRegistroArquivoSaldoBatchItemReader() {
        // Em produção, essas linhas devem vir de um parser de arquivo em NFS/S3.
        this(List.of());
    }

    LeitorRegistroArquivoSaldoBatchItemReader(List<String[]> linhasArquivo) {
        this.linhasArquivo = linhasArquivo;
        this.indice = 0;
    }

    @Override
    public RegistroArquivoSaldoBatch read() {
        if (indice >= linhasArquivo.size()) {
            return null;
        }

        String[] colunas = linhasArquivo.get(indice++);
        return RegistroArquivoSaldoBatch.deCamposTexto(
                colunas[0],
                colunas[1],
                colunas[2],
                colunas[3],
                colunas[4],
                colunas[5]
        );
    }
}
