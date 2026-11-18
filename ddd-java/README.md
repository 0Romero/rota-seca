# Rota Seca API (Domain Driven Design – Java)

API RESTful em **Spring Boot 3.5** com banco **Oracle (FIAP)**, organizada nas camadas
`model` → `dao` → `bo` → `controller`, mais a camada `exception` para o tratamento de erros.

- **Repositório (GitHub):** https://github.com/0Romero/rota-seca (projeto na pasta `ddd-java`)
- **Deploy (Render):** https://rota-seca-api-l3hb.onrender.com
- **Documentação interativa (Swagger):** https://rota-seca-api-l3hb.onrender.com/swagger-ui.html

## Integrantes

| Nome | RM |
|---|---|
| Felipe Bernardes de Almeida | RM564360 |
| Guilherme Romero Nogueira de Lima | RM564431 |

## Ferramentas e versões

| Ferramenta | Versão |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 3.5.6 |
| Maven | 3.9 ou superior |
| Banco de dados | Oracle 19c (servidor `oracle.fiap.com.br`) |
| Driver | ojdbc11 |
| IDE usada | IntelliJ IDEA |
| Deploy | Render.com (Docker) |

## Estrutura do código

```
src/main/java/br/com/fiap/rotaseca
├── model/       classes de domínio, iguais às tabelas (Bairro, PontoRisco, PontoApoio,
│                LeituraChuva, Alerta, Morador, Ocorrencia) + enums
├── dao/         SQL de cada tabela com JdbcTemplate (inserir, buscar, listar, atualizar, excluir)
├── bo/          regras de negócio (validações, alerta automático, ciclo de status...)
├── controller/  endpoints REST (GET, POST, PUT, DELETE)
├── exception/   exceções do domínio e GlobalExceptionHandler (@RestControllerAdvice)
└── config/      Swagger e CORS
src/main/resources/db
├── schema.sql   DDL (CREATE TABLE + ALTER TABLE com as FKs)
├── data.sql     DML (5 linhas por tabela, dados simulados)
└── drop.sql     remove as tabelas
```

## Regras de negócio principais

1. **Alerta automático:** ao registrar uma leitura de chuva (`POST /api/leituras`), o BO classifica a chuva
   pela escala do CGE (≥10 mm/h moderada, ≥25 forte, ≥50 muito forte). A partir de *moderada*, gera um alerta para o bairro,
   válido por 3 horas, dizendo quais pontos de risco evitar e qual ponto de apoio ativo (o de maior capacidade) procurar.
   Leitura e alerta são gravados na mesma transação.
2. **Alertas do morador:** `GET /api/moradores/{id}/alertas` devolve os alertas em vigor no bairro do morador.
   Idosos (60+) e pessoas com mobilidade reduzida aparecem como `prioritario: true`.
3. **Ocorrência ensina o ponto de risco:** se um morador reporta uma lâmina d'água maior que a máxima conhecida, o ponto de risco é atualizado.
4. **Ciclo da ocorrência:** `ABERTA → EM_ATENDIMENTO → ENCERRADA`. Uma ocorrência encerrada não pode ser reaberta.
5. **Integridade:** telefone de morador único, bairro único por cidade, validade do alerta posterior à emissão,
   leituras e ocorrências não podem estar no futuro, e um bairro com dados ligados não pode ser excluído (HTTP 409).

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/bairros` | Lista bairros |
| GET | `/api/bairros/{id}` | Busca bairro |
| POST | `/api/bairros` | Cadastra bairro |
| PUT | `/api/bairros/{id}` | Atualiza bairro |
| DELETE | `/api/bairros/{id}` | Exclui bairro |
| GET | `/api/pontos-risco?bairroId=` | Lista pontos de risco (filtro opcional) |
| GET | `/api/pontos-risco/{id}` | Busca ponto de risco |
| POST | `/api/pontos-risco` | Cadastra ponto de risco |
| PUT | `/api/pontos-risco/{id}` | Atualiza ponto de risco |
| DELETE | `/api/pontos-risco/{id}` | Exclui ponto de risco |
| GET | `/api/pontos-apoio?bairroId=` | Lista pontos de apoio (com bairro: só ativos, por capacidade) |
| GET | `/api/pontos-apoio/{id}` | Busca ponto de apoio |
| POST | `/api/pontos-apoio` | Cadastra ponto de apoio |
| PUT | `/api/pontos-apoio/{id}` | Atualiza ponto de apoio |
| DELETE | `/api/pontos-apoio/{id}` | Exclui ponto de apoio |
| GET | `/api/leituras?bairroId=` | Lista leituras de chuva |
| GET | `/api/leituras/{id}` | Busca leitura |
| POST | `/api/leituras` | Registra leitura e gera alerta se necessário |
| PUT | `/api/leituras/{id}` | Corrige leitura |
| DELETE | `/api/leituras/{id}` | Exclui leitura |
| GET | `/api/alertas` | Lista alertas |
| GET | `/api/alertas/ativos?bairroId=` | Alertas em vigor agora |
| GET | `/api/alertas/{id}` | Busca alerta |
| POST | `/api/alertas` | Emite alerta manual (Defesa Civil) |
| PUT | `/api/alertas/{id}` | Atualiza alerta |
| DELETE | `/api/alertas/{id}` | Exclui alerta |
| GET | `/api/moradores` | Lista moradores |
| GET | `/api/moradores/{id}` | Busca morador |
| GET | `/api/moradores/{id}/alertas` | Alertas ativos no bairro do morador |
| POST | `/api/moradores` | Cadastra morador |
| PUT | `/api/moradores/{id}` | Atualiza morador |
| DELETE | `/api/moradores/{id}` | Exclui morador |
| GET | `/api/ocorrencias?status=` | Lista ocorrências (filtro opcional) |
| GET | `/api/ocorrencias/{id}` | Busca ocorrência |
| POST | `/api/ocorrencias` | Reporta alagamento |
| PUT | `/api/ocorrencias/{id}` | Atualiza ocorrência / status |
| DELETE | `/api/ocorrencias/{id}` | Exclui ocorrência |

Exemplo, para registrar uma chuva forte e receber o alerta gerado:

```http
POST /api/leituras
Content-Type: application/json

