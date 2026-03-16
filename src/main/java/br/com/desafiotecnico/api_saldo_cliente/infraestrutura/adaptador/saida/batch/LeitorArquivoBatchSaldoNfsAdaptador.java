package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.batch;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.LeitorArquivoBatchSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.configuracao.PropriedadesBatchSaldo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Component
public class LeitorArquivoBatchSaldoNfsAdaptador implements LeitorArquivoBatchSaldoPortaSaida {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final String SEPARADOR = ";";

    private final PropriedadesBatchSaldo propriedadesBatchSaldo;

    public LeitorArquivoBatchSaldoNfsAdaptador(PropriedadesBatchSaldo propriedadesBatchSaldo) {
        this.propriedadesBatchSaldo = propriedadesBatchSaldo;
    }

    @Override
    public Stream<SaldoConta> lerSaldosConsolidados() {
        final Path caminhoArquivo = propriedadesBatchSaldo.caminhoArquivoEntrada();

        // Em produção, o diretório configurado apontaria para um mount NFS/EFS/volume equivalente.
        // Credenciais, secrets e endpoint de armazenamento devem ser externalizados
        // (IAM/secret manager/variáveis de ambiente), nunca hardcoded no código.
        if (!Files.exists(caminhoArquivo)) {
            return Stream.empty();
        }

        try {
            return Files.lines(caminhoArquivo)
                    .map(String::trim)
                    .filter(linha -> !linha.isBlank())
                    .map(this::mapearLinhaParaSaldoConta);
        } catch (IOException excecao) {
            throw new IllegalStateException("Falha ao ler arquivo de batch de saldo em " + caminhoArquivo, excecao);
        }
    }

    private SaldoConta mapearLinhaParaSaldoConta(String linha) {
        String[] colunas = linha.split(SEPARADOR);

        if (colunas.length != 5) {
            throw new IllegalStateException("Linha de batch inválida. Formato esperado: idConta;idTitular;valor;moeda;atualizadoEm");
        }

        String idConta = colunas[0].trim();
        String idTitular = colunas[1].trim();
        BigDecimal valor = new BigDecimal(colunas[2].trim());
        String moeda = colunas[3].trim();
        OffsetDateTime atualizadoEm = OffsetDateTime.parse(colunas[4].trim(), FORMATADOR_DATA);

        return new SaldoConta(new Conta(idConta, idTitular), valor, moeda, atualizadoEm);
    }
}
