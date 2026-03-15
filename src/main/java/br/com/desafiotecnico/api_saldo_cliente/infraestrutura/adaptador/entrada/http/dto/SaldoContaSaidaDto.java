package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaldoContaSaidaDto(
        String idConta,
        String idClienteTitular,
        BigDecimal valorSaldo,
        String moeda,
        OffsetDateTime dataHoraUltimaAtualizacao
) {
}
