package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "saldo.batch")
public class PropriedadesBatchSaldo {

    /**
     * Diretório local onde o arquivo de batch é lido.
     *
     * Em produção, este diretório apontaria para um mount NFS/EFS
     * (ou volume equivalente) provisionado pela infraestrutura.
     */
    private Path diretorioEntrada = Path.of("./data");

    /**
     * Nome do arquivo de saldo consolidado no diretório de entrada.
     */
    private String nomeArquivo = "saldo-consolidado.csv";

    public Path getDiretorioEntrada() {
        return diretorioEntrada;
    }

    public void setDiretorioEntrada(Path diretorioEntrada) {
        this.diretorioEntrada = diretorioEntrada;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Path caminhoArquivoEntrada() {
        return diretorioEntrada.resolve(nomeArquivo);
    }
}
