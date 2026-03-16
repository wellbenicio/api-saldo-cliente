package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "saldo_consolidado")
public class SaldoContaJpaEntidade {

    @Id
    @Column(name = "id_conta", nullable = false, length = 64)
    private String idConta;

    @Column(name = "id_titular", nullable = false, length = 64)
    private String idTitular;

    @Column(name = "valor_saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "moeda", nullable = false, length = 8)
    private String moeda;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected SaldoContaJpaEntidade() {
    }

    public SaldoContaJpaEntidade(String idConta, String idTitular, BigDecimal valor, String moeda, OffsetDateTime atualizadoEm) {
        this.idConta = idConta;
        this.idTitular = idTitular;
        this.valor = valor;
        this.moeda = moeda;
        this.atualizadoEm = atualizadoEm;
    }

    public String getIdConta() {
        return idConta;
    }

    public String getIdTitular() {
        return idTitular;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getMoeda() {
        return moeda;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
