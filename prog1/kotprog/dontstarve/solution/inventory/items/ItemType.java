package prog1.kotprog.dontstarve.solution.inventory.items;

/**
 * A tárgy típusok enumja.
 */
public enum ItemType {
    /**
     * fejsze.
     */
    AXE(false, false, true, 0, 0, 1),

    /**
     * csákány.
     */
    PICKAXE(false, false, true, 0, 0, 1),

    /**
     * lándzsa.
     */
    SPEAR(false, false, true, 0, 0, 1),

    /**
     * fáklya.
     */
    TORCH(false, false, true, 0, 0, 1),

    /**
     * farönk.
     */
    LOG(false, false, false, 0, 0, 15),

    /**
     * kő.
     */
    STONE(false, false, false, 0, 0, 10),

    /**
     * gally.
     */
    TWIG(false, false, false, 0, 0, 20),

    /**
     * nyers bogyó.
     */
    RAW_BERRY(true, true, false, 20, -5, 10),

    /**
     * nyers répa.
     */
    RAW_CARROT(true, true, false, 12, 1, 10),

    /**
     * főtt bogyó.
     */
    COOKED_BERRY(true, false, false, 10, 1, 10),

    /**
     * főtt répa.
     */
    COOKED_CARROT(true, false, false, 10, 3, 10),

    /**
     * tábortűz (inventory-ban nem tárolható!).
     */
    FIRE(false, false, false, 0, 0, 0);

    private final boolean isEdible;
    private final boolean isCookable;
    private final boolean isEquippable;
    private final int hungerModifier;
    private final int healthModifier;
    private final int maxStackAmount;

    ItemType(boolean isEdible, boolean isCookable, boolean isEquippable, int hungerModifier, int healthModifier, int maxStackAmount) {
        this.isEdible = isEdible;
        this.isCookable = isCookable;
        this.isEquippable = isEquippable;
        this.hungerModifier = hungerModifier;
        this.healthModifier = healthModifier;
        this.maxStackAmount = maxStackAmount;
    }


    public boolean isEdible() {
        return this.isEdible;
    }

    public boolean isStackable() {
        return (this.maxStackAmount > 1);
    }

    public boolean isCookable() {
        return this.isCookable;
    }

    public int getHealthModifier() {
        return this.healthModifier;
    }

    public int getHungerModifier() {
        return this.hungerModifier;
    }

    public int getMaxStackAmount() {
        return this.maxStackAmount;
    }

    public boolean isEquippable() {
        return isEquippable;
    }

}