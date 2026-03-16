# Operação Batch

Este documento concentra o detalhamento da trilha batch oficial e o mapeamento de classes.

## Fluxo batch oficial

O fluxo batch oficial desta base está centralizado no pacote `infraestrutura.batch`:

1. `ConfiguracaoImportacaoSaldoBatch` define o `Job` e o `Step` de importação.
2. `LeitorRegistroArquivoSaldoBatch` cria `FlatFileItemReader` para ler CSV/arquivo delimitado.
3. `ProcessadorRegistroSaldoBatch` converte o registro bruto para `SaldoConta` de domínio.
4. `EscritorSaldoContaBatch` persiste os saldos pela porta `RepositorioSaldoContaPortaSaida`.

A configuração de arquivo/delimitador fica centralizada em `PropriedadesBatchSaldo`:
- `saldo.batch.diretorio-entrada`
- `saldo.batch.nome-arquivo`
- `saldo.batch.delimitador`

## Mapeamento de classes batch

### Ativas (oficiais)
- `infraestrutura.batch.configuracao.ConfiguracaoImportacaoSaldoBatch`
- `infraestrutura.batch.componentes.LeitorRegistroArquivoSaldoBatch`
- `infraestrutura.batch.componentes.ProcessadorRegistroSaldoBatch`
- `infraestrutura.batch.componentes.EscritorSaldoContaBatch`
- `infraestrutura.batch.componentes.RegistroArquivoSaldoBatch`

### Legadas (removidas da trilha principal)
- `infraestrutura.batch.LeitorRegistroArquivoSaldoBatchItemReader`
- `infraestrutura.batch.ProcessadorRegistroArquivoSaldoBatchItemProcessor`
- `infraestrutura.batch.modelo.RegistroArquivoSaldoBatch`
- `infraestrutura.adaptador.saida.batch.LeitorArquivoBatchSaldoNfsAdaptador`
