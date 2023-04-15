package prog1.kotprog.dontstarve.solution.character;

import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.inventory.BaseInventory;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquippableItem;
import prog1.kotprog.dontstarve.solution.utility.Position;

public class Character implements BaseCharacter {

    private Action lastAction;
    private Action action;
    private String name;
    private float healthPoint;
    private float hunger;
    private float speed;
    private Position currentPosition;
    private boolean player;
    private AbstractItem[] inventory = new AbstractItem[10];

    public Character(String name, boolean player) {
        if (name != null && !name.equals("")) {
            this.name = name;
        } else {
            this.name = "New Player";
        }
        this.player = player;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * @return játékos-e
     */
    public boolean isPlayer() {
        return player;
    }

    /**
     * @param player beallitas, hogy ő jatekos-e
     */
    public void setPlayer(boolean player) {
        this.player = player;
    }

    /**
     * beallitasa a speednek
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
     * @return hogy mennyi a karakternek a sebessege
     */
    @Override
    public float getSpeed() {
        return speed;
    }

    /**
     * @param hunger es beallitja az ehseget a megfelelo ertekre
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
     * @return hogy mennyi az ehsege a karakternek
     */
    @Override
    public float getHunger() {
        return hunger;
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

    /**
     * @return hogy mennyi a karakter elete
     */
    @Override
    public float getHp() {
        return healthPoint;
    }

    public void addItem(AbstractItem item) {
        for (AbstractItem currentSlot : inventory) {
            if (currentSlot != null) {
                currentSlot = item;
                currentSlot.setAmount(item.getAmount());
                break;
            }
        }
    }

    /**
     * @return az inventory tartalma
     */
    @Override
    public BaseInventory getInventory() {
        return null;
    }

    /**
     * @param currentPosition beallitja a karakter aktualis poziciojat a kapott ertekre
     */
    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * @return a karakternek az aktualis pozicioja
     */
    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    /**
     * @param lastAction beallitja az ertekre, hogy mi volt az utolso cselekvese
     */
    private void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    /**
     * @return hogy mi volt az utolso cselekvese
     */
    @Override
    public Action getLastAction() {
        return lastAction;
    }

    /**
     * @return mi a karakter neve
     */
    @Override
    public String getName() {
        return name;
    }

}