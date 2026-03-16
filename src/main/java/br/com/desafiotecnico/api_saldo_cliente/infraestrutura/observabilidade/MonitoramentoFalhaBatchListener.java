package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("batch | local-batch")
public class MonitoramentoFalhaBatchListener implements StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoramentoFalhaBatchListener.class);

    private final ObservabilidadePortaSaida observabilidadeMetricasAplicacao;

    public MonitoramentoFalhaBatchListener(ObservabilidadePortaSaida observabilidadeMetricasAplicacao) {
        this.observabilidadeMetricasAplicacao = observabilidadeMetricasAplicacao;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getFailureExceptions() != null && !stepExecution.getFailureExceptions().isEmpty()) {
            observabilidadeMetricasAplicacao.incrementarFalhasBatch();
            LOGGER.error("Falha no processamento batch. step={}, totalFalhasStep={}",
                    stepExecution.getStepName(),
                    stepExecution.getFailureExceptions().size());
        }

        return stepExecution.getExitStatus();
    }
}
