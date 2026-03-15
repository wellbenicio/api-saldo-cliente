package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;

import java.util.stream.Stream;

public interface LeitorArquivoBatchSaldoPortaSaida {

    Stream<SaldoConta> lerSaldosConsolidados();
}
