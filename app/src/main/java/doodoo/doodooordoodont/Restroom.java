package doodoo.doodooordoodont;

/**
 * Created by David on 2/11/2018.
 */

public class Restroom {
    private String UID;
    private String name;

    private float avgRating;
    private int numRatings;
    private int numStalls;
    private boolean menDisplayed;

    public Restroom(String UID, String name) {
        this.UID = UID;
        this.name = name;
        menDisplayed = true;
    }


    public void setRatings(float avgRating, int numRatings) {
        this.numRatings = numRatings;
        this.avgRating = avgRating;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public boolean isMenDisplayed() {
        return menDisplayed;
    }

    public void setMenDisplayed(boolean menDisplayed) {
        this.menDisplayed = menDisplayed;
    }
}
