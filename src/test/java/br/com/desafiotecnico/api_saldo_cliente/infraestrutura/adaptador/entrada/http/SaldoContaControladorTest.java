package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.ConsultarSaldoContaPortaEntrada;
import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.entrada.comando.ConsultarSaldoContaComando;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.dto.SaldoContaSaidaDto;
import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.PrincipalConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class SaldoContaControladorTest {

    private static final String ID_CONTA = "12345";

    private ConsultarSaldoContaPortaEntrada consultarSaldoContaPortaEntrada;
    private SaldoContaControlador saldoContaControlador;

    @BeforeEach
    void setUp() {
        consultarSaldoContaPortaEntrada = mock(ConsultarSaldoContaPortaEntrada.class);
        saldoContaControlador = new SaldoContaControlador(consultarSaldoContaPortaEntrada);
    }

    @Test
    void deveConsultarComPrincipalContaValido() {
        PrincipalConta principal = new PrincipalConta("cliente-001", "12345678900", Set.of("conta:saldo:consultar"));

        consultarComSucesso(principal, "cliente-001");
    }

    @Test
    void deveConsultarComJwtAuthenticationTokenComIdCliente() {
        Jwt jwt = criarJwt(Map.of("idCliente", "cliente-002", "sub", "sub-nao-utilizado"));
        JwtAuthenticationToken principal = new JwtAuthenticationToken(jwt);

        consultarComSucesso(principal, "cliente-002");
    }

    @Test
    void deveConsultarComJwtAuthenticationTokenSemIdClienteEComSub() {
        Jwt jwt = criarJwt(Map.of("sub", "cliente-sub-003"));
        JwtAuthenticationToken principal = new JwtAuthenticationToken(jwt);

        consultarComSucesso(principal, "cliente-sub-003");
    }

    @Test
    void deveConsultarComJwtComIdCliente() {
        Jwt jwt = mock(Jwt.class, withSettings().extraInterfaces(Principal.class));
        when(jwt.getClaimAsString("idCliente")).thenReturn("cliente-004");

        consultarComSucesso((Principal) jwt, "cliente-004");
    }

    @Test
    void deveConsultarComJwtSemIdClienteEComSubject() {
        Jwt jwt = mock(Jwt.class, withSettings().extraInterfaces(Principal.class));
        when(jwt.getClaimAsString("idCliente")).thenReturn(" ");
        when(jwt.getSubject()).thenReturn("cliente-sub-005");

        consultarComSucesso((Principal) jwt, "cliente-sub-005");
    }

    @Test
    void deveConsultarComPrincipalCustomComNomeValido() {
        Principal principal = () -> "cliente-006";

        consultarComSucesso(principal, "cliente-006");
    }

    @Test
    void deveRetornar401QuandoPrincipalForNulo() {
        ResponseStatusException excecao = assertThrows(ResponseStatusException.class,
                () -> saldoContaControlador.consultar(null, ID_CONTA));

        assertEquals(HttpStatus.UNAUTHORIZED, excecao.getStatusCode());
        assertEquals("Usuário autenticado não encontrado no contexto de segurança.", excecao.getReason());
    }

    @Test
    void deveRetornar401QuandoPrincipalNaoTiverNomeNemClaimsValidas() {
        Jwt jwtSemClaimsValidas = criarJwt(Map.of("idCliente", " ", "sub", " "));
        JwtAuthenticationToken principal = new JwtAuthenticationToken(jwtSemClaimsValidas);

        ResponseStatusException excecao = assertThrows(ResponseStatusException.class,
                () -> saldoContaControlador.consultar(principal, ID_CONTA));

        assertEquals(HttpStatus.UNAUTHORIZED, excecao.getStatusCode());
        assertEquals("Usuário autenticado não encontrado no contexto de segurança.", excecao.getReason());
    }

    private void consultarComSucesso(Principal principal, String idTitularEsperado) {
        OffsetDateTime atualizadoEm = OffsetDateTime.parse("2026-01-01T10:15:30Z");
        SaldoConta saldoConta = new SaldoConta(
                new Conta(ID_CONTA, idTitularEsperado),
                new BigDecimal("100.00"),
                "BRL",
                atualizadoEm,
                atualizadoEm,
                1L
        );

        when(consultarSaldoContaPortaEntrada.consultar(org.mockito.ArgumentMatchers.any(ConsultarSaldoContaComando.class)))
                .thenReturn(saldoConta);

        ResponseEntity<SaldoContaSaidaDto> resposta = saldoContaControlador.consultar(principal, ID_CONTA);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(ID_CONTA, resposta.getBody().idConta());
        assertEquals(idTitularEsperado, resposta.getBody().idClienteTitular());

        ArgumentCaptor<ConsultarSaldoContaComando> captor = ArgumentCaptor.forClass(ConsultarSaldoContaComando.class);
        verify(consultarSaldoContaPortaEntrada).consultar(captor.capture());
        assertEquals(ID_CONTA, captor.getValue().idConta());
        assertEquals(idTitularEsperado, captor.getValue().idTitularSolicitante());
    }

    private Jwt criarJwt(Map<String, Object> claims) {
        return new Jwt(
                "token-valor",
                Instant.parse("2026-01-01T10:00:00Z"),
                Instant.parse("2026-01-01T11:00:00Z"),
                Map.of("alg", "none"),
                claims
        );
    }
}
