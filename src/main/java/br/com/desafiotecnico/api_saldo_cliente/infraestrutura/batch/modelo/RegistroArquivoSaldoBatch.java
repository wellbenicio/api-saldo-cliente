package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record RegistroArquivoSaldoBatch(
        Long idConta,
        Long idTitular,
        BigDecimal valor,
        String moeda,
        OffsetDateTime dataHoraReferencia,
        Integer versaoSaldo
) {

    public RegistroArquivoSaldoBatch {
        Objects.requireNonNull(idConta, "idConta é obrigatório");
        Objects.requireNonNull(idTitular, "idTitular é obrigatório");
        Objects.requireNonNull(valor, "valor é obrigatório");
        Objects.requireNonNull(moeda, "moeda é obrigatória");
        Objects.requireNonNull(dataHoraReferencia, "dataHoraReferencia é obrigatória");
        Objects.requireNonNull(versaoSaldo, "versaoSaldo é obrigatória");

        if (idConta <= 0) {
            throw new IllegalArgumentException("idConta deve ser maior que zero");
        }
        if (idTitular <= 0) {
            throw new IllegalArgumentException("idTitular deve ser maior que zero");
        }
        if (versaoSaldo < 0) {
            throw new IllegalArgumentException("versaoSaldo deve ser maior ou igual a zero");
        }
        if (moeda.isBlank()) {
            throw new IllegalArgumentException("moeda não pode ser vazia");
        }
    }

    public static RegistroArquivoSaldoBatch deCamposTexto(
            String idConta,
            String idTitular,
            String valor,
            String moeda,
            String dataHoraReferencia,
            String versaoSaldo
    ) {
        return new RegistroArquivoSaldoBatch(
                Long.valueOf(idConta),
                Long.valueOf(idTitular),
                new BigDecimal(valor),
                moeda,
                OffsetDateTime.parse(dataHoraReferencia),
                Integer.valueOf(versaoSaldo)
        );
    }
}
