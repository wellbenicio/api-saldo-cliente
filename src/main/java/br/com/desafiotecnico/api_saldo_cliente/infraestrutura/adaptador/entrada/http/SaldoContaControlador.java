package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.SaldoContaSaidaDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Validated
@RequestMapping("/v1/saldos")
public class SaldoContaControlador {

    private final ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;
    private final Validator validator;

    public SaldoContaControlador(
            ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada,
            Validator validator
    ) {
        this.consultarSaldoContaPortaEntrada = consultarSaldoContaPortaEntrada;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<SaldoContaSaidaDto> consultar(
            @RequestParam String idConta,
            @RequestHeader("X-Id-Titular") String idTitular
    ) {
        SaldoConta saldoConta = consultarSaldoContaPortaEntrada.consultar(idConta, idTitular);

        SaldoContaSaidaDto saidaDto = new SaldoContaSaidaDto(
                saldoConta.conta().idConta(),
                saldoConta.conta().idTitular(),
                saldoConta.valor(),
                saldoConta.moeda(),
                saldoConta.atualizadoEm()
        );

        return ResponseEntity.ok(saidaDto);
    }
}
