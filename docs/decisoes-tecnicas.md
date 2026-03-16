# Decisões Técnicas

## Premissas do desafio
A solução foi desenhada para representar um serviço de saldo robusto e evolutivo, sem integração real com serviços externos neste primeiro momento.

## Decisões adotadas
- Java 21 + Spring Boot como base tecnológica.
- Arquitetura hexagonal (ports and adapters) para desacoplamento.
- Serviço central de saldo integrado a ecossistema distribuído existente.
- Estrutura para observabilidade, tratamento global de erros e segurança com Spring Security OAuth2 Resource Server para autenticação JWT e separação da autorização por titularidade.
- Decisão explícita de manter autenticação (JWT e montagem de principal) desacoplada da autorização por titularidade no caso de uso, seguindo os limites da arquitetura hexagonal.

## Refinamento pós-análise
Algumas decisões iniciais foram tomadas sob tempo curto de análise e refinadas após avaliação mais profunda do problema e dos requisitos não funcionais.

Esse refinamento técnico levou, entre outros pontos, a:
- troca de EC2 por ECS/Fargate + ALB para melhor operação e escalabilidade;
- substituição do discurso de microserviços por serviço central de saldo integrado a ecossistema distribuído;
- adoção explícita de arquitetura hexagonal;
- detalhamento do uso de SNS + SQS para desacoplamento de eventos;
- separação explícita entre autenticação e autorização por titularidade.
- separação explícita entre fluxo de API online (consulta/autorização de titularidade) e fluxo batch (carga massiva/reconciliação), preservando domínio compartilhado com responsabilidades operacionais distintas.

Esse movimento é tratado como amadurecimento técnico da solução, e não como contradição.


## Persistência refinada para disponibilidade e evolução
Após análise mais profunda do requisito de alta disponibilidade, a estratégia de persistência foi refinada:
- execução local com H2 + JPA para desenvolvimento e validação rápida, sem dependência de infraestrutura externa;
- desenho conceitual de produção com DynamoDB via adapter dedicado no profile `aws`, priorizando disponibilidade gerenciada e escalabilidade horizontal;
- manutenção do domínio desacoplado da persistência (entidades JPA isoladas na infraestrutura), aderente à arquitetura hexagonal.


## Consolidação da separação API/Batch
Como parte do amadurecimento técnico, a solução passou a documentar de forma objetiva a separação entre:
- **API online**, responsável por consulta síncrona e autorização de titularidade;
- **Batch**, responsável por carga massiva consolidada e reconciliação;
- **domínio compartilhado**, reaproveitado por ambos os fluxos, com limites de responsabilidade explícitos.

Essa decisão reduz ambiguidades de escopo, facilita evolução independente de operação online e processamento em lote, e reforça os limites arquiteturais já definidos nas ADRs.

A integração real com NFS/AWS permanece fora do escopo deste teste técnico, mantendo apenas contratos e adaptadores preparados para evolução.

> Convenção de linguagem deste desafio: documentação e código em português; em contexto de projeto real, a convenção preferível é inglês técnico.
