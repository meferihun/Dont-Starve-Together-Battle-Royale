package prog1.kotprog.dontstarve.solution.level;

import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

public class Field implements BaseField{
    private int hex;
    private ItemType position;

    public Field(int hex, ItemType position){
        this.hex = hex;
        this.position = position;
    }
    @Override
    public boolean isWalkable() {
        return hex != MapColors.WATER;
    }

    @Override
    public boolean hasTree() {
        return hex == MapColors.TREE;
    }

    @Override
    public boolean hasStone() {
        return hex == MapColors.STONE;
    }

    @Override
    public boolean hasTwig() {
        return hex == MapColors.TWIG;
    }

    @Override
    public boolean hasBerry() {
        return hex == MapColors.BERRY;
    }

    @Override
    public boolean hasCarrot() {
        return hex == MapColors.CARROT;
    }

    @Override
    public boolean hasFire() { return position.getName().equals("Tabortuz");
    }

    @Override
    public AbstractItem[] items() {
        return new AbstractItem[0];
    }
}
