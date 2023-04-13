package prog1.kotprog.dontstarve.solution.level;

import prog1.kotprog.dontstarve.solution.character.Character;
import prog1.kotprog.dontstarve.solution.exceptions.NotImplementedException;
import prog1.kotprog.dontstarve.solution.inventory.items.AbstractItem;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemFire;
import prog1.kotprog.dontstarve.solution.inventory.items.ItemType;

import java.util.ArrayList;
import java.util.List;

public class Field implements BaseField {
    private final int color;

    /* A kitermelt nyersanyag mennyiseg
    Ha eleri az 1.0-t, akkor befejeztuk a kitermelest, az inventoryba kerulhet az adott nyersanyag.
     */
    private float extractionProgress;

    // a BaseField interfeszben latszik, hogy van egy items() metodus es annak a parametere alapjan
    // tudjuk, hogy ide milyen tipusu valtozot varunk
    // ez egy dynamic array lesz; egyelore nem tudjuk, mekkora, majd amikor pakolaszunk bele elemeket,
    // akkor majd atmeretezodik mindig
    private List<AbstractItem> items;

    public Field(int color) {
        this.color = color;
        this.extractionProgress = 0.0f;
        this.items = new ArrayList<AbstractItem>();
    }

    @Override
    public boolean isWalkable() {
        return color != MapColors.WATER;
    }

    @Override
    public boolean hasTree() {
        return color == MapColors.TREE;
    }

    @Override
    public boolean hasStone() {
        return color == MapColors.STONE;
    }

    @Override
    public boolean hasTwig() {
        return color == MapColors.TWIG;
    }

    @Override
    public boolean hasBerry() {
        return color == MapColors.BERRY;
    }

    @Override
    public boolean hasCarrot() {
        return color == MapColors.CARROT;
    }

    @Override
    public boolean hasFire() {
        // megnezed az items arraylistet, hogy van-e benne tabortuz
        return items.contains(ItemType.FIRE);
    }

    @Override
    public AbstractItem[] items() {
        // a tavon nem lehet semmilyen targy
        if (color != MapColors.WATER) {
            // az items tombunk egy ArrayList, ezt at kell konvertalni tombbe, mielott returnoljuk
            // https://www.javatpoint.com/dynamic-array-in-java
            AbstractItem[] itemArray = new AbstractItem[items.size()];
            return items.toArray(itemArray);
        } else {
            return null;
        }
    }

    /*
    A mezore elhelyezunk egy elemet, pl. mert kitermeles befejezodott vagy mert kivesszuk az inventorynkbol.
     */
    public void addItem(AbstractItem item) {
        // gyakorlatilag ide most egy mezei items.add(item) kell es kesz :) szokasos null checkekkel
        throw new NotImplementedException();
    }

    /*
    A mezorol felvesszuk a legfelso elemet, ha van rajta.
     */
    public AbstractItem getItem() {
        // igy kell majd hasznalni add(), remove() fuggvenyekkel: https://stackoverflow.com/a/53611633
        // - items.isEmpty()?
        //   - ha nem, akkor fogjuk le a legfelso itemet: items.get(0), taroljuk el egy valtozoban
        //   - toroljuk a legfelso elemet: items.remove(0)
        //   - return az eltarolt valtozot
        // - return null, ha ures volt az arraylist
        throw new NotImplementedException();
    }

    /*
    Lekerdezi, hogy az adott mezon hany %-on all a kitermeles merteke.

    @return a kitermeles allapota az adott mezon
     */
    public float getExtractionProgress() {
        return extractionProgress;
    }

    /*
    A kitermeles akcio soran ezt a fuggvenyt hivjuk meg, hogy modositsa a mezon levo kitermelesi allapotot
    leiro privat adattagot.

    Ha a kitermeles epp most erne el a 100%-ot, akkor a kitermeles akcio soran a nyersanyagot egybol be is gyujtjuk
    a jatekos inventoryjaba. Ha ott nem fert el, meghivja a Field.placeItem() fuggvenyt, hogy elhelyezze oda a be nem
    gyujtott, de kitermelt nyersanyagot.
    Ez utan lenullazzuk az kitermeles allapotat.

    A fa es a ko eseteben a kitermeles vegleges, tobbet nem lehet majd banyaszas akciot futtatni rajta,
    nem novelhetjuk a kitermeles allapotat 1.0 fole.
    (Mivel a Field mezo tipusa final int, nem tudjuk EMPTY tipusura valtoztatni, igy tovabbra is konek, fanak
    mutatjuk, csak nem lehet tobbe banyaszni rajta.)

    @param value a kitermeles uj allapota, nyersanyagfuggo.
    Fa kitermeles 4 idoegyseg, igy minden kitermeles akcio 0.25-tel noveli ezt az erteket.
    Ko eseten ugyanez 5 egyseg -> 0.2-vel noveli.
    Gally -> 0.5.
    Bogyo, repa eseten nem szukseges ezt meghivni, mert egybol kitermelodik, es vegtelen mennyiseg van belole.
     */
    public void setExtractionProgress(float value) {
        // szoval ide nem kell sok logika, a kitermeles actionben kell majd ezeket a 0.2, 0.4, stb. szamokat kezelni
        // itt most csak ennyi kell:
        // - value < 0, value > 1? hibas input, do nothing
        // - minden mas esetben: allitsd value-ra a mezo kitermelesi allapotat
        throw new NotImplementedException();
    }
}
