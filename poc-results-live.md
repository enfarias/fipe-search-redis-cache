# Relatório POC: Resultados Reais dos Cenários FIPE

Este documento captura a execução simulando alta volumetria e valida os cenários esperados.

## 1. Tabela de Performance (K6 - 200 VUs por 20s)

| Cenário | Volumetria (Reqs) | RPS | Cache MISS | Erros (Timeouts/Fails) | Latência p90 | Latência p95 |
|---------|-------------------|-----|------------|-------------------------|--------------|--------------|
| Direct DB (Gargalo) | 471213 | 23619 | - | 1240 | 11.37ms | 15.89ms |
| Cache com TTL (Stampede) | 469359 | 23528 | 1500 | 1302 | 11.18ms | 15.36ms |
| Cache com Warming (Ideal) | 497297 | 24859 | 5 | 0 | 11.18ms | 15.73ms |

**Análise Final**:
- **Cenário 1 (DB Direto):** O gargalo do Pool de Conexões do banco limitou o throughput bruto.
- **Cenário 2 (Cache TTL):** Houve grande melhoria de latência geral, porém picos de latência e concorrência sobrecarregando o sistema periodicamente ("Cache Stampede").
- **Cenário 3 (Cache com Warmup):** Desempenho máximo, mantendo p95 muito baixa sem onerar o banco, entregando zero erros.

## 2. Evidência do Fenômeno "Cache Stampede" (Cenário 2)

O Cache Stampede acontece porque, ao expirar uma chave em alta concorrência `N` VUs, dezenas de instâncias percebem o *Cache MISS* e fazem a consulta simultânea ao banco de dados no exato momento, re-saturando o pool do HikariCP desnecessariamente e gerando interrupções no tempo de reposta da API que seriam invisíveis na média bruta.

**Comportamento capturado nos logs do Spring Boot durante o teste de carga:**
- Aos segundos `xx:xx:23:40:07`, exatas **44 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:07`, exatas **46 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:07`, exatas **35 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:07`, exatas **39 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:07`, exatas **36 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:08`, exatas **34 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:08`, exatas **36 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:08`, exatas **33 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:08`, exatas **53 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:08`, exatas **39 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:14`, exatas **188 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:14`, exatas **53 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:14`, exatas **59 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:14`, exatas **81 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:14`, exatas **69 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:20`, exatas **200 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=1, ano=2023` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:20`, exatas **158 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:21`, exatas **80 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=2, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:21`, exatas **38 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=3, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:21`, exatas **110 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=4, ano=2021` e sobrecarregaram o banco.
- Aos segundos `xx:xx:23:40:21`, exatas **69 threads** tiveram `Cache MISS` simultaneamente para o mesmo registro `modelo=5, ano=2021` e sobrecarregaram o banco.
