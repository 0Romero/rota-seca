# Uso de IA no projeto

Conforme os combinados da GS, a IA foi usada como copiloto. Abaixo, os prompts usados e o que foi feito com cada resposta.
A equipe revisou o código gerado e sabe explicar cada parte.

## Domain Driven Design – Java

**Ferramenta:** Claude Code (Anthropic), em 16/11/2026.

**Prompt 1:** enviamos o PDF da GS CitizenTech e pedimos: *"faz o ddd pfv"*.

**Prompt 2:** enviamos o planejamento da equipe para o projeto **Rota Seca** (Ficha da CitizenTech,
tabelas sugeridas para Database Design e os requisitos de cada matéria).

**O que a IA gerou:**
- a estrutura do projeto Spring Boot (camadas model, DAO, BO, controller e exception);
- os scripts `schema.sql` e `data.sql` (dados simulados);
- as regras de negócio (alerta automático por nível de chuva, ciclo de status da ocorrência, atualização da lâmina máxima);
- os testes automatizados, o Dockerfile para o Render e a documentação.

**O que a equipe fez / revisou:**
- `<descrevam aqui o que vocês mudaram, testaram e conferiram, por exemplo: rodamos os scripts no Oracle da FIAP, fizemos o deploy, ajustamos X>`

<!-- Acrescentem abaixo os prompts das outras matérias, no mesmo formato. -->
