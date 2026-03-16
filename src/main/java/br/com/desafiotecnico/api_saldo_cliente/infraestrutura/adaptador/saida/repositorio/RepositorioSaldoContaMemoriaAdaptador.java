package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class RepositorioSaldoContaMemoriaAdaptador implements RepositorioSaldoContaPortaSaida {

    private final Map<String, SaldoConta> saldos = new ConcurrentHashMap<>();

    public RepositorioSaldoContaMemoriaAdaptador() {
        Conta contaExemplo = new Conta("12345", "titular-001");
        saldos.put(contaExemplo.idConta(), new SaldoConta(contaExemplo, new BigDecimal("1500.00"), "BRL", OffsetDateTime.now()));
    }

    @Override
    public Optional<SaldoConta> buscarPorIdConta(String idConta) {
        return Optional.ofNullable(saldos.get(idConta));
    }

    @Override
    public SaldoConta salvar(SaldoConta saldoConta) {
        saldos.put(saldoConta.conta().idConta(), saldoConta);
        return saldoConta;
    }
}
