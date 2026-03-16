# Terraform (esqueleto) - Observabilidade AWS

Este diretório contém **apenas um esqueleto comentado** para recursos de observabilidade na AWS.

> Neste desafio técnico não há integração real com AWS. Em projeto real, o estado remoto, providers, IAM e naming seriam configurados por ambiente (dev/hml/prd), com secrets em cofre apropriado.

## Objetivo
- Criar estrutura para CloudWatch alarms.
- Encaminhar alarmes para SNS.
- Preparar ponto de extensão para integrações reais (PagerDuty, Opsgenie, e-mail, ChatOps etc.).
