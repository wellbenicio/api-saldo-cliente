# API Saldo Cliente

API backend em **Java 21 + Spring Boot** para consulta de saldo por conta, com foco em arquitetura hexagonal, segurança por JWT, processamento batch e observabilidade.

> Convenção deste desafio: classes, métodos e pacotes em português como escolha simbólica de avaliação.
> Em um projeto corporativo real, a convenção recomendada seria nomenclatura em inglês.

## Sumário
- [Visão geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Execução local](#execução-local)
- [Perfis de execução](#perfis-de-execução)
- [API HTTP](#api-http)
- [Fluxo batch](#fluxo-batch)
- [Consumo de eventos](#consumo-de-eventos)
- [Segurança](#segurança)
- [Observabilidade](#observabilidade)
- [Documentação complementar](#documentação-complementar)
- [Infraestrutura (Terraform)](#infraestrutura-terraform)

## Visão geral
Este projeto simula um cenário bancário no qual:
- um arquivo batch consolidado de saldos é processado periodicamente;
- eventos de atualização de saldo chegam por mensageria (simulada);
- a consulta de saldo via API é permitida apenas ao titular da conta.

As integrações externas (AWS, MQ, NFS) estão representadas como **adaptadores/configurações conceituais**, sem conexão real neste repositório.

## Arquitetura
A base segue **arquitetura hexagonal (ports and adapters)**:
- `dominio`: modelos e regras de negócio;
- `aplicacao`: casos de uso e portas de entrada/saída;
- `infraestrutura`: adaptadores técnicos (HTTP, batch, segurança, persistência, mensageria);
- `compartilhado`: componentes transversais (ex.: tratamento global de exceções).

## Pré-requisitos
- Java 21
- Maven Wrapper (`./mvnw`)
- Porta local:
  - `8080` (API HTTP)

## Execução local
### Build
```bash
./mvnw clean install
```

### Subir API (perfil local)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
> Alternativa: `./mvnw spring-boot:run` (default local do projeto)

### Testes
```bash
./mvnw test
```

### Healthcheck esperado
```text
http://localhost:8080/actuator/health
```

## Perfis de execução
- `local`: API web + persistência local (JPA/H2)
- `batch`: execução do job batch com web desativada (`web-application-type: none`)
- `aws-exemplo`: ativa componentes conceituais de integração AWS

### Combinações úteis
**Batch com persistência local**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local
```

**Batch com arquivo customizado**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local -Dspring-boot.run.arguments="--saldo.batch.diretorio-entrada=/tmp --saldo.batch.nome-arquivo=saldos.csv --saldo.batch.delimitador=|"
```

**Perfil AWS de exemplo**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=aws-exemplo
```

## API HTTP
### Endpoint principal
`GET /v1/contas/{idConta}/saldo`

### Exemplo de requisição
```bash
curl -X GET "http://localhost:8080/v1/contas/12345/saldo" \
  -H "Authorization: Bearer <seu-jwt-aqui>" \
  -H "Accept: application/json"
```

### Respostas resumidas
**200 OK**
```json
{
  "idConta": "12345",
  "saldo": 1500.75,
  "moeda": "BRL",
  "atualizadoEm": "2025-01-10T14:30:00Z"
}
```

**401 Unauthorized**
```json
{
  "status": 401,
  "erro": "nao_autenticado",
  "mensagem": "Token ausente, inválido ou expirado"
}
```

**403 Forbidden**
```json
{
  "status": 403,
  "erro": "acesso_negado",
  "mensagem": "Usuário autenticado sem permissão para consultar a conta informada"
}
```

## Fluxo batch
Fluxo oficial no pacote `infraestrutura.batch`:
1. `ConfiguracaoImportacaoSaldoBatch` define `Job`/`Step`.
2. `LeitorRegistroArquivoSaldoBatch` lê arquivo delimitado.
3. `ProcessadorRegistroSaldoBatch` transforma em `SaldoConta`.
4. `EscritorSaldoContaBatch` persiste via porta `RepositorioSaldoContaPortaSaida`.

Propriedades principais (`PropriedadesBatchSaldo`):
- `saldo.batch.diretorio-entrada`
- `saldo.batch.nome-arquivo`
- `saldo.batch.delimitador`

## Consumo de eventos
Fluxo (mensageria simulada):
1. `ConsumidorSaldoMqJmsSimuladoAdaptador` recebe evento.
2. Chamada da porta `ConsumirEventoSaldoAtualizadoPortaEntrada`.
3. `ServicoProcessamentoEventoSaldoAtualizado` aplica regras:
   - idempotência por `idEvento`;
   - descarte de duplicados;
   - descarte de eventos fora de ordem.
4. Atualiza saldo via `RepositorioSaldoContaPortaSaida`.
5. Registra evento via `RepositorioEventoProcessadoPortaSaida`.

## Segurança
A autenticação utiliza **Spring Security OAuth2 Resource Server (JWT Bearer)** com `oauth2ResourceServer().jwt(...)`.

Conversão para principal de domínio (`PrincipalConta`) em `ConversorJwtAutenticacao` com:
- identificador do cliente: `idCliente` (preferencial) ou `sub`;
- documento obrigatório: `documento`, `cpf` ou `cnpj`;
- authorities/scopes: `perfisOuScopes`, `scope` ou `scp`.

A autorização de negócio (titularidade) ocorre no caso de uso: o `idTitular` da conta deve corresponder ao `idCliente` autenticado.

## Observabilidade
- Spring Actuator: `health`, `info`, `metrics`
- Correlation ID via `X-Correlation-Id` com MDC
- Logs estruturados JSON (`logback-spring.xml`)
- Métricas:
  - `saldo_consultas_total`
  - `saldo_negacoes_acesso_total`
  - `saldo_falhas_batch_total`
  - `saldo_falhas_processamento_evento_total`

## Documentação complementar
- Arquitetura: `docs/arquitetura.md`
- Decisões técnicas: `docs/decisoes-tecnicas.md`
- Observabilidade: `docs/observabilidade.md`
- Operação batch: `docs/operacao-batch.md`

## Infraestrutura (Terraform)
Em `infra/terraform/` há um esqueleto comentado para referência conceitual (SNS, alarmes CloudWatch, variáveis/outputs), sem provisionamento real neste desafio.
