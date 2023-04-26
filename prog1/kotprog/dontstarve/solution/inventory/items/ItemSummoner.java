package prog1.kotprog.dontstarve.solution.inventory.items;

public class ItemSummoner {
    public static AbstractItem summonItem(ItemType type, int amount) {
        return switch (type) {
            case AXE -> new ItemAxe();
            case PICKAXE -> new ItemPickaxe();
            case TORCH -> new ItemTorch();
            case SPEAR -> new ItemSpear();
            case LOG -> new ItemLog(amount);
            case STONE -> new ItemStone(amount);
            case TWIG -> new ItemTwig(amount);
            case RAW_CARROT -> new ItemRawCarrot(amount);
            case COOKED_CARROT -> new ItemCookedCarrot(amount);
            case RAW_BERRY -> new ItemRawBerry(amount);
            case COOKED_BERRY -> new ItemCookedBerry(amount);
            default -> null;
        };
    }
}
