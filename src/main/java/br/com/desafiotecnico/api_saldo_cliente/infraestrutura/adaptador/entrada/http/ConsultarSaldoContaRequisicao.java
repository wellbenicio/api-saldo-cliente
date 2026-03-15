package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.ConsultarSaldoContaHttpEntrada;

/**
 * @deprecated Use {@link ConsultarSaldoContaHttpEntrada} como único DTO HTTP canônico.
 */
@Deprecated(since = "1.0", forRemoval = true)
public record ConsultarSaldoContaRequisicao(String idConta) {

    public ConsultarSaldoContaHttpEntrada paraHttpEntrada(String idTitularSolicitante) {
        return new ConsultarSaldoContaHttpEntrada(idConta, idTitularSolicitante);
    }
}
