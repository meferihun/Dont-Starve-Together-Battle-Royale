package prog1.kotprog.dontstarve.solution.inventory.items;

public class EquipItem {
    private EquippableItem equippedItem;

    /**
     * @return milyen item van a kezében
     */
    public EquippableItem getEquippedItem() {
        return equippedItem;
    }

    /**
     * @param equippedItem beallitja, hogy milyen item van a kezben
     */
    public void setEquippedItem(EquippableItem equippedItem) {
        this.equippedItem = equippedItem;
    }
}
