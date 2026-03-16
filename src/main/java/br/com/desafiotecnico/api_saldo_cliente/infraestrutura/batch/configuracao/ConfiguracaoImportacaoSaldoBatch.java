package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.configuracao;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.EscritorSaldoContaBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.LeitorRegistroArquivoSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.ProcessadorRegistroSaldoBatch;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes.RegistroArquivoSaldoBatch;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("batch")
public class ConfiguracaoImportacaoSaldoBatch {

    // Observação: aqui os nomes seguem o padrão do time em português; em produção,
    // nomes técnicos e de infraestrutura tendem a ser padronizados em inglês.

    @Bean
    public Job jobImportacaoSaldoConsolidado(JobRepository jobRepository, Step passoImportacaoSaldoConsolidado) {
        return new JobBuilder("jobImportacaoSaldoConsolidado", jobRepository)
            .start(passoImportacaoSaldoConsolidado)
            .build();
    }

    @Bean
    public Step passoImportacaoSaldoConsolidado(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<RegistroArquivoSaldoBatch> leitorRegistroArquivoSaldoBatch,
        ProcessadorRegistroSaldoBatch processadorRegistroSaldoBatch,
        EscritorSaldoContaBatch escritorSaldoContaBatch
    ) {
        return new StepBuilder("passoImportacaoSaldoConsolidado", jobRepository)
            .<RegistroArquivoSaldoBatch, SaldoConta>chunk(500, transactionManager)
            .reader(leitorRegistroArquivoSaldoBatch)
            .processor(processadorRegistroSaldoBatch)
            .writer(escritorSaldoContaBatch)
            .build();
    }

    @Bean
    public FlatFileItemReader<RegistroArquivoSaldoBatch> leitorRegistroArquivoSaldoBatch(
        LeitorRegistroArquivoSaldoBatch leitorRegistroArquivoSaldoBatch,
        @Value("${saldo.batch.arquivo-entrada:./dados/saldos-consolidados.csv}") String caminhoArquivo,
        @Value("${saldo.batch.delimitador:|}") String delimitador
    ) {
        return leitorRegistroArquivoSaldoBatch.criarLeitor(caminhoArquivo, delimitador);
    }
}
