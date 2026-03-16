variable "regiao_aws" {
  description = "Região AWS para provisionamento"
  type        = string
  default     = "sa-east-1"
}

variable "prefixo_nome" {
  description = "Prefixo para nomes de recursos"
  type        = string
  default     = "api-saldo-cliente"
}

variable "email_alerta" {
  description = "E-mail para assinatura do tópico SNS (opcional)"
  type        = string
  default     = ""
}

variable "nome_dimensao_load_balancer" {
  description = "Dimensão LoadBalancer para alarmes de ALB (preencher no ambiente real)"
  type        = string
  default     = ""
}

variable "nome_dimensao_target_group" {
  description = "Dimensão TargetGroup para alarmes de ALB (preencher no ambiente real)"
  type        = string
  default     = ""
}

variable "namespace_metrica_aplicacao" {
  description = "Namespace de métricas customizadas da aplicação"
  type        = string
  default     = "ApiSaldoCliente"
}
