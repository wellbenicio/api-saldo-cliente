# Decisões Técnicas

## Premissas do desafio
A solução foi desenhada para representar um serviço de saldo robusto e evolutivo, sem integração real com serviços externos neste primeiro momento.

## O que foi mantido
- Java 21 + Spring Boot como base tecnológica.
- Foco em backend.
- Separação de responsabilidades entre camadas e casos de uso.
- Testes como prática de qualidade.
- Observabilidade básica (logs, tratamento global de erros e pontos de monitoramento).

## O que foi alterado
- Estratégia conceitual de execução em nuvem: de **EC2** para **ECS/Fargate + ALB**, visando melhor operação e escalabilidade.
- Posicionamento arquitetural: de discurso de decomposição em microserviços para **serviço central de saldo** integrado a ecossistema distribuído.
- Arquitetura principal explicitada como **hexagonal (ports and adapters)**.

## O que foi adicionado
- Detalhamento de mensageria com **SNS + SQS** para fanout de eventos e isolamento de consumidores.
- Separação explícita entre **autenticação** (JWT/identidade) e **autorização por titularidade** (regra de negócio).
- Fluxo de ingestão de eventos com idempotência por `idEvento`, descarte de duplicidade e proteção contra processamento fora de ordem.
- Clarificação dos fluxos **API online** (consulta/autorização de titularidade) e **batch** (carga massiva/reconciliação), com domínio compartilhado e responsabilidades operacionais distintas.

## Refinamento pós-análise (explícito)
As decisões iniciais foram tomadas sob tempo curto de análise. Após avaliação mais profunda dos requisitos e trade-offs operacionais, o desenho foi refinado.

Esse refinamento é tratado como **amadurecimento técnico da solução**, e não como contradição.

Refinamentos explicitamente consolidados:
- **EC2 -> ECS/Fargate + ALB** para melhorar elasticidade operacional e reduzir custo de gestão de infraestrutura.
- **Discurso de microsserviços -> serviço central de saldo** para refletir melhor o problema real e evitar decomposição artificial.
- **MVC -> arquitetura hexagonal** como arquitetura principal para reduzir acoplamento com frameworks.
- **Mensageria genérica -> SNS + SQS** com papéis claros (fanout no SNS, isolamento/retry/DLQ por consumidor em SQS).
- **JWT simples -> autenticação JWT validada + autorização por titularidade** no caso de uso.

## Escopo de evolução
A integração real com NFS/AWS permanece fora do escopo deste teste técnico, mantendo apenas contratos e adaptadores preparados para evolução.

## Referências consolidadas
- Arquitetura da solução: `docs/arquitetura.md`.
- Observabilidade operacional: `docs/observabilidade.md`.
- Operação batch: `docs/operacao-batch.md`.
