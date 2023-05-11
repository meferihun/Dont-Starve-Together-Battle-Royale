package prog1.kotprog.dontstarve.solution.character.actions;

import prog1.kotprog.dontstarve.solution.GameManager;
import prog1.kotprog.dontstarve.solution.character.Character;


public class ActionChooser {
    private Character character;
    private Action action;
    private GameManager game = GameManager.getInstance();

    public ActionChooser(Character character, Action action) {
        this.character = character;
        this.action = action;
    }

    public void action() {
        switch (action.getType()) {
            case STEP -> game.step(character, ((ActionStep) action).getDirection());
            case INTERACT -> game.interact(character);
            case EAT -> game.eat(character, ((ActionEat) action).getIndex());
            case COOK -> game.cook(character, ((ActionCook) action).getIndex());
            case CRAFT -> game.craft(character, ((ActionCraft) action).getItemType());
            case DROP_ITEM -> game.drop(character, ((ActionDropItem) action).getIndex());
            case MOVE_ITEM ->
                    game.move(character, ((ActionMoveItem) action).getOldIndex(), ((ActionMoveItem) action).getNewIndex());
            case SWAP_ITEMS ->
                    game.swap(character, ((ActionSwapItems) action).getIndex1(), ((ActionSwapItems) action).getIndex2());
            case COMBINE_ITEMS ->
                    game.combine(character, ((ActionCombineItems) action).getIndex1(), ((ActionCombineItems) action).getIndex2());
            case COLLECT_ITEM -> game.collect(character);
            case EQUIP -> game.equip(character, ((ActionEquip) action).getIndex());
            case UNEQUIP -> game.unequip(character);
            case ATTACK -> game.attack(character);
            case STEP_AND_ATTACK -> {
                game.step(character, ((ActionStep) action).getDirection());
                game.attack(character);
            }
        }
    }
}
