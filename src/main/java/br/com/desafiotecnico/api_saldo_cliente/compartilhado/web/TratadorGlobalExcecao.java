package br.com.desafiotecnico.api_saldo_cliente.compartilhado.web;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta.DetalheErroValidacao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ExcecaoDominio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class TratadorGlobalExcecao {

    @ExceptionHandler(ContaNaoEncontradaExcecao.class)
    public ResponseEntity<ErroApiResposta> tratarContaNaoEncontrada(ContaNaoEncontradaExcecao excecao) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroApiResposta("CONTA_NAO_ENCONTRADA", excecao.getMessage(), OffsetDateTime.now()));
    }

    @ExceptionHandler(AcessoNaoAutorizadoContaExcecao.class)
    public ResponseEntity<ErroApiResposta> tratarAcessoNaoAutorizado(AcessoNaoAutorizadoContaExcecao excecao) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErroApiResposta("ACESSO_NAO_AUTORIZADO", excecao.getMessage(), OffsetDateTime.now()));
    }

    @ExceptionHandler(ExcecaoDominio.class)
    public ResponseEntity<ErroApiResposta> tratarExcecaoDominio(ExcecaoDominio excecao) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErroApiResposta("ERRO_DOMINIO", excecao.getMessage(), OffsetDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApiResposta> tratarErroValidacaoCorpo(MethodArgumentNotValidException excecao) {
        List<DetalheErroValidacao> detalhes = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapearErroCampo)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroApiResposta(
                        "REQUISICAO_INVALIDA",
                        "Um ou mais campos da requisição são inválidos.",
                        OffsetDateTime.now(),
                        detalhes
                ));
    }



    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroApiResposta> tratarErroValidacaoConstraint(ConstraintViolationException excecao) {
        List<DetalheErroValidacao> detalhes = excecao.getConstraintViolations()
                .stream()
                .map(violacao -> new DetalheErroValidacao(violacao.getPropertyPath().toString(), violacao.getMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroApiResposta(
                        "REQUISICAO_INVALIDA",
                        "Um ou mais campos da requisição são inválidos.",
                        OffsetDateTime.now(),
                        detalhes
                ));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErroApiResposta> tratarErroValidacaoParametros(Exception excecao) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroApiResposta(
                        "REQUISICAO_INVALIDA",
                        excecao.getMessage(),
                        OffsetDateTime.now(),
                        List.of(new DetalheErroValidacao("requisicao", excecao.getMessage()))
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroApiResposta> tratarGenerica(Exception excecao) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroApiResposta("ERRO_INTERNO", excecao.getMessage(), OffsetDateTime.now()));
    }

    private DetalheErroValidacao mapearErroCampo(FieldError erroCampo) {
        return new DetalheErroValidacao(erroCampo.getField(), erroCampo.getDefaultMessage());
    }
}
