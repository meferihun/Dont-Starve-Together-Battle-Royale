package prog1.kotprog.dontstarve.solution.inventory.items;

/**
 * A fire item leírására szolgáló osztály.
 */
public class ItemFire extends AbstractItem {
    private int tick;

    /**
     * Konstruktor, amellyel a tárgy létrehozható.
     */
    public ItemFire() {
        super(ItemType.FIRE, 1);
        this.tick = 0;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
