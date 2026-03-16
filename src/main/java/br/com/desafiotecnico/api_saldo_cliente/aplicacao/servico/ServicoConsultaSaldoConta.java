package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import org.springframework.stereotype.Service;

@Service
public class ServicoConsultaSaldoConta implements ConsultarSaldoContaPortaEntrada {

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;
    private final ObservabilidadePortaSaida observabilidadeMetricasAplicacao;

    public ServicoConsultaSaldoConta(
            RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida,
            ObservabilidadePortaSaida observabilidadeMetricasAplicacao
    ) {
        this.repositorioSaldoContaPortaSaida = repositorioSaldoContaPortaSaida;
        this.observabilidadePortaSaida = observabilidadePortaSaida;
    }

    @Override
    public SaldoConta consultar(ConsultarSaldoContaComando comando) {
        SaldoConta saldoConta = repositorioSaldoContaPortaSaida
                .buscarPorIdConta(comando.idConta())
                .orElseThrow(() -> new ContaNaoEncontradaExcecao(comando.idConta()));

        if (!saldoConta.conta().idTitular().equals(comando.idTitularSolicitante())) {
            observabilidadePortaSaida.incrementarNegacoesAcesso();
            throw new AcessoNaoAutorizadoContaExcecao(comando.idConta(), comando.idTitularSolicitante());
        }

        observabilidadePortaSaida.incrementarConsultasSaldo();
        return saldoConta;
    }
}
