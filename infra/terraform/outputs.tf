output "sns_alertas_arn" {
  description = "ARN do tópico SNS de alertas"
  value       = aws_sns_topic.topico_alertas_observabilidade.arn
}
