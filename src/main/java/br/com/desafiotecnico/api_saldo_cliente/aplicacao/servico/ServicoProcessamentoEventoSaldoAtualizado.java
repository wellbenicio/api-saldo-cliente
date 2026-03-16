package br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsumirEventoSaldoAtualizadoPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsumirEventoSaldoAtualizadoComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.PublicadorEventoIntegracaoSaldoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioEventoProcessadoPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoIntegracaoSaldoAtualizado;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicoProcessamentoEventoSaldoAtualizado implements ConsumirEventoSaldoAtualizadoPortaEntrada {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicoProcessamentoEventoSaldoAtualizado.class);

    private final RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;
    private final RepositorioEventoProcessadoPortaSaida repositorioEventoProcessadoPortaSaida;
    private final PublicadorEventoIntegracaoSaldoPortaSaida publicadorEventoIntegracaoSaldoPortaSaida;
    private final ObservabilidadePortaSaida observabilidadePortaSaida;

    public ServicoProcessamentoEventoSaldoAtualizado(
            RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida,
            RepositorioEventoProcessadoPortaSaida repositorioEventoProcessadoPortaSaida,
            PublicadorEventoIntegracaoSaldoPortaSaida publicadorEventoIntegracaoSaldoPortaSaida,
            ObservabilidadePortaSaida observabilidadePortaSaida
    ) {
        this.repositorioSaldoContaPortaSaida = repositorioSaldoContaPortaSaida;
        this.repositorioEventoProcessadoPortaSaida = repositorioEventoProcessadoPortaSaida;
        this.publicadorEventoIntegracaoSaldoPortaSaida = publicadorEventoIntegracaoSaldoPortaSaida;
        this.observabilidadePortaSaida = observabilidadePortaSaida;
    }

    @Override
    @Transactional
    public void consumir(ConsumirEventoSaldoAtualizadoComando comando) {
        if (repositorioEventoProcessadoPortaSaida.jaProcessado(comando.idEvento())) {
            LOGGER.info("Evento de saldo duplicado ignorado. idEvento={}", comando.idEvento());
            return;
        }

        Optional<SaldoConta> saldoAtual = repositorioSaldoContaPortaSaida.buscarPorIdConta(comando.idConta());
        SaldoConta saldoNovo = criarSaldoDoEvento(comando, saldoAtual.orElse(null));

        if (saldoAtual.isPresent() && !saldoNovo.deveSobrescrever(saldoAtual.get())) {
            repositorioEventoProcessadoPortaSaida.registrarProcessamento(comando.idEvento(), comando.origem());
            LOGGER.info("Evento desatualizado ignorado para não sobrescrever saldo atual. idEvento={}, idConta={}", comando.idEvento(), comando.idConta());
            return;
        }

        try {
            repositorioSaldoContaPortaSaida.salvar(saldoNovo);
            repositorioEventoProcessadoPortaSaida.registrarProcessamento(comando.idEvento(), comando.origem());
            publicadorEventoIntegracaoSaldoPortaSaida.publicar(criarEventoIntegracao(comando, saldoNovo));

            LOGGER.info("Saldo atualizado por evento quase em tempo real. idEvento={}, idConta={}, ocorridoEm={}",
                    comando.idEvento(), comando.idConta(), comando.ocorridoEm());
        } catch (Exception excecao) {
            observabilidadePortaSaida.incrementarFalhasProcessamentoEvento();
            throw excecao;
        }

        // Evolução futura recomendada: padrão Outbox para garantir consistência transacional
        // entre persistência local e publicação assíncrona externa.
    }

    private SaldoConta criarSaldoDoEvento(ConsumirEventoSaldoAtualizadoComando comando, SaldoConta saldoAtual) {
        String moeda = saldoAtual == null ? "BRL" : saldoAtual.moeda();
        return new SaldoConta(
                new Conta(comando.idConta(), comando.idTitular()),
                comando.novoValorSaldo(),
                moeda,
                OffsetDateTime.now(),
                comando.ocorridoEm(),
                comando.versaoSaldo()
        );
    }

    private EventoIntegracaoSaldoAtualizado criarEventoIntegracao(ConsumirEventoSaldoAtualizadoComando comando, SaldoConta saldoNovo) {
        return new EventoIntegracaoSaldoAtualizado(
                UUID.randomUUID().toString(),
                comando.idEvento(),
                comando.idConta(),
                comando.idTitular(),
                saldoNovo.valor(),
                saldoNovo.moeda(),
                saldoNovo.versaoSaldo(),
                comando.ocorridoEm(),
                OffsetDateTime.now(),
                comando.origem()
        );
    }
}
