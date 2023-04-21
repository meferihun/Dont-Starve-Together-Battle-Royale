package prog1.kotprog.dontstarve.solution;

import prog1.kotprog.dontstarve.solution.character.BaseCharacter;
import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.character.actions.Action;
import prog1.kotprog.dontstarve.solution.character.actions.ActionNone;
import prog1.kotprog.dontstarve.solution.character.actions.ActionStep;
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
        Position error = new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);

        // palya mar be lett toltve es a jatek meg nem kezdodott el
        if (isLevelLoaded() && !isGameStarted()) {

            // ha mar van csatlakozott emberi jatekos, masik nem lehet
            if (isPlayerJoinedAlready && player) {
                return error;
            }

            // ha mar van ilyen nevu jatekos, masik ugyanilyen nem lehet
            for (Character current : playersInTheGame) {
                if (current != null) {
                    if (current.getName().equals(name)) {
                        return error;
                    }
                }
            }

            // keresunk egy ures poziciot az uj beleponek, alapbol a vegtelenre allitjuk
            Position newPlayerPosition = error;

            int height = map.length;
            int width = map[0].length;

            // eltaroljuk az osszes palyan levo jatekos koordinatajat
            List<Position> enemyPositions = new ArrayList<>();
            if (!playersInTheGame.isEmpty()) {
                for (Character enemy : playersInTheGame) {
                    if (enemy != null) {
                        enemyPositions.add(enemy.getCurrentPosition().getNearestWholePosition());
                    }
                }
            }

            boolean foundSuitablePosition = false;
            float distance = 50;

            while (distance > 0 && !foundSuitablePosition) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (!map[y][x].isEmpty()) {
                            // ha nem ures a mezo, oda semmikepp nem kerulhet, megyunk a kovetkezo (x;y)-ra
                            continue;
                        }

                        boolean hasEnemyTooClose = false;
                        for (Position p : enemyPositions) {
                            if (p.getDistance(x, y) < distance) {
                                // ha van akar csak egyetlen ellenfel, aki tul kozel van, maris nincs ertelme tovabb ellenorizni
                                // a tobbi jatekost
                                hasEnemyTooClose = true;
                                break;
                            }
                        }
                        if (hasEnemyTooClose) {
                            // es ez esetben ezt az (x;y) koordinatat sem nezzuk tovabb
                            continue;
                        } else {
                            // ha idaig nem leptunk ki, akkor ez a koordinata jo!
                            newPlayerPosition.setX(x);
                            newPlayerPosition.setY(y);
                            foundSuitablePosition = true;
                            break;
                        }
                    }
                    if (foundSuitablePosition) {
                        // nem kell tovabbi koordinatakat nezni
                        break;
                    }
                }
                // ha elfogytak a mezok es meg mindig nem helyeztuk el a jatekost, akkor csokkentjuk a tavolsagot
                // es ujra probaljuk
                distance -= 5;
            }

            // ha idaig nem sikerult meg helyet talalni neki, akkor ezt buktuk
            if (!foundSuitablePosition) {
                return error;
            }

            // vegul az uj beleponek 4 db random targyat sorsolunk az 5 alap fajtabol
            AbstractItem[] items = new AbstractItem[]{
                    new ItemRawCarrot(1),
                    new ItemTwig(1),
                    new ItemRawBerry(1),
                    new ItemLog(1),
                    new ItemStone(1)
            };
            int alreadyAddedAmount = 0;

            Character newCharacter = new Character(name, player);
            while (alreadyAddedAmount < 4) {
                int randomInt = getRandom().nextInt(items.length);
                newCharacter.getInventory().addItem(items[randomInt]);
                alreadyAddedAmount++;
            }

            // ha idaig nem leptunk meg ki, akkor sikerult elhelyezni
            newCharacter.setCurrentPosition(newPlayerPosition);
            playersInTheGame.add(newCharacter);
            if (player) {
                isPlayerJoinedAlready = true;
            }
            return newPlayerPosition;
        }

        return error;
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
                        if (!current.isAlive()) {
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
            if (current.isAlive()) {
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

            // felhasznaloi action eloszor
            for (Character current : playersInTheGame) {
                if (current.isPlayer() && current.isAlive()) {
                    switch (action.getType()) {
                        case STEP -> step(current, ((ActionStep) action).getDirection());
                    }
                    current.setLastAction(action);
                }
            }

            // majd vegigmegyunk mindenkin, az emberi jatekost is beleertve
            for (Character current : playersInTheGame) {
                if (current.isAlive()) {
                    // gepi ellenfelek actionje
                    if (!current.isPlayer()) {
                        if (!isTutorialMode) {
                            // TODO gepi logika, hogy milyen actionoket csinaljon
                            current.setLastAction(new ActionNone());
                        } else {
                            current.setLastAction(new ActionNone());
                        }
                    }
                    // TODO koron beluli ehseg erteket kulon kell tarolni es kezelni? vagy a setHunger()-t atirni, hogy engedjen 100 folottit
                    /*
                     * A jóllakottság egy körön belül mehet 100 fölé, de a kör végével már maximum 100 lehet.
                     * Pl. ha a jóllakottságunk 81 és megeszünk egy nyers bogyót, akkor a jóllakottság ezzel 101 lesz,
                     * viszont a kör végén ez csökken 0.4-del, tehát a kör végén 100 lesz (nem pedig 99.6).
                     */
                    current.setHunger(current.getHunger() - 0.4f);
                    if (current.getHunger() == 0) {
                        current.setHp(current.getHp() - 5);
                    }
                    // az eletero es jollakottsag alapjan valtozni fog a sebessege
                    current.setSpeed();
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
                if (current.isAlive()) {
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
                if (current.isAlive()) {
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

    public Position step(Character character, Direction direction) {
        Position position = character.getCurrentPosition();
        float x = position.getX();
        float y = position.getY();
        int width = map[0].length;
        int height = map.length;
        float speed = character.getSpeed();
        if (direction == Direction.LEFT) {
            if (x - speed >= 0 && map[(int) y][(int) (x - speed)].isWalkable()) {
                position.setX(x - speed);
                return position;
            }
        } else if (direction == Direction.RIGHT) {
            if (x + speed < width && map[(int) y][(int) (x + speed)].isWalkable()) {
                position.setX(x + speed);
                return position;
            }
        } else if (direction == Direction.UP) {
            if (y - speed >= 0 && map[(int) (y - speed)][(int) x].isWalkable()) {
                position.setY(y - speed);
                return position;
            }
        } else if (direction == Direction.DOWN) {
            if (y + speed < height && map[(int) (y + speed)][(int) x].isWalkable()) {
                position.setY(y + speed);
                return position;
            }
        }
        return position;
    }
}