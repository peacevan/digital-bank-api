# Projeto: digital-bank-api — Checklist de Estado

- **Branch ativa:** feat/transfer
- **Commits principais:**
  - 75ad9e4 feat(transfer): add transfer use-case and unit tests
  - 2acf609 feat(transfer): add REST controller and DTOs for transfer with controller tests
  - 4306f2a feat(notification): add Notifier port, in-memory adapter and tests
  - 3bac61b feat(concurrency): propagate JPA version and add concurrency integration test
- **Último build/tests:** `mvn test` — BUILD SUCCESS (todos os testes locais passaram)
- **Última alteração relevante:** adicionada anotação `@Transactional` em `TransferUseCaseImpl.transfer()` para atomicidade

- **Status das tarefas:**
  - [x] Implementar use-case de transfer (TDD)
  - [x] Controller + DTOs + testes de controller
  - [x] Notifier + in-memory adapter + testes
  - [x] Propagar `version` para domain + testes de concorrência
  - [x] Rodar suíte de testes completa (`mvn test`)
  - [ ] Criar Pull Request `feat/transfer` → `main`
  - [ ] Revisão de código / aprovações
  - [ ] Merge e preparação de release

- **Notas/observações:**
  - Perfil de testes ativo: `test` (H2 em memória)
  - Arquitetura: Hexagonal (ports & adapters)
  - Transações: `TransferUseCaseImpl.transfer()` anotado com `@Transactional` para garantir atomicidade

-- Gerado automaticamente pelo assistente (GitHub Copilot, modelo: GPT-5 mini)
