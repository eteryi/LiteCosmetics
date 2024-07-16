package dev.etery.litecosmetics.data;

import dev.etery.litecosmetics.Category;
import dev.etery.litecosmetics.cosmetic.Cosmetic;

import java.util.*;

public interface CosmeticPlayer {
    <T extends Cosmetic> T getSelected(Category<T> category);
    boolean has(Cosmetic cosmetic);
    <T extends Cosmetic> void select(Category<T> category, T cosmetic);
    void give(Cosmetic cosmetic);
    void give(String id);
    <T extends Cosmetic> Collection<T> boughtCosmetics(Category<T> category);
    List<String> boughtCosmetics();
    Set<Map.Entry<Category<?>, Cosmetic>> selectedCosmetics();
}
