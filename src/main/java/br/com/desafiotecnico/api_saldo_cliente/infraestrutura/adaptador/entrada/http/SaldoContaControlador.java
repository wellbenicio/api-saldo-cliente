package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import jakarta.validation.constraints.NotBlank;
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

    @GetMapping("/{idConta}/saldo")
    public ResponseEntity<SaldoConta> consultar(
            @RequestParam @NotBlank(message = "O parâmetro idConta é obrigatório.") String idConta,
            @RequestHeader("X-Id-Titular") @NotBlank(message = "O header X-Id-Titular é obrigatório.") String idTitular
    ) {
        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando(requisicao.idConta(), idTitular);
        return ResponseEntity.ok(consultarSaldoContaPortaEntrada.consultar(comando));
    }
}
