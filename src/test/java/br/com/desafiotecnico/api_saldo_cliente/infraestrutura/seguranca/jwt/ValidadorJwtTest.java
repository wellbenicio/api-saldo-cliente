package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorJwtTest {

    @Test
    void deveRetornarPendenteQuandoEstruturaJwtValidaEMasValidacaoCriptograficaNaoHabilitada() {
        PropriedadesSegurancaJwt propriedades = new PropriedadesSegurancaJwt();
        propriedades.setHabilitado(true);

        ValidadorTokenJwt validadorTokenJwt = new ValidadorTokenJwt(propriedades);
        ValidadorTokenJwt.ResultadoValidacaoTokenJwt resultado =
                validadorTokenJwt.validar("header.payload.signature");

        assertFalse(resultado.valido());
        assertTrue(resultado.pendente());
    }
}
