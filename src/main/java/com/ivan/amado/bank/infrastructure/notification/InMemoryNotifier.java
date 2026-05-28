package com.ivan.amado.bank.infrastructure.notification;

import com.ivan.amado.bank.application.port.output.Notifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Profile("test")
public class InMemoryNotifier implements Notifier {

    private final Map<UUID, List<String>> inbox = new ConcurrentHashMap<>();

    @Override
    public void notify(UUID accountId, String message) {
        inbox.computeIfAbsent(accountId, id -> new CopyOnWriteArrayList<>()).add(message);
    }

    public List<String> getNotifications(UUID accountId) {
        return inbox.getOrDefault(accountId, List.of());
    }
}
