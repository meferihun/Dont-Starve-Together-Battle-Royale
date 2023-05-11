package prog1.kotprog.dontstarve.solution.level;

import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemFire;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        if (!items.isEmpty()) {
            for (AbstractItem item : items) {
                if (item.getType().equals(ItemType.FIRE)) {
                    return true;
                }
            }
        }
        return false;
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
            return new AbstractItem[0];
        }
    }

    /**
     * A mezore elhelyezunk egy elemet, pl. mert kitermeles befejezodott vagy mert kivesszuk az inventorynkbol.
     *
     * @param item amit belerakunk
     */
    public void placeItem(AbstractItem item) {
        if (item != null) {
            if (item.getType().equals(ItemType.FIRE) && !hasFire()) {
                items.add(item);
            } else if (hasFire() && !item.getType().equals(ItemType.FIRE)) {

                AbstractItem temp = items.get(items.size() - 1);
                items.set(items.size() - 1, item);
                items.add(temp);
            } else if (!item.getType().equals(ItemType.FIRE) && !hasFire()) {
                items.add(item);
            }
        }
    }

    /**
     * A mezorol felvesszuk a legfelso elemet, ha van rajta.
     *
     * @return az item
     */
    public boolean pickUpItem() {
        if (!items.isEmpty()) {
            if (!items.get(0).getType().equals(ItemType.FIRE)) {
                items.remove(0);
                return true;
            }
        }
        return false;
    }

    public ItemFire placedFire() {
        for (AbstractItem item : items) {
            if (item.getType().equals(ItemType.FIRE)) {
                return (ItemFire) item;
            }
        }
        return null;
    }

    public boolean removeItem(AbstractItem item) {

        for (AbstractItem currentItem : items) {
            if (currentItem.getType().equals(item.getType())) {
                items.remove(item);
                return true;
            }
        }
        return false;
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