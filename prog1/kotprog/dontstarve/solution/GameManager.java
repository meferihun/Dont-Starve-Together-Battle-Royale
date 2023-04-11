package prog1.kotprog.dontstarve.solution;

import prog1.kotprog.dontstarve.solution.character.BaseCharacter;
import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.character.actions.ActionStep;
import prog1.kotprog.dontstarve.solution.character.actions.ActionType;
import prog1.kotprog.dontstarve.solution.exceptions.NotImplementedException;
import prog1.kotprog.dontstarve.solution.inventory.items.*;
import prog1.kotprog.dontstarve.solution.level.BaseField;
import prog1.kotprog.dontstarve.solution.level.Field;
import prog1.kotprog.dontstarve.solution.level.Level;
import prog1.kotprog.dontstarve.solution.utility.Direction;
import prog1.kotprog.dontstarve.solution.utility.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A játék működéséért felelős osztály.<br>
 * Az osztály a singleton tervezési mintát valósítja meg.
 */
public final class GameManager {
    /**
     * Az osztályból létrehozott egyetlen példány (nem lehet final).
     */
    private static GameManager instance = new GameManager();
    /**
     * Random objektum, amit a játék során használni lehet.
     */
    private final Random random = new Random();
    private List<Character> playersInTheGame;
    private Level level;
    private boolean gameStarted;

    /**
     * Az osztály privát konstruktora.
     */
    private GameManager() {
        this.playersInTheGame = new ArrayList<>();
        this.level = null;
        this.gameStarted = false;
    }

    /**
     * Az osztályból létrehozott példány elérésére szolgáló metódus.
     *
     * @return az osztályból létrehozott példányfinal
     */
    public static GameManager getInstance() {
        return instance;
    }

