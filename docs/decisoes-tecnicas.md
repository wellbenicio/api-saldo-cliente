# Decisões Técnicas

## Premissas do desafio
A solução foi desenhada para representar um serviço de saldo robusto e evolutivo, sem integração real com serviços externos neste primeiro momento.

## Decisões adotadas
- Java 21 + Spring Boot como base tecnológica.
- Arquitetura hexagonal (ports and adapters) para desacoplamento.
- Serviço central de saldo integrado a ecossistema distribuído existente.
- Estrutura para observabilidade, tratamento global de erros e segurança por titularidade.

## Refinamento pós-análise
Algumas decisões iniciais foram tomadas sob tempo curto de análise e refinadas após avaliação mais profunda do problema e dos requisitos não funcionais.

Esse refinamento técnico levou, entre outros pontos, a:
- troca de EC2 por ECS/Fargate + ALB para melhor operação e escalabilidade;
- substituição do discurso de microserviços por serviço central de saldo integrado a ecossistema distribuído;
- adoção explícita de arquitetura hexagonal;
- detalhamento do uso de SNS + SQS para desacoplamento de eventos;
- separação explícita entre autenticação e autorização por titularidade.

Esse movimento é tratado como amadurecimento técnico da solução, e não como contradição.
