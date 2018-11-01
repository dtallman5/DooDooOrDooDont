package doodoo.doodooordoodont;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2/11/2018.
 */

public class Restroom implements Parcelable{
    private String UID;

    private String name;

    private double mAvgRating;
    private int mNumRatings;
    private int mNumStalls;
    private double fAvgRating;
    private int fNumRatings;
    private int fNumStalls;
    private double lat,lon;

    public static boolean menDisplayed;

    public Restroom(String UID, String name) {
        this.UID = UID;
        this.name = name;
        menDisplayed = true;
    }

    public  Restroom(Map map){
        UID = (String) map.get("UID");
        name = (String) map.get("name");
        mAvgRating = (double) map.get("mAvgRating");
        mNumRatings = (int) ((long) map.get("mNumRatings"));
        mNumRatings = (int) ((long)map.get("mNumStalls"));
        fAvgRating = (double) map.get("fAvgRating");
        fNumRatings = (int) ((long)map.get("fNumRatings"));
        fNumStalls = (int) ((long) map.get("fNumStalls"));
        lat = (double) map.get("lat");
        lon = (double) map.get("lon");
    }


    public void setRatings(float mAvgRating, int mNumRatings,float fAvgRating, int fNumRatings) {
        this.fNumRatings = fNumRatings;
        this.fAvgRating = fAvgRating;
        this.mNumRatings = mNumRatings;
        this.mAvgRating = mAvgRating;
    }

    public double getmAvgRating() {
        return mAvgRating;
    }

    public int getmNumRatings() {
        return mNumRatings;
    }

    public double getfAvgRating() {
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

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    protected Restroom(Parcel in) {
        UID = in.readString();
        name = in.readString();
        mAvgRating = in.readFloat();
        mNumRatings = in.readInt();
        mNumStalls = in.readInt();
        fAvgRating = in.readFloat();
        fNumRatings = in.readInt();
        fNumStalls = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UID);
        dest.writeString(name);
        dest.writeDouble(mAvgRating);
        dest.writeInt(mNumRatings);
        dest.writeInt(mNumStalls);
        dest.writeDouble(fAvgRating);
        dest.writeInt(fNumRatings);
        dest.writeInt(fNumStalls);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
    }


    public Map toMap() {

        Map<String, Object> map  = new HashMap<>();

        map.put("UID", UID);
        map.put("name", name);
        map.put("mAvgRating", mAvgRating);
        map.put("mNumRatings", (Integer) mNumRatings);
        map.put("mNumStalls", (Integer) mNumStalls);
        map.put("fAvgRating", fAvgRating);
        map.put("fNumRatings", (Integer) fNumRatings);
        map.put("fNumStalls", (Integer) fNumStalls);
        map.put("lat",lat);
        map.put("lon",lon);

        return map;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Restroom> CREATOR = new Parcelable.Creator<Restroom>() {
        @Override
        public Restroom createFromParcel(Parcel in) {
            return new Restroom(in);
        }

        @Override
        public Restroom[] newArray(int size) {
            return new Restroom[size];
        }
    };
}
