# ADR-006: Separação entre autenticação e autorização de acesso ao saldo

## Status
Aceito

## Contexto
A consulta de saldo requer garantias de segurança em dois níveis distintos:
1. validar identidade e integridade do token de acesso;
2. garantir que o usuário autenticado só consulte saldos dos recursos sob sua titularidade.

Misturar esses níveis em um único ponto tende a reduzir clareza, dificultar testes e aumentar risco de regressões de segurança.

## Decisão
Separar explicitamente responsabilidades:
- **Autenticação** no boundary de entrada (resource server JWT): validação de token, assinatura, expiração, issuer/audience e extração de claims.
- **Autorização por titularidade** no caso de uso de consulta de saldo: validação de que o sujeito autenticado possui permissão para o recurso solicitado.

Essa separação preserva a arquitetura hexagonal: autenticação é preocupação de adapter de entrada; autorização de negócio é regra de aplicação/domínio.

## Alternativas
1. **Centralizar autenticação e autorização apenas no framework de segurança HTTP**
   - Prós: implementação inicial mais rápida.
   - Contras: regras de titularidade ficam acopladas ao transporte e menos reutilizáveis em outros canais.

2. **Delegar toda autorização a um serviço externo de política (PDP) desde já**
   - Prós: governança centralizada de políticas.
   - Contras: complexidade e dependência operacional desnecessárias para o escopo deste desafio.

3. **Separação autenticação (infra) + autorização por titularidade (caso de uso) (escolhida)**
   - Prós: clareza arquitetural, melhor testabilidade e menor acoplamento.
   - Contras: exige disciplina para evitar duplicação de regras entre camadas.

## Consequências
### Positivas
- Testes de autenticação podem focar filtros/configuração de segurança (integração).
- Testes de autorização por titularidade podem focar regra de negócio (unidade/aplicação), independentes de JWT real.
- Maior capacidade de reutilizar regra de autorização em API, batch assistido ou outros adaptadores.

### Trade-offs
- Mais contratos e cenários de teste para cobrir integralmente o fluxo seguro.
- Necessidade de mapear claims para identidade de negócio de forma consistente.

## Limitações de escopo do desafio
- Não inclui integração com IAM corporativo, IdP real ou política externa avançada (OPA/ABAC completo).
- Não cobre controles adicionais como step-up authentication ou consentimento transacional.
- A modelagem considera principalmente o cenário de titularidade direta no caso de uso de consulta.
