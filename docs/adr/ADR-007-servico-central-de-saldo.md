# ADR-007: Serviço central de saldo no escopo do desafio

## Status
Aceito

## Contexto
O domínio de saldo poderia, em um ambiente real, ser decomposto em múltiplos microsserviços (consulta, reconciliação, ingestão de eventos, projeções, antifraude etc.). No entanto, para o objetivo de avaliação técnica, há necessidade de equilíbrio entre clareza arquitetural e complexidade operacional.

## Decisão
Formalizar que este repositório representa um **serviço central de saldo** com responsabilidades bem delimitadas e separação interna por portas/adaptadores, sem decomposição em múltiplos microsserviços independentes no escopo atual.

## Alternativas
1. **Decompor em vários microsserviços desde o início**
   - Prós: isolamento de deploy e escalabilidade por capacidade específica.
   - Contras: aumento expressivo de complexidade (rede, observabilidade, contratos, consistência distribuída).

2. **Monólito sem fronteiras arquiteturais internas**
   - Prós: menor custo inicial de implementação.
   - Contras: risco alto de acoplamento e dificuldade de evolução.

3. **Serviço central com arquitetura hexagonal e separação interna de fluxos (escolhida)**
   - Prós: mantém simplicidade operacional com fronteiras claras para evolução.
   - Contras: futuras extrações para microsserviços exigirão governança de contratos e migração incremental.

## Consequências
### Positivas
- Entrega mais objetiva para o desafio, mantendo qualidade de desenho arquitetural.
- Facilita evolução posterior por extração seletiva de capacidades quando houver evidência de necessidade.
- Evita custos prematuros de operação distribuída.

### Trade-offs
- Escalabilidade e deploy independentes por subcapacidade ficam limitados no estado atual.
- Algumas decisões de consistência e concorrência distribuída ficam postergadas para fases futuras.

## Limitações de escopo do desafio
- Não contempla malha de serviços, descoberta dinâmica, tracing distribuído multi-serviço completo nem governança de APIs entre múltiplos times.
- A decisão não invalida futura decomposição; apenas evita complexidade prematura neste ciclo de avaliação.