{ "bairroId": 1, "precipitacaoMm": 55.0, "umidade": 97, "fonte": "Sensor simulado" }
```

### Respostas de erro

Todos os erros têm o mesmo formato:

```json
{ "timestamp": "2026-11-16T18:30:00", "status": 422, "erro": "Unprocessable Entity",
  "mensagem": "O bairro 99 não existe", "caminho": "/api/pontos-apoio" }
```

| Status | Quando |
|---|---|
| 400 | Campos inválidos (com a lista `campos`), JSON malformado, enum ou data inválidos |
| 404 | Registro ou endpoint não encontrado |
| 409 | Violação de restrição do banco (duplicado ou registro ainda referenciado) |
| 422 | Regra de negócio violada |
| 503 / 500 | Falha no banco / erro inesperado |

## Como executar

### 1. Criar as tabelas no Oracle da FIAP

No SQL Developer, conectado com o seu usuário (`RMxxxxx`), rode na ordem:
`src/main/resources/db/schema.sql` e depois `src/main/resources/db/data.sql`.
Para recriar do zero, rode antes o `drop.sql`.

### 2. Rodar localmente com o Oracle

```bash
# Windows (PowerShell)
$env:DB_USER="RM000000"; $env:DB_PASSWORD="sua_senha"
mvn spring-boot:run

# Linux / macOS
DB_USER=RM000000 DB_PASSWORD=sua_senha mvn spring-boot:run
```

Acesse http://localhost:8080. A página inicial abre o Swagger.

### 3. Rodar sem o Oracle (perfil `dev`, banco H2 em memória)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

As tabelas e os dados de exemplo são criados automaticamente a cada execução.

### 4. Testes automatizados

```bash
mvn test
```

São 6 testes de ponta a ponta (MockMvc + H2) que cobrem o CRUD, o alerta automático, o ciclo de status e as respostas de erro.

### 5. Deploy no Render

1. Suba o repositório no GitHub.
2. No Render: **New → Web Service**, conecte o repositório.
3. **Root Directory:** `ddd-java` · **Runtime/Language:** `Docker` · **Instance:** Free.
4. Em **Environment**, adicione:
   - `DB_USER` = seu RM (ex.: `RM000000`)
   - `DB_PASSWORD` = sua senha do Oracle
   - `DB_URL` = `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` (opcional, esse já é o padrão)
5. Clique em **Deploy**. Quando aparecer `Started RotaSecaApplication`, abra a URL gerada (ela cai no Swagger).

> Se as tabelas ainda não existirem no Oracle, adicione também `SQL_INIT_MODE=always` no primeiro deploy
> e **remova** depois que subir; senão os dados de exemplo serão inseridos de novo a cada reinício.
>
> O plano gratuito do Render "dorme" após 15 min sem acesso. O primeiro acesso depois disso leva ~1 min.
