package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/saldos")
public class SaldoContaControlador {

    private final ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;

    public SaldoContaControlador(ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada) {
        this.consultarSaldoContaPortaEntrada = consultarSaldoContaPortaEntrada;
    }

    @GetMapping
    public ResponseEntity<SaldoConta> consultar(
            @Valid @ModelAttribute ConsultarSaldoContaRequisicao requisicao,
            @RequestHeader("X-Id-Titular")
            @NotBlank(message = "Cabeçalho 'X-Id-Titular' é obrigatório.")
            @Size(min = 5, max = 20, message = "Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres.")
            String idTitular
    ) {
        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando(requisicao.idConta(), idTitular);
        return ResponseEntity.ok(consultarSaldoContaPortaEntrada.consultar(comando));
    }
}
