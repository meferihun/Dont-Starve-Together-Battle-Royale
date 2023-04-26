package prog1.kotprog.dontstarve.solution.level;

import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.exceptions.NotImplementedException;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemFire;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Field implements BaseField {
    private int color;

    private float extractionProgress;

    private List<AbstractItem> items;

    public Field(int color) {
        this.color = color;
        this.extractionProgress = 0.0f;
        this.items = new ArrayList<>();
    }

    @Override
    public boolean isWalkable() {
        return color != MapColors.WATER;
    }

    public boolean isEmpty() {
        return (color == MapColors.EMPTY && !hasFire());
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
        return items.contains(ItemType.FIRE);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public AbstractItem[] items() {
        // a tavon nem lehet semmilyen targy
        if (color != MapColors.WATER) {
            AbstractItem[] itemArray = new AbstractItem[items.size()];
            return items.toArray(itemArray);
        } else {
            return null;
        }
    }

    /**
     * A mezore elhelyezunk egy elemet, pl. mert kitermeles befejezodott vagy mert kivesszuk az inventorynkbol.
     *
     * @param item amit belerakunk
     */
    public void placeItem(AbstractItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * A mezorol felvesszuk a legfelso elemet, ha van rajta.
     *
     * @return az item
     */
    public AbstractItem getItem() {
        if (!items.isEmpty()) {
            AbstractItem tempItem = items.get(0);
            items.remove(0);
            return tempItem;
        }
        return null;
    }

    /**
     * Lekerdezi, hogy az adott mezon hany %-on all a kitermeles merteke.
     *
     * @return a kitermeles allapota az adott mezon
     */
    public float getExtractionProgress() {
        return extractionProgress;
    }

    /**
     * @param value, hogy az adott kitermeles hogy all
     */
    public void setExtractionProgress(float value) {
        if (value >= 0 && value <= 1) {
            this.extractionProgress = value;
        }
    }
}