package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ObservabilidadeMetricasAplicacao implements ObservabilidadePortaSaida {

    private final Counter consultasSaldoCounter;
    private final Counter negacoesAcessoCounter;
    private final Counter falhasBatchCounter;
    private final Counter falhasProcessamentoEventoCounter;

    public ObservabilidadeMetricasAplicacao(MeterRegistry meterRegistry) {
        this.consultasSaldoCounter = Counter.builder("saldo_consultas_total")
                .description("Total de consultas de saldo realizadas")
                .register(meterRegistry);

        this.negacoesAcessoCounter = Counter.builder("saldo_negacoes_acesso_total")
                .description("Total de negações de acesso por autorização")
                .register(meterRegistry);

        this.falhasBatchCounter = Counter.builder("saldo_falhas_batch_total")
                .description("Total de falhas no processamento batch")
                .register(meterRegistry);

        this.falhasProcessamentoEventoCounter = Counter.builder("saldo_falhas_processamento_evento_total")
                .description("Total de falhas no processamento de eventos de saldo")
                .register(meterRegistry);
    }

    @Override
    public void incrementarConsultasSaldo() {
        consultasSaldoCounter.increment();
    }

    @Override
    public void incrementarNegacoesAcesso() {
        negacoesAcessoCounter.increment();
    }

    @Override
    public void incrementarFalhasBatch() {
        falhasBatchCounter.increment();
    }

    @Override
    public void incrementarFalhasProcessamentoEvento() {
        falhasProcessamentoEventoCounter.increment();
    }
}
