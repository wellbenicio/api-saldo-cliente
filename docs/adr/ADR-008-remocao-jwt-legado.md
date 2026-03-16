# ADR-008: Remoção de classes JWT legadas e centralização no Resource Server

## Status
Aceito

## Contexto
A solução já utiliza autenticação oficial por meio do Spring Security OAuth2 Resource Server com `oauth2ResourceServer().jwt(...)` e `ConversorJwtAutenticacao`.

Havia classes legadas em `infraestrutura.seguranca.jwt` (filtro e validador manual) mantidas de forma transitória, sem participação no fluxo ativo de autenticação/autorização.

Isso criava ruído arquitetural e duplicidade conceitual sobre qual estratégia JWT é, de fato, suportada.

## Decisão
Remover as classes JWT legadas não utilizadas no fluxo ativo:
- `FiltroAutenticacaoJwt`;
- `ValidadorTokenJwt`.

Manter a configuração oficial de decoder/Resource Server como estratégia única de autenticação JWT da aplicação.

## Consequências
### Positivas
- Elimina ambiguidade sobre o caminho de autenticação suportado.
- Reduz custo de manutenção de código inativo.
- Reforça coerência entre arquitetura documentada e implementação efetiva.

### Trade-offs
- Perde-se um esqueleto de validação manual que poderia servir como referência histórica.
- Caso seja necessário suporte futuro fora do Resource Server, a solução deverá ser redesenhada explicitamente, e não reativada de forma implícita.
