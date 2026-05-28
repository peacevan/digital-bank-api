package com.ivan.amado.bank.application.port.output;

import java.util.UUID;

public interface Notifier {
    void notify(UUID accountId, String message);
}
