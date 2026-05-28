import unittest

from app import create_app


class DigitalBankApiTestCase(unittest.TestCase):
    def setUp(self):
        app = create_app()
        app.testing = True
        self.client = app.test_client()

    def create_account(self, name, initial_balance=0):
        response = self.client.post("/accounts", json={"name": name, "initial_balance": initial_balance})
        self.assertEqual(response.status_code, 201)
        return response.get_json()

    def test_create_account_and_get_account(self):
        account = self.create_account("Alice", 100)
        response = self.client.get(f"/accounts/{account['id']}")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.get_json()["balance"], 100)

    def test_create_account_with_invalid_initial_balance(self):
        response = self.client.post("/accounts", json={"name": "Alice", "initial_balance": "abc"})
        self.assertEqual(response.status_code, 400)
        self.assertEqual(response.get_json()["error"], "initial_balance must be a valid number")

    def test_create_account_with_negative_initial_balance(self):
        response = self.client.post("/accounts", json={"name": "Alice", "initial_balance": -10})
        self.assertEqual(response.status_code, 400)
        self.assertEqual(response.get_json()["error"], "initial_balance cannot be negative")

    def test_deposit_and_withdraw(self):
        account = self.create_account("Bob", 100)

        deposit_response = self.client.post(f"/accounts/{account['id']}/deposit", json={"amount": 50})
        self.assertEqual(deposit_response.status_code, 200)
        self.assertEqual(deposit_response.get_json()["account"]["balance"], 150)

        withdraw_response = self.client.post(f"/accounts/{account['id']}/withdraw", json={"amount": 70})
        self.assertEqual(withdraw_response.status_code, 200)
        self.assertEqual(withdraw_response.get_json()["account"]["balance"], 80)

    def test_withdraw_with_insufficient_funds(self):
        account = self.create_account("Carol", 20)
        response = self.client.post(f"/accounts/{account['id']}/withdraw", json={"amount": 30})
        self.assertEqual(response.status_code, 400)
        self.assertEqual(response.get_json()["error"], "insufficient funds")

    def test_transfer_between_accounts(self):
        from_account = self.create_account("Alice", 200)
        to_account = self.create_account("Bob", 50)

        transfer_response = self.client.post(
            "/transfers",
            json={
                "from_account_id": from_account["id"],
                "to_account_id": to_account["id"],
                "amount": 70,
            },
        )
        self.assertEqual(transfer_response.status_code, 200)
        transfer_data = transfer_response.get_json()
        self.assertEqual(transfer_data["from_account"]["balance"], 130)
        self.assertEqual(transfer_data["to_account"]["balance"], 120)

        statement_response = self.client.get(f"/accounts/{from_account['id']}/statement")
        self.assertEqual(statement_response.status_code, 200)
        statement = statement_response.get_json()
        self.assertEqual(len(statement["transactions"]), 1)
        self.assertEqual(statement["transactions"][0]["type"], "transfer")

    def test_transfer_with_missing_account_ids(self):
        response = self.client.post("/transfers", json={"amount": 10})
        self.assertEqual(response.status_code, 400)
        self.assertEqual(response.get_json()["error"], "from_account_id and to_account_id are required")

    def test_transfer_with_same_account_ids(self):
        account = self.create_account("Alice", 100)
        response = self.client.post(
            "/transfers",
            json={"from_account_id": account["id"], "to_account_id": account["id"], "amount": 10},
        )
        self.assertEqual(response.status_code, 400)
        self.assertEqual(response.get_json()["error"], "from_account_id and to_account_id must be different")


if __name__ == "__main__":
    unittest.main()
