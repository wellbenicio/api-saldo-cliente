# Decisões Técnicas

## Premissas do desafio
A solução foi desenhada para representar um serviço central de saldo robusto e evolutivo, sem integração real com serviços externos neste primeiro momento.

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

## Escopo de evolução
A integração real com NFS/AWS permanece fora do escopo deste teste técnico, mantendo apenas contratos e adaptadores preparados para evolução.

## Situação atual validada
- A suíte de testes (`./mvnw test`) executa localmente com sucesso.
- O profile `local` não depende de AWS, MQ ou NFS reais para subir os componentes centrais.
- A autenticação JWT continua obrigatória para os endpoints de negócio, mas usa decoder HS256 local com segredo configurável em propriedade (`seguranca.jwt.segredo-assinatura`), sem dependência de IdP externo neste repositório.
