package dev.etery.litecosmetics.event;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.LiteCosmetics;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public class LCOpenShopEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private final LiteCosmetics cosmetics;
    private final Category<?> optCategory;

    public LCOpenShopEvent(final Player player, final LiteCosmetics cosmetics, Category<?> categoryShop) {
        super(player);
        this.cosmetics = cosmetics;
        this.optCategory = categoryShop;
    }

    public Optional<Category<?>> getCategoryShop() {
        return Optional.ofNullable(optCategory);
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
