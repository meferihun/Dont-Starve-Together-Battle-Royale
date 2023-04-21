package prog1.kotprog.dontstarve.solution.inventory.items;

/**
 * Felvehető / kézbe vehető item leírására szolgáló osztály.
 */
public abstract class EquippableItem extends AbstractItem {
    private float percentage;

    /**
     * Konstruktor, amellyel a tárgy létrehozható.
     *
     * @param type az item típusa
     */
    public EquippableItem(ItemType type, float percentage) {
        super(type, 1);
        if (percentage >= 100) {
            this.percentage = 100;
        } else if (percentage > 0 && percentage < 100) {
            this.percentage = percentage;
        } else {
            this.percentage = 0;
        }
    }

    /**
     * Megadja, hogy milyen állapotban van a tárgy.
     *
     * @return a tárgy használatlansága, %-ban (100%: tökéletes állapot)
     */
    public float percentage() {
        return percentage;
    }
}
