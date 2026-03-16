# Arquitetura da Solução

## Visão geral
Este projeto foi estruturado como um serviço central de saldo dentro de um ecossistema distribuído bancário, adotando arquitetura hexagonal para isolar regras de negócio de detalhes de infraestrutura.

Camadas:
- **Domínio**: modelos e exceções do negócio de saldo.
- **Aplicação**: portas e serviços de orquestração dos casos de uso.
- **Infraestrutura**: adaptadores de entrada e saída (HTTP, leitura batch, publicação de eventos, repositório).
- **Compartilhado**: tratamento global de erro e objetos transversais.


## Separação explícita entre API e Batch
- **API (consulta online/autorização de titularidade):** fluxo síncrono orientado a baixa latência para consulta de saldo, com autenticação JWT e autorização por titularidade aplicada no caso de uso.
- **Batch (carga massiva consolidada e reconciliação):** fluxo assíncrono para ingestão de massa e reconciliação periódica, preparado para processar grandes volumes sem impacto direto na experiência online.
- **Domínio compartilhado, responsabilidades diferentes:** ambos os fluxos usam o mesmo domínio e portas de aplicação, porém com objetivos operacionais distintos e ciclos de execução diferentes.

## Fluxo da API
1. Cliente envia requisição ao endpoint de saldo com `Authorization: Bearer <token JWT>`.
2. Camada de segurança valida o token JWT via Spring Security OAuth2 Resource Server.
3. Após validação, `ConversorJwtAutenticacao` monta o principal `PrincipalConta` com `idTitular`.
4. Adaptador HTTP encaminha `idConta` e o `idTitular` autenticado para a porta de entrada da aplicação.
5. Caso de uso executa a regra de autorização por titularidade, verificando se o usuário autenticado é titular da conta consultada.
6. Se autorizado, o caso de uso consulta a porta de saída de saldo e devolve resposta de sucesso.
7. Se não autorizado, a aplicação retorna erro de acesso; demais erros de domínio seguem para o handler global.

## Fluxo batch
1. Arquivo consolidado (~50GB) é disponibilizado no ambiente de arquivos (NFS).
2. Adaptador de leitura batch representa o ponto de integração com esse arquivo.
3. Em implementação futura, o conteúdo lido será processado e persistido pelo fluxo de aplicação.
4. Neste estágio, a estrutura está preparada sem processamento complexo.

## Fluxo de eventos
1. Atualizações de saldo chegam por mensageria (ex.: MQ em cenário real).
2. Serviço de aplicação atualiza estado de saldo.
3. Evento de domínio `EventoSaldoAtualizado` é criado.
4. Porta de saída publica evento para ecossistema AWS (SNS/SQS em cenário real).
5. Adaptador de publicação contém comentários para configuração por variáveis de ambiente/secrets.


## Estratégia de persistência
- No **profile local**, a porta de repositório de saldo é atendida por adaptador JPA/H2 com entidades de infraestrutura, preservando o domínio limpo (sem anotações JPA nos records de domínio).
- Também no local, há persistência de **eventos processados** para suportar deduplicação/idempotência em evoluções de consumo de fila.
- No **profile aws**, existe um adaptador esqueleto para DynamoDB com configuração dedicada (tabela, região, endpoint e credenciais via IAM role/secrets/variáveis de ambiente).
- A integração real com AWS está fora do escopo deste teste técnico, mas a estrutura foi deixada pronta para evolução segura.


> Convenção de linguagem adotada: **português neste desafio**; em projeto real, a preferência é por nomenclatura técnica em **inglês**.
