# ADR-005: Estratégia conceitual de execução e deploy (ECS/Fargate + ALB)

## Status
Aceito

## Contexto
O serviço de saldo precisa de uma estratégia de execução com foco em disponibilidade, operação previsível e capacidade de evolução para um ambiente corporativo. Mesmo sendo um desafio técnico sem infraestrutura real provisionada, é importante explicitar uma topologia-alvo para orientar decisões de arquitetura, observabilidade e segurança operacional.

## Decisão
Adotar, de forma **conceitual para este desafio**, a seguinte estratégia de deploy:
- execução da aplicação em contêineres no **Amazon ECS com Fargate**;
- exposição da API por meio de **Application Load Balancer (ALB)**;
- múltiplas tasks por serviço (quando aplicável) para alta disponibilidade entre zonas;
- uso de health checks do ALB e do orquestrador para substituição automática de instâncias não saudáveis.

## Alternativas
1. **EC2 com autoscaling e gerenciamento de host**
   - Prós: maior controle de runtime e tuning de sistema operacional.
   - Contras: maior carga operacional (patching, hardening e manutenção de hosts).

2. **Kubernetes (EKS) desde o início**
   - Prós: ecossistema robusto e portabilidade entre ambientes.
   - Contras: complexidade operacional maior para o escopo deste desafio.

3. **PaaS simplificado (ex.: App Runner/Elastic Beanstalk)**
   - Prós: onboarding rápido e menor esforço inicial.
   - Contras: menor flexibilidade para requisitos mais específicos de rede, segurança e observabilidade.

4. **ECS/Fargate + ALB (escolhida)**
   - Prós: bom equilíbrio entre simplicidade operacional e controle arquitetural.
   - Contras: lock-in moderado no ecossistema AWS e custos por tarefa em execução contínua.

## Consequências
### Positivas
- Redução de esforço de operação de infraestrutura base (sem gestão de hosts).
- Padronização de deploy containerizado para API e componentes correlatos.
- Facilidade para implementar estratégia de rolling deploy e escalabilidade horizontal.

### Trade-offs
- Dependência conceitual de serviços AWS para topologia-alvo.
- Necessidade de definir políticas claras de custo e escalonamento para evitar sobreprovisionamento.
- Requer maturidade mínima em configuração de rede, health checks, logs e métricas.

## Limitações de escopo do desafio
- Não há provisionamento real de ECS/Fargate/ALB neste repositório.
- A decisão documenta o direcionamento arquitetural, sem pipeline completo de deploy em produção.
- Hardening detalhado (WAF, mTLS, service mesh, políticas avançadas de rede) fica como evolução futura.
