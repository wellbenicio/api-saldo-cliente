package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class LeitorRegistroArquivoSaldoBatch {

    public FlatFileItemReader<RegistroArquivoSaldoBatch> criarLeitor(String caminhoArquivo, String delimitador) {
        return new FlatFileItemReaderBuilder<RegistroArquivoSaldoBatch>()
            .name("leitorRegistroArquivoSaldoBatch")
            .resource(new FileSystemResource(caminhoArquivo))
            .linesToSkip(1)
            .delimited()
            .delimiter(delimitador)
            .names("idConta", "idTitular", "valor", "moeda", "atualizadoEm")
            .targetType(RegistroArquivoSaldoBatch.class)
            .build();
    }
}
