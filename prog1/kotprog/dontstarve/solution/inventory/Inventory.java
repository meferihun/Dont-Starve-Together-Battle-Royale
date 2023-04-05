package prog1.kotprog.dontstarve.solution.inventory;

import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquippableItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

public class Inventory implements BaseInventory {
    private AbstractItem[] inventory = new AbstractItem[10];
    private Character equippedItem = new Character();

    public Inventory() {
    }

    public boolean hasItem(int index) {
        if (index < inventory.length && index >= 0) {
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

    @Override
    public boolean addItem(AbstractItem item) {
        if (emptySlots() == 0) {
            return false;
        }
        int remainingItems = item.getAmount();

        for (int i = 0; i < inventory.length; i++) {
            int currentSlotAmount = inventory[i].getAmount();
            if (!hasItem(i) && !item.getType().isStackable()) {
                inventory[i] = item;
                return true;
            } else if (hasItem(i) && inventory[i].getType().equals(item.getType()) && item.getType().isStackable() && currentSlotAmount < inventory[i].getType().getMaxStackAmount()) {
                int newAmount = inventory[i].getType().getMaxStackAmount() - currentSlotAmount;
                inventory[i].setAmount(remainingItems);
                remainingItems -= newAmount;
                if (remainingItems <= 0) {
                    return true;
                }

            }
        }
        if (emptySlots() == 0) {
            return false;
        }
        for (int j = 0; j < inventory.length; j++) {
            if (!hasItem(j)) {
                inventory[j] = item;
                inventory[j].setAmount(remainingItems);
                remainingItems -= inventory[j].getAmount();
            }
            if (remainingItems <= 0) {
                return true;
            }
        }
        if (remainingItems > 0) {
            System.out.println("A targybol ennyi maradt ki, amit nem adtunk hozza: " + remainingItems);
            return false;
        }
        return false;
    }


    @Override
    public AbstractItem dropItem(int index) {
        if (index >= 0 && index < inventory.length && hasItem(index)) {
            AbstractItem droppableItem = inventory[index];
            inventory[index] = null;
            return droppableItem;
        }
        return null;
    }

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

    @Override
    public boolean swapItems(int index1, int index2) {
        if (index1 < inventory.length && index2 < inventory.length && index1 >= 0 && index2 >= 0) {
            if (hasItem(index1) && hasItem(index2)) {
                AbstractItem temp = inventory[index1];
                inventory[index1] = inventory[index2];
                inventory[index2] = temp;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean moveItem(int index, int newIndex) {
        if (index < inventory.length && newIndex < inventory.length && index >= 0 && newIndex >= 0) {
            if (hasItem(index) && !hasItem(newIndex)) {
                inventory[newIndex] = inventory[index];
                inventory[index] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean combineItems(int index1, int index2) {

        if (index1 < inventory.length && index2 < inventory.length && index1 >= 0 && index2 >= 0 && hasItem(index1) && hasItem(index2) && inventory[index1].getType().equals(inventory[index2].getType()) && inventory[index1].getType().isStackable()) {

            if (inventory[index1].getAmount() == inventory[index1].getType().getMaxStackAmount()) {
                return false;
            }

            if (inventory[index1].getType().getMaxStackAmount() >= (inventory[index1].getAmount() + inventory[index2].getAmount())) {
                inventory[index1].setAmount(inventory[index1].getAmount() + inventory[index2].getAmount());
                inventory[index2] = null;
                return true;

            } else {
                int left = inventory[index1].getType().getMaxStackAmount() - inventory[index1].getAmount();
                inventory[index1].setAmount(inventory[index1].getAmount() + inventory[index2].getAmount());
                inventory[index2].setAmount(inventory[index2].getAmount() - left);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equipItem(int index) {
        if (index < inventory.length && index >= 0 && hasItem(index) && inventory[index].getType().isEquippable()) {
            if (equippedItem.getEquippedItem() == null) {
                equippedItem.setEquippedItem((EquippableItem) inventory[index]);
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

    @Override
    public EquippableItem unequipItem() {
        return null;
    }

    @Override
    public ItemType cookItem(int index) {
        if (index >= 0 && index < inventory.length && hasItem(index) && inventory[index].getType().isCookable()) {
            ItemType cookableItem = inventory[index].getType();
            inventory[index].setAmount(inventory[index].getAmount() - 1);
            if (inventory[index].getAmount() == 0) {
                inventory[index] = null;
            }
            return cookableItem;
        }
        return null;
    }

    @Override
    public ItemType eatItem(int index) {
        if (index >= 0 && index < inventory.length && hasItem(index) && inventory[index].getType().isEdible()) {
            ItemType food = inventory[index].getType();
            inventory[index].setAmount(inventory[index].getAmount() - 1);
            if (inventory[index].getAmount() == 0) {
                inventory[index] = null;
            }
            return food;
        }
        return null;
    }

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

    @Override
    public EquippableItem equippedItem() {
        if (equippedItem.getEquippedItem() != null) {
            return equippedItem.getEquippedItem();
        }
        return null;
    }

    @Override
    public AbstractItem getItem(int index) {
        if (index >= 0 && index < inventory.length && hasItem(index)) {
            return inventory[index];
        }

        return null;
    }
}