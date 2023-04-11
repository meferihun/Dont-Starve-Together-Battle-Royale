package prog1.kotprog.dontstarve.solution.level;

import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

public class Field implements BaseField {
    private final int color;
    private ItemType placed;

    public Field(int color) {
        this.color = color;
    }

    @Override
    public boolean isWalkable() {
        return color != MapColors.WATER;
    }

    @Override
    public boolean hasTree() {
        return color == MapColors.TREE;
    }

    @Override
    public boolean hasStone() {
        return color == MapColors.STONE;
    }

    @Override
    public boolean hasTwig() {
        return color == MapColors.TWIG;
    }

    @Override
    public boolean hasBerry() {
        return color == MapColors.BERRY;
    }

    @Override
    public boolean hasCarrot() {
        return color == MapColors.CARROT;
    }

    @Override
    public boolean hasFire() {
        return true;
    }

    @Override
    public AbstractItem[] items() {
        return new AbstractItem[0];
    }
}
