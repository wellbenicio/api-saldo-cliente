package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropriedadesSegurancaJwtTest {

    @Test
    void devePossuirValorPadraoDeToleranciaClockSkewEm30Segundos() {
        PropriedadesSegurancaJwt propriedadesSegurancaJwt = new PropriedadesSegurancaJwt();

        assertEquals(Duration.ofSeconds(30), propriedadesSegurancaJwt.getToleranciaClockSkew());
    }

    @Test
    void devePermitirGettersESettersBasicos() {
        PropriedadesSegurancaJwt propriedadesSegurancaJwt = new PropriedadesSegurancaJwt();

        propriedadesSegurancaJwt.setHabilitado(true);
        propriedadesSegurancaJwt.setSegredoAssinatura("segredo");
        propriedadesSegurancaJwt.setIssuer("issuer-teste");
        propriedadesSegurancaJwt.setJwkSetUri("https://exemplo.test/.well-known/jwks.json");
        propriedadesSegurancaJwt.setChavePublicaPem("-----BEGIN PUBLIC KEY-----abc");
        propriedadesSegurancaJwt.setAudience("api-saldo-cliente");
        propriedadesSegurancaJwt.setToleranciaClockSkew(Duration.ofSeconds(45));

        assertTrue(propriedadesSegurancaJwt.isHabilitado());
        assertEquals("segredo", propriedadesSegurancaJwt.getSegredoAssinatura());
        assertEquals("issuer-teste", propriedadesSegurancaJwt.getIssuer());
        assertEquals("https://exemplo.test/.well-known/jwks.json", propriedadesSegurancaJwt.getJwkSetUri());
        assertEquals("-----BEGIN PUBLIC KEY-----abc", propriedadesSegurancaJwt.getChavePublicaPem());
        assertEquals("api-saldo-cliente", propriedadesSegurancaJwt.getAudience());
        assertEquals(Duration.ofSeconds(45), propriedadesSegurancaJwt.getToleranciaClockSkew());
    }
}
