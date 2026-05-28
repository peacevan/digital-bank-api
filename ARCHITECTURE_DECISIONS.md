# Architecture Decisions — Digital Bank API

# Objetivo

Este documento descreve as decisões arquiteturais adotadas para o projeto Digital Bank API.

O foco principal do projeto é:

* simplicidade
* clareza
* consistência
* concorrência
* qualidade de código

A arquitetura foi desenhada para atender os requisitos do teste técnico sem overengineering.

---

# Decisão Arquitetural

## Arquitetura escolhida

Foi adotada uma abordagem baseada em:

* MVC do Spring Boot
* princípios de Clean Architecture
* separação de responsabilidades
* isolamento parcial do domínio

A decisão de NÃO utilizar uma arquitetura hexagonal completa foi intencional para evitar:

* excesso de abstrações
* complexidade desnecessária
* grande quantidade de interfaces e adapters
* aumento artificial do acoplamento arquitetural

O objetivo foi manter o projeto:

* fácil de entender
* fácil de explicar
* fácil de manter
* alinhado ao escopo do desafio

---

# Estrutura do Projeto

```text
src/main/java/com/ivan/bank

controller/
service/
repository/
entity/
dto/
exception/
config/
domain/
```

---

# Responsabilidades das Camadas

## Controller

Responsável por:

* receber requisições HTTP
* validar DTOs
* retornar responses HTTP
* delegar regras ao service

Controllers NÃO possuem regra de negócio.

---

## Service

Responsável por:

* regras de negócio
* transferências financeiras
* concorrência
* validações
* controle transacional

Toda lógica principal do sistema fica centralizada nesta camada.

---

## Repository

Responsável apenas por:

* persistência
* consultas
* acesso ao banco

Sem regras de negócio.

---

## Domain

Responsável por:

* entidades centrais
* comportamentos importantes
* regras de domínio reutilizáveis

Com baixo acoplamento ao framework.

---

# Decisão sobre Entidade Customer

Foi decidido NÃO criar uma entidade Customer/Client.

Motivos:

* o teste não exige modelagem bancária complexa
* o foco principal é transferência e concorrência
* simplificação do domínio
* redução de complexidade desnecessária

A entidade Account já contém:

* id
* ownerName
* balance

Isso atende totalmente os requisitos funcionais.

---

# Decisão sobre Notificações

Foi decidido NÃO persistir notificações em banco.

A notificação será apenas simulada através de:

* NotificationService
* logs da aplicação

Exemplo:

```java
log.info("Notification sent to account {}", accountId);
```

Motivos:

* o requisito apenas solicita simulação de envio
* evita criação de tabelas desnecessárias
* mantém foco no domínio principal

---

# Estratégia de Concorrência

O sistema deve suportar transferências simultâneas com consistência.

Foi adotado:

## Optimistic Lock

Utilizando:

```java
@Version
```

Objetivo:

* evitar race conditions
* impedir inconsistência de saldo
* garantir integridade transacional

---

## Controle Transacional

Utilizando:

```java
@Transactional
```

Objetivo:

* garantir atomicidade da transferência
* evitar estados inconsistentes

---

# Banco de Dados

Foi escolhido:

* H2 Database

Motivos:

* simplicidade
* setup rápido
* facilidade de testes
* ideal para teste técnico

---

# Estratégia de Testes

Serão implementados:

* testes unitários
* validações de regras de negócio
* cenários de concorrência
* validação de saldo insuficiente

Ferramentas:

* JUnit 5
* Mockito

---

# Qualidade de Código

Boas práticas adotadas:

* SOLID
* DTOs
* Exception Handler global
* separação de responsabilidades
* Clean Code
* baixa complexidade

---

# Objetivo Final

O projeto deve demonstrar:

* domínio técnico
* capacidade de modelagem
* tratamento de concorrência
* organização
* clareza arquitetural
* qualidade de código

Sem transformar o desafio em um sistema bancário excessivamente complexo.
