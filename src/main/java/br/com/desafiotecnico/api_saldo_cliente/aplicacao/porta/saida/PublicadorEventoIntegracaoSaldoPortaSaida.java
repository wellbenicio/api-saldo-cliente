package br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoIntegracaoSaldoAtualizado;

public interface PublicadorEventoIntegracaoSaldoPortaSaida {

    void publicar(EventoIntegracaoSaldoAtualizado eventoIntegracaoSaldoAtualizado);
}
