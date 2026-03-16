package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.aplicacao.porta.saida.RepositorioSaldoContaPortaSaida;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.Conta;
import br.com.desafiotecnico.api_saldo_cliente.dominio.modelo.SaldoConta;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SaldoContaControladorSegurancaIntegracaoTest {

    private static final String SEGREDO = "segredo-jwt-api-saldo-cliente-2026-chave-segura";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepositorioSaldoContaPortaSaida repositorioSaldoContaPortaSaida;

    @BeforeEach
    void prepararMassa() {
        repositorioSaldoContaPortaSaida.salvar(new SaldoConta(
                new Conta("12345", "titular-001"),
                new BigDecimal("1500.00"),
                "BRL",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null
        ));
    }

    @Test
    void deveRetornar200QuandoTokenValidoETitularDaConta() throws Exception {
        String token = gerarToken("titular-001", "12345678900", "conta:saldo:consultar");

        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idConta").value("12345"))
                .andExpect(jsonPath("$.idClienteTitular").value("titular-001"));
    }

    @Test
    void deveRetornar403QuandoTokenValidoMasSolicitanteNaoTitular() throws Exception {
        String token = gerarToken("titular-999", "12345678900", "conta:saldo:consultar");

        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ACESSO_NAO_AUTORIZADO"))
                .andExpect(jsonPath("$.mensagem").value("Acesso não autorizado para titular titular-999 na conta 12345"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        mockMvc.perform(get("/v1/contas/12345/saldo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/v1/contas/12345/saldo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarEndpointsNaoMapeadosNaPoliticaDeSeguranca() throws Exception {
        mockMvc.perform(get("/rota-inexistente"))
                .andExpect(status().isUnauthorized());

        String token = gerarToken("titular-001", "12345678900", "conta:saldo:consultar");
        mockMvc.perform(get("/rota-inexistente")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String gerarToken(String sujeito, String documento, String scope) {
        Instant agora = Instant.now();
        SecretKey chave = Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(sujeito)
                .claim("documento", documento)
                .claim("scope", scope)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(10, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }
}
