# Arquitetura da Solução

## Visão geral
Este projeto foi estruturado como um serviço central de saldo dentro de um ecossistema distribuído bancário, adotando arquitetura hexagonal para isolar regras de negócio de detalhes de infraestrutura.

Camadas:
- **Domínio**: modelos e exceções do negócio de saldo.
- **Aplicação**: portas e serviços de orquestração dos casos de uso.
- **Infraestrutura**: adaptadores de entrada e saída (HTTP, leitura batch, publicação de eventos, repositório).
- **Compartilhado**: tratamento global de erro e objetos transversais.

## Fluxo da API
1. Cliente autenticado chama endpoint de consulta de saldo.
2. Adaptador HTTP recebe os dados de conta e titular.
3. Porta de entrada da aplicação executa caso de uso de consulta.
4. Caso de uso consulta porta de saída de saldo.
5. Resultado retorna ao adaptador HTTP.
6. Erros de domínio são tratados no handler global.

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
