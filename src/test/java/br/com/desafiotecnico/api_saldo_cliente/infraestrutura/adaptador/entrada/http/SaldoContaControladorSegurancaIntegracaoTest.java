package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NAO_AUTENTICADO"))
                .andExpect(jsonPath("$.mensagem").value("Autenticação obrigatória para acessar este recurso."))
                .andExpect(jsonPath("$.timestamp").exists());

        mockMvc.perform(get("/v1/contas/12345/saldo"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NAO_AUTENTICADO"))
                .andExpect(jsonPath("$.mensagem").value("Autenticação obrigatória para acessar este recurso."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private String gerarToken(String sujeito, String documento, String escopo) {
        Instant agora = Instant.now();
        SecretKey chave = Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(sujeito)
                .claim("documento", documento)
                .claim("escopo", escopo)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(10, ChronoUnit.MINUTES)))
                .signWith(chave)
                .compact();
    }
}
