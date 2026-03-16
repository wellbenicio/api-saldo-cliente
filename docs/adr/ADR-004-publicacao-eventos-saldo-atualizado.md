# ADR-004: Publicação assíncrona de eventos de saldo atualizado com SNS fanout

## Status
Aceito

## Contexto
Após processar atualizações de saldo por mensageria de entrada, o serviço central de saldo precisa notificar outros sistemas do ecossistema sem acoplamento direto entre produtor e consumidores.

Neste desafio técnico, não haverá integração real com AWS, mas a arquitetura deve deixar explícito onde essa infraestrutura entra e como seria evoluída em ambiente corporativo.

## Decisão
- Publicar evento de integração `EventoIntegracaoSaldoAtualizado` por uma porta de saída dedicada da aplicação.
- Usar `SNS` como barramento de fanout no cenário AWS.
- Consumidores devem assinar o tópico por filas `SQS`, permitindo isolamento por consumidor e políticas independentes de retry/DLQ.
- Neste repositório, manter dois adaptadores de saída:
  - local (`log estruturado`) para execução sem dependência externa;
  - AWS esqueleto (`SNS`) apenas com documentação e pontos de extensão.

## Alternativas
1. **Integração síncrona ponto a ponto (HTTP direto)**
   - Prós: simplicidade inicial para poucos consumidores.
   - Contras: acoplamento forte, propagação de falhas e baixa escalabilidade organizacional.

2. **Fila única sem fanout**
   - Prós: menor complexidade inicial.
   - Contras: acoplamento entre consumidores e competição por mensagens.

3. **Publicação via SNS + SQS por consumidor (escolhida)**
   - Prós: desacoplamento, isolamento por consumidor e robustez operacional.
   - Contras: maior governança de contratos de evento e operação de múltiplas filas.

## Consequências
### Positivas
- Reduz acoplamento entre serviço de saldo e sistemas consumidores.
- Facilita evolução incremental dos consumidores sem alterar o produtor.
- Aumenta resiliência operacional via buffering/reprocessamento por SQS.
- Mantém alinhamento com arquitetura hexagonal (porta no núcleo e adapters na borda).

### Trade-offs
- Sem padrão Outbox, ainda existe risco de inconsistência entre persistência e publicação em falha parcial.
- Introduz necessidade futura de governança de esquema de evento e versionamento.

## Limitações de escopo do desafio
- Não inclui integração real com SNS/SQS neste repositório.
- Não contempla implementação completa de Outbox transacional nesta etapa.
- Observabilidade operacional de mensageria permanece em nível conceitual/documental.
