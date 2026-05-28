# Projeto: Digital Bank API

## Objetivo

Construir uma API REST simplificada de banco digital utilizando:

- Java 17
- Spring Boot
- Arquitetura Hexagonal
- H2 Database
- Swagger/OpenAPI
- Testes Unitários

O sistema deve permitir:

- Cadastro de contas
- Transferência entre contas
- Consulta de movimentações
- Notificação após transferência
- Controle de concorrência
- Qualidade de código e boa arquitetura

---

# Diretrizes Arquiteturais

## Arquitetura

Utilizar Arquitetura Hexagonal (Ports & Adapters).

Separação obrigatória:

- domain → regras de negócio
- application → casos de uso
- infrastructure → banco, REST, mensageria, adapters

O domínio NÃO pode depender do Spring.

---

# Regras de Negócio

## Transferência

- Não permitir saldo negativo
- Não permitir transferência para mesma conta
- Validar existência das contas
- Registrar movimentação
- Enviar notificação após sucesso

---

# Concorrência

Implementar proteção contra concorrência usando:

- @Version (Optimistic Lock)
OU
- UPDATE saldo WHERE saldo >= valor

Evitar transferência duplicada.

---

# Qualidade

Obrigatório:

- Testes unitários
- Clean Code
- SOLID
- DTOs
- Tratamento global de exceções
- Logs básicos

Desejável:

- SonarQube
- Jacoco
- Testcontainers
- Docker

---

# Estrutura Esperada

src/main/java/com/ivan/bank

- domain
  - model
  - exception
  - service

- application
  - ports
  - usecases
  - dto

- infrastructure
  - controller
  - persistence
  - config
  - notification

---

# Banco de Dados

Usar H2.

Criar:
- accounts
- transfers

Popular dados iniciais.

---

# API

Endpoints mínimos:

POST /accounts
POST /transfers
GET /accounts/{id}
GET /transfers/account/{id}

---

# Swagger

Disponibilizar documentação via OpenAPI.

---

# README

Explicar:

- Arquitetura
- Como rodar
- Tecnologias
- Estratégias de concorrência
- Decisões técnicas

---

# Objetivo da Avaliação

Demonstrar:

- Capacidade arquitetural
- Organização de código
- Domínio de Java/Spring
- Concorrência
- Boas práticas
- Testabilidade