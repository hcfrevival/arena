package net.hcfrevival.arena.session.request;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.arena.ArenaManager;
import net.hcfrevival.arena.session.IDuelRequest;
import net.hcfrevival.arena.session.SessionManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
public final class DuelRequestManager extends ArenaManager {
    public SessionManager sessionManager;
    public final DuelRequestExecutor executor;
    public final Set<IDuelRequest<?>> requestRepository;

    public DuelRequestManager(SessionManager sessionManager) {
        super(sessionManager.getPlugin());
        this.sessionManager = sessionManager;
        this.executor = new DuelRequestExecutor(this);
        this.requestRepository = Sets.newConcurrentHashSet();
    }

    public <T> Optional<IDuelRequest<?>> getRequest(UUID id) {
        return requestRepository.stream().filter(req -> req.getId().equals(id)).findFirst();
    }

    public <T> Optional<IDuelRequest<?>> getRequest(T sender, T receiver) {
        return requestRepository.stream().filter(req -> req.getSender().equals(sender) && req.getReceiver().equals(receiver) && !req.isExpired()).findFirst();
    }
}
