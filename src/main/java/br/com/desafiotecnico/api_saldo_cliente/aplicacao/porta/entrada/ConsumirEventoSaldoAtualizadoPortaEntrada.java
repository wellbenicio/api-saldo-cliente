package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsumirEventoSaldoAtualizadoComando;

public interface ConsumirEventoSaldoAtualizadoPortaEntrada {

    void consumir(ConsumirEventoSaldoAtualizadoComando comando);
}
