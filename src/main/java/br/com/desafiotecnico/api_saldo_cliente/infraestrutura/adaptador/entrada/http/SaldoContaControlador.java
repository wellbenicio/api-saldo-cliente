package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.ConsultarSaldoContaHttpEntrada;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.SaldoContaSaidaDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/v1/contas")
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

    @GetMapping("/{idConta}/saldo")
    public ResponseEntity<SaldoContaSaidaDto> consultar(
            @PathVariable String idConta,
            @RequestHeader(value = "X-Id-Titular", required = false) String idTitular
    ) {
        ConsultarSaldoContaHttpEntrada httpEntrada = new ConsultarSaldoContaHttpEntrada(idConta, idTitular);
        Set<ConstraintViolation<ConsultarSaldoContaHttpEntrada>> violacoes = validator.validate(httpEntrada);

        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando(
                httpEntrada.idConta(),
                httpEntrada.idTitularSolicitante()
        );

        SaldoConta saldoConta = consultarSaldoContaPortaEntrada.consultar(comando);

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
