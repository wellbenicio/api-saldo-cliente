package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "evento_processado")
public class EventoProcessadoJpaEntidade {

    @Id
    @Column(name = "id_evento", nullable = false, length = 128)
    private String idEvento;

    @Column(name = "origem", nullable = false, length = 64)
    private String origem;

    @Column(name = "processado_em", nullable = false)
    private OffsetDateTime processadoEm;

    protected EventoProcessadoJpaEntidade() {
    }

    public EventoProcessadoJpaEntidade(String idEvento, String origem, OffsetDateTime processadoEm) {
        this.idEvento = idEvento;
        this.origem = origem;
        this.processadoEm = processadoEm;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getOrigem() {
        return origem;
    }

    public OffsetDateTime getProcessadoEm() {
        return processadoEm;
    }
}
