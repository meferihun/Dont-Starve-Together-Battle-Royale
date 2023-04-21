package prog1.kotprog.dontstarve.solution.utility;

/**
 * Egy 2 dimenziós (x, y) koordinátát leíró osztály.
 */
public class Position {
    /**
     * vízszintes (x) irányú koordináta.
     */
    private float x;

    /**
     * függőleges (y) irányú koordináta.
     */
    private float y;

    /**
     * Két paraméteres konstruktor, amely segítségével egy új pozíciót lehet létrehozni.
     *
     * @param x vízszintes (x) irányú koordináta
     * @param y függőleges (y) irányú koordináta
     */
    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Az aktuális koordinátához legközelebbi, csak egész értékű komponensekből álló koordináta kiszámítása.<br>
     * A kerekítés a matematika szabályainak megfelelően történik.
     *
     * @return a kiszámolt pozíció
     */
    public Position getNearestWholePosition() {
        float nearX = Math.round(getX());
        float nearY = Math.round(getY());
        return new Position(nearX, nearY);
    }

    /**
     * x koordináta gettere.
     *
     * @return x koordináta
     */
    public float getX() {
        return x;
    }

    /**
     * x koordináta settere.
     *
     * @param x új x érték
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * y koordináta gettere.
     *
     * @return y koordináta
     */
    public float getY() {
        return y;
    }

    /**
     * y koordináta settere.
     *
     * @param y új y érték
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Az aktualis koordinata es a parameterkent adott masik koordinata kozotti tavolsag kiszamitasa.
     *
     * @param x2 a masik pont x koordinataja
     * @param y2 a masik pont y koordinataja
     * @return a ket pont kozotti tavolsag
     */
    public float getDistance(float x2, float y2) {
        return (float) Math.sqrt((y2 - y) * (y2 - y) + (x2 - x) * (x2 - x));
    }
}