# API Saldo Cliente

## 1. Visão do problema
Este projeto modela um cenário bancário com ecossistema distribuído e forte dependência de legado:

- Núcleo transacional em **mainframe**.
- Processo **batch consolidado (~50GB)** gerado diariamente às **2AM**, disponibilizado via servidor de arquivos/NFS.
- Atualizações incrementais por **eventos MQ por transação**, em quase tempo real.
- Consulta de saldo com requisito de segurança: acesso permitido somente por **titularidade da conta**.
- Exigência operacional de **alta disponibilidade** e **observabilidade**, com capacidade de evolução para integração com componentes AWS.

O objetivo é entregar um serviço de saldo com clareza arquitetural, isolando regras de negócio de detalhes tecnológicos para permitir evolução segura.

## 2. Arquitetura proposta
A proposta é um **serviço central de saldo** dentro do ecossistema distribuído, mantendo este projeto como uma unidade coesa (sem decompor em múltiplos microsserviços dentro do próprio repositório).

A arquitetura principal é **hexagonal (ports and adapters)**:

- **dominio**: regras e modelos centrais.
- **aplicacao**: casos de uso e contratos (portas).
- **infraestrutura**: adaptadores de entrada/saída (HTTP, segurança, batch, mensageria, persistência).
- **compartilhado**: aspectos transversais.

Com isso, entradas (API, batch, eventos) e saídas (persistência/publicação) evoluem sem contaminar o núcleo de negócio.

## 3. Justificativa das escolhas
- **Por que hexagonal:** reduz acoplamento entre regra de negócio e frameworks/provedores, aumentando testabilidade e substituição de tecnologia.
- **Por que separar API x batch:** o fluxo online prioriza latência, disponibilidade e autorização por titularidade; o fluxo batch prioriza carga massiva e reconciliação sem bloquear atendimento síncrono.
- **Por que SNS + SQS no desenho conceitual:** SNS habilita fanout para múltiplos consumidores; SQS adiciona desacoplamento, controle de backpressure e reprocessamento por consumidor.
- **Por que autenticação separada de autorização:** autenticação comprova identidade (JWT), enquanto autorização aplica a regra de negócio de titularidade; separar responsabilidades evita fragilidade e aumenta clareza de segurança.

## 4. Como rodar localmente
### Pré-requisitos
- **Java 21**
- **Maven Wrapper** (já versionado no projeto, usar `./mvnw`)

### Subir a API
```bash
./mvnw spring-boot:run
```

### Profile local
O projeto inicia com profile local por padrão (`spring.profiles.active=local`).

### Endpoints úteis
- `GET /v1/contas/{idConta}/saldo`
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/metrics`

## 5. Como testar
### Suíte completa
```bash
./mvnw test
```
Valida o comportamento de domínio, aplicação, web, segurança e componentes de batch/mensageria simulada.

### Execução por pacote (quando necessário)
```bash
./mvnw -Dtest='br.com.desafiotecnico.api_saldo_cliente.aplicacao.servico.*Test' test
./mvnw -Dtest='br.com.desafiotecnico.api_saldo_cliente.infraestrutura.adaptador.entrada.http.*Test' test
./mvnw -Dtest='br.com.desafiotecnico.api_saldo_cliente.infraestrutura.batch.*Test' test
```
- **aplicacao.servico**: valida casos de uso e decisões de atualização de saldo.
- **infraestrutura.adaptador.entrada.http**: valida contrato HTTP, validações e segurança na borda da API.
- **infraestrutura.batch**: valida parsing/processing dos registros de carga batch.

## 6. Limitações conscientes
Este repositório **não** faz integração real com:

- AWS (SNS/SQS/DynamoDB operacionais)
- MQ corporativo real
- NFS corporativo real
- Provedor JWT corporativo

Os pontos de extensão existem como portas/adaptadores e configurações preparadas para evolução:

- adaptadores de mensageria/publicação em infraestrutura;
- profile e configuração de persistência para cenário AWS;
- configuração de segurança JWT desacoplada da regra de autorização de negócio.

## 7. Evoluções futuras
- Implementar **outbox transacional** para publicação confiável de eventos.
- Adicionar **DLQ e estratégia de reprocessamento** para falhas em mensageria.
- Reforçar **hardening de segurança/JWT** (validações avançadas de token, rotação de chaves, políticas corporativas).
- Evoluir para **observabilidade avançada** (tracing distribuído, dashboards e alertas operacionais completos).
- Estruturar **esteira de deploy** com validações automatizadas e promoção entre ambientes.

## 8. Escolhas simbólicas deste projeto de avaliação
- Classes, métodos e pacotes com nomes em **português** por aderência ao desafio.
- Em contexto real, preferência por **inglês técnico** para padronização entre times.
- Ausência de integração real com nuvem por **escopo da avaliação**.
- Foco em **clareza arquitetural** e **testabilidade**.
