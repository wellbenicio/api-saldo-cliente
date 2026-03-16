# ADR-001: Adoção de Arquitetura Hexagonal

## Status
Aceito

## Contexto
O serviço de saldo precisa integrar múltiplas fontes e destinos (API, batch, mensageria e publicação de eventos), com possibilidade de evolução sem acoplamento excessivo a frameworks e provedores.

## Decisão
Adotar arquitetura hexagonal (ports and adapters) como arquitetura principal do serviço.

## Alternativas
1. **Arquitetura em camadas tradicional (controller-service-repository)**
   - Prós: curva de adoção menor para times já acostumados.
   - Contras: risco de acoplamento do domínio com infraestrutura.

2. **Arquitetura orientada a microsserviços desde o início**
   - Prós: isolamento de deploy por capacidade.
   - Contras: complexidade operacional excessiva para o escopo do desafio.

3. **Arquitetura hexagonal (escolhida)**
   - Prós: separação clara de responsabilidades e adaptabilidade.
   - Contras: maior número inicial de classes/contratos.

## Consequências
### Positivas
- Melhor separação de responsabilidades.
- Facilidade para testes de aplicação e domínio.
- Troca simplificada de adaptadores técnicos.

### Trade-offs
- Maior quantidade inicial de classes e contratos.
- Curva de aprendizado para equipes acostumadas a abordagem puramente MVC.

## Limitações de escopo do desafio
- Nem todos os adaptadores previstos estão implementados com integração real.
- Parte da infraestrutura permanece como desenho/contrato para evolução futura.
