# Rota Seca

> CitizenTech da Global Solution FIAP 2026.2, Engenharia de Software, 2º ano (Fev).

## Ficha da CitizenTech

| Campo | Resposta |
|---|---|
| **Nome da CitizenTech** | Rota Seca |
| **Cidade e bairro** | São Paulo, Zona Leste: Jardim Pantanal (e bairros vizinhos da várzea do Tietê) |
| **Cidadão beneficiado** | Morador de área de risco de alagamento, principalmente idosos, pessoas com mobilidade reduzida e quem volta do trabalho a pé ou de transporte público |
| **Problema, em uma frase** | Moradores de áreas que alagam não sabem a tempo quando a chuva vai ser forte nem qual caminho é seguro para sair ou voltar para casa |
| **Como funciona** | Leituras de chuva entram no sistema; quando a chuva passa de moderada, um alerta é gerado para o bairro dizendo quais trechos evitar e para qual ponto de apoio (abrigo, escola, UBS) ir; o algoritmo calcula a rota mais segura até esse ponto |
| **O que cada parte faz** | **Data Science:** prevê chuva forte a partir de dados públicos. **DDD Java:** API que guarda e expõe bairros, pontos de risco, pontos de apoio, leituras, alertas, moradores e ocorrências. **Database Design:** modelo relacional no Oracle. **Dynamic Programming:** rota mais segura no grafo de ruas. **Network:** rede do ponto de apoio que hospeda o servidor. **AR/VR:** vídeo 3D da rua alagada e do trajeto seguro. **Agile:** design da solução, backlog e pitch |
| **Link do repositório** | https://github.com/0Romero/rota-seca |
| **Indicador de impacto** | % de alertas emitidos com pelo menos 1h de antecedência; nº de rotas seguras calculadas; nº de ocorrências reportadas pelos moradores; redução estimada de pessoas passando por ruas alagadas |
| **ODS conectados** | **11** (cidades e comunidades sustentáveis), **13** (ação contra a mudança do clima), **3** (saúde e bem-estar), **10** (redução das desigualdades) |

### Como o projeto contribui para cada ODS

- **ODS 11:** a meta 11.5 fala em reduzir mortes e pessoas afetadas por desastres, inclusive os relacionados à água. O Rota Seca avisa antes e tira as pessoas do caminho da água.
- **ODS 13:** a meta 13.1 pede resiliência e capacidade de adaptação a riscos climáticos. Chuvas extremas ficam mais frequentes, e o alerta por bairro é uma adaptação de baixo custo.
- **ODS 3:** evita afogamentos, quedas e doenças de veiculação hídrica (como a leptospirose), que vêm do contato com a água da enchente.
- **ODS 10:** quem mais sofre com alagamento é a população de baixa renda da periferia. O sistema prioriza idosos e pessoas com mobilidade reduzida.

## Integrantes

| Nome | RM |
|---|---|
| `<Nome completo>` | `<RM>` |
| `<Nome completo>` | `<RM>` |
| `<Nome completo>` | `<RM>` |

## Estrutura do repositório

| Pasta | Disciplina |
|---|---|
| [`ddd-java/`](ddd-java/) | Domain Driven Design – Java (API REST Spring Boot + Oracle) |
| `database/` | Database Design (modelo lógico e scripts SQL) |
| `data-science/` | Data Science & Statistical Computing (notebook) |
| `dynamic-programming/` | Dynamic Programming (rota mais segura) |
| `network/` | Network Architect Solutions (Packet Tracer) |

Uso de IA: registrado em [`prompts.md`](prompts.md).
Dados: todos os dados pessoais são **simulados**. As fontes de dados públicos estão indicadas em cada pasta.
