package br.com.desafiotecnico.api_saldo_cliente.dominio.modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Evento de integração publicado para outros sistemas após atualização de saldo.
 *
 * <p>Neste desafio os nomes estão em português por escolha simbólica.
 * Em projeto real, a preferência é nomenclatura em inglês para padronização de mercado.</p>
 */
public record EventoIntegracaoSaldoAtualizado(
        String idEventoIntegracao,
        String idEventoOrigem,
        String idConta,
        String idTitular,
        BigDecimal saldoAtual,
        String moeda,
        Long versaoSaldo,
        OffsetDateTime ocorridoEm,
        OffsetDateTime publicadoEm,
        String origemAtualizacao
) {

    public EventoIntegracaoSaldoAtualizado {
        Objects.requireNonNull(idEventoIntegracao, "idEventoIntegracao é obrigatório");
        Objects.requireNonNull(idEventoOrigem, "idEventoOrigem é obrigatório");
        Objects.requireNonNull(idConta, "idConta é obrigatório");
        Objects.requireNonNull(idTitular, "idTitular é obrigatório");
        Objects.requireNonNull(saldoAtual, "saldoAtual é obrigatório");
        Objects.requireNonNull(moeda, "moeda é obrigatória");
        Objects.requireNonNull(versaoSaldo, "versaoSaldo é obrigatória");
        Objects.requireNonNull(ocorridoEm, "ocorridoEm é obrigatório");
        Objects.requireNonNull(publicadoEm, "publicadoEm é obrigatório");
        Objects.requireNonNull(origemAtualizacao, "origemAtualizacao é obrigatória");
    }
}