    /**
     * A létrehozott random objektum elérésére szolgáló metódus.
     *
     * @return a létrehozott random objektum
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Egy karakter becsatlakozása a játékba.<br>
     * A becsatlakozásnak számos feltétele van:
     * <ul>
     *     <li>A pálya már be lett töltve</li>
     *     <li>A játék még nem kezdődött el</li>
     *     <li>Csak egyetlen emberi játékos lehet, a többi karaktert a gép irányítja</li>
     *     <li>A névnek egyedinek kell lennie</li>
     * </ul>
     *
     * @param name   a csatlakozni kívánt karakter neve
     * @param player igaz, ha emberi játékosról van szó; hamis egyébként
     * @return a karakter pozíciója a pályán, vagy (Integer.MAX_VALUE, Integer.MAX_VALUE) ha nem sikerült hozzáadni
     */
    public Position joinCharacter(String name, boolean player) {
        boolean isPlayerJoinedAlready = false;
        int humans = 0;
        if (playersInTheGame != null && name != null && !name.equals("")) {
            for (Character current : playersInTheGame) {
                if (current != null) {
                    if (current.isPlayer()) {
                        humans++;
                    }
                    if (current.getName().equals(name)) {
                        return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    }
                }
            }
        }
        if (humans > 1 || (humans == 1 && player)) {
            return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
        } else if (humans == 1) {
            isPlayerJoinedAlready = true;
        }

        AbstractItem[] items = new AbstractItem[]{new ItemRawCarrot(0), new ItemTwig(0), new ItemRawBerry(0), new ItemLog(0), new ItemStone(0)};

        if (isLevelLoaded() && !isGameStarted()) {
            if ((isPlayerJoinedAlready && !player) || (!isPlayerJoinedAlready && player)) {
                Position newPlayerPosition = new Position(0, 0);
                Character newCharacter = new Character("01", player);
                if (name != null && !name.equals("")) {
                    newCharacter = new Character(name, player);
                }
                for (int i = 0; i < 4; i++) {
                    int randomInt = getRandom().nextInt(items.length);
                    AbstractItem newItem;
                    if (items[randomInt] != null) {
                        newItem = items[randomInt];
                    } else {
                        i--;
                        continue;
                    }
                    items[randomInt] = null;
                    newItem.setAmount(getRandom().nextInt(newItem.getType().getMaxStackAmount() - 1) + 1);
                    newCharacter.addItem(newItem);
                }
                playersInTheGame.add(newCharacter);
                boolean[][] mapSlots = new boolean[level.getWidth()][level.getHeight()];
                for (int x = 0; x < level.getWidth(); x++) {
                    for (int y = 0; y < level.getHeight(); y++) {
                        Field current = new Field(level.getColor(x, y));
                        if (current.hasBerry() || current.hasCarrot() || current.hasFire() || current.hasTwig()) {
                            if (current.hasTree() || current.hasStone() || current.isWalkable()) {
                                mapSlots[x][y] = true;
                            } else {
                                mapSlots[x][y] = false;
                            }
                        } else {
                            mapSlots[x][y] = false;
                        }
                    }
                }

                boolean[][] mapAvailableSlots = mapSlots.clone();
                float distance = 50;
                int blockedFields = 0;
                int index = 0;
                for (int x = 0; x < level.getWidth(); x++) {
                    for (int y = 0; y < level.getHeight(); y++) {
                        if (mapAvailableSlots[x][y]) {
                            mapAvailableSlots[x][y] = false;
                            playersInTheGame.get(index).setCurrentPosition(new Position(x, y));
                            if (index == playersInTheGame.size() - 1) {
                                newPlayerPosition = playersInTheGame.get(index).getCurrentPosition();
                            }
                            index++;
                            if (x + distance < level.getWidth() && y + distance < level.getHeight()) {
                                for (int z = 0; z < distance; z++) {
                                    if (x + z < level.getWidth() && y + z < level.getHeight()) {
                                        mapAvailableSlots[x + z][y + z] = false;
                                        blockedFields++;
                                    }
                                }
                            } else if (x - distance >= 0 && y - distance >= 0) {
                                for (int k = 0; k < distance; k++) {
                                    if (x - k >= 0 && y + k >= 0) {
                                        mapAvailableSlots[x - k][y - k] = false;
                                        blockedFields++;
                                    }
                                }
                            }
                        }


                        if (blockedFields == level.getHeight() * level.getWidth()) {
                            distance -= 5;
                            x = 0;
                            y = 0;
                            if (distance < 5) {
                                break;
                            }
                        }
                    }
                }
                return newPlayerPosition;
            }
        }
        return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Egy adott nevű karakter lekérésére szolgáló metódus.<br>
     *
     * @param name A lekérdezni kívánt karakter neve
     * @return Az adott nevű karakter objektum, vagy null, ha már a karakter meghalt vagy nem is létezett
     */
    public BaseCharacter getCharacter(String name) {
        for (Character current : playersInTheGame) {
            if (current != null) {
                if (current.getName().equals(name)) {
                    if (current.getHp() == 0) {
                        return null;
                    }
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Ezen metódus segítségével lekérdezhető, hogy hány karakter van még életben.
     *
     * @return Az életben lévő karakterek száma
     */
    public int remainingCharacters() {
        int aliveOnes = 0;
        for (Character current : playersInTheGame) {
            if (current.getHp() > 0) {
                aliveOnes++;
            }
        }
        return aliveOnes;
    }

    /**
     * Ezen metódus segítségével történhet meg a pálya betöltése.<br>
     * A pálya betöltésének azelőtt kell megtörténnie, hogy akár 1 karakter is csatlakozott volna a játékhoz.<br>
     * A pálya egyetlen alkalommal tölthető be, később nem módosítható.
     *
     * @param level a fájlból betöltött pálya
     */
    public void loadLevel(Level level) {
        if (this.level == null) {
            this.level = level;
            final int[][] map = new int[level.getWidth()][level.getHeight()];
            for (int y = 0; y < level.getHeight(); y++) {
                for (int x = 0; x < level.getWidth(); x++) {
                    map[x][y] = level.getColor(x, y);
                    System.out.print(map[x][y]);
                }
                System.out.println();
            }
        }
    }

    public boolean isLevelLoaded() {
        return this.level != null;
    }

    /**
     * A pálya egy adott pozícióján lévő mező lekérdezésére szolgáló metódus.
     *
     * @param x a vízszintes (x) irányú koordináta
     * @param y a függőleges (y) irányú koordináta
     * @return az adott koordinátán lévő mező
     */
    public BaseField getField(int x, int y) {
        if (x >= 0 && x < level.getWidth() && y >= 0 && y < level.getHeight()) {
            return new Field(level.getColor(x, y));
        }
        return null;
    }

    /**
     * A játék megkezdésére szolgáló metódus.<br>
     * A játék csak akkor kezdhető el, ha legalább 2 karakter már a pályán van,
     * és közülük pontosan az egyik az emberi játékos által irányított karakter.
     *
     * @return igaz, ha sikerült elkezdeni a játékot; hamis egyébként
     */
    public boolean startGame() {
        if (!isLevelLoaded() || playersInTheGame.size() < 2) {
            return false;
        }
        if (gameStarted) {
            return false;
        }
        for (Character current : playersInTheGame) {
            if (current.isPlayer()) {
                gameStarted = true;
                return true;
            }
        }

        return false;
    }

    /**
     * Ez a metódus jelzi, hogy 1 időegység eltelt.<br>
     * A metódus először lekezeli a felhasználói inputot, majd a gépi ellenfelek cselekvését végzi el,
     * végül eltelik egy időegység.<br>
     * Csak akkor csinál bármit is, ha a játék már elkezdődött, de még nem fejeződött be.
     *
     * @param action az emberi játékos által végrehajtani kívánt akció
     */
    public void tick(Action action) {
        throw new NotImplementedException();
    }

    /**
     * Ezen metódus segítségével lekérdezhető az aktuális időpillanat.<br>
     * A játék kezdetekor ez az érték 0 (tehát a legelső időpillanatban az idő 0),
     * majd minden eltelt időpillanat után 1-gyel növelődik.
     *
     * @return az aktuális időpillanat
     */
    public int time() {
        throw new NotImplementedException();
    }

    /**
     * Ezen metódus segítségével lekérdezhetjük a játék győztesét.<br>
     * Amennyiben a játéknak még nincs vége (vagy esetleg nincs győztes), akkor null-t ad vissza.
     *
     * @return a győztes karakter vagy null
     */
    public BaseCharacter getWinner() {
        throw new NotImplementedException();
    }

    /**
     * Ezen metódus segítségével lekérdezhetjük, hogy a játék elkezdődött-e már.
     *
     * @return igaz, ha a játék már elkezdődött; hamis egyébként
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Ezen metódus segítségével lekérdezhetjük, hogy a játék befejeződött-e már.
     *
     * @return igaz, ha a játék már befejeződött; hamis egyébként
     */
    public boolean isGameEnded() {
        throw new NotImplementedException();
    }

    /**
     * Ezen metódus segítségével beállítható, hogy a játékot tutorial módban szeretnénk-e elindítani.<br>
     * Alapértelmezetten (ha nem mondunk semmit) nem tutorial módban indul el a játék.<br>
     * Tutorial módban a gépi karakterek nem végeznek cselekvést, csak egy helyben állnak.<br>
     * A tutorial mód beállítása még a karakterek csatlakozása előtt történik.
     *
     * @param tutorial igaz, amennyiben tutorial módot szeretnénk; hamis egyébként
     */
    public void setTutorial(boolean tutorial) {
        throw new NotImplementedException();
    }

}
