package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;

public interface ConsultarSaldoContaPortaEntrada {

    SaldoConta consultar(ConsultarSaldoContaComando comando);
}
