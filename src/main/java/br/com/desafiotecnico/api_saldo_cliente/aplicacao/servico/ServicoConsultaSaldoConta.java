package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.springframework.stereotype.Service;

@Service
public class ServicoConsultaSaldoConta implements ConsultarSaldoContaPortaEntrada {

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;

    public ServicoConsultaSaldoConta(RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida) {
        this.repositorioSaldoContaPortaSaida = repositorioSaldoContaPortaSaida;
    }

    /**
     * Regra de autorização por titularidade mantida no caso de uso.
     * A autenticação (quem é o usuário) será tratada pela camada de segurança
     * quando integrada nas próximas etapas.
     */
    @Override
    public SaldoConta consultar(ConsultarSaldoContaComando comando) {
        SaldoConta saldoConta = repositorioSaldoContaPortaSaida
                .buscarPorIdConta(comando.idConta())
                .orElseThrow(() -> new ContaNaoEncontradaExcecao(comando.idConta()));

        if (!saldoConta.conta().idTitular().equals(comando.idTitularSolicitante())) {
            throw new AcessoNaoAutorizadoContaExcecao(comando.idConta(), comando.idTitularSolicitante());
        }

        return saldoConta;
    }
}
