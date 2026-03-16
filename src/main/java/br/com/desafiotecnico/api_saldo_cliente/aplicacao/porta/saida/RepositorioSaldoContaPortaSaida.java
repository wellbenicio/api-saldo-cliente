package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;

import java.util.Optional;

public interface RepositorioSaldoContaPortaSaida {

    Optional<SaldoConta> buscarPorIdConta(String idConta);

    SaldoConta salvar(SaldoConta saldoConta);
}
