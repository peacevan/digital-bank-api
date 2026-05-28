# Projeto: digital-bank-api — Checklist de Estado

- **Branch ativa:** main
- **Último build/tests:** `mvn test` — BUILD SUCCESS (19/19 testes passando)

## Tarefas Concluídas

- [x] Estrutura Hexagonal (domain / application / infrastructure)
- [x] Domain model `Account` com `debit()`, `credit()`, `@Version`
- [x] Exceções de domínio: `AccountNotFoundException`, `InsufficientFundsException`, `InvalidTransferException`
- [x] Use cases: `CreateAccount`, `GetAccount`, `Transfer` (com `@Transactional`)
- [x] Ports de entrada (input) e saída (output)
- [x] DTOs: `AccountCreateRequest`, `AccountResponse`, `TransferRequest`, `TransferResponse`, `TransferRecord`
- [x] Controllers REST: `AccountController`, `TransferController`
- [x] JPA adapters e entidades: `AccountEntity`, `TransferEntity`, `IdempotencyEntity`
- [x] `GlobalExceptionHandler` centralizado com respostas padronizadas
- [x] Idempotência via header `Idempotency-Key`
- [x] Notifier port + `InMemoryNotifier` adapter
- [x] Concorrência com Optimistic Lock (`@Version`)
- [x] H2 in-memory database configurado (runtime + testes)
- [x] Dados iniciais pré-carregados (`data.sql` com 5 contas)
- [x] Swagger/OpenAPI habilitado (`springdoc-openapi-starter-webmvc-ui`)
- [x] Testes unitários (use cases, controllers com mocks)
- [x] Testes de integração com H2 (`EndpointIntegrationTest` — 5 cenários)
- [x] Teste de concorrência (`TransferConcurrencyTest`)
- [x] README.md completo (arquitetura, endpoints, como rodar, decisões técnicas)

## Itens Desejáveis (não implementados — fora do escopo do teste)

- [ ] Docker / docker-compose
- [ ] CI/CD pipeline
- [ ] Jacoco (cobertura de código)
- [ ] SonarQube
- [ ] Testcontainers (PostgreSQL real nos testes de integração)

## Notas

- Perfil de testes ativo: `test` (H2 in-memory isolado)
- H2 Console disponível em `/h2-console` durante execução local
- Swagger UI: `http://localhost:8080/swagger-ui.html`



