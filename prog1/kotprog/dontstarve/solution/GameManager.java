package prog1.kotprog.dontstarve.solution;

import prog1.kotprog.dontstarve.solution.character.BaseCharacter;
import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.character.actions.*;
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
    private final List<Character> playersInTheGame;
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
            AbstractItem[] items = new AbstractItem[]{new ItemRawCarrot(1), new ItemTwig(1), new ItemRawBerry(1), new ItemLog(1), new ItemStone(1)};
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
            Position playerPosition = null;
            List<Character> aliveNPCs = new ArrayList<>();
            // felhasznaloi action eloszor
            for (Character current : playersInTheGame) {
                if (current.isPlayer() && current.isAlive()) {
                    playerPosition = current.getCurrentPosition();
                    ActionChooser chooser = new ActionChooser(current, action);
                    chooser.action();
                    current.setLastAction(action);
                }
                if (current.isAlive() && !current.isPlayer()) {
                    aliveNPCs.add(current);
                }
            }

            // majd vegigmegyunk mindenkin, az emberi jatekost is beleertve
            for (Character current : aliveNPCs) {
                // gepi ellenfelek actionje
                if (!isTutorialMode) {
                    float NPCx = current.getCurrentPosition().getX();
                    float NPCy = current.getCurrentPosition().getY();

                    float playerX = playerPosition.getX();
                    float playerY = playerPosition.getY();

                    float xDiff = playerX - NPCx;
                    float yDiff = playerY - NPCy;

                    int width = map[0].length;
                    int height = map.length;

                    if (Math.abs(xDiff) > Math.abs(yDiff)) {
                        if (xDiff > 0) {
                            if (xDiff + current.getSpeed() < width) {
                                if (step(current, Direction.RIGHT)) {
                                    attack(current);
                                    current.setLastAction(new ActionStepAndAttack(Direction.RIGHT));
                                }
                            }
                        } else if (xDiff < 0) {
                            if (xDiff - current.getSpeed() >= 0) {
                                if (step(current, Direction.LEFT)) {
                                    attack(current);
                                    current.setLastAction(new ActionStepAndAttack(Direction.LEFT));
                                }
                            }
                        }
                    } else {
                        if (yDiff - current.getSpeed() >= 0) {
                            if (yDiff > 0) {
                                if (step(current, Direction.UP)) {
                                    attack(current);
                                    current.setLastAction(new ActionStepAndAttack(Direction.UP));
                                }
                            }
                        } else if (yDiff < 0) {
                            if (yDiff + current.getSpeed() < height) {
                                if (step(current, Direction.DOWN)) {
                                    attack(current);
                                    current.setLastAction(new ActionStepAndAttack(Direction.DOWN));
                                }
                            }
                        }
                    }


                } else {
                    current.setLastAction(new ActionNone());
                }
            }
            for (Character current : playersInTheGame) {
                if (current.isAlive()) {
                    if (current.getHunger() > 100) {
                        current.setHunger(100);
                    } else {
                        current.setHunger(current.getHunger() - 0.4f);
                    }
                    if (current.getHunger() == 0) {
                        current.setHp(current.getHp() - 5);
                    }
                    current.setSpeed();
                    if (current.getInventory().equippedItem() != null && current.getInventory().equippedItem().getType().equals(ItemType.TORCH) && current.getInventory().equippedItem().percentage() > 0) {
                        current.getInventory().equippedItem().setPercentage(current.getInventory().equippedItem().percentage() - 5);
                        if (current.getInventory().equippedItem().percentage() == 0) {
                            current.getInventory().itemBreak();
                        }
                    }
                }
            }
        }
        for (int x = 0; x < map[0].length; x++) {
            for (Field[] fields : map) {
                if (fields[x].hasFire()) {
                    fields[x].placedFire().setTick(fields[x].placedFire().getTick() + 1);
                    if (fields[x].placedFire().getTick() > 60) {
                        fields[x].removeItem(fields[x].items()[fields[x].items().length - 1]);
                    }
                }
            }
        }
        currentTime++;
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
            if (!isPlayerAlive) {
                return true;
            } else return isPlayerAlive && aliveOnes == 1;
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

    /**
     * a karakter mozgása
     *
     * @param character az adott karakter
     * @param direction lép a megadott irányba, ha tud
     */
    public boolean step(Character character, Direction direction) {
        Position position = character.getCurrentPosition();
        float x = position.getX();
        float y = position.getY();
        int width = map[0].length;
        int height = map.length;
        float speed = character.getSpeed();

        switch (direction) {
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
            case UP -> y -= speed;
            case DOWN -> y += speed;
        }

        if (x >= 0 && x < width && y >= 0 && y < height && map[Math.round(y)][Math.round(x)].isWalkable()) {
            position.setX(x);
            position.setY(y);
            return true;
        }
        return false;
    }

    /**
     * a karakter interakcioja a palyaval
     *
     * @param character ha az aktualis mezon van amit be tud gyujteni akkor azt megteszi
     */
    public void interact(Character character) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();
        float width = map[0].length;
        float height = map.length;

        if (x < width && x >= 0 && y < height && y >= 0) {
            Field field = map[(int) y][(int) x];

            if (field.hasBerry()) {
                character.getInventory().addItem(new ItemRawBerry(1));
                map[(int) y][(int) x].setColor(0xFF32C832);

            } else if (field.hasCarrot()) {
                character.getInventory().addItem(new ItemRawCarrot(1));
                map[(int) y][(int) x].setColor(0xFF32C832);

            } else if (field.hasTwig()) {
                field.setExtractionProgress(field.getExtractionProgress() + 0.5f);
                if (field.getExtractionProgress() == 1) {
                    character.getInventory().addItem(new ItemTwig(1));
                    map[(int) y][(int) x].setColor(0xFF32C832);
                }

            } else if (field.hasStone()) {
                if (character.getInventory().equippedItem() != null && character.getInventory().equippedItem().getType().equals(ItemType.PICKAXE)) {
                    if (field.getExtractionProgress() < 1) {
                        if (character.getInventory().equippedItem().percentage() > 0) {
                            field.setExtractionProgress(field.getExtractionProgress() + 0.2f);
                            character.getInventory().equippedItem().setPercentage(character.getInventory().equippedItem().percentage() - 3.34f);
                            if (character.getInventory().equippedItem().percentage() == 0) {
                                character.getInventory().itemBreak();
                            }
                            if (field.getExtractionProgress() == 1) {
                                field.placeItem(new ItemStone(3));
                                map[(int) y][(int) x].setColor(0xFF32C832);
                            }
                        }
                    }
                }

            } else if (field.hasTree()) {
                if (character.getInventory().equippedItem() != null && character.getInventory().equippedItem().getType().equals(ItemType.AXE)) {
                    if (field.getExtractionProgress() < 1) {
                        if (character.getInventory().equippedItem().percentage() > 0) {
                            field.setExtractionProgress(field.getExtractionProgress() + 0.25f);
                            character.getInventory().equippedItem().setPercentage(character.getInventory().equippedItem().percentage() - 2.5f);
                            if (character.getInventory().equippedItem().percentage() == 0) {
                                character.getInventory().itemBreak();
                            }
                            if (field.getExtractionProgress() == 1) {
                                field.placeItem(new ItemLog(2));
                                map[(int) y][(int) x].setColor(0xFF32C832);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * a karakter etkezese
     *
     * @param character
     * @param index     eszik az adott inventory slotrol ha az eheto
     */
    public void eat(Character character, int index) {
        if (index < 10 && index >= 0 && character.getInventory().getItem(index).getType().isEdible() && character.getHunger() < 100) {
            ItemType itemToEat = character.getInventory().eatItem(index);
            switch (itemToEat) {
                case RAW_BERRY, COOKED_CARROT, RAW_CARROT, COOKED_BERRY -> {
                    character.setHunger(character.getHunger() + itemToEat.getHungerModifier());
                    character.setHp(character.getHp() + itemToEat.getHealthModifier());
                }
            }
        }
    }

    /**
     * a karakter fozese
     *
     * @param character
     * @param index     m karakter megfozi az inventory adott elemet ha az fozhato es tabortuzon all a karakter
     */
    public void cook(Character character, int index) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();
        float width = map[0].length;
        float height = map.length;
        if (x < width && x >= 0 && y < height && y >= 0) {
            if (map[(int) y][(int) x].hasFire() && map[(int) y][(int) x].placedFire().getTick() < 60) {
                if (index >= 0 && index < 10) {
                    if (character.getInventory().getItem(index).getType().isCookable()) {
                        ItemType cookable = character.getInventory().getItem(index).getType();
                        character.getInventory().cookItem(index);
                        switch (cookable) {
                            case RAW_BERRY -> {
                                character.getInventory().addItem(new ItemCookedBerry(1));
                            }
                            case RAW_CARROT -> {
                                character.getInventory().addItem(new ItemCookedCarrot(1));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * a karakter barkacsolasa
     *
     * @param character
     * @param craftableItem a karakter lekraftolja az adott itemet havvan ra eleg nyersanyagad
     */
    public void craft(Character character, ItemType craftableItem) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();
        int width = map[0].length;
        int height = map.length;

        if (x < width && x >= 0 && y < height && y >= 0) {
            switch (craftableItem) {

                case AXE -> {
                    if (character.getInventory().hasItem(ItemType.TWIG, 3)) {
                        character.getInventory().removeItem(ItemType.TWIG, 3);
                        if (character.getInventory().emptySlots() > 0) {
                            character.getInventory().addItem(new ItemAxe());
                        } else {
                            map[(int) y][(int) x].placeItem(new ItemAxe());
                        }
                    }
                }

                case PICKAXE -> {
                    if (character.getInventory().hasItem(ItemType.TWIG, 2) && character.getInventory().hasItem(ItemType.LOG, 2)) {
                        character.getInventory().removeItem(ItemType.TWIG, 2);
                        character.getInventory().removeItem(ItemType.LOG, 2);
                        if (character.getInventory().emptySlots() > 0) {
                            character.getInventory().addItem(new ItemPickaxe());
                        } else {
                            map[(int) y][(int) x].placeItem(new ItemPickaxe());
                        }
                    }
                }

                case SPEAR -> {
                    if (character.getInventory().hasItem(ItemType.LOG, 2) && character.getInventory().hasItem(ItemType.STONE, 2)) {
                        character.getInventory().removeItem(ItemType.LOG, 2);
                        character.getInventory().removeItem(ItemType.STONE, 2);
                        if (character.getInventory().emptySlots() > 0) {
                            character.getInventory().addItem(new ItemSpear());
                        } else {
                            map[(int) y][(int) x].placeItem(new ItemSpear());
                        }
                    }
                }

                case TORCH -> {
                    if (character.getInventory().hasItem(ItemType.TWIG, 3) && character.getInventory().hasItem(ItemType.LOG, 1)) {
                        character.getInventory().removeItem(ItemType.TWIG, 3);
                        character.getInventory().removeItem(ItemType.LOG, 1);
                        if (character.getInventory().emptySlots() > 0) {
                            character.getInventory().addItem(new ItemTorch());
                        } else {
                            map[(int) y][(int) x].placeItem(new ItemTorch());
                        }
                    }
                }

                case FIRE -> {
                    if (map[(int) y][(int) x].isEmpty()) {
                        if (!map[(int) y][(int) x].hasFire()) {
                            if (character.getInventory().hasItem(ItemType.TWIG, 2) && character.getInventory().hasItem(ItemType.LOG, 2) && character.getInventory().hasItem(ItemType.STONE, 4)) {
                                character.getInventory().removeItem(ItemType.TWIG, 2);
                                character.getInventory().removeItem(ItemType.LOG, 2);
                                character.getInventory().removeItem(ItemType.STONE, 4);
                                map[(int) y][(int) x].placeItem(new ItemFire());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * a karakter item eldobasa az adott mezore
     *
     * @param character
     * @param index     az adott inventory slot tartalmat dobja ki a mezore
     */
    public void drop(Character character, int index) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();
        if (index < 10 && index >= 0 && character.getInventory().getItem(index) != null) {
            AbstractItem droppableItem = character.getInventory().getItem(index);
            character.getInventory().dropItem(index);
            map[(int) y][(int) x].placeItem(droppableItem);
        }
    }

    /**
     * a karakter itemek mozgatasa a 2 megadott pozicio kozott
     *
     * @param character
     * @param index     az index ahonnan mozgatunk
     * @param newIndex  az index ahova mozgatunk
     */
    public void move(Character character, int index, int newIndex) {
        if (index >= 0 && index < 10 && newIndex >= 0 && newIndex < 10 && character.getInventory().getItem(index) != null && character.getInventory().getItem(newIndex) == null) {
            character.getInventory().moveItem(index, newIndex);
        }
    }

    /**
     * az inventoryban 2 item megcserelese
     *
     * @param character
     * @param index1
     * @param index2
     */
    public void swap(Character character, int index1, int index2) {
        if (index1 >= 0 && index1 < 10 && index2 >= 0 && index2 < 10 && character.getInventory().getItem(index1) != null && character.getInventory().getItem(index2) != null) {
            character.getInventory().swapItems(index1, index2);
        }
    }

    /**
     * 2 stackelheto item osszeorakasa
     *
     * @param character
     * @param index1
     * @param index2
     */
    public void combine(Character character, int index1, int index2) {
        character.getInventory().combineItems(index1, index2);
    }

    /**
     * a karakter felszed egy eldobott itemet a foldrol
     *
     * @param character
     */
    public void collect(Character character) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();
        float width = map[0].length;
        float height = map.length;

        if (x < width && x >= 0 && y < height && y >= 0) {
            AbstractItem[] items = map[(int) y][(int) x].items();
            if (items != null && items.length > 0) {
                AbstractItem pickable = items[0];
                if (pickable != null && !pickable.getType().equals(ItemType.FIRE)) {
                    if (character.getInventory().addItem(pickable)) {
                        map[(int) y][(int) x].pickUpItem();
                    }
                }
            }
        }
    }

    /**
     * a karakter berakja a kezebe az eszkozt
     *
     * @param character
     * @param index
     */
    public void equip(Character character, int index) {
        if (index >= 0 && index < 10 && character.getInventory().getItem(index) != null && character.getInventory().getItem(index).getType().isEquippable()) {
            character.getInventory().equipItem(index);
        }
    }

    /**
     * a karakter kiveszi a kezebol az eszkozt
     *
     * @param character
     */
    public void unequip(Character character) {
        if (character.getInventory().equippedItem() != null) {
            float x = character.getCurrentPosition().getNearestWholePosition().getX();
            float y = character.getCurrentPosition().getNearestWholePosition().getY();

            if (character.getInventory().emptySlots() == 0) {
                AbstractItem equipped = character.getInventory().equippedItem();
                map[(int) y][(int) x].placeItem(equipped);
            }
            character.getInventory().unequipItem();
        }
    }

    /**
     * a karakter tamad
     *
     * @param character
     */
    public void attack(Character character) {
        float x = character.getCurrentPosition().getNearestWholePosition().getX();
        float y = character.getCurrentPosition().getNearestWholePosition().getY();

        if (!playersInTheGame.isEmpty()) {
            for (Character enemy : playersInTheGame) {
                if (enemy != null && enemy.isAlive() && enemy != character) {
                    if (enemy.getCurrentPosition().getDistance(x, y) <= 2) {
                        if (character.getInventory().equippedItem() != null) {
                            switch (character.getInventory().equippedItem().getType()) {

                                case SPEAR -> {
                                    enemy.setHp(enemy.getHp() - 19);
                                    character.getInventory().equippedItem().setPercentage(character.getInventory().equippedItem().percentage() - 10);
                                    if (character.getInventory().equippedItem().percentage() == 0) {
                                        character.getInventory().itemBreak();
                                    }
                                    return;
                                }

                                case AXE -> {
                                    enemy.setHp(enemy.getHp() - 8);
                                    character.getInventory().equippedItem().setPercentage(character.getInventory().equippedItem().percentage() - 2.5f);
                                    if (character.getInventory().equippedItem().percentage() == 0) {
                                        character.getInventory().itemBreak();
                                    }
                                    return;
                                }

                                case PICKAXE -> {
                                    enemy.setHp(enemy.getHp() - 8);
                                    character.getInventory().equippedItem().setPercentage(character.getInventory().equippedItem().percentage() - 3.34f);
                                    if (character.getInventory().equippedItem().percentage() == 0) {
                                        character.getInventory().itemBreak();
                                    }
                                    return;
                                }

                                case TORCH -> enemy.setHp(enemy.getHp() - 6);
                            }
                        } else {
                            enemy.setHp(enemy.getHp() - 4);
                        }
                    }
                    if (!enemy.isAlive()) {
                        float enemyCoX = enemy.getCurrentPosition().getNearestWholePosition().getX();
                        float enemyCoY = enemy.getCurrentPosition().getNearestWholePosition().getY();
                        if (enemy.getInventory().equippedItem() != null) {
                            AbstractItem equipped = enemy.getInventory().equippedItem();
                            enemy.getInventory().unequipItem();
                            map[(int) enemyCoY][(int) enemyCoX].placeItem(equipped);
                        }
                        for (int i = 0; i < 10; i++) {
                            if (enemy.getInventory().getItem(i) != null) {
                                drop(enemy, i);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

}
