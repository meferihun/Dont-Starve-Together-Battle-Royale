package prog1.kotprog.dontstarve.solution.inventory;

import prog1.kotprog.dontstarve.solution.inventory.items.*;

public class Main {
    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        inventory.addItem(new ItemLog(10));
        inventory.addItem(new ItemRawBerry(10));
        inventory.addItem(new ItemPickaxe());
        inventory.addItem(new ItemRawCarrot(1));
        inventory.addItem(new ItemStone(1));
        itemDuplicate(inventory);

        inventory.addItem(new ItemRawCarrot(6));
        itemDuplicate(inventory);

        inventory.addItem(new ItemRawCarrot(24));
        itemDuplicate(inventory);

    }

    private static void itemDuplicate(Inventory inventory) {
        for (int i = 0; i < 11; i++) {
            if (i == 10) {
                System.out.println();
            }
            if (inventory.getItem(i) == null) {
                continue;
            }
            if (i < 10) {
                System.out.print(inventory.getItem(i).getType() + " ");
                System.out.print(inventory.getItem(i).getAmount() + ", ");
            }
        }
    }
}
