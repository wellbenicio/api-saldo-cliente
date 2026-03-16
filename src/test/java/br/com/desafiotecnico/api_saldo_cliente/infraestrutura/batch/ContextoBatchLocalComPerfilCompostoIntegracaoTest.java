package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.batch.job.enabled=false", "logging.config=classpath:logback-test.xml"})
@ActiveProfiles("local-batch")
class ContextoBatchLocalComPerfilCompostoIntegracaoTest {

    @Autowired
    private RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;

    @Autowired
    private DataSource dataSource;

    @Test
    void deveSubirContextoBatchLocalComRepositorioSaldoEDataSourceNoMesmoContexto() {
        assertThat(repositorioSaldoContaPortaSaida).isNotNull();
        assertThat(dataSource).isNotNull();
    }
}
