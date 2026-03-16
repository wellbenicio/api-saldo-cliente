# API Saldo Cliente

## Objetivo do projeto
Este projeto representa uma API backend em Java 21 com Spring Boot para consulta de saldo de conta por canais bancários, com foco em qualidade técnica, arquitetura e clareza de evolução para ambiente corporativo.

## Contexto do desafio
- Ecossistema bancário distribuído, com legado em mainframe.
- Arquivo batch consolidado de saldos (~50GB) gerado às 2AM e disponibilizado via servidor de arquivos/NFS.
- Mensagens de atualização de saldo via MQ a cada nova transação.
- Consulta de saldo permitida somente ao titular da conta.
- Necessidade de alta disponibilidade, observabilidade e publicação de saldo atualizado para outros sistemas na AWS.
- Neste repositório, integrações com AWS/MQ/NFS são estruturadas como adaptadores e configurações, sem integração real.

## Visão geral da arquitetura
A solução adota arquitetura hexagonal (ports and adapters), com separação em:
- `dominio`: regras e modelos centrais.
- `aplicacao`: casos de uso e portas de entrada/saída.
- `infraestrutura`: adaptadores técnicos (HTTP, batch, mensageria, persistência).
- `compartilhado`: componentes transversais (ex.: tratamento global de erro).

## Separação explícita entre API e Batch
- **API (consulta online/autorização de titularidade):** atende requisições síncronas de saldo, valida identidade via JWT e aplica autorização de titularidade no caso de uso.
- **Batch (carga massiva consolidada e reconciliação):** processa cargas de grande volume e prepara reconciliação de dados sem bloquear o fluxo online.
- **Domínio compartilhado, responsabilidades diferentes:** API e Batch reutilizam o mesmo núcleo de domínio e contratos de aplicação, mas com responsabilidades operacionais distintas.

## Justificativa para classes em português
Para este desafio técnico, classes, métodos e pacotes foram nomeados em português como escolha simbólica e para manter consistência com o enunciado.

## Observação sobre convenção real de mercado
Em projeto real, a convenção preferível é utilizar nomes em inglês para código, pacotes e artefatos técnicos, visando padronização internacional e melhor interoperabilidade entre times.

> Convenção explícita deste repositório: **português neste desafio; inglês preferível em projeto real**.

## Segurança nesta fase
- A API usa **Spring Security OAuth2 Resource Server** para autenticação via JWT Bearer Token.
- O fluxo de autenticação usa `oauth2ResourceServer().jwt(...)` com `ConversorJwtAutenticacao`, montando o principal de domínio `PrincipalConta` a partir dos claims (`idTitular`, `titular_id` ou `sub`).
- A autorização de negócio por titularidade permanece no caso de uso: mesmo autenticado, o usuário só pode consultar saldo quando for titular da conta.
- Essa separação evita acoplamento entre prova de identidade (autenticação) e regra de acesso ao recurso de saldo (autorização por titularidade).


## Estratégia de testes
- **Teste unitário (aplicação/domínio):** valida a regra do caso de uso `ServicoConsultaSaldoConta` de forma isolada, com mock/stub da porta de saída (`RepositorioSaldoContaPortaSaida`) para garantir cenários de titular autorizado, titular não autorizado e conta inexistente.
- **Teste de integração (HTTP + segurança):** valida a cadeia completa da API no endpoint `/v1/contas/{idConta}/saldo`, incluindo autenticação/autorização e contratos HTTP (status 200/403/401), além do payload de erro retornado pela camada web/security.

## Persistência por profile
- **local**: usa JPA + H2 em memória para permitir execução rápida, isolamento de testes e sem dependências externas.
- **aws** (conceitual): usa adaptador esqueleto profissional para DynamoDB, com configuração separada e comentários sobre tabela, região, endpoint e credenciais.

> Neste desafio, o adaptador AWS é propositalmente não integrado para manter foco em arquitetura e separação de responsabilidades.

## Consumo de eventos de saldo atualizado (quase em tempo real)

Para complementar o batch consolidado, o projeto agora inclui a estrutura de consumo de eventos de saldo via mensageria de entrada simulada (MQ/JMS), mantendo desacoplamento total do controller HTTP.

Fluxo arquitetural:
1. `ConsumidorSaldoMqJmsSimuladoAdaptador` recebe a mensagem (simulada).
2. O adaptador chama a porta de entrada `ConsumirEventoSaldoAtualizadoPortaEntrada`.
3. `ServicoProcessamentoEventoSaldoAtualizado` aplica regras de negócio:
   - idempotência por `idEvento`;
   - ignorar evento duplicado;
   - ignorar evento fora de ordem (desatualizado) para não sobrescrever saldo mais novo.
4. Persistência é feita via `RepositorioSaldoContaPortaSaida`.
5. Registro de evento processado via `RepositorioEventoProcessadoPortaSaida`.

> Importante: em ambiente real, o listener seria integrado a IBM MQ/JMS com configuração segura de host, channel, queue manager e credenciais vindas de secret manager. Neste desafio, a integração é propositalmente simulada.

### Convenção de nomes
Neste desafio, classes/pacotes estão em português por escolha simbólica.
Em projeto real de mercado, a convenção preferível continua sendo nomes em inglês.
