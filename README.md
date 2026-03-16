# API Saldo Cliente

## Quickstart

### 1) Pré-requisitos
- Java 21 instalado.
- Maven Wrapper disponível no projeto (`./mvnw`).
- Portas usadas localmente:
  - `8080`: API HTTP.

### 2) Bootstrap local
1. **Build do projeto**
   - `./mvnw clean install`
2. **Execução da API em profile local**
   - `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
   - (alternativa default já suportada) `./mvnw spring-boot:run`
3. **Execução de testes**
   - `./mvnw test`

### 3) Critério de sucesso esperado
- Aplicação sobe sem erros e o healthcheck responde em `http://localhost:8080/actuator/health`.

### 4) Próximos passos (seções avançadas)
- Batch: seção [Fluxo batch oficial](#fluxo-batch-oficial).
- AWS exemplo: seções [Persistência por profile](#persistência-por-profile) e [Infraestrutura (esqueleto Terraform)](#infraestrutura-esqueleto-terraform).
- ADRs: seção [Decisões arquiteturais consolidadas](#decisões-arquiteturais-consolidadas) e diretório `docs/adr/`.

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
- **Batch (carga massiva consolidada e reconciliação):** fluxo oficial implementado com Spring Batch (`FlatFileItemReader` + processor + writer) para processar arquivo consolidado sem bloquear o fluxo online.
- **Domínio compartilhado, responsabilidades diferentes:** API e Batch reutilizam o mesmo núcleo de domínio e contratos de aplicação, mas com responsabilidades operacionais distintas.

## Segurança nesta fase
- A API usa **Spring Security OAuth2 Resource Server** para autenticação via JWT Bearer Token.
- O **único fluxo de autenticação ativo** usa `oauth2ResourceServer().jwt(...)` com `ConversorJwtAutenticacao`, montando o principal de domínio `PrincipalConta` com:
  - identificador do cliente por `idCliente` (preferencial) ou `sub`;
  - documento obrigatório por `documento`, `cpf` ou `cnpj`;
  - perfis/scopes extraídos de authorities e dos claims `perfisOuScopes`, `scope` ou `scp`.
- Não há filtro JWT customizado nem validador JWT legado participando da autenticação em runtime.
- A autorização de negócio por titularidade permanece no caso de uso: mesmo autenticado, o usuário só pode consultar saldo quando for titular da conta (comparação entre o `idTitular` da conta e o `idCliente` autenticado).
- Essa separação evita acoplamento entre prova de identidade (autenticação) e regra de acesso ao recurso de saldo (autorização por titularidade).


## Estratégia de testes
- **Teste unitário (aplicação/domínio):** valida a regra do caso de uso `ServicoConsultaSaldoConta` de forma isolada, com mock/stub da porta de saída (`RepositorioSaldoContaPortaSaida`) para garantir cenários de titular autorizado, titular não autorizado e conta inexistente.
- **Teste de integração (HTTP + segurança):** valida a cadeia completa da API no endpoint `/v1/contas/{idConta}/saldo`, incluindo autenticação/autorização e contratos HTTP (status 200/403/401), além do payload de erro retornado pela camada web/security.

## Persistência por profile
- **local**: usa JPA + H2 em memória para permitir execução rápida, isolamento de testes e sem dependências externas.
- **aws-exemplo** (conceitual): usa adaptador esqueleto profissional para DynamoDB, com configuração separada e comentários sobre tabela, região, endpoint e credenciais.

> Neste desafio, o adaptador AWS é propositalmente não integrado para manter foco em arquitetura e separação de responsabilidades.


## Estratégia explícita de profiles
- `local`: execução padrão da API com adaptadores JPA/H2 e web app ativo.
- `batch`: habilita o job batch e desativa camada web (`web-application-type: none`).
- `aws-exemplo`: ativa apenas os componentes de exemplo para persistência AWS (DynamoDB), sem integração real.

> Para processar o batch com persistência local, execute com perfis combinados: `batch,local`.

## Comandos de execução
- **Build do projeto:**
  - `./mvnw clean install`
- **API local (default / onboarding):**
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
  - ou `./mvnw spring-boot:run`
- **Testes:**
  - `./mvnw test`
- **Batch com persistência local (recomendado no repositório):**
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local`
- Batch com arquivo customizado:
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local -Dspring-boot.run.arguments="--saldo.batch.diretorio-entrada=/tmp --saldo.batch.nome-arquivo=saldos.csv --saldo.batch.delimitador=|"`
- Profile AWS de exemplo:
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=aws-exemplo`

## 3) Aprofundamento
Para visão detalhada de arquitetura, decisões técnicas e material de suporte:
- Arquitetura: `docs/arquitetura.md`
- Decisões técnicas: `docs/decisoes-tecnicas.md`
- ADRs: `docs/adr/`
- Observabilidade: `docs/observabilidade.md`
- Operação batch detalhada: `docs/operacao-batch.md`
