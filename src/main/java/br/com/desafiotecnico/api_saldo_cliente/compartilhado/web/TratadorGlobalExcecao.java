package br.com.desafiotecnico.api_saldo_cliente.compartilhado.web;

import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ExcecaoDominio;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorGlobalExcecao {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApiResposta> tratarValidacaoDto(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return respostaRequisicaoInvalida(mensagem);
    }

    @ExceptionHandler({HandlerMethodValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<ErroApiResposta> tratarValidacaoParametros(Exception excecao) {
        return respostaRequisicaoInvalida(excecao.getMessage());
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroApiResposta> tratarGenerica(Exception excecao) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErroApiResposta("ERRO_INTERNO", excecao.getMessage(), OffsetDateTime.now()));
    }

    private ResponseEntity<ErroApiResposta> respostaRequisicaoInvalida(String detalhes) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroApiResposta("REQUISICAO_INVALIDA", detalhes, OffsetDateTime.now()));
    }
}
