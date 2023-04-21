package prog1.kotprog.dontstarve.solution.inventory;

import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquipItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquippableItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

public class Inventory implements BaseInventory {
    private final EquipItem equippedItem;
    private final AbstractItem[] inventory;

    public Inventory() {
        this.inventory = new AbstractItem[10];
        this.equippedItem = new EquipItem();
    }

    /**
     * @param index ellenorzi, hogy helyes-e az index
     * @return hogy helyes volt-e
     */
    public boolean validIndex(int index) {
        return index < inventory.length && index >= 0;
    }

    /**
     * @param index -en levo poziciot ellenorzi, hogy null-e, vagyis hogy van-e ott valami vagy nincs
     * @return hogy talált-e ott valamit
     */
    public boolean hasItem(int index) {
        if (validIndex(index)) {
            if (inventory[index] != null) {
                if (inventory[index].getAmount() == 0) {
                    inventory[index] = null;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @param item a hozzáadni kívánt tárgy
     * @return hogy sikerult-e
     */
    @Override
    public boolean addItem(AbstractItem item) {
        int remainingItems = item.getAmount();

        for (int i = 0; i < inventory.length; i++) {
            int currentSlotAmount;

            if (!hasItem(i) && !item.getType().isStackable()) {
                inventory[i] = item;
                return true;
            } else if (hasItem(i) && inventory[i].getType().equals(item.getType()) && item.getType().isStackable()) {
                currentSlotAmount = inventory[i].getAmount();
                if (currentSlotAmount < inventory[i].getType().getMaxStackAmount()) {
                    int remainingCapacity = inventory[i].getType().getMaxStackAmount() - currentSlotAmount;
                    if (remainingCapacity >= remainingItems) {
                        inventory[i].setAmount(currentSlotAmount + remainingItems);
                        return true;
                    } else {
                        inventory[i].setAmount(inventory[i].getType().getMaxStackAmount());
                        remainingItems -= remainingCapacity;
                    }
                }
            }
        }

        for (int j = 0; j < inventory.length; j++) {
            if (!hasItem(j)) {
                inventory[j] = new AbstractItem(item.getType(), item.getAmount()) {
                };
                int currentSlotMaxAmount = inventory[j].getType().getMaxStackAmount();
                if (currentSlotMaxAmount >= remainingItems) {
                    inventory[j].setAmount(remainingItems);
                    return true;
                } else {
                    inventory[j].setAmount(currentSlotMaxAmount);
                    remainingItems -= currentSlotMaxAmount;
                }
            }
        }

        item.setAmount(remainingItems);
        return false;
    }


    /**
     * @param index a slot indexe, amelyen lévő itemet szeretnénk eldobni
     * @return hogy sikerult-e
     */
    @Override
    public AbstractItem dropItem(int index) {
        if (validIndex(index) && hasItem(index)) {
            AbstractItem droppableItem = inventory[index];
            inventory[index] = null;
            return droppableItem;
        }
        return null;
    }

    /**
     * @param type   a törlendő item típusa
     * @param amount a törlendő item mennyisége
     * @return sikerult-e torolni
     */
    @Override
    public boolean removeItem(ItemType type, int amount) {
        int inventoryAmount = 0;

        for (int i = 0; i < inventory.length; i++) {
            if (hasItem(i) && inventory[i].getType().equals(type)) {
                inventoryAmount += inventory[i].getAmount();
            }
        }

        if (inventoryAmount < amount) {
            return false;
        }

        for (int i = 0; i < inventory.length; i++) {
            if (hasItem(i) && inventory[i].getType().equals(type)) {
                int currentSlotAmount = inventory[i].getAmount();
                if (currentSlotAmount >= amount) {
                    int newAmount = currentSlotAmount - amount;
                    if (newAmount == 0) {
                        inventory[i] = null;
                    } else {
                        inventory[i].setAmount(newAmount);
                    }
                    return true;
                } else {
                    inventory[i] = null;
                    amount -= currentSlotAmount;
                }


            }
        }
        return false;
    }

    /**
     * @param index1 a slot indexe, amelyen az első item található
     * @param index2 a slot indexe, amelyen a második item található
     * @return sikerult-e kicserelni
     */
    @Override
    public boolean swapItems(int index1, int index2) {
        if (validIndex(index1) && validIndex(index2)) {
            if (hasItem(index1) && hasItem(index2)) {
                AbstractItem temp = inventory[index1];
                inventory[index1] = inventory[index2];
                inventory[index2] = temp;
                return true;
            }
        }
        return false;
    }

    /**
     * @param index    a mozgatni kívánt item pozíciója az inventory-ban
     * @param newIndex az új pozíció, ahova mozgatni szeretnénk az itemet
     * @return sikerult-e mozgatni
     */
    @Override
    public boolean moveItem(int index, int newIndex) {
        if (validIndex(index) && validIndex(newIndex)) {
            if (hasItem(index) && !hasItem(newIndex)) {
                inventory[newIndex] = inventory[index];
                inventory[index] = null;
                return true;
            }
        }
        return false;
    }

    /**
     * @param index1 első item pozíciója az inventory-ban
     * @param index2 második item pozíciója az inventory-ban
     * @return sikerult-e összevonni
     */
    @Override
    public boolean combineItems(int index1, int index2) {

        if (validIndex(index1) && validIndex(index2) && hasItem(index1) && hasItem(index2)) {
            if (inventory[index1].getType().equals(inventory[index2].getType()) && inventory[index1].getType().isStackable()) {
                if (inventory[index1].getAmount() == inventory[index1].getType().getMaxStackAmount()) {
                    return false;
                }

                if (inventory[index1].getType().getMaxStackAmount() >= (inventory[index1].getAmount() + inventory[index2].getAmount())) {
                    inventory[index1].setAmount(inventory[index1].getAmount() + inventory[index2].getAmount());
                    inventory[index2] = null;
                    return true;

                } else {
                    int left = inventory[index1].getType().getMaxStackAmount() - inventory[index1].getAmount();
                    int maxAmount = inventory[index1].getType().getMaxStackAmount();
                    inventory[index1].setAmount(maxAmount);
                    inventory[index2].setAmount(inventory[index2].getAmount() - left);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param index a kézbe venni kívánt tárgy pozíciója az inventory-ban
     * @return sikerult-e kézbe venni
     */
    @Override
    public boolean equipItem(int index) {
        if (validIndex(index) && hasItem(index) && inventory[index].getType().isEquippable()) {
            if (equippedItem.getEquippedItem() == null) {
                equippedItem.setEquippedItem((EquippableItem) inventory[index]);
                inventory[index] = null;
                return true;
            } else {
                AbstractItem temp = inventory[index];
                inventory[index] = equippedItem.getEquippedItem();
                equippedItem.setEquippedItem((EquippableItem) temp);
                return true;
            }
        }
        return false;
    }

    /**
     * @return hogy milyen itemet vettunk ki a kezunkbol
     */
    @Override
    public EquippableItem unequipItem() {
        if (equippedItem != null) {
            if (emptySlots() == 0) {
                EquippableItem temp = equippedItem.getEquippedItem();
                equippedItem.setEquippedItem(null);
                return temp;
            }
            for (int i = 0; i < inventory.length; i++) {
                if (!hasItem(i)) {
                    inventory[i] = equippedItem.getEquippedItem();
                    equippedItem.setEquippedItem(null);
                    return null;
                }
            }

        }
        return null;
    }

    /**
     * @param index A megfőzni kívánt item pozíciója az inventory-ban
     * @return hogy mit foztunk meg
     */
    @Override
    public ItemType cookItem(int index) {
        if (validIndex(index) && hasItem(index) && inventory[index].getType().isCookable()) {
            ItemType cookableItem = inventory[index].getType();
            inventory[index].setAmount(inventory[index].getAmount() - 1);
            if (inventory[index].getAmount() == 0) {
                inventory[index] = null;
            }
            return cookableItem;
        }
        return null;
    }

    /**
     * @param index A megenni kívánt item pozíciója az inventory-ban
     * @return hogy mit ettunk meg
     */
    @Override
    public ItemType eatItem(int index) {
        if (validIndex(index) && hasItem(index) && inventory[index].getType().isEdible()) {
            ItemType food = inventory[index].getType();
            inventory[index].setAmount(inventory[index].getAmount() - 1);
            if (inventory[index].getAmount() == 0) {
                inventory[index] = null;
            }
            return food;
        }
        return null;
    }

    /**
     * @return mennyi ures hely van az inventoryban
     */
    @Override
    public int emptySlots() {
        if (inventory == null) {
            return 10;
        }
        int emptySlotAmount = 0;
        for (int i = 0; i < inventory.length; i++) {
            if (!hasItem(i)) {
                emptySlotAmount++;
            }
        }
        return emptySlotAmount;
    }

    /**
     * @return hogy mi volt a kezeben a karakternek
     */
    @Override
    public EquippableItem equippedItem() {
        if (equippedItem.getEquippedItem() != null) {
            return equippedItem.getEquippedItem();
        }
        return null;
    }

    /**
     * @param index a lekérdezni kívánt pozíció
     * @return hogy milyen item volt ott
     */
    @Override
    public AbstractItem getItem(int index) {
        if (validIndex(index) && hasItem(index)) {
            return inventory[index];
        }

        return null;
    }
}