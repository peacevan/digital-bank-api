# Digital Bank API

API REST de banco digital simplificado desenvolvida em Java 17 com Spring Boot, seguindo arquitetura MVC em camadas.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.x |
| Spring Data JPA | — |
| H2 Database | 2.x (in-memory) |
| Swagger / OpenAPI | springdoc 2.x |
| JUnit 5 + Mockito | — |
| Maven | 3.9.x |

---

## Como rodar

### Pré-requisitos
- Java 17+
- Maven 3.9+ (ou usar o Maven Wrapper incluso)

### Executar a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em: **http://localhost:8080**

### Documentação Swagger

Acesse após iniciar: **http://localhost:8080/swagger-ui.html**

### Console H2

Para inspecionar o banco em memória: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:bankdb`
- User: `sa` / Password: _(vazio)_

### Executar testes

```bash
./mvnw test
```

Todos os 21 testes passam (unitários + integração com H2).

---

## Arquitetura

O projeto segue **arquitetura MVC em camadas**, priorizando clareza, simplicidade e testabilidade.

```
src/main/java/com/ivan/amado/bank/
├── controller/        ← REST Controllers (AccountController, TransferController)
├── service/           ← Regras de negócio (AccountService, TransferService)
├── repository/        ← Spring Data JPA (AccountRepository, TransferRepository, IdempotencyRepository)
├── entity/            ← Entidades JPA (@Version para optimistic locking)
├── dto/               ← Objetos de request/response da API
├── exception/         ← GlobalExceptionHandler + exceções de domínio
└── domain/            ← Modelo de domínio puro (Account, sem dependências Spring)
```

### Fluxo de uma requisição
`Controller` → `Service` → `Repository` → `Entity` (JPA / H2)

---

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/accounts` | Criar conta bancária |
| `GET` | `/accounts/{id}` | Consultar conta por ID |
| `POST` | `/transfers` | Realizar transferência |
| `GET` | `/transfers/account/{id}` | Histórico de transferências da conta |

### Contas pré-carregadas

| ID | Nome | Saldo |
|---|---|---|
| `a1b2c3d4-0001-0001-0001-000000000001` | Alice Souza | R$ 1.000,00 |
| `a1b2c3d4-0002-0002-0002-000000000002` | Bruno Lima | R$ 2.500,00 |
| `a1b2c3d4-0003-0003-0003-000000000003` | Carla Mendes | R$ 500,00 |
| `a1b2c3d4-0004-0004-0004-000000000004` | Daniel Costa | R$ 3.000,00 |
| `a1b2c3d4-0005-0005-0005-000000000005` | Eva Rodrigues | R$ 750,00 |

### Exemplo de transferência

```bash
curl -X POST http://localhost:8080/transfers \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: minha-chave-unica-123" \
  -d '{
    "fromAccountId": "a1b2c3d4-0001-0001-0001-000000000001",
    "toAccountId":   "a1b2c3d4-0002-0002-0002-000000000002",
    "amount": 100.00
  }'
```

---

## Estratégia de Concorrência

O sistema utiliza **Optimistic Locking** com `@Version` na entidade `AccountEntity`:

- Cada conta possui um campo `version` gerenciado pelo Hibernate
- Em transferências simultâneas, apenas uma transação vence; a outra recebe `409 Conflict` e pode ser reprocessada
- A anotação `@Transactional` em `TransferService.transfer()` garante atomicidade: débito + crédito + registro ocorrem em uma única transação

### Idempotência

O header `Idempotency-Key` evita processamento duplicado:
- Na primeira requisição, o resultado (OK ou ERROR) é persistido na tabela `idempotency_keys`
- Requisições repetidas com a mesma chave retornam o resultado cacheado sem processar novamente

---

## Decisões Técnicas

| Decisão | Justificativa |
|---|---|
| Arquitetura MVC em camadas | Estrutura simples, clara e amplamente conhecida; adequada ao escopo do projeto |
| Modelo de domínio separado (`domain/`) | `Account.java` sem dependências Spring, facilita testes unitários puros |
| H2 in-memory | Simplicidade para o contexto do teste técnico; substituível por PostgreSQL alterando apenas `application.properties` |
| Optimistic Lock (`@Version`) | Adequado para carga moderada; sem overhead de lock pessimista |
| `Idempotency-Key` via header | Padrão de mercado (similar ao Stripe/Braintree) para APIs financeiras |
| GlobalExceptionHandler | Centraliza tratamento de erros, retorna respostas padronizadas com timestamp e HTTP status |
| DTOs separados do domínio | Evita acoplamento entre contrato de API e modelo interno |

