package dev.etery.litecosmetics.event;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class LCEquipCosmeticEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;
    private final LiteCosmetics cosmetics;
    private final Category<?> category;
    private final Cosmetic cosmetic;

    public LCEquipCosmeticEvent(final Player player, LiteCosmetics cosmetics, Category<?> category, Cosmetic cosmetic) {
        super(player);
        this.cosmetics = cosmetics;
        this.category = category;
        this.cosmetic = cosmetic;
    }

    public Cosmetic getCosmetic() {
        return this.cosmetic;
    }

    @SuppressWarnings("unchecked")
    public <T extends Cosmetic> Category<T> getCategory() {
        return (Category<T>) category;
    }

    public LiteCosmetics getCosmetics() {
        return this.cosmetics;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
