package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.saida.repositorio.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoContaJpaRepositorio extends JpaRepository<SaldoContaJpaEntidade, String> {
}
