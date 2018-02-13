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

    public Restroom(String UID, String name) {
        this.UID = UID;
        this.name = name;
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
}
