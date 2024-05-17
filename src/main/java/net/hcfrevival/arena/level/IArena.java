package net.hcfrevival.arena.level;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public interface IArena {
    /**
     * @return Internal name
     */
    String getName();

    /**
     * @return Display name
     */
    Component getDisplayName();

    /**
     * @return Authors
     */
    String getAuthors();

    /**
     * @return List of Arena Instances
     */
    List<IArenaInstance> getInstances();

    /**
     * @return Returns true if the provided Arena has an available instance
     */
    default boolean hasAvailableInstances() {
        return getAvailableInstance().isPresent();
    }

    /**
     * @return Optional of available Arena Instances
     */
    default Optional<IArenaInstance> getAvailableInstance() {
        return getInstances().stream().filter(IArenaInstance::isAvailable).findFirst();
    }

    /**
     * Register a new Arena Instance
     * @param inst Arena Instance
     */
    default void registerInstance(IArenaInstance inst) {
        inst.setOwner(this);
        getInstances().add(inst);
    }
}
