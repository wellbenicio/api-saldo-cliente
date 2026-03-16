# API Saldo Cliente

## Índice
- [1) Visão rápida](#1-visão-rápida)
  - [Objetivo](#objetivo)
  - [Stack](#stack)
  - [Quickstart](#quickstart)
  - [Endpoint principal](#endpoint-principal)
- [2) Operação local](#2-operação-local)
  - [Profiles](#profiles)
  - [Batch](#batch)
  - [Observabilidade](#observabilidade)
  - [Comandos úteis](#comandos-úteis)
- [3) Aprofundamento](#3-aprofundamento)

## 1) Visão rápida

### Objetivo
API backend em Java 21 + Spring Boot para consulta de saldo por conta, com autenticação JWT, autorização por titularidade e trilha batch para carga consolidada de saldos.

### Stack
- Java 21
- Spring Boot (Web, Security OAuth2 Resource Server, Actuator)
- Spring Batch
- Spring Data JPA + H2 (profile `local`)
- Estrutura pronta para adaptadores AWS/MQ/NFS (conceitual neste repositório)

### Quickstart
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Endpoint principal
- `GET /v1/contas/{idConta}/saldo`
- Requer Bearer Token JWT
- Retorna saldo apenas quando o cliente autenticado é titular da conta

## 2) Operação local

### Profiles
- `local`: API web com persistência JPA/H2.
- `batch`: executa job batch e desativa camada web (`web-application-type: none`).
- `aws-exemplo`: ativa componentes de exemplo para DynamoDB (sem integração real).

> Para rodar batch com persistência local: `batch,local`.

### Batch
Fluxo oficial (resumo):
1. Configuração de `Job`/`Step`.
2. Leitura de arquivo consolidado (`FlatFileItemReader`).
3. Processamento de registros para domínio.
4. Persistência pela porta de saída.

Detalhamento de classes oficiais/legadas e mapeamento completo:
- `docs/operacao-batch.md`

### Observabilidade
- Actuator habilitado (`health`, `info`, `metrics`)
- Correlation ID por requisição (`X-Correlation-Id`)
- Logs estruturados em JSON
- Métricas de negócio/técnicas para consulta, autorização, batch e eventos

Detalhes:
- `docs/observabilidade.md`

### Comandos úteis
- API local:
  - `./mvnw spring-boot:run`
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
- Batch local:
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
