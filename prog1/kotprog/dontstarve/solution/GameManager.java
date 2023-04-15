package prog1.kotprog.dontstarve.solution;

import prog1.kotprog.dontstarve.solution.character.BaseCharacter;
import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.character.actions.ActionNone;
import prog1.kotprog.dontstarve.solution.exceptions.NotImplementedException;
import prog1.kotprog.dontstarve.solution.inventory.items.*;
import prog1.kotprog.dontstarve.solution.level.BaseField;
import prog1.kotprog.dontstarve.solution.level.Field;
import prog1.kotprog.dontstarve.solution.level.Level;
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
    private Field[][] map;
    private boolean gameStarted;
    private int currentTime;

    private boolean isTutorialMode;
    private boolean isPlayerJoinedAlready;

    /**
     * Az osztály privát konstruktora.
     */
    private GameManager() {
        this.playersInTheGame = new ArrayList<>();
        this.gameStarted = false;
        this.map = null;
        this.currentTime = 0;
        this.isTutorialMode = false;
        this.isPlayerJoinedAlready = false;
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
        for (Character current : playersInTheGame) {
            if (current != null) {
                if (current.getName().equals(name)) {
                    return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
                }
            }
        }

        if (isPlayerJoinedAlready && player) {
            return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        if (!isPlayerJoinedAlready && player) {
            isPlayerJoinedAlready = true;
        }

        AbstractItem[] items = new AbstractItem[]{new ItemRawCarrot(0), new ItemTwig(0), new ItemRawBerry(0), new ItemLog(0), new ItemStone(0)};

        if (isLevelLoaded() && !isGameStarted()) {
            Position newPlayerPosition = new Position(0, 0);
            Character newCharacter = new Character(name, player);
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
            isPlayerJoinedAlready = true;
            int height = map.length;
            int width = map[0].length;
            boolean[][] mapSlots = new boolean[width][height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Field current = map[y][x];
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
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (mapAvailableSlots[y][x]) {
                        mapAvailableSlots[y][x] = false;
                        playersInTheGame.get(index).setCurrentPosition(new Position(x, y));
                        if (index == playersInTheGame.size() - 1) {
                            newPlayerPosition = playersInTheGame.get(index).getCurrentPosition();
                        }
                        index++;
                        if (x + distance < width && y + distance < height) {
                            for (int z = 0; z < distance; z++) {
                                if (x + z < width && y + z < height) {
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


                    if (blockedFields == height * width) {
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
        return new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Egy adott nevű karakter lekérésére szolgáló metódus.<br>
     *
     * @param name A lekérdezni kívánt karakter neve
     * @return Az adott nevű karakter objektum, vagy null, ha már a karakter meghalt vagy nem is létezett
     */
    public BaseCharacter getCharacter(String name) {
        if (name != null && !name.equals("")) {
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
        if (playersInTheGame.isEmpty() && !isLevelLoaded()) {
            Field[][] loadedMap = new Field[level.getHeight()][level.getWidth()];
            for (int y = 0; y < level.getHeight(); y++) {
                for (int x = 0; x < level.getWidth(); x++) {
                    loadedMap[y][x] = new Field(level.getColor(x, y));
                }
            }
            this.map = loadedMap;
        }
    }


    public boolean isLevelLoaded() {
        return this.map != null;
    }

    /**
     * A pálya egy adott pozícióján lévő mező lekérdezésére szolgáló metódus.
     *
     * @param x a vízszintes (x) irányú koordináta
     * @param y a függőleges (y) irányú koordináta
     * @return az adott koordinátán lévő mező
     */
    public BaseField getField(int x, int y) {
        int height = map.length;
        int width = map[0].length;
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return map[y][x];
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
        if (isLevelLoaded() && isPlayerJoinedAlready && playersInTheGame.size() >= 2 && !gameStarted) {
            gameStarted = true;
            return true;
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
        if (isGameStarted() && !isGameEnded()) {
            for (Character current : playersInTheGame) {
                if (!current.isPlayer() && current.getHp() > 0) {
                    if (!isTutorialMode) {
                        current.setAction(action);
                    } else {
                        current.setAction(new ActionNone());
                    }
                }
                current.setHunger(current.getHunger() - 0.4f);
                if (current.getHunger() == 0) {
                    current.setHp(current.getHp() - 5);
                }
            }
            currentTime++;
        }
    }

    /**
     * Ezen metódus segítségével lekérdezhető az aktuális időpillanat.<br>
     * A játék kezdetekor ez az érték 0 (tehát a legelső időpillanatban az idő 0),
     * majd minden eltelt időpillanat után 1-gyel növelődik.
     *
     * @return az aktuális időpillanat
     */
    public int time() {
        return currentTime;
    }

    /**
     * Ezen metódus segítségével lekérdezhetjük a játék győztesét.<br>
     * Amennyiben a játéknak még nincs vége (vagy esetleg nincs győztes), akkor null-t ad vissza.
     *
     * @return a győztes karakter vagy null
     */
    public BaseCharacter getWinner() {
        if (isGameEnded() && !playersInTheGame.isEmpty()) {
            for (Character current : playersInTheGame) {
                if (current.getHp() > 0) {
                    return current;
                }
            }
        }
        return null;
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
        int aliveOnes = 0;
        boolean isPlayerAlive = false;
        if (isGameStarted()) {
            for (Character current : playersInTheGame) {
                if (current.getHp() > 0) {
                    aliveOnes++;
                    if (current.isPlayer()) {
                        isPlayerAlive = true;
                    }
                }
            }
            if (aliveOnes == 1 && isPlayerAlive) {
                return true;
            }
            return false;
        }
        return false;
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
        if (!isGameStarted()) {
            this.isTutorialMode = tutorial;
        }
    }

}
