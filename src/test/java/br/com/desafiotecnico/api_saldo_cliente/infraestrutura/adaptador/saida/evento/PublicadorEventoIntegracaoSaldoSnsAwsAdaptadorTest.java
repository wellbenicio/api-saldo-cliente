package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.evento;

import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.EventoIntegracaoSaldoAtualizado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(OutputCaptureExtension.class)
class PublicadorEventoIntegracaoSaldoSnsAwsAdaptadorTest {

    private final PublicadorEventoIntegracaoSaldoSnsAwsAdaptador adaptador = new PublicadorEventoIntegracaoSaldoSnsAwsAdaptador();

    @Test
    void devePublicarSemExcecaoERegistrarCamposChaveNoLog(CapturedOutput output) {
        EventoIntegracaoSaldoAtualizado evento = criarEventoIntegracaoSaldoAtualizadoFixo();

        assertThatCode(() -> adaptador.publicar(evento)).doesNotThrowAnyException();

        String logs = output.getOut();
        assertThat(logs).contains("idEventoIntegracao=" + evento.idEventoIntegracao());
        assertThat(logs).contains("idConta=" + evento.idConta());
        assertThat(logs).contains("SALDO_AWS_SNS_TOPIC_ARN");
    }

    private EventoIntegracaoSaldoAtualizado criarEventoIntegracaoSaldoAtualizadoFixo() {
        return new EventoIntegracaoSaldoAtualizado(
                "evt-int-001",
                "evt-origem-001",
                "conta-123",
                "titular-456",
                new BigDecimal("9876.54"),
                "BRL",
                42L,
                OffsetDateTime.parse("2026-03-10T10:15:30Z"),
                OffsetDateTime.parse("2026-03-10T10:16:00Z"),
                "MQ_JMS_SIMULADO"
        );
    }
}
