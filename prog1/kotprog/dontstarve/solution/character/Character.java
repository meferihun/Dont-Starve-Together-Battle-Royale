package prog1.kotprog.dontstarve.solution.character;

import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.character.actions.ActionNone;
import prog1.kotprog.dontstarve.solution.inventory.BaseInventory;
import prog1.kotprog.dontstarve.solution.inventory.Inventory;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquippableItem;
import prog1.kotprog.dontstarve.solution.utility.Position;

public class Character implements BaseCharacter {

    private String name;
    private boolean player;
    private float healthPoint;
    private float hunger;
    private float speed;

    private Position currentPosition;
    private Action lastAction;
    private Inventory inventory;

    public Character(String name, boolean player) {
        if (name != null && !name.equals("")) {
            this.name = name;
        } else {
            this.name = "New Player";
        }
        this.player = player;
        this.healthPoint = 100;
        this.hunger = 100;
        this.speed = 1;
        this.currentPosition = null;
        this.lastAction = new ActionNone();
        this.inventory = new Inventory();
    }

    /**
     * @return játékos-e
     */
    public boolean isPlayer() {
        return player;
    }

    /**
     * A karakter mozgási sebességének lekérdezésére szolgáló metódus.
     *
     * @return a karakter mozgási sebessége
     */
    @Override
    public float getSpeed() {
        return speed;
    }

    /**
     * A karakter mozgási sebességének beallitasara szolgáló metódus.
     */
    public void setSpeed() {
        this.speed = 1;
        if (getHp() > 50 && getHp() <= 100) {
            this.speed *= 1;
        } else if (getHp() > 30 && getHp() <= 50) {
            this.speed *= 0.9;
        } else if (getHp() > 10 && getHp() <= 30) {
            this.speed *= 0.75;
        } else if (getHp() >= 0 && getHp() <= 10) {
            this.speed *= 0.6f;
        }

        if (getHunger() > 50 && getHunger() <= 100) {
            this.speed *= 1;
        } else if (getHunger() > 20 && getHunger() <= 50) {
            this.speed *= 0.9;
        } else if (getHunger() > 0 && getHunger() <= 20) {
            this.speed *= 0.8;
        } else if (getHunger() == 0) {
            this.speed *= 0.5;
        }
    }

    /**
     * A karakter jóllakottságának mértékének lekérdezésére szolgáló metódus.
     *
     * @return a karakter jóllakottsága
     */
    @Override
    public float getHunger() {
        return hunger;
    }

    /**
     * A karakter jóllakottságának mértékének beallitasara szolgáló metódus.
     *
     * @param hunger a karakter jóllakottsága
     */
    public void setHunger(float hunger) {
        if (hunger >= 100) {
            this.hunger = 100;
        } else if (hunger < 100 && hunger > 0) {
            this.hunger = hunger;
        } else {
            this.hunger = 0;
        }
    }

    /**
     * A karakter életerejének lekérdezésére szolgáló metódus.
     *
     * @return a karakter életereje
     */
    @Override
    public float getHp() {
        return healthPoint;
    }

    /**
     * @param hp es beallitja az eletet a megfelelo ertekre
     */
    public void setHp(float hp) {
        if (hp >= 100) {
            this.healthPoint = 100;
        } else if (hp < 100 && hp > 0) {
            this.healthPoint = hp;
        } else {
            this.healthPoint = 0;
        }
    }

    public boolean isAlive() {
        return healthPoint > 0;
    }

    /**
     * A karakter inventory-jának lekérdezésére szolgáló metódus.
     * <br>
     * A karakter inventory-ja végig ugyanaz marad, amelyet referencia szerint kell visszaadni.
     *
     * @return a karakter inventory-ja
     */
    @Override
    public BaseInventory getInventory() {
        return inventory;
    }

    /**
     * A játékos aktuális pozíciójának lekérdezésére szolgáló metódus.
     *
     * @return a játékos pozíciója
     */
    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * @param position beallitja a karakter aktualis poziciojat a kapott ertekre
     */
    public void setCurrentPosition(Position position) {
        if (position != null) {
            this.currentPosition = position;
        }
    }

    /**
     * Az utolsó cselekvés lekérdezésére szolgáló metódus.
     * <br>
     * Egy létező Action-nek kell lennie, nem lehet null.
     *
     * @return az utolsó cselekvés
     */
    @Override
    public Action getLastAction() {
        if (lastAction != null) {
            return lastAction;
        } else {
            return new ActionNone();
        }
    }

    /**
     * @param lastAction beallitja az ertekre, hogy mi volt az utolso cselekvese
     */
    public void setLastAction(Action lastAction) {
        if (lastAction != null) {
            this.lastAction = lastAction;
        } else {
            this.lastAction = new ActionNone();
        }
        // TODO execute-olni is kellene az action ez utan (meghivni az action logikat valahogy?)
    }

    /**
     * A játékos nevének lekérdezésére szolgáló metódus.
     *
     * @return a játékos neve
     */
    @Override
    public String getName() {
        return name;
    }

}