# ADR-004 - Publicação assíncrona de eventos de saldo atualizado com SNS fanout

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

## Consequências
### Positivas
- Reduz acoplamento entre serviço de saldo e sistemas consumidores.
- Facilita evolução incremental dos consumidores sem alterar o produtor.
- Aumenta resiliência operacional via buffering/reprocessamento por SQS.
- Mantém alinhamento com arquitetura hexagonal (porta no núcleo e adapters na borda).

### Trade-offs
- Sem padrão Outbox, ainda existe risco de inconsistência entre persistência e publicação em falha parcial.
- Introduz necessidade futura de governança de esquema de evento e versionamento.

## Evolução recomendada
Implementar padrão Outbox transacional em etapa futura:
1. persistir evento em tabela outbox no mesmo commit da atualização de saldo;
2. processo assíncrono dedicado publica no SNS;
3. marcar/reconciliar status de publicação com observabilidade e retry.

## Observação sobre nomenclatura
Neste desafio, nomes estão em português por escolha simbólica. Em projeto real, a preferência é nomenclatura técnica em inglês.
