# Observabilidade

## Visão geral
Este projeto usa observabilidade básica para operação local e como base de evolução para produção:
- **Logs estruturados** em JSON no console (`logback-spring.xml`);
- **Métricas** via Spring Actuator + Micrometer (`/actuator/metrics`);
- **Correlation ID** por requisição HTTP via `X-Correlation-Id`.

> Convenção deste desafio: classes/pacotes em português por escolha simbólica. Em projeto real, a preferência geral seria por nomenclatura em inglês.

## Logs
- Configurados em `src/main/resources/logback-spring.xml`.
- Campos principais:
  - `timestamp`
  - `nivel`
  - `aplicacao`
  - `thread`
  - `logger`
  - `correlationId`
  - `mensagem`
  - `excecao`

Como funciona:
1. O filtro `FiltroCorrelationId` lê o header `X-Correlation-Id` ou gera UUID.
2. O valor é colocado no MDC (`correlationId`).
3. O appender inclui `correlationId` no JSON de log.

## Métricas
Expostas em `/actuator/metrics`.

Métricas customizadas implementadas:
- `saldo_consultas_total`
  - Incrementada em `ServicoConsultaSaldoConta` ao concluir consulta autorizada.
- `saldo_negacoes_acesso_total`
  - Incrementada em `ServicoConsultaSaldoConta` quando há quebra de titularidade.
  - Também incrementada em `ManipuladorAcessoNegado` para negação via camada de segurança.
- `saldo_falhas_batch_total`
  - Incrementada por `MonitoramentoFalhaBatchListener` quando step batch registra exceções.
- `saldo_falhas_processamento_evento_total`
  - Incrementada em `ServicoProcessamentoEventoSaldoAtualizado` no bloco de falha de processamento/publicação.

Métricas e endpoints do Actuator habilitados em `application.properties`:
- `health`
- `info`
- `metrics`

## Traces / Correlation ID
Embora tracing distribuído completo (OpenTelemetry/Zipkin/X-Ray) não esteja habilitado no desafio, há rastreabilidade básica ponta a ponta:
- Header de entrada/saída: `X-Correlation-Id`.
- Inclusão no MDC e logs estruturados.
- Uso recomendado: propagar o mesmo header para chamadas entre serviços.

Em ambiente real, evoluir para:
- OpenTelemetry SDK + Collector;
- Export para CloudWatch/X-Ray/Tempo;
- correlação entre logs, métricas e spans.

## Alarmes recomendados
Alarmes sugeridos para operação:
1. **Falhas no processamento de eventos**
   - métrica: `saldo_falhas_processamento_evento_total`
   - severidade: alta
2. **Falhas no batch**
   - métrica: `saldo_falhas_batch_total`
   - severidade: alta (janela pós-2AM)
3. **Aumento de negações de acesso**
   - métrica: `saldo_negacoes_acesso_total`
   - severidade: média/alta (pode indicar fraude ou erro de autorização)
4. **Taxa de erro HTTP 5xx**
   - origem: ALB/API Gateway + aplicação
   - severidade: alta
5. **Liveness/Readiness indisponível**
   - endpoint: `/actuator/health`
   - severidade: crítica

A estrutura Terraform de exemplo para CloudWatch + SNS está em `infra/terraform/` e é propositalmente um **esqueleto comentado** para este desafio.
