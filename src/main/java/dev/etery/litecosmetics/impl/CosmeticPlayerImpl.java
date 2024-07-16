package dev.etery.litecosmetics.impl;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.cosmetic.Cosmetic;
import dev.etery.litecosmetics.data.CosmeticPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class CosmeticPlayerImpl implements CosmeticPlayer {
    private final HashMap<Category<?>, Cosmetic> selectedCosmetics = new HashMap<>();
    private final HashSet<String> cosmeticsBought = new HashSet<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Cosmetic> T getSelected(Category<T> category) {
        return (T) selectedCosmetics.get(category);
    }

    @Override
    public boolean has(Cosmetic cosmetic) {
        return cosmeticsBought.contains(cosmetic.id());
    }

    @Override
    public <T extends Cosmetic> void select(Category<T> category, T cosmetic) {
        this.selectedCosmetics.put(category, cosmetic);
    }

    @Override
    public void give(Cosmetic cosmetic) {
        give(cosmetic.id());
    }

    @Override
    public void give(String id) {
        this.cosmeticsBought.add(id);
    }

    @Override
    public <T extends Cosmetic> Collection<T> boughtCosmetics(Category<T> category) {
        return this.cosmeticsBought.stream().filter(it -> it.split(":")[0].equals(category.id)).map(category::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public List<String> boughtCosmetics() {
        return new ArrayList<>(this.cosmeticsBought);
    }

    @Override
    public Set<Map.Entry<Category<?>, Cosmetic>> selectedCosmetics() {
        return this.selectedCosmetics.entrySet();
    }
}
