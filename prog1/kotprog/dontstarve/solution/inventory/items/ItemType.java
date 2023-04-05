package prog1.kotprog.dontstarve.solution.inventory.items;

/**
 * A tárgy típusok enumja.
 */
public enum ItemType {
    /**
     * fejsze.
     */
    AXE("Fejsze", false, false, true,0, 0, 1, ""),

    /**
     * csákány.
     */
    PICKAXE("Csakany", false, false, true,0, 0, 1, ""),

    /**
     * lándzsa.
     */
    SPEAR("Landzsa", false, false, true,0, 0, 1, ""),

    /**
     * fáklya.
     */
    TORCH("Faklya", false, false, true,0, 0, 1, ""),

    /**
     * farönk.
     */
    LOG("Faronk", false, false, false,0, 0, 15, ""),

    /**
     * kő.
     */
    STONE("Ko", false, false, false,0, 0, 10, ""),

    /**
     * gally.
     */
    TWIG("Gally", false, false, false,0, 0, 20, ""),

    /**
     * nyers bogyó.
     */
    RAW_BERRY("Nyers bogyo", true,true ,false, 20, -5, 10, "Fott bogyo"),

    /**
     * nyers répa.
     */
    RAW_CARROT("Nyers repa", true, true, false,12, 1, 10, "Fott repa"),

    /**
     * főtt bogyó.
     */
    COOKED_BERRY("Fott bogyo", true, false, false,10, 1, 10, ""),

    /**
     * főtt répa.
     */
    COOKED_CARROT("Fott repa", true, false, false,10, 3, 10, ""),

    /**
     * tábortűz (inventory-ban nem tárolható!).
     */
    FIRE("Tabortuz", false, false, false,0, 0, 0, "");

    private String name;
    private boolean isEdible;
    private boolean isCookable;
    private boolean isEquippable;
    private int hungerModifier;
    private int healthModifier;
    private int maxStackAmount;
    private String cookedVersion;

    ItemType(String name, boolean isEdible, boolean isCookable, boolean isEquippable,int hungerModifier, int healthModifier, int maxStackAmount, String cookedVersion) {
        this.name = name;
        this.isEdible = isEdible;
        this.isCookable = isCookable;
        this.isEquippable = isEquippable;
        this.hungerModifier = hungerModifier;
        this.healthModifier = healthModifier;
        this.maxStackAmount = maxStackAmount;
        this.cookedVersion = cookedVersion;
    }

    public String getName() {
        return name;
    }

    public boolean isEdible() {
        return this.isEdible;
    }

    public boolean isStackable() {
        return (this.maxStackAmount > 1);
    }

    public boolean isCookable(){
        return this.isCookable;
    }

    public int getHealthModifier(){
        return this.healthModifier;
    }

    public int getHungerModifier(){
        return this.hungerModifier;
    }

    public int getMaxStackAmount(){
        return this.maxStackAmount;
    }

    public boolean isEquippable() {
        return isEquippable;
    }

    public String getCookedVersion() {
        return cookedVersion;
    }
}