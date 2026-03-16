# ADR-003: Separação entre API online e Batch de reconciliação

## Status
Aceito

## Contexto
O serviço de saldo atende dois tipos de necessidade com características operacionais diferentes:
- consultas online com baixa latência e autorização por titularidade;
- processamento em lote de arquivos consolidados de grande volume para carga e reconciliação.

Sem uma separação explícita, há risco de acoplamento indevido entre requisitos de tempo real e requisitos de throughput, dificultando evolução, operação e observabilidade.

## Motivação
- Tornar explícitos os limites de responsabilidade entre o fluxo transacional online e o fluxo de processamento em lote.
- Preservar um domínio compartilhado, evitando duplicação de regras de negócio.
- Permitir evolução e operação independentes para API e batch (escalabilidade, janelas de execução, tratamento de falhas).

## Alternativas avaliadas
1. **Fluxo único para API e batch**
   - Prós: menor número inicial de componentes e documentação simplificada.
   - Contras: mistura de responsabilidades, maior risco operacional e menor clareza arquitetural.

2. **Separação total com domínios independentes**
   - Prós: isolamento máximo entre contextos.
   - Contras: duplicação de regras, maior custo de manutenção e risco de divergência semântica.

3. **Separação de responsabilidades com domínio compartilhado (escolhida)**
   - Prós: clareza de fronteiras operacionais com reaproveitamento das regras centrais.
   - Contras: exige disciplina de arquitetura para manter contratos estáveis entre fluxos.

## Decisão
Adotar separação explícita entre:
- **API:** consulta online de saldo e autorização de titularidade;
- **Batch:** carga massiva consolidada e reconciliação;
- **Domínio compartilhado:** regras centrais comuns, com responsabilidades operacionais distintas em cada fluxo.

## Consequências
### Positivas
- Maior clareza de escopo e responsabilidades.
- Evolução mais segura de desempenho/escala por tipo de fluxo.
- Melhor alinhamento com arquitetura hexagonal e com as ADRs existentes.

### Negativas
- Incremento de esforço documental e de governança arquitetural.
- Necessidade de garantir consistência entre processos online e em lote ao evoluir regras.

## Observação de escopo deste teste
Neste teste técnico, **não há integração real com NFS/AWS**. Os pontos de integração permanecem representados por contratos/adaptadores para evolução futura.
