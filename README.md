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

| Profile | Finalidade | Componentes ativos/inativos (web, batch, persistência) | Comando de execução | Arquivo de configuração relacionado |
|---|---|---|---|---|
| `local` | Execução padrão da API local para consulta de saldo. | **Web:** ativo (padrão Spring Boot)<br>**Batch:** inativo por padrão<br>**Persistência:** ativa via JPA + H2 em memória | `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` | `src/main/resources/application-local.yml` |
| `batch` | Execução do job de importação em modo não-web. | **Web:** inativo (`web-application-type: none`)<br>**Batch:** ativo (`spring.batch.job.enabled=true`)<br>**Persistência:** depende da combinação com outro profile (ex.: `local`) | `./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local` | `src/main/resources/application-batch.yml` |
| `aws-exemplo` | Exemplo conceitual de configuração de persistência AWS (DynamoDB). | **Web:** ativo por padrão (não desativado nesse profile)<br>**Batch:** inativo por padrão<br>**Persistência:** configuração AWS de exemplo (sem integração real) | `./mvnw spring-boot:run -Dspring-boot.run.profiles=aws-exemplo` | `src/main/resources/application-aws-exemplo.yml` |

> Para processar o batch com persistência local, execute com perfis combinados: `batch,local`.

### Propriedades relevantes (busca rápida)

- `saldo.batch.*`
  - `saldo.batch.diretorio-entrada`
  - `saldo.batch.nome-arquivo`
  - `saldo.batch.delimitador`
- Configurações de segurança JWT
  - `seguranca.jwt.habilitado`
  - `seguranca.jwt.issuer`
  - `seguranca.jwt.jwk-set-uri`
  - `seguranca.jwt.chave-publica-pem`
  - `seguranca.jwt.audience`
  - `seguranca.jwt.tolerancia-clock-skew`
- Configurações de mensageria simulada
  - `saldo.integracao.mq.host`
  - `saldo.integracao.mq.porta`
  - `saldo.integracao.mq.channel`
  - `saldo.integracao.mq.queue-manager`
  - `saldo.integracao.mq.fila-atualizacao`
  - `saldo.integracao.mq.usuario`
  - `saldo.integracao.mq.senha`
  - `saldo.integracao.mq.segredo-referencia`

Referências:
- `src/main/resources/application-local.yml`
- `src/main/resources/application-batch.yml`
- `src/main/resources/application-aws-exemplo.yml`
- `src/main/resources/application.properties`

## Comandos de execução
- **API local (default):**
  - `./mvnw spring-boot:run`
  - ou `./mvnw spring-boot:run -Dspring-boot.run.profiles=local`
- **Batch com persistência local (recomendado no repositório):**
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local`
- **Batch com caminho de arquivo customizado:**
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=batch,local -Dspring-boot.run.arguments="--saldo.batch.diretorio-entrada=/tmp --saldo.batch.nome-arquivo=saldos.csv --saldo.batch.delimitador=|"`
- **Profile AWS de exemplo (conceitual):**
  - `./mvnw spring-boot:run -Dspring-boot.run.profiles=aws-exemplo`

## Consumo de eventos de saldo atualizado (quase em tempo real)

Para complementar o batch consolidado, o projeto agora inclui a estrutura de consumo de eventos de saldo via mensageria de entrada simulada (MQ/JMS), mantendo desacoplamento total do controller HTTP.

## Fluxo batch oficial

O fluxo batch oficial desta base está centralizado no pacote `infraestrutura.batch`:

1. `ConfiguracaoImportacaoSaldoBatch` define o `Job` e o `Step` de importação.
2. `LeitorRegistroArquivoSaldoBatch` cria `FlatFileItemReader` para ler CSV/arquivo delimitado.
3. `ProcessadorRegistroSaldoBatch` converte o registro bruto para `SaldoConta` de domínio.
4. `EscritorSaldoContaBatch` persiste os saldos pela porta `RepositorioSaldoContaPortaSaida`.

> A trilha legada paralela de reader/processor/modelo batch foi removida para manter um único caminho de execução e testes.

> A configuração de arquivo/delimitador fica centralizada em `PropriedadesBatchSaldo` (`saldo.batch.diretorio-entrada`, `saldo.batch.nome-arquivo`, `saldo.batch.delimitador`).

### Mapeamento de classes batch

- **Ativas (oficiais)**
  - `infraestrutura.batch.configuracao.ConfiguracaoImportacaoSaldoBatch`
  - `infraestrutura.batch.componentes.LeitorRegistroArquivoSaldoBatch`
  - `infraestrutura.batch.componentes.ProcessadorRegistroSaldoBatch`
  - `infraestrutura.batch.componentes.EscritorSaldoContaBatch`
  - `infraestrutura.batch.componentes.RegistroArquivoSaldoBatch`

- **Legadas (removidas da trilha principal)**
  - `infraestrutura.batch.LeitorRegistroArquivoSaldoBatchItemReader`
  - `infraestrutura.batch.ProcessadorRegistroArquivoSaldoBatchItemProcessor`
  - `infraestrutura.batch.modelo.RegistroArquivoSaldoBatch`
  - `infraestrutura.adaptador.saida.batch.LeitorArquivoBatchSaldoNfsAdaptador`

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

## Decisões arquiteturais consolidadas
- **Convenção linguística:** o repositório mantém nomenclatura em português por contexto do desafio; para cenários reais, a convenção preferível é nomenclatura técnica em inglês. Detalhes em `docs/adr/ADR-002-nomes-em-portugues.md`.
- **JWT legado:** classes de validação/filtro JWT legadas foram removidas para eliminar duplicidade de estratégia. A autenticação oficial fica centralizada em Spring Security OAuth2 Resource Server (`oauth2ResourceServer().jwt(...)`) com `ConversorJwtAutenticacao`. Detalhes em `docs/adr/ADR-008-remocao-jwt-legado.md`.

## Observabilidade e operacionalização básica
A base de observabilidade foi adicionada para manter execução local simples e preparar evolução para operação real:

- **Spring Actuator** habilitado com `health`, `info` e `metrics`.
- **Correlation ID** por requisição HTTP (`X-Correlation-Id`) com propagação no MDC.
- **Logs estruturados** em JSON no console (`logback-spring.xml`).
- **Métricas de aplicação**:
  - `saldo_consultas_total`
  - `saldo_negacoes_acesso_total`
  - `saldo_falhas_batch_total`
  - `saldo_falhas_processamento_evento_total`

Consulte documentação detalhada em: `docs/observabilidade.md`.

## Infraestrutura (esqueleto Terraform)
Foi incluída a pasta `infra/terraform/` com um **esqueleto comentado** para:
- tópico SNS de alertas;
- alarmes CloudWatch (ALB 5xx e métrica customizada de falha de evento);
- variáveis/outputs básicos.

> Como é um desafio técnico, os recursos AWS estão apenas modelados com comentários claros do que seria configurado em ambiente real (IAM, dimensions reais, KMS, endpoints corporativos, estado remoto etc.).
