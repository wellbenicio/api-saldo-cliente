package br.com.desafiotecnico.api_saldo_cliente.compartilhado.web;

import br.com.desafiotecnico.api_saldo_cliente.compartilhado.web.ErroApiResposta.DetalheErroValidacao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.AcessoNaoAutorizadoContaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ContaNaoEncontradaExcecao;
import br.com.desafiotecnico.api_saldo_cliente.dominio.excecao.ExcecaoDominio;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class TratadorGlobalExcecao {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroApiResposta> tratarErroValidacaoCorpo(MethodArgumentNotValidException excecao) {
        List<DetalheErroValidacao> detalhes = excecao.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapearErroCampo)
                .toList();

        String mensagem = detalhes.isEmpty()
                ? "Um ou mais campos da requisição são inválidos."
                : detalhes.get(0).mensagem();

        return respostaRequisicaoInvalida(mensagem, detalhes);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroApiResposta> tratarErroValidacaoConstraint(ConstraintViolationException excecao) {
        List<DetalheErroValidacao> detalhes = excecao.getConstraintViolations()
                .stream()
                .map(violacao -> new DetalheErroValidacao(
                        normalizarCampo(violacao.getPropertyPath().toString()),
                        violacao.getMessage()
                ))
                .toList();

        String mensagem = detalhes.isEmpty()
                ? "Um ou mais campos da requisição são inválidos."
                : detalhes.get(0).mensagem();

        return respostaRequisicaoInvalida(mensagem, detalhes);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErroApiResposta> tratarCabecalhoAusente(MissingRequestHeaderException excecao) {
        String nomeCabecalho = excecao.getHeaderName();
        String mensagem = "Cabeçalho '" + nomeCabecalho + "' é obrigatório.";
        return respostaRequisicaoInvalida(mensagem, List.of(new DetalheErroValidacao(nomeCabecalho, mensagem)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroApiResposta> tratarParametroAusente(MissingServletRequestParameterException excecao) {
        String nomeParametro = excecao.getParameterName();
        String mensagem = "Parâmetro '" + nomeParametro + "' é obrigatório.";
        return respostaRequisicaoInvalida(mensagem, List.of(new DetalheErroValidacao(nomeParametro, mensagem)));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErroApiResposta> tratarVariavelCaminhoAusente(MissingPathVariableException excecao) {
        String nomeVariavel = excecao.getVariableName();
        String mensagem = "Parâmetro de rota '" + nomeVariavel + "' é obrigatório.";
        return respostaRequisicaoInvalida(mensagem, List.of(new DetalheErroValidacao(nomeVariavel, mensagem)));
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


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroApiResposta> tratarResponseStatus(ResponseStatusException excecao) {
        HttpStatus status = HttpStatus.resolve(excecao.getStatusCode().value());
        HttpStatus statusResolvido = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(statusResolvido)
                .body(new ErroApiResposta(codigoPorStatus(statusResolvido), excecao.getReason(), OffsetDateTime.now()));
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


    private String codigoPorStatus(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> "NAO_AUTENTICADO";
            case FORBIDDEN -> "ACESSO_NEGADO";
            case BAD_REQUEST -> "REQUISICAO_INVALIDA";
            default -> "ERRO_INTERNO";
        };
    }

    private ResponseEntity<ErroApiResposta> respostaRequisicaoInvalida(String mensagem, List<DetalheErroValidacao> detalhes) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroApiResposta(
                        "REQUISICAO_INVALIDA",
                        mensagem,
                        OffsetDateTime.now(),
                        detalhes
                ));
    }

    private DetalheErroValidacao mapearErroCampo(FieldError erroCampo) {
        return new DetalheErroValidacao(erroCampo.getField(), erroCampo.getDefaultMessage());
    }

    private String normalizarCampo(String nomeCampo) {
        String campoNormalizado = nomeCampo.contains(".")
                ? nomeCampo.substring(nomeCampo.lastIndexOf('.') + 1)
                : nomeCampo;

        return switch (campoNormalizado) {
            case "idTitularSolicitante" -> "idTitularSolicitante";
            default -> campoNormalizado;
        };
    }
}
