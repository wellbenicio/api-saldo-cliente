package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.SaldoContaSaidaDto;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.PrincipalConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

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
            Principal principal,
            @PathVariable("idConta")
            @NotBlank(message = "Parâmetro 'idConta' é obrigatório.")
            @Size(min = 5, max = 20, message = "Parâmetro 'idConta' deve ter entre 5 e 20 caracteres.")
            String idConta
    ) {
        String idTitular = extrairIdTitular(principal);

        ConsultarSaldoContaComando comando = new ConsultarSaldoContaComando(idConta, idTitular);
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

    private String extrairIdTitular(Principal principal) {
        if (principal instanceof PrincipalConta principalConta) {
            return principalConta.idTitular();
        }

        if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Object claim = jwtAuthenticationToken.getTokenAttributes().get("sub");
            if (claim instanceof String idTitular && !idTitular.isBlank()) {
                return idTitular;
            }
        }

        if (principal instanceof Jwt jwt) {
            String idTitular = jwt.getSubject();
            if (idTitular != null && !idTitular.isBlank()) {
                return idTitular;
            }
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado no contexto de segurança.");
    }
}
