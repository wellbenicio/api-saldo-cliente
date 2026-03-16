package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.jpa;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("local")
public class RepositorioSaldoContaJpaAdaptador implements RepositorioSaldoContaPortaSaida {

    private final SaldoContaJpaRepositorio saldoContaJpaRepositorio;

    public RepositorioSaldoContaJpaAdaptador(SaldoContaJpaRepositorio saldoContaJpaRepositorio) {
        this.saldoContaJpaRepositorio = saldoContaJpaRepositorio;
    }

    @Override
    public Optional<SaldoConta> buscarPorIdConta(String idConta) {
        return saldoContaJpaRepositorio.findById(idConta)
                .map(this::paraDominio);
    }

    @Override
    public SaldoConta salvar(SaldoConta saldoConta) {
        Optional<SaldoConta> saldoExistente = buscarPorIdConta(saldoConta.conta().idConta());
        if (saldoExistente.isPresent() && !saldoConta.deveSobrescrever(saldoExistente.get())) {
            return saldoExistente.get();
        }

        SaldoContaJpaEntidade salvo = saldoContaJpaRepositorio.save(paraEntidade(saldoConta));
        return paraDominio(salvo);
    }

    private SaldoConta paraDominio(SaldoContaJpaEntidade entidade) {
        Conta conta = new Conta(entidade.getIdConta(), entidade.getIdTitular());
        return new SaldoConta(
                conta,
                entidade.getValor(),
                entidade.getMoeda(),
                entidade.getAtualizadoEm(),
                entidade.getDataHoraReferencia(),
                entidade.getVersaoSaldo()
        );
    }

    private SaldoContaJpaEntidade paraEntidade(SaldoConta saldoConta) {
        return new SaldoContaJpaEntidade(
                saldoConta.conta().idConta(),
                saldoConta.conta().idTitular(),
                saldoConta.valor(),
                saldoConta.moeda(),
                saldoConta.atualizadoEm(),
                saldoConta.dataHoraReferencia(),
                saldoConta.versaoSaldo()
        );
    }
}
