package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta;
import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.TratadorGlobalExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ExcecaoDominio;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TratadorGlobalExcecaoTest {

    private TratadorGlobalExcecao tratadorGlobalExcecao;

    @BeforeEach
    void configurar() {
        tratadorGlobalExcecao = new TratadorGlobalExcecao();
    }

    @Test
    void deveTratarErroValidacaoConstraintNormalizandoCampoComPonto() {
        ConstraintViolation<?> violacao = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("consulta.idTitularSolicitante");
        when(violacao.getPropertyPath()).thenReturn(path);
        when(violacao.getMessage()).thenReturn("idTitularSolicitante é obrigatório");

        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao
                .tratarErroValidacaoConstraint(new ConstraintViolationException(Set.of(violacao)));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("REQUISICAO_INVALIDA");
        assertThat(resposta.getBody().mensagem()).isEqualTo("idTitularSolicitante é obrigatório");
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).hasSize(1);
        assertThat(resposta.getBody().detalhes().getFirst().campo()).isEqualTo("idTitularSolicitante");
        assertThat(resposta.getBody().detalhes().getFirst().mensagem()).isEqualTo("idTitularSolicitante é obrigatório");
    }

    @Test
    void deveRetornarMensagemPadraoQuandoNaoHouverDetalhesDeConstraint() {
        ConstraintViolationException excecao = mock(ConstraintViolationException.class);
        when(excecao.getConstraintViolations()).thenReturn(Collections.emptySet());

        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao.tratarErroValidacaoConstraint(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("REQUISICAO_INVALIDA");
        assertThat(resposta.getBody().mensagem()).isEqualTo("Um ou mais campos da requisição são inválidos.");
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).isEmpty();
    }

    @Test
    void deveMapearCodigosParaResponseStatusException() {
        validarRespostaStatus(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO");
        validarRespostaStatus(HttpStatus.FORBIDDEN, "ACESSO_NEGADO");
        validarRespostaStatus(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA");
        validarRespostaStatus(HttpStatus.I_AM_A_TEAPOT, "ERRO_INTERNO");
    }

    @Test
    void deveTratarContaNaoEncontradaComContratoPadronizado() {
        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao
                .tratarContaNaoEncontrada(new ContaNaoEncontradaExcecao("conta-123"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("CONTA_NAO_ENCONTRADA");
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).isEmpty();
    }

    @Test
    void deveTratarExcecaoDominioComContratoPadronizado() {
        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao
                .tratarExcecaoDominio(new ExcecaoDominio("Regra de negócio violada"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("ERRO_DOMINIO");
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).isEmpty();
    }

    @Test
    void deveTratarExcecaoGenericaComContratoPadronizado() {
        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao
                .tratarGenerica(new RuntimeException("Falha inesperada"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("ERRO_INTERNO");
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).isEmpty();
    }

    private void validarRespostaStatus(HttpStatus status, String codigoEsperado) {
        ResponseStatusException excecao = new ResponseStatusException(status, "Motivo " + status.value());

        ResponseEntity<ErroApiResposta> resposta = tratadorGlobalExcecao.tratarResponseStatus(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(status);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo(codigoEsperado);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Motivo " + status.value());
        assertThat(resposta.getBody().timestamp()).isNotNull();
        assertThat(resposta.getBody().detalhes()).isEmpty();
    }
}
