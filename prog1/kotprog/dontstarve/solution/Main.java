package prog1.kotprog.dontstarve.solution;

import prog1.kotprog.dontstarve.solution.character.BaseCharacter;
import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.character.actions.ActionNone;
import prog1.kotprog.dontstarve.solution.inventory.Inventory;
import prog1.kotprog.dontstarve.solution.inventory.items.*;
import prog1.kotprog.dontstarve.solution.level.Level;
import prog1.kotprog.dontstarve.solution.utility.Direction;
import prog1.kotprog.dontstarve.solution.utility.Position;
import prog1.kotprog.dontstarve.solution.GameManager;

import java.net.URL;

public class Main {

    public static void main(String[] args) {

        /*


        Position p1 = game.joinCharacter("Elso", true);
        System.out.printf("palya betoltes elott: %.2f %.2f\n", p1.getX(), p1.getY());

        URL filename = Main.class.getResource("level00.png");
        Level palya = new Level(filename.getPath());
        game.loadLevel(palya);

        System.out.println("palya betoltes utan:");

        Position p2 = game.joinCharacter("Masodik", false);
        System.out.printf("gep: %.2f %.2f\n", p2.getX(), p2.getY());

        Position p3 = game.joinCharacter("Harmadik", true);
        System.out.printf("utana ember: %.2f %.2f\n", p3.getX(), p3.getY());

        Position p4 = game.joinCharacter("Masodik", false);
        System.out.printf("utana 2-es gep ujra: %.2f %.2f\n", p4.getX(), p4.getY());

        Position p5 = game.joinCharacter("Ujabb ember", true);
        System.out.printf("ujabb ember: %.2f %.2f\n", p5.getX(), p5.getY());

        for (Integer i = 10; i < 400; i++) {
            Position p = game.joinCharacter(i.toString(), false);
            System.out.printf("Player %d: %.2f %.2f\n", i, p.getX(), p.getY());
        }
*/


        GameManager game = GameManager.getInstance();
        Inventory inventory = new Inventory();
        URL filename = Main.class.getResource("level00.png");
        Level palya = new Level(filename.getPath());
        game.loadLevel(palya);
        inventory.addItem(new ItemRawCarrot(2));
        Position p1 = game.joinCharacter("Elso", false);
        BaseCharacter player1 = game.getCharacter("Elso");
        Position p2 = game.joinCharacter("Masodik", true);
        BaseCharacter player2 = game.getCharacter("Masodik");


    }

    private static void InventoryPrint(BaseCharacter player2) {
        for (int i = 0; i < 10; i++) {
            if (player2.getInventory().getItem(i) != null) {
                System.out.print(player2.getInventory().getItem(i).getType() + " " + player2.getInventory().getItem(i).getAmount() + ", ");
            }
            if (i == 9) {
                System.out.println();
            }
        }
    }


}