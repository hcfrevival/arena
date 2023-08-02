package net.hcfrevival.arena.queue.impl;

import lombok.Getter;
import net.hcfrevival.arena.gamerule.EGamerule;

import java.util.UUID;

public record UnrankedQueueEntry(@Getter UUID uniqueId, @Getter EGamerule gamerule) implements IArenaQueue {}
