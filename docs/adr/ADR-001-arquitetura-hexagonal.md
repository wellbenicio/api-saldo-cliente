# ADR-001: Adoção de Arquitetura Hexagonal

## Status
Aceito

## Contexto
O serviço de saldo precisa integrar múltiplas fontes e destinos (API, batch, mensageria e publicação de eventos), com possibilidade de evolução sem acoplamento excessivo a frameworks e provedores.

## Decisão
Adotar arquitetura hexagonal (ports and adapters) como arquitetura principal do serviço.

## Consequências
### Positivas
- Melhor separação de responsabilidades.
- Facilidade para testes de aplicação e domínio.
- Troca simplificada de adaptadores técnicos.

### Negativas
- Maior quantidade inicial de classes e contratos.
- Curva de aprendizado para equipes acostumadas a abordagem puramente MVC.
