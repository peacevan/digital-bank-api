from __future__ import annotations

from dataclasses import dataclass, asdict
from itertools import count
from typing import Dict, List

from flask import Flask, jsonify, request


@dataclass
class Account:
    id: int
    name: str
    balance: float


def create_app() -> Flask:
    app = Flask(__name__)

    account_ids = count(1)
    transaction_ids = count(1)
    accounts: Dict[int, Account] = {}
    transactions: List[dict] = []

    def get_account_or_404(account_id: int):
        account = accounts.get(account_id)
        if account is None:
            return None, (jsonify({"error": "account not found"}), 404)
        return account, None

    def parse_amount(data):
        amount = data.get("amount")
        try:
            amount = float(amount)
        except (TypeError, ValueError):
            return None, (jsonify({"error": "amount must be a valid number greater than zero"}), 400)
        if amount <= 0:
            return None, (jsonify({"error": "amount must be a valid number greater than zero"}), 400)
        return amount, None

    @app.post("/accounts")
    def create_account():
        data = request.get_json(silent=True) or {}
        name = (data.get("name") or "").strip()
        if not name:
            return jsonify({"error": "name is required"}), 400

        try:
            initial_balance = float(data.get("initial_balance", 0))
        except (TypeError, ValueError):
            return jsonify({"error": "initial_balance must be a valid number"}), 400
        if initial_balance < 0:
            return jsonify({"error": "initial_balance cannot be negative"}), 400

        account = Account(id=next(account_ids), name=name, balance=initial_balance)
        accounts[account.id] = account
        return jsonify(asdict(account)), 201

    @app.get("/accounts/<int:account_id>")
    def get_account(account_id: int):
        account, error = get_account_or_404(account_id)
        if error:
            return error
        return jsonify(asdict(account))

    @app.post("/accounts/<int:account_id>/deposit")
    def deposit(account_id: int):
        account, error = get_account_or_404(account_id)
        if error:
            return error

        amount, amount_error = parse_amount(request.get_json(silent=True) or {})
        if amount_error:
            return amount_error

        account.balance += amount
        tx = {"id": next(transaction_ids), "type": "deposit", "account_id": account.id, "amount": amount}
        transactions.append(tx)
        return jsonify({"account": asdict(account), "transaction": tx})

    @app.post("/accounts/<int:account_id>/withdraw")
    def withdraw(account_id: int):
        account, error = get_account_or_404(account_id)
        if error:
            return error

        amount, amount_error = parse_amount(request.get_json(silent=True) or {})
        if amount_error:
            return amount_error
        if account.balance < amount:
            return jsonify({"error": "insufficient funds"}), 400

        account.balance -= amount
        tx = {"id": next(transaction_ids), "type": "withdraw", "account_id": account.id, "amount": amount}
        transactions.append(tx)
        return jsonify({"account": asdict(account), "transaction": tx})

    @app.post("/transfers")
    def transfer():
        data = request.get_json(silent=True) or {}
        from_id = data.get("from_account_id")
        to_id = data.get("to_account_id")
        if from_id == to_id:
            return jsonify({"error": "from_account_id and to_account_id must be different"}), 400

        from_account, from_error = get_account_or_404(from_id)
        if from_error:
            return from_error
        to_account, to_error = get_account_or_404(to_id)
        if to_error:
            return to_error

        amount, amount_error = parse_amount(data)
        if amount_error:
            return amount_error
        if from_account.balance < amount:
            return jsonify({"error": "insufficient funds"}), 400

        from_account.balance -= amount
        to_account.balance += amount
        tx = {
            "id": next(transaction_ids),
            "type": "transfer",
            "from_account_id": from_account.id,
            "to_account_id": to_account.id,
            "amount": amount,
        }
        transactions.append(tx)
        return jsonify(
            {"from_account": asdict(from_account), "to_account": asdict(to_account), "transaction": tx}
        )

    @app.get("/accounts/<int:account_id>/statement")
    def statement(account_id: int):
        account, error = get_account_or_404(account_id)
        if error:
            return error

        account_transactions = [
            tx
            for tx in transactions
            if tx.get("account_id") == account_id
            or tx.get("from_account_id") == account_id
            or tx.get("to_account_id") == account_id
        ]
        return jsonify({"account": asdict(account), "transactions": account_transactions})

    return app


app = create_app()

if __name__ == "__main__":
    app.run()
