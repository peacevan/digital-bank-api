# digital-bank-api

Simulação de uma API para banco digital.

## Funcionalidades

- Criar conta
- Consultar conta
- Depositar
- Sacar
- Transferir entre contas
- Consultar extrato

## Executar localmente

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python app.py
```

> Observação: esta é uma simulação para testes (armazenamento em memória e servidor de desenvolvimento Flask), não indicada para produção.

## Executar testes

```bash
python -m unittest discover -s tests -v
```
