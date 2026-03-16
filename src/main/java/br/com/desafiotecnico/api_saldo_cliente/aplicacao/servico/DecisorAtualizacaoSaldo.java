package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import java.time.OffsetDateTime;
import java.util.Objects;

public class DecisorAtualizacaoSaldo {

    public boolean deveAtualizar(Long versaoAtual, Long versaoNova, OffsetDateTime atualizadoEmAtual, OffsetDateTime atualizadoEmNovo) {
        Objects.requireNonNull(atualizadoEmAtual, "atualizadoEmAtual é obrigatório");
        Objects.requireNonNull(atualizadoEmNovo, "atualizadoEmNovo é obrigatório");

        if (versaoAtual != null && versaoNova != null) {
            return versaoNova > versaoAtual;
        }

        return atualizadoEmNovo.isAfter(atualizadoEmAtual);
    }
}
