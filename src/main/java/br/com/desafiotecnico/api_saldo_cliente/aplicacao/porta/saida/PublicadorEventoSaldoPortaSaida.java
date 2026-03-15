package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoSaldoAtualizado;

public interface PublicadorEventoSaldoPortaSaida {

    void publicar(EventoSaldoAtualizado eventoSaldoAtualizado);
}
