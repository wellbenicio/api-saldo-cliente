package br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.componentes;

/**
 * Registro bruto lido do arquivo de entrada do batch.
 */
public record RegistroArquivoSaldoBatch(
    String idConta,
    String idTitular,
    String valor,
    String moeda,
    String atualizadoEm
) {
}
