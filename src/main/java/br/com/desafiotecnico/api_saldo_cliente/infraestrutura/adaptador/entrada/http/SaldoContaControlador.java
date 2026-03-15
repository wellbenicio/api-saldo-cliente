package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.SaldoContaSaidaDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/contas")
public class SaldoContaControlador {

    private final ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;

    public SaldoContaControlador(ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada) {
        this.consultarSaldoContaPortaEntrada = consultarSaldoContaPortaEntrada;
    }

    @GetMapping("/{idConta}/saldo")
    public ResponseEntity<SaldoContaSaidaDto> consultar(
            @PathVariable
            @NotBlank(message = "Parâmetro 'idConta' é obrigatório.")
            @Size(min = 5, max = 20, message = "Parâmetro 'idConta' deve ter entre 5 e 20 caracteres.")
            String idConta,
            @RequestHeader("X-Id-Titular")
            @NotBlank(message = "Cabeçalho 'X-Id-Titular' é obrigatório.")
            @Size(min = 5, max = 20, message = "Cabeçalho 'X-Id-Titular' deve ter entre 5 e 20 caracteres.")
            String idTitular
    ) {
        SaldoConta saldoConta = consultarSaldoContaPortaEntrada.consultar(new ConsultarSaldoContaComando(idConta, idTitular));

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
