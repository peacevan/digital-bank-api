# Digital Bank API — Especificação Geral

# Visão Geral

API REST para simulação de um banco digital simplificado com foco em:

* Transferência financeira entre contas
* Consistência transacional
* Concorrência
* Arquitetura limpa
* Escalabilidade
* Resiliência
* Qualidade de código

O objetivo do projeto é demonstrar boas práticas de desenvolvimento backend em um cenário similar a sistemas financeiros de alta criticidade.

---

# Objetivos Técnicos

O projeto deve demonstrar:

* Arquitetura MVC em camadas com isolamento de domínio
* Separação de responsabilidades
* Isolamento do domínio
* Regras de negócio desacopladas do framework
* Tratamento de concorrência
* Consistência de dados
* Testes automatizados
* APIs REST documentadas

---

# Stack Tecnológica

| Item                  | Tecnologia             |
| --------------------- | ---------------------- |
| Linguagem             | Java 17                |
| Framework             | Spring Boot            |
| Build Tool            | Maven                  |
| Banco de Dados        | H2                     |
| Persistência          | Spring Data JPA        |
| Documentação          | Swagger / OpenAPI      |
| Testes                | JUnit 5 + Mockito      |
| Arquitetura           | MVC em camadas         |
| Controle de Qualidade | Sonar-ready            |

---

# Requisitos Funcionais

## RF-01 — Criar Conta

Permitir criação de contas bancárias.

### Entrada

* nome do cliente
* saldo inicial

### Regras

* saldo inicial deve ser maior ou igual a zero
* nome é obrigatório
* conta deve possuir identificador único

### Saída

* conta criada
* identificador da conta
* saldo atual

---

## RF-02 — Consultar Conta

Permitir consulta de dados da conta.

### Retorno

* id
* nome
* saldo atual

---

## RF-03 — Transferência de Valores

Permitir transferência financeira entre contas.

### Entrada

* conta origem
* conta destino
* valor

### Regras de Negócio

* contas devem existir
* valor deve ser maior que zero
* contas devem ser diferentes
* conta origem deve possuir saldo suficiente
* operação deve ser transacional

### Saída

* transferência registrada
* saldo atualizado das contas
* identificador da transação

---

## RF-04 — Histórico de Movimentações

Toda transferência deve gerar histórico financeiro.

### Dados registrados

* conta origem
* conta destino
* valor
* data/hora da transação

---

## RF-05 — Notificação

Após transferência concluída com sucesso:

* simular envio de notificação
* registrar log da operação

---

# Requisitos Não Funcionais

## RNF-01 — Concorrência

O sistema deve garantir consistência em transferências simultâneas.

### Estratégias

* Optimistic Lock com `@Version`
* Controle transacional
* Operações atômicas
* Validação de saldo durante transação

### Objetivo

Evitar:

* race conditions
* saldo inconsistente
* dupla movimentação

---

## RNF-02 — Idempotência

O sistema deve evitar processamento duplicado de transferências.

### Estratégia

* utilização de `Idempotency-Key`
* validação de requisições repetidas

### Objetivo

Evitar pagamentos duplicados em caso de retry.

---

## RNF-03 — Testes Automatizados

Cobertura mínima para:

* regras de negócio
* transferências
* validações
* cenários de erro

---

## RNF-04 — Documentação

A API deve possuir documentação Swagger/OpenAPI habilitada.

---

## RNF-05 — Qualidade de Código

Aplicar:

* SOLID
* Clean Code
* Clean Architecture
* tratamento global de exceções
* separação de responsabilidades

---

# Arquitetura

> **Nota de decisão:** A especificação original previa Arquitetura Hexagonal (Ports and Adapters).
> Após análise do escopo do projeto, a decisão foi revisada para adotar **MVC em camadas com isolamento de domínio**.
> O registro completo da decisão e seus motivos está em [`ARCHITECTURE_DECISIONS.md`](ARCHITECTURE_DECISIONS.md).

O projeto adota **arquitetura MVC em camadas**, mantendo isolamento do domínio sem o overhead de ports e adapters.

---

## Camadas

### Controller

Responsável por:

* receber e validar requisições HTTP
* retornar responses padronizados
* delegar lógica ao Service

Sem regras de negócio.

---

### Service

Responsável por:

* regras de negócio
* orquestração das operações
* controle transacional (`@Transactional`)
* concorrência

---

### Repository

Responsável por:

* persistência via Spring Data JPA
* consultas ao banco H2

Sem regras de negócio.

---

### Domain

Responsável por:

* modelo de domínio puro (`Account`)
* sem dependências do Spring ou de infraestrutura

---

### Exception

Responsável por:

* `GlobalExceptionHandler` centralizado
* exceções de domínio tipadas (`AccountNotFoundException`, `InsufficientFundsException`, `InvalidTransferException`)

---

# Fluxo de Transferência

```
HTTP Request
→ Controller (validação de entrada)
→ Service (regras de negócio + @Transactional)
→ Repository (Spring Data JPA)
→ Entity / H2 Database

Após sucesso:
→ log.info("[NOTIFICATION] ...") — notificação simulada
```

---

# Modelo de Dados

## Tabela: accounts

| Campo   | Tipo    |
| ------- | ------- |
| id      | UUID    |
| name    | VARCHAR |
| balance | DECIMAL |
| version | LONG    |

---

## Tabela: transfers

| Campo           | Tipo      |
| --------------- | --------- |
| id              | UUID      |
| from_account_id | UUID      |
| to_account_id   | UUID      |
| amount          | DECIMAL   |
| created_at      | TIMESTAMP |

---

# Estratégia de Concorrência

A aplicação utilizará:

* `@Version` para optimistic locking
* controle transacional
* validação de saldo em tempo de execução
* consistência transacional

### Objetivo

Garantir:

* integridade financeira
* consistência de saldo
* segurança em cenários concorrentes

---

# Endpoints

## POST /accounts

Criar conta bancária.

---

## GET /accounts/{id}

Consultar conta.

---

## POST /transfers

Realizar transferência financeira.

---

## GET /transfers/account/{id}

Consultar histórico de movimentações da conta.

---

# Estratégia de Testes

O projeto deve possuir:

* testes unitários
* validação das regras de negócio
* cenários positivos e negativos
* testes de transferência
* testes de concorrência básicos

---

# Critérios de Qualidade

O projeto deve priorizar:

* legibilidade
* baixo acoplamento
* alta coesão
* manutenibilidade
* simplicidade arquitetural
* clareza de domínio

---

# Diferenciais Técnicos (Opcional)

Os itens abaixo são opcionais e não devem gerar overengineering no contexto do teste:

* Kafka
* Outbox Pattern
* Docker
* CI/CD
* Sonar
* Trivy
* Semgrep
* Observabilidade
* Retry Pattern

---

# Considerações Finais

O foco principal do projeto é demonstrar:

* boas práticas backend
* arquitetura limpa
* isolamento de domínio

* consistência transacional
* qualidade de código
* capacidade de modelagem de sistemas financeiros
