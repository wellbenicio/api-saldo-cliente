terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Em projeto real: região, credenciais e role assumida por ambiente (ex.: CI/CD + OIDC).
provider "aws" {
  region = var.regiao_aws
}

# Tópico SNS para centralizar notificações de alarmes.
resource "aws_sns_topic" "topico_alertas_observabilidade" {
  name = "${var.prefixo_nome}-alertas-observabilidade"

  # Em produção:
  # - configurar policy de publicação/assinatura
  # - habilitar criptografia KMS
  # - tags obrigatórias de governança
}

# Assinatura de e-mail é opcional e ilustrativa.
# Em produção, normalmente seriam usados endpoints corporativos (chatops/webhook/incidente).
resource "aws_sns_topic_subscription" "assinatura_email" {
  count     = var.email_alerta == "" ? 0 : 1
  topic_arn = aws_sns_topic.topico_alertas_observabilidade.arn
  protocol  = "email"
  endpoint  = var.email_alerta
}

# Alarme de exemplo para alta taxa de erro HTTP da API.
resource "aws_cloudwatch_metric_alarm" "alarme_erros_http" {
  alarm_name          = "${var.prefixo_nome}-http-5xx-alto"
  alarm_description   = "Dispara quando erro HTTP 5xx excede o limite definido"
  namespace           = "AWS/ApplicationELB"
  metric_name         = "HTTPCode_Target_5XX_Count"
  statistic           = "Sum"
  period              = 60
  evaluation_periods  = 5
  threshold           = 10
  comparison_operator = "GreaterThanThreshold"

  alarm_actions = [aws_sns_topic.topico_alertas_observabilidade.arn]
  ok_actions    = [aws_sns_topic.topico_alertas_observabilidade.arn]

  # Em projeto real: preencher com dimensões reais do ALB/TargetGroup.
  dimensions = {
    LoadBalancer = var.nome_dimensao_load_balancer
    TargetGroup  = var.nome_dimensao_target_group
  }
}

# Alarme de exemplo para métrica customizada da aplicação (Micrometer -> CloudWatch Agent/OTel Collector).
resource "aws_cloudwatch_metric_alarm" "alarme_falhas_evento" {
  alarm_name          = "${var.prefixo_nome}-falhas-processamento-evento"
  alarm_description   = "Dispara quando falhas de processamento de evento aumentam acima do esperado"
  namespace           = var.namespace_metrica_aplicacao
  metric_name         = "saldo_falhas_processamento_evento_total"
  statistic           = "Sum"
  period              = 300
  evaluation_periods  = 1
  threshold           = 1
  comparison_operator = "GreaterThanOrEqualToThreshold"

  alarm_actions = [aws_sns_topic.topico_alertas_observabilidade.arn]
}
