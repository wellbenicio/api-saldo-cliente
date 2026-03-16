package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.jpa;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioEventoProcessadoPortaSaida;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@Profile("local | local-batch")
public class RepositorioEventoProcessadoJpaAdaptador implements RepositorioEventoProcessadoPortaSaida {

    private final EventoProcessadoJpaRepositorio eventoProcessadoJpaRepositorio;

    public RepositorioEventoProcessadoJpaAdaptador(EventoProcessadoJpaRepositorio eventoProcessadoJpaRepositorio) {
        this.eventoProcessadoJpaRepositorio = eventoProcessadoJpaRepositorio;
    }

    @Override
    public boolean jaProcessado(String idEvento) {
        return eventoProcessadoJpaRepositorio.existsById(idEvento);
    }

    @Override
    public void registrarProcessamento(String idEvento, String origem) {
        EventoProcessadoJpaEntidade entidade = new EventoProcessadoJpaEntidade(idEvento, origem, OffsetDateTime.now());
        eventoProcessadoJpaRepositorio.save(entidade);
    }
}
