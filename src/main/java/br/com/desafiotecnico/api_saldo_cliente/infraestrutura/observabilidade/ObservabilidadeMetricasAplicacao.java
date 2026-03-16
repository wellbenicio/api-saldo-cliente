package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.observabilidade;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.observabilidade.ObservabilidadeMicrometerAdaptador;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Classe mantida por compatibilidade durante transição para o adaptador de saída de observabilidade.
 */
@Deprecated(forRemoval = true)
public class ObservabilidadeMetricasAplicacao extends ObservabilidadeMicrometerAdaptador {

    public ObservabilidadeMetricasAplicacao(MeterRegistry meterRegistry) {
        super(meterRegistry);
    }
}
