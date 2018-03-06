package doodoo.doodooordoodont;

/**
 * Created by David on 2/11/2018.
 */

public class Restroom {
    private String UID;
    private String name;

    private float mAvgRating;
    private int mNumRatings;
    private int mNumStalls;
    private float fAvgRating;
    private int fNumRatings;
    private int fNumStalls;

    public static boolean menDisplayed;

    public Restroom(String UID, String name) {
        this.UID = UID;
        this.name = name;
        menDisplayed = true;
    }


    public void setRatings(float mAvgRating, int mNumRatings,float fAvgRating, int fNumRatings) {
        this.fNumRatings = fNumRatings;
        this.fAvgRating = fAvgRating;
        this.mNumRatings = mNumRatings;
        this.mAvgRating = mAvgRating;
    }

    public float getmAvgRating() {
        return mAvgRating;
    }

    public int getmNumRatings() {
        return mNumRatings;
    }

    public float getfAvgRating() {
        return fAvgRating;
    }

    public int getfNumRatings() {
        return fNumRatings;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public static boolean isMenDisplayed() {
        return menDisplayed;
    }

    public static void setMenDisplayed(boolean bool) {
        menDisplayed = bool;
    }
}
