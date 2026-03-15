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

## Justificativa para classes em português
Para este desafio técnico, classes, métodos e pacotes foram nomeados em português como escolha simbólica e para manter consistência com o enunciado.

## Observação sobre convenção real de mercado
Em projeto real, a convenção preferível é utilizar nomes em inglês para código, pacotes e artefatos técnicos, visando padronização internacional e melhor interoperabilidade entre times.
