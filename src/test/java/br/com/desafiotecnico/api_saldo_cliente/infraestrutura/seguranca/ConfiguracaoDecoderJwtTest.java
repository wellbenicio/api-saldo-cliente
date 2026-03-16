package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca;

import br.com.desafiotecnico.api_saldo_cliente.infraestrutura.seguranca.jwt.PropriedadesSegurancaJwt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfiguracaoDecoderJwtTest {

    private final ConfiguracaoDecoderJwt configuracaoDecoderJwt = new ConfiguracaoDecoderJwt();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveLancarExcecaoQuandoSegredoAssinaturaAusenteOuVazio(String segredoAssinatura) {
        PropriedadesSegurancaJwt propriedadesSegurancaJwt = new PropriedadesSegurancaJwt();
        propriedadesSegurancaJwt.setSegredoAssinatura(segredoAssinatura);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> configuracaoDecoderJwt.jwtDecoder(propriedadesSegurancaJwt)
        );

        assertEquals("Configuração obrigatória ausente: seguranca.jwt.segredo-assinatura", excecao.getMessage());
    }

    @Test
    void deveCriarJwtDecoderQuandoSegredoAssinaturaValido() {
        PropriedadesSegurancaJwt propriedadesSegurancaJwt = new PropriedadesSegurancaJwt();
        propriedadesSegurancaJwt.setSegredoAssinatura("segredo-super-seguro-com-tamanho-minimo");

        JwtDecoder jwtDecoder = configuracaoDecoderJwt.jwtDecoder(propriedadesSegurancaJwt);

        assertNotNull(jwtDecoder);
        assertInstanceOf(NimbusJwtDecoder.class, jwtDecoder);
    }
}
