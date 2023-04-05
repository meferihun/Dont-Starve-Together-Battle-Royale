package prog1.kotprog.dontstarve.solution.character;

import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.inventory.BaseInventory;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.EquippableItem;
import prog1.kotprog.dontstarve.solution.utility.Position;

import java.nio.file.attribute.AttributeView;

public class Character implements BaseCharacter {

    private EquippableItem equippedItem;

    private Action lastAction;

    private String name;
    private float healthPoint;
    private float hunger;
    private float speed = 1;

    public Character() {
    }

    public EquippableItem getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(EquippableItem equippedItem) {
        this.equippedItem = equippedItem;
    }

    public void setSpeed() {
        this.speed = 1;
        if (getHp() > 50 && getHp() <= 100) {
            this.speed *= 1;
        } else if (getHp() > 30 && getHp() <= 50) {
            this.speed *= 0.9f;
        } else if (getHp() > 10 && getHp() <= 30) {
            this.speed *= 0.75f;
        } else if (getHp() >= 0 && getHp() <= 10) {
            this.speed *= 0.6f;
        }

        if (getHunger() > 50 && getHunger() <= 100) {
            this.speed *= 1;
        } else if (getHunger() > 20 && getHunger() <= 50) {
            this.speed *= 0.9f;
        } else if (getHunger() > 0 && getHunger() <= 20) {
            this.speed *= 0.8f;
        } else if (getHunger() == 0) {
            this.speed *= 0.5;
        }
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public void setHunger(float hunger) {
        if (hunger >= 100) {
            this.hunger = 100;
        } else if (hunger < 100 && hunger > 0) {
            this.hunger = hunger;
        } else {
            this.hunger = 0;
        }
    }

    @Override
    public float getHunger() {
        return hunger;
    }

    public void setHp(float hp) {
        if (hp >= 100) {
            this.healthPoint = 100;
        } else if (hp < 100 && hp > 0) {
            this.healthPoint = hp;
        } else {
            this.healthPoint = 0;
        }
    }

    @Override
    public float getHp() {
        return healthPoint;
    }

    @Override
    public BaseInventory getInventory() {
        return null;
    }

    @Override
    public Position getCurrentPosition() {
        return null;
    }

    private void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    @Override
    public Action getLastAction() {
        return lastAction;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
