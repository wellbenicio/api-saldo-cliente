package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.ObservabilidadePortaSaida;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MonitoramentoFalhaBatchListenerTest {

    private final ObservabilidadePortaSaida observabilidadePortaSaida = mock(ObservabilidadePortaSaida.class);
    private final MonitoramentoFalhaBatchListener listener = new MonitoramentoFalhaBatchListener(observabilidadePortaSaida);

    @Test
    void naoDeveIncrementarMetricaQuandoStepNaoPossuirFalhas() {
        StepExecution stepExecution = new StepExecution("step-sem-falha", new JobExecution(1L));
        ExitStatus exitStatusOriginal = ExitStatus.COMPLETED;
        stepExecution.setExitStatus(exitStatusOriginal);

        ExitStatus exitStatusRetornado = listener.afterStep(stepExecution);

        verify(observabilidadePortaSaida, never()).incrementarFalhasBatch();
        assertSame(exitStatusOriginal, exitStatusRetornado);
    }

    @Test
    void deveIncrementarMetricaQuandoStepPossuirFalhasESemAlterarExitStatus() {
        StepExecution stepExecution = new StepExecution("step-com-falha", new JobExecution(2L));
        ExitStatus exitStatusOriginal = ExitStatus.FAILED;
        stepExecution.setExitStatus(exitStatusOriginal);
        stepExecution.addFailureException(new RuntimeException("falha 1"));
        stepExecution.addFailureException(new IllegalStateException("falha 2"));

        ExitStatus exitStatusRetornado = listener.afterStep(stepExecution);

        verify(observabilidadePortaSaida).incrementarFalhasBatch();
        assertSame(exitStatusOriginal, exitStatusRetornado);
    }
}
